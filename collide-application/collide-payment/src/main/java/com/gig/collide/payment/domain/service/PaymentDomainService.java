package com.gig.collide.payment.domain.service;

import com.gig.collide.payment.infrastructure.config.PaymentConfiguration;
import com.gig.collide.payment.infrastructure.entity.PaymentCallback;
import com.gig.collide.payment.infrastructure.entity.PaymentRecord;
import com.gig.collide.payment.infrastructure.exception.PaymentBusinessException;
import com.gig.collide.payment.infrastructure.exception.PaymentErrorCode;
import com.gig.collide.payment.infrastructure.mapper.PaymentCallbackMapper;
import com.gig.collide.payment.infrastructure.mapper.PaymentRecordMapper;
import com.gig.collide.payment.infrastructure.service.IdempotentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 支付域服务
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final PaymentCallbackMapper paymentCallbackMapper;
    private final IdempotentService idempotentService;
    private final PaymentConfiguration paymentConfiguration;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 创建支付记录（幂等）
     * 
     * @param orderNo 订单号
     * @param userId 用户ID
     * @param payAmount 支付金额
     * @param payType 支付方式
     * @param payScene 支付场景
     * @param clientIp 客户端IP
     * @param notifyUrl 回调地址
     * @return 支付记录
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord createPayment(String orderNo, Long userId, BigDecimal payAmount, 
                                     String payType, String payScene, String clientIp, String notifyUrl) {
        
        // 参数验证
        validateCreatePaymentParams(orderNo, userId, payAmount, payType);
        
        // 使用幂等性保证重复请求安全
        String idempotentKey = IdempotentService.generatePaymentIdempotentKey(orderNo, userId);
        
        return idempotentService.executeIdempotent(idempotentKey, () -> {
            log.info("创建支付记录，订单号：{}，用户ID：{}，金额：{}，支付方式：{}", 
                orderNo, userId, payAmount, payType);
            
            // 检查是否已存在支付记录
            PaymentRecord existingRecord = paymentRecordMapper.selectByOrderNoAndUserId(orderNo, userId);
            if (existingRecord != null) {
                
                // 如果是相同用户的重复请求，返回已有记录
                if ("PENDING".equals(existingRecord.getPayStatus())) {
                    log.info("返回已存在的待支付记录，订单号：{}", orderNo);
                    return existingRecord;
                } else if ("SUCCESS".equals(existingRecord.getPayStatus())) {
                    throw PaymentBusinessException.of(PaymentErrorCode.ORDER_ALREADY_PAID);
                }
            }
            
            // 创建新的支付记录
            PaymentRecord paymentRecord = buildPaymentRecord(orderNo, userId, payAmount, 
                payType, payScene, clientIp, notifyUrl);
            
            int insertResult = paymentRecordMapper.insert(paymentRecord);
            if (insertResult <= 0) {
                throw PaymentBusinessException.of(PaymentErrorCode.CREATE_PAYMENT_FAILED);
            }
            
            // 缓存支付记录
            cachePaymentRecord(paymentRecord);
            
            log.info("支付记录创建成功，ID：{}，内部流水号：{}", 
                paymentRecord.getId(), paymentRecord.getInternalTransactionNo());
            
            return paymentRecord;
        });
    }

    /**
     * 处理支付回调（幂等）
     * 
     * @param orderNo 订单号
     * @param transactionNo 外部交易流水号
     * @param callbackParams 回调参数
     * @return 处理结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean processPaymentCallback(String orderNo, String transactionNo, Map<String, String> callbackParams) {
        
        // 使用幂等性保证回调重复处理安全
        String idempotentKey = IdempotentService.generateCallbackIdempotentKey(orderNo, transactionNo);
        
        return idempotentService.executeIdempotent(idempotentKey, () -> {
            log.info("处理支付回调，订单号：{}，交易流水号：{}，回调参数：{}", 
                orderNo, transactionNo, callbackParams);
            
            long startTime = System.currentTimeMillis();
            
            try {
                // 查找支付记录 (通过内部交易号查询，符合去连表设计)
                PaymentRecord paymentRecord = paymentRecordMapper.selectByInternalTransactionNo(transactionNo);
                if (paymentRecord == null) {
                    throw PaymentBusinessException.of(PaymentErrorCode.PAYMENT_NOT_FOUND);
                }
                
                // 检查支付状态
                if ("SUCCESS".equals(paymentRecord.getPayStatus())) {
                    log.info("支付已成功，跳过处理，订单号：{}", orderNo);
                    return true;
                }
                
                if (!"PENDING".equals(paymentRecord.getPayStatus())) {
                    log.warn("支付状态不是待支付，无法处理回调，订单号：{}，当前状态：{}", 
                        orderNo, paymentRecord.getPayStatus());
                    return false;
                }
                
                // 记录回调信息
                PaymentCallback callback = recordCallback(paymentRecord, transactionNo, callbackParams, startTime);
                
                // 验证回调数据
                boolean isValid = validateCallback(callbackParams, paymentRecord.getPayType());
                if (!isValid) {
                    updateCallbackStatus(callback, "FAILED", "回调数据验证失败", startTime);
                    return false;
                }
                
                // 更新支付状态为成功
                String payStatus = determinePayStatus(callbackParams);
                if ("SUCCESS".equals(payStatus)) {
                    updatePaymentSuccess(paymentRecord, transactionNo);
                    updateCallbackStatus(callback, "SUCCESS", "处理成功", startTime);
                    
                    // 清除缓存中的待支付状态
                    clearPaymentCache(orderNo);
                    
                    log.info("支付回调处理成功，订单号：{}，交易流水号：{}", orderNo, transactionNo);
                    return true;
                } else {
                    // 支付失败
                    String failureReason = callbackParams.getOrDefault("failure_reason", "支付失败");
                    updatePaymentFailure(paymentRecord, failureReason);
                    updateCallbackStatus(callback, "SUCCESS", "处理成功（支付失败）", startTime);
                    
                    log.info("支付失败，订单号：{}，失败原因：{}", orderNo, failureReason);
                    return true;
                }
                
            } catch (Exception e) {
                log.error("支付回调处理异常，订单号：{}", orderNo, e);
                throw PaymentBusinessException.of(PaymentErrorCode.CALLBACK_PROCESS_ERROR, 
                    "回调处理异常：" + e.getMessage(), e);
            }
        });
    }

    /**
     * 查询支付状态（v2.0 去连表设计）
     * 
     * @param orderNo 订单号
     * @param userId 用户ID
     * @param userName 用户名称
     * @return 支付状态信息
     */
    public Map<String, Object> getPaymentStatusV2(String orderNo, Long userId, String userName) {
        if (!StringUtils.hasText(orderNo)) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "订单号不能为空");
        }
        if (userId == null || userId <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "用户ID无效");
        }
        
        log.info("查询支付状态（v2.0），订单号：{}，用户ID：{}，用户名：{}", orderNo, userId, userName);
        
        // 先从缓存查询
        String cacheKey = "payment:status:" + orderNo + ":" + userId;
        Map<String, Object> cachedStatus = getCachedPaymentStatusV2(cacheKey);
        if (cachedStatus != null) {
            log.info("从缓存获取支付状态（v2.0），订单号：{}", orderNo);
            return cachedStatus;
        }
        
        // 从数据库查询（去连表设计）
        PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (paymentRecord == null) {
            Map<String, Object> notFoundStatus = new HashMap<>();
            notFoundStatus.put("orderNo", orderNo);
            notFoundStatus.put("userId", userId);
            notFoundStatus.put("userName", userName);
            notFoundStatus.put("payStatus", "NOT_FOUND");
            notFoundStatus.put("message", "支付记录不存在");
            return notFoundStatus;
        }
        
        Map<String, Object> status = buildPaymentStatusResponseV2(paymentRecord);
        
        // 缓存支付状态
        cachePaymentStatusV2(cacheKey, status);
        
        return status;
    }

    /**
     * 查询支付状态（兼容旧版本，需要userId参数）
     * 
     * @param orderNo 订单号
     * @param userId 用户ID（必需参数，去连表设计）
     * @return 支付状态信息
     */
    public Map<String, Object> getPaymentStatus(String orderNo, Long userId) {
        if (!StringUtils.hasText(orderNo)) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "订单号不能为空");
        }
        if (userId == null || userId <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "用户ID无效（去连表设计要求）");
        }
        
        log.info("查询支付状态，订单号：{}，用户ID：{}", orderNo, userId);
        
        // 先从缓存查询
        String cacheKey = "payment:status:" + orderNo + ":" + userId;
        Map<String, Object> cachedStatus = getCachedPaymentStatusV2(cacheKey);
        if (cachedStatus != null) {
            log.info("从缓存获取支付状态，订单号：{}", orderNo);
            return cachedStatus;
        }
        
        // 从数据库查询（去连表设计）
        PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (paymentRecord == null) {
            Map<String, Object> notFoundStatus = new HashMap<>();
            notFoundStatus.put("orderNo", orderNo);
            notFoundStatus.put("userId", userId);
            notFoundStatus.put("payStatus", "NOT_FOUND");
            notFoundStatus.put("message", "支付记录不存在");
            return notFoundStatus;
        }
        
        Map<String, Object> status = buildPaymentStatusResponse(paymentRecord);
        
        // 缓存支付状态
        cachePaymentStatusV2(cacheKey, status);
        
        return status;
    }

    /**
     * 重置支付状态（测试用，去连表设计）
     * 
     * @param orderNo 订单号
     * @param userId 用户ID（必需参数，去连表设计）
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPaymentStatus(String orderNo, Long userId) {
        if (!StringUtils.hasText(orderNo)) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "订单号不能为空");
        }
        if (userId == null || userId <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "用户ID无效（去连表设计要求）");
        }
        
        log.info("重置支付状态（去连表设计），订单号：{}，用户ID：{}", orderNo, userId);
        
        // 从数据库查询（去连表设计）
        PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderNoAndUserId(orderNo, userId);
        if (paymentRecord == null) {
            throw PaymentBusinessException.of(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        
        // 重置支付状态
        paymentRecord.setPayStatus("PENDING");
        paymentRecord.setTransactionNo(null);
        paymentRecord.setCompleteTime(null);
        paymentRecord.setFailureReason(null);
        paymentRecord.setNotifyStatus("PENDING");
        paymentRecord.setNotifyCount(0);
        paymentRecord.setLastNotifyTime(null);
        
        int updateResult = paymentRecordMapper.updateById(paymentRecord);
        if (updateResult <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.SYSTEM_ERROR, "重置支付状态失败");
        }
        
        // 清除缓存
        clearPaymentCacheV2(orderNo, userId);
        
        // 清除幂等性记录
        String paymentIdempotentKey = IdempotentService.generatePaymentIdempotentKey(orderNo, userId);
        idempotentService.clearIdempotentRecord(paymentIdempotentKey);
        
        log.info("支付状态重置成功（去连表设计），订单号：{}，用户ID：{}", orderNo, userId);
    }

    /**
     * 创建支付记录（v2.0 去连表设计）
     * 
     * @param request 创建支付请求（包含完整的去连表数据）
     * @return 支付记录
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord createPaymentV2(com.gig.collide.api.payment.request.CreatePaymentRequest request) {
        
        // 参数验证
        validateCreatePaymentRequestV2(request);
        
        // 使用幂等性保证重复请求安全
        String idempotentKey = IdempotentService.generatePaymentIdempotentKey(request.getOrderNo(), request.getUserId());
        
        return idempotentService.executeIdempotent(idempotentKey, () -> {
            log.info("创建支付记录（v2.0 去连表设计），订单号：{}，用户：{}，金额：{}", 
                request.getOrderNo(), request.getUserName(), request.getPayAmount());
            
            // 检查是否已存在支付记录（去连表设计）
            PaymentRecord existingRecord = paymentRecordMapper.selectByOrderNoAndUserId(
                request.getOrderNo(), request.getUserId());
            if (existingRecord != null) {
                if ("PENDING".equals(existingRecord.getPayStatus())) {
                    log.info("返回已存在的待支付记录（去连表设计），订单号：{}", request.getOrderNo());
                    return existingRecord;
                } else if ("SUCCESS".equals(existingRecord.getPayStatus())) {
                    throw PaymentBusinessException.of(PaymentErrorCode.ORDER_ALREADY_PAID);
                }
            }
            
            // 创建新的支付记录（包含完整的冗余数据）
            PaymentRecord paymentRecord = buildPaymentRecordV2(request);
            
            int insertResult = paymentRecordMapper.insert(paymentRecord);
            if (insertResult <= 0) {
                throw PaymentBusinessException.of(PaymentErrorCode.CREATE_PAYMENT_FAILED);
            }
            
            // 缓存支付记录
            cachePaymentRecordV2(paymentRecord);
            
            log.info("支付记录创建成功（v2.0 去连表设计），ID：{}，内部流水号：{}", 
                paymentRecord.getId(), paymentRecord.getInternalTransactionNo());
            
            return paymentRecord;
        });
    }

    /**
     * 处理支付回调（v2.0 去连表设计）
     * 
     * @param request 支付回调请求（包含完整的去连表数据）
     * @return 处理结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean processPaymentCallbackV2(com.gig.collide.api.payment.request.PaymentCallbackRequest request) {
        
        // 使用幂等性保证回调重复处理安全
        String idempotentKey = IdempotentService.generateCallbackIdempotentKey(
            request.getOrderNo(), request.getTransactionNo());
        
        return idempotentService.executeIdempotent(idempotentKey, () -> {
            log.info("处理支付回调（v2.0 去连表设计），订单号：{}，用户：{}，流水号：{}", 
                request.getOrderNo(), request.getUserName(), request.getTransactionNo());
            
            long startTime = System.currentTimeMillis();
            
            try {
                // 查找支付记录（去连表设计：使用内部交易号查询）
                PaymentRecord paymentRecord = paymentRecordMapper.selectByInternalTransactionNo(
                    request.getInternalTransactionNo());
                if (paymentRecord == null) {
                    throw PaymentBusinessException.of(PaymentErrorCode.PAYMENT_NOT_FOUND);
                }
                
                // 验证订单号和用户ID是否匹配（去连表设计的数据一致性检查）
                if (!paymentRecord.getOrderNo().equals(request.getOrderNo()) || 
                    !paymentRecord.getUserId().equals(request.getUserId())) {
                    throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "回调数据不匹配");
                }
                
                // 检查支付状态
                if ("SUCCESS".equals(paymentRecord.getPayStatus())) {
                    log.info("支付已成功，跳过处理（v2.0），订单号：{}", request.getOrderNo());
                    return true;
                }
                
                if (!"PENDING".equals(paymentRecord.getPayStatus())) {
                    log.warn("支付状态不是待支付，无法处理回调（v2.0），订单号：{}，当前状态：{}", 
                        request.getOrderNo(), paymentRecord.getPayStatus());
                    return false;
                }
                
                // 记录回调信息（包含完整的冗余数据）
                PaymentCallback callback = recordCallbackV2(paymentRecord, request, startTime);
                
                // 验证回调数据
                boolean isValid = validateCallbackV2(request);
                if (!isValid) {
                    updateCallbackStatus(callback, "FAILED", "回调数据验证失败", startTime);
                    return false;
                }
                
                // 更新支付状态
                String payStatus = determinePayStatusV2(request);
                if ("SUCCESS".equals(payStatus)) {
                    updatePaymentSuccessV2(paymentRecord, request);
                    updateCallbackStatus(callback, "SUCCESS", "处理成功", startTime);
                    
                    // 清除缓存中的待支付状态
                    clearPaymentCacheV2(request.getOrderNo(), request.getUserId());
                    
                    log.info("支付回调处理成功（v2.0），订单号：{}，用户：{}", 
                        request.getOrderNo(), request.getUserName());
                    return true;
                } else {
                    // 支付失败
                    String failureReason = request.getFailureReason() != null ? 
                        request.getFailureReason() : "支付失败";
                    updatePaymentFailureV2(paymentRecord, failureReason, request.getFailureCode());
                    updateCallbackStatus(callback, "SUCCESS", "处理成功（支付失败）", startTime);
                    
                    log.info("支付失败（v2.0），订单号：{}，失败原因：{}", 
                        request.getOrderNo(), failureReason);
                    return true;
                }
                
            } catch (Exception e) {
                log.error("支付回调处理异常（v2.0），订单号：{}", request.getOrderNo(), e);
                throw PaymentBusinessException.of(PaymentErrorCode.CALLBACK_PROCESS_ERROR, 
                    "回调处理异常：" + e.getMessage(), e);
            }
        });
    }

    /**
     * 获取支付配置
     * 
     * @return 配置信息
     */
    public Map<String, Object> getPaymentConfig() {
        Map<String, Object> config = new HashMap<>();
        
        PaymentConfiguration.TestConfig testConfig = paymentConfiguration.getTest();
        PaymentConfiguration.RealConfig realConfig = paymentConfiguration.getReal();
        
        config.put("testEnabled", testConfig.getEnabled());
        config.put("realEnabled", realConfig.getEnabled());
        config.put("supportedPayTypes", testConfig.getSupportedPayTypes());
        config.put("autoSuccessDelay", testConfig.getAutoSuccessDelay());
        config.put("idempotencyEnabled", paymentConfiguration.getIdempotency().getEnabled());
        config.put("cacheEnabled", paymentConfiguration.getCache().getEnabled());
        
        return config;
    }

    // ===== 私有方法 =====

    /**
     * 验证创建支付参数
     */
    private void validateCreatePaymentParams(String orderNo, Long userId, BigDecimal payAmount, String payType) {
        if (!StringUtils.hasText(orderNo)) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "订单号不能为空");
        }
        if (userId == null || userId <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "用户ID无效");
        }
        if (payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.INVALID_PAY_AMOUNT);
        }
        if (!StringUtils.hasText(payType)) {
            throw PaymentBusinessException.of(PaymentErrorCode.INVALID_PAY_TYPE);
        }
    }

    /**
     * 构建支付记录
     */
    private PaymentRecord buildPaymentRecord(String orderNo, Long userId, BigDecimal payAmount, 
                                           String payType, String payScene, String clientIp, String notifyUrl) {
        return PaymentRecord.builder()
            .orderNo(orderNo)
            .internalTransactionNo(generateInternalTransactionNo())
            .userId(userId)
            .payAmount(payAmount)
            .payType(payType)
            .payStatus("PENDING")
            .payScene(payScene != null ? payScene : "WEB")
            .clientIp(clientIp)
            .payTime(LocalDateTime.now())
            .expireTime(LocalDateTime.now().plusSeconds(1800)) // 30分钟过期
            .notifyUrl(notifyUrl)
            .notifyStatus("PENDING")
            .notifyCount(0)
            .version(0)
            .build();
    }

    /**
     * 生成内部交易流水号
     */
    private String generateInternalTransactionNo() {
        return "COLLIDE_" + System.currentTimeMillis() + "_" + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    /**
     * 记录回调信息
     */
    private PaymentCallback recordCallback(PaymentRecord paymentRecord, String transactionNo, 
                                         Map<String, String> callbackParams, long startTime) {
        PaymentCallback callback = PaymentCallback.builder()
            .paymentRecordId(paymentRecord.getId())
            .orderNo(paymentRecord.getOrderNo())
            .transactionNo(transactionNo)
            .internalTransactionNo(paymentRecord.getInternalTransactionNo())
            .userId(paymentRecord.getUserId())
            .userName(paymentRecord.getUserName())
            .payAmount(paymentRecord.getPayAmount())
            .payType(paymentRecord.getPayType())
            .callbackType("PAYMENT")
            .callbackSource(paymentRecord.getPayType())
            .callbackStatus("PENDING")
            .callbackContent(callbackParams.toString())
            .clientIp(callbackParams.get("client_ip"))
            .userAgent(callbackParams.get("user_agent"))
            .retryCount(0)
            .maxRetryCount(3)
            .build();
        
        paymentCallbackMapper.insert(callback);
        return callback;
    }

    /**
     * 验证回调数据
     */
    private boolean validateCallback(Map<String, String> callbackParams, String payType) {
        // 简单验证（实际环境中需要验证签名等）
        if (callbackParams == null || callbackParams.isEmpty()) {
            return false;
        }
        
        // 检查必要参数
        if (!callbackParams.containsKey("order_no") || 
            !callbackParams.containsKey("pay_status")) {
            return false;
        }
        
        // TODO: 根据不同支付方式验证签名
        switch (payType) {
            case "ALIPAY":
                return validateAlipayCallback(callbackParams);
            case "WECHAT":
                return validateWechatCallback(callbackParams);
            case "TEST":
                return true; // 测试模式跳过验证
            default:
                return false;
        }
    }

    /**
     * 验证支付宝回调
     */
    private boolean validateAlipayCallback(Map<String, String> callbackParams) {
        // TODO: 实现支付宝签名验证
        return true;
    }

    /**
     * 验证微信回调
     */
    private boolean validateWechatCallback(Map<String, String> callbackParams) {
        // TODO: 实现微信支付签名验证
        return true;
    }

    /**
     * 确定支付状态
     */
    private String determinePayStatus(Map<String, String> callbackParams) {
        String payStatus = callbackParams.get("pay_status");
        return "SUCCESS".equalsIgnoreCase(payStatus) ? "SUCCESS" : "FAILED";
    }

    /**
     * 更新支付成功状态
     */
    private void updatePaymentSuccess(PaymentRecord paymentRecord, String transactionNo) {
        int updateResult = paymentRecordMapper.updatePayStatus(
            paymentRecord.getId(), 
            paymentRecord.getVersion(), 
            "SUCCESS", 
            paymentRecord.getPayAmount(), 
            LocalDateTime.now(), 
            transactionNo
        );
        
        if (updateResult <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.CONCURRENT_UPDATE, "支付状态更新失败，可能存在并发冲突");
        }
    }

    /**
     * 更新支付失败状态
     */
    private void updatePaymentFailure(PaymentRecord paymentRecord, String failureReason) {
        // 使用updatePayStatus方法来更新失败状态
        int updateResult = paymentRecordMapper.updatePayStatus(
            paymentRecord.getId(), 
            paymentRecord.getVersion(),
            "FAILED",
            BigDecimal.ZERO, // 失败时实际支付金额为0
            LocalDateTime.now(),
            null // 失败时没有交易号
        );
        
        if (updateResult <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.CONCURRENT_UPDATE, "支付状态更新失败，可能存在并发冲突");
        }
        
        // 单独更新失败原因（如果需要的话，可以扩展mapper方法）
        paymentRecord.setFailureReason(failureReason);
        paymentRecord.setFailureTime(LocalDateTime.now());
    }

    /**
     * 更新回调状态
     */
    private void updateCallbackStatus(PaymentCallback callback, String status, String result, long startTime) {
        callback.setCallbackStatus(status);
        callback.setProcessResult(result);
        callback.setProcessTimeMs(System.currentTimeMillis() - startTime);
        paymentCallbackMapper.updateById(callback);
    }

    /**
     * 缓存支付记录
     */
    private void cachePaymentRecord(PaymentRecord paymentRecord) {
        if (!paymentConfiguration.getCache().getEnabled()) {
            return;
        }
        
        try {
            String cacheKey = "payment:record:" + paymentRecord.getOrderNo();
            redisTemplate.opsForValue().set(cacheKey, paymentRecord, 
                java.time.Duration.ofSeconds(paymentConfiguration.getCache().getPaymentStatusTtl()));
        } catch (Exception e) {
            log.warn("缓存支付记录失败，订单号：{}", paymentRecord.getOrderNo(), e);
        }
    }

    /**
     * 获取缓存的支付状态
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getCachedPaymentStatus(String orderNo) {
        if (!paymentConfiguration.getCache().getEnabled()) {
            return null;
        }
        
        try {
            String cacheKey = "payment:status:" + orderNo;
            return (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("获取缓存支付状态失败，订单号：{}", orderNo, e);
            return null;
        }
    }

    /**
     * 缓存支付状态
     */
    private void cachePaymentStatus(String orderNo, Map<String, Object> status) {
        if (!paymentConfiguration.getCache().getEnabled()) {
            return;
        }
        
        try {
            String cacheKey = "payment:status:" + orderNo;
            redisTemplate.opsForValue().set(cacheKey, status, 
                java.time.Duration.ofSeconds(paymentConfiguration.getCache().getPaymentStatusTtl()));
        } catch (Exception e) {
            log.warn("缓存支付状态失败，订单号：{}", orderNo, e);
        }
    }

    /**
     * 清除支付缓存
     */
    private void clearPaymentCache(String orderNo) {
        try {
            redisTemplate.delete("payment:record:" + orderNo);
            redisTemplate.delete("payment:status:" + orderNo);
        } catch (Exception e) {
            log.warn("清除支付缓存失败，订单号：{}", orderNo, e);
        }
    }

    /**
     * 构建支付状态响应
     */
    private Map<String, Object> buildPaymentStatusResponse(PaymentRecord paymentRecord) {
        Map<String, Object> status = new HashMap<>();
        status.put("orderNo", paymentRecord.getOrderNo());
        status.put("internalTransactionNo", paymentRecord.getInternalTransactionNo());
        status.put("transactionNo", paymentRecord.getTransactionNo());
        status.put("payStatus", paymentRecord.getPayStatus());
        status.put("payAmount", paymentRecord.getPayAmount());
        status.put("actualPayAmount", paymentRecord.getActualPayAmount());
        status.put("payType", paymentRecord.getPayType());
        status.put("payTime", paymentRecord.getPayTime());
        status.put("completeTime", paymentRecord.getCompleteTime());
        status.put("expireTime", paymentRecord.getExpireTime());
        status.put("failureReason", paymentRecord.getFailureReason());
        status.put("message", getStatusMessage(paymentRecord.getPayStatus()));
        return status;
    }

    /**
     * 获取状态消息
     */
    private String getStatusMessage(String payStatus) {
        switch (payStatus) {
            case "PENDING": return "支付处理中";
            case "SUCCESS": return "支付成功";
            case "FAILED": return "支付失败";
            case "CANCELLED": return "支付已取消";
            case "REFUNDED": return "已退款";
            default: return "未知状态";
        }
    }

    // ===== V2版本私有方法（去连表设计） =====

    /**
     * 验证创建支付请求参数（v2.0）
     */
    private void validateCreatePaymentRequestV2(com.gig.collide.api.payment.request.CreatePaymentRequest request) {
        if (request == null) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "请求参数不能为空");
        }
        if (!StringUtils.hasText(request.getOrderNo())) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "订单号不能为空");
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "用户ID无效");
        }
        if (!StringUtils.hasText(request.getUserName())) {
            throw PaymentBusinessException.of(PaymentErrorCode.PARAM_INVALID, "用户名称不能为空");
        }
        if (request.getPayAmount() == null || request.getPayAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.INVALID_PAY_AMOUNT);
        }
        if (!StringUtils.hasText(request.getPayType())) {
            throw PaymentBusinessException.of(PaymentErrorCode.INVALID_PAY_TYPE);
        }
    }

    /**
     * 构建支付记录（v2.0 去连表设计）
     */
    private PaymentRecord buildPaymentRecordV2(com.gig.collide.api.payment.request.CreatePaymentRequest request) {
        return PaymentRecord.builder()
            // 基础信息
            .orderNo(request.getOrderNo())
            .internalTransactionNo(request.getInternalTransactionNo() != null ? 
                request.getInternalTransactionNo() : generateInternalTransactionNo())
            
            // 用户信息（冗余字段，避免联表查询）
            .userId(request.getUserId())
            .userName(request.getUserName())
            .userPhone(request.getUserPhone())
            .userEmail(request.getUserEmail())
            
            // 订单信息（冗余字段，避免联表查询）
            .orderTitle(request.getOrderTitle())
            .productName(request.getProductName())
            .productType(request.getProductType())
            .merchantId(request.getMerchantId())
            .merchantName(request.getMerchantName())
            
            // 金额信息
            .payAmount(request.getPayAmount())
            .discountAmount(request.getDiscountAmount() != null ? 
                request.getDiscountAmount() : BigDecimal.ZERO)
            .currencyCode(request.getCurrencyCode() != null ? 
                request.getCurrencyCode() : "CNY")
            
            // 支付信息
            .payType(request.getPayType())
            .payStatus("PENDING")
            .payScene(request.getPayScene() != null ? request.getPayScene() : "WEB")
            .payMethod(request.getPayMethod())
            
            // 时间信息
            .payTime(LocalDateTime.now())
            .expireTime(request.getExpireTime() != null ? 
                request.getExpireTime() : LocalDateTime.now().plusMinutes(30))
            
            // 网络信息
            .clientIp(request.getClientIp())
            .userAgent(request.getUserAgent())
            .deviceInfo(request.getDeviceInfo())
            
            // 回调通知信息
            .notifyUrl(request.getNotifyUrl())
            .returnUrl(request.getReturnUrl())
            .notifyStatus("PENDING")
            .notifyCount(0)
            .maxNotifyCount(request.getMaxNotifyCount() != null ? 
                request.getMaxNotifyCount() : 5)
            
            // 风控信息
            .riskLevel(request.getRiskLevel() != null ? request.getRiskLevel() : "LOW")
            .riskScore(request.getRiskScore() != null ? request.getRiskScore() : 0)
            .isBlocked(false)
            
            // 扩展信息
            .extraData(request.getExtraData() != null ? 
                request.getExtraData().toString() : null)
            .businessData(request.getBusinessData() != null ? 
                request.getBusinessData().toString() : null)
            .remark(request.getRemark())
            
            // 系统字段
            .version(0)
            .build();
    }

    /**
     * 记录回调信息（v2.0 去连表设计）
     */
    private PaymentCallback recordCallbackV2(PaymentRecord paymentRecord, 
                                           com.gig.collide.api.payment.request.PaymentCallbackRequest request, 
                                           long startTime) {
        PaymentCallback callback = PaymentCallback.builder()
            // 基础信息
            .paymentRecordId(request.getPaymentRecordId())
            
            // 冗余支付信息（避免联表查询）
            .orderNo(request.getOrderNo())
            .transactionNo(request.getTransactionNo())
            .internalTransactionNo(request.getInternalTransactionNo())
            .userId(request.getUserId())
            .userName(request.getUserName())
            .payAmount(request.getPayAmount())
            .payType(request.getPayType())
            
            // 回调信息
            .callbackType(request.getCallbackType())
            .callbackSource(request.getCallbackSource())
            .callbackStatus("PENDING")
            .callbackResult(request.getCallbackResult())
            
            // 回调数据
            .callbackContent(request.getCallbackContent())
            .callbackSignature(request.getCallbackSignature())
            .callbackParams(request.getCallbackParams() != null ? 
                request.getCallbackParams().toString() : null)
            
            // 处理信息
            .processMessage(request.getProcessMessage())
            .errorCode(request.getErrorCode())
            .errorMessage(request.getErrorMessage())
            
            // 网络信息
            .clientIp(request.getClientIp())
            .userAgent(request.getUserAgent())
            .requestHeaders(request.getRequestHeaders() != null ? 
                request.getRequestHeaders().toString() : null)
            
            // 性能信息
            .processStartTime(LocalDateTime.now())
            
            // 重试信息
            .retryCount(0)
            .maxRetryCount(request.getMaxRetryCount() != null ? 
                request.getMaxRetryCount() : 3)
            
            .build();
        
        paymentCallbackMapper.insert(callback);
        return callback;
    }

    /**
     * 验证回调数据（v2.0）
     */
    private boolean validateCallbackV2(com.gig.collide.api.payment.request.PaymentCallbackRequest request) {
        if (request == null) {
            return false;
        }
        
        // 检查必要参数
        if (!StringUtils.hasText(request.getOrderNo()) || 
            !StringUtils.hasText(request.getInternalTransactionNo()) ||
            request.getUserId() == null) {
            return false;
        }
        
        // 根据不同支付方式验证签名
        if (request.getNeedVerifySignature() != null && request.getNeedVerifySignature()) {
            switch (request.getCallbackSource()) {
                case "ALIPAY":
                    return validateAlipayCallbackV2(request);
                case "WECHAT":
                    return validateWechatCallbackV2(request);
                case "TEST":
                    return true; // 测试模式跳过验证
                default:
                    return false;
            }
        }
        
        return true;
    }

    /**
     * 验证支付宝回调（v2.0）
     */
    private boolean validateAlipayCallbackV2(com.gig.collide.api.payment.request.PaymentCallbackRequest request) {
        // TODO: 实现支付宝签名验证
        return true;
    }

    /**
     * 验证微信回调（v2.0）
     */
    private boolean validateWechatCallbackV2(com.gig.collide.api.payment.request.PaymentCallbackRequest request) {
        // TODO: 实现微信支付签名验证
        return true;
    }

    /**
     * 确定支付状态（v2.0）
     */
    private String determinePayStatusV2(com.gig.collide.api.payment.request.PaymentCallbackRequest request) {
        return "SUCCESS".equalsIgnoreCase(request.getCallbackResult()) ? "SUCCESS" : "FAILED";
    }

    /**
     * 更新支付成功状态（v2.0）
     */
    private void updatePaymentSuccessV2(PaymentRecord paymentRecord, 
                                      com.gig.collide.api.payment.request.PaymentCallbackRequest request) {
        int updateResult = paymentRecordMapper.updatePayStatus(
            paymentRecord.getId(), 
            paymentRecord.getVersion(), 
            "SUCCESS", 
            request.getActualPayAmount() != null ? request.getActualPayAmount() : paymentRecord.getPayAmount(), 
            LocalDateTime.now(), 
            request.getTransactionNo()
        );
        
        if (updateResult <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.CONCURRENT_UPDATE, 
                "支付状态更新失败，可能存在并发冲突");
        }
    }

    /**
     * 更新支付失败状态（v2.0）
     */
    private void updatePaymentFailureV2(PaymentRecord paymentRecord, String failureReason, String failureCode) {
        // 使用updatePayStatus方法来更新失败状态
        int updateResult = paymentRecordMapper.updatePayStatus(
            paymentRecord.getId(), 
            paymentRecord.getVersion(),
            "FAILED",
            BigDecimal.ZERO, // 失败时实际支付金额为0
            LocalDateTime.now(),
            null // 失败时没有交易号
        );
        
        if (updateResult <= 0) {
            throw PaymentBusinessException.of(PaymentErrorCode.CONCURRENT_UPDATE, 
                "支付状态更新失败，可能存在并发冲突");
        }
        
        // 单独更新失败原因和错误码
        paymentRecord.setFailureReason(failureReason);
        paymentRecord.setFailureCode(failureCode);
        paymentRecord.setFailureTime(LocalDateTime.now());
    }

    /**
     * 缓存支付记录（v2.0）
     */
    private void cachePaymentRecordV2(PaymentRecord paymentRecord) {
        if (!paymentConfiguration.getCache().getEnabled()) {
            return;
        }
        
        try {
            String cacheKey = "payment:record:" + paymentRecord.getOrderNo() + ":" + paymentRecord.getUserId();
            redisTemplate.opsForValue().set(cacheKey, paymentRecord, 
                java.time.Duration.ofSeconds(paymentConfiguration.getCache().getPaymentStatusTtl()));
        } catch (Exception e) {
            log.warn("缓存支付记录失败（v2.0），订单号：{}", paymentRecord.getOrderNo(), e);
        }
    }

    /**
     * 获取缓存的支付状态（v2.0）
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getCachedPaymentStatusV2(String cacheKey) {
        if (!paymentConfiguration.getCache().getEnabled()) {
            return null;
        }
        
        try {
            return (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("获取缓存支付状态失败（v2.0），缓存键：{}", cacheKey, e);
            return null;
        }
    }

    /**
     * 缓存支付状态（v2.0）
     */
    private void cachePaymentStatusV2(String cacheKey, Map<String, Object> status) {
        if (!paymentConfiguration.getCache().getEnabled()) {
            return;
        }
        
        try {
            redisTemplate.opsForValue().set(cacheKey, status, 
                java.time.Duration.ofSeconds(paymentConfiguration.getCache().getPaymentStatusTtl()));
        } catch (Exception e) {
            log.warn("缓存支付状态失败（v2.0），缓存键：{}", cacheKey, e);
        }
    }

    /**
     * 清除支付缓存（v2.0）
     */
    private void clearPaymentCacheV2(String orderNo, Long userId) {
        try {
            redisTemplate.delete("payment:record:" + orderNo + ":" + userId);
            redisTemplate.delete("payment:status:" + orderNo + ":" + userId);
        } catch (Exception e) {
            log.warn("清除支付缓存失败（v2.0），订单号：{}，用户ID：{}", orderNo, userId, e);
        }
    }

    /**
     * 构建支付状态响应（v2.0）
     */
    private Map<String, Object> buildPaymentStatusResponseV2(PaymentRecord paymentRecord) {
        Map<String, Object> status = new HashMap<>();
        status.put("orderNo", paymentRecord.getOrderNo());
        status.put("internalTransactionNo", paymentRecord.getInternalTransactionNo());
        status.put("transactionNo", paymentRecord.getTransactionNo());
        status.put("userId", paymentRecord.getUserId());
        status.put("userName", paymentRecord.getUserName());
        status.put("payStatus", paymentRecord.getPayStatus());
        status.put("payAmount", paymentRecord.getPayAmount());
        status.put("actualPayAmount", paymentRecord.getActualPayAmount());
        status.put("discountAmount", paymentRecord.getDiscountAmount());
        status.put("payType", paymentRecord.getPayType());
        status.put("payScene", paymentRecord.getPayScene());
        status.put("orderTitle", paymentRecord.getOrderTitle());
        status.put("productName", paymentRecord.getProductName());
        status.put("merchantName", paymentRecord.getMerchantName());
        status.put("payTime", paymentRecord.getPayTime());
        status.put("completeTime", paymentRecord.getCompleteTime());
        status.put("expireTime", paymentRecord.getExpireTime());
        status.put("failureReason", paymentRecord.getFailureReason());
        status.put("failureCode", paymentRecord.getFailureCode());
        status.put("riskLevel", paymentRecord.getRiskLevel());
        status.put("message", getStatusMessage(paymentRecord.getPayStatus()));
        return status;
    }

    // ===== 其他V2方法（简单实现） =====

    /**
     * 查询支付记录（v2.0 去连表设计）
     */
    public Map<String, Object> queryPaymentRecordsV2(Long userId, String userName, Integer page, Integer size) {
        // TODO: 实现完整的支付记录查询逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("records", new java.util.ArrayList<>());
        result.put("total", 0);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    /**
     * 取消支付（v2.0 去连表设计）
     */
    public boolean cancelPaymentV2(String orderNo, Long userId, String userName, String reason) {
        // TODO: 实现完整的取消支付逻辑
        log.info("取消支付（v2.0），订单号：{}，用户：{}，原因：{}", orderNo, userName, reason);
        return true;
    }

    /**
     * 查询商户支付统计（v2.0 去连表设计）
     */
    public Map<String, Object> queryMerchantPaymentStatsV2(Long merchantId, String merchantName, String startDate, String endDate) {
        // TODO: 实现完整的商户统计逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("merchantId", merchantId);
        result.put("merchantName", merchantName);
        result.put("totalAmount", BigDecimal.ZERO);
        result.put("totalCount", 0);
        result.put("successCount", 0);
        result.put("failedCount", 0);
        return result;
    }

    /**
     * 更新支付配置（v2.0 去连表设计）
     */
    public boolean updatePaymentConfigV2(Long merchantId, String merchantName, String configType, Map<String, Object> configData) {
        // TODO: 实现完整的配置更新逻辑
        log.info("更新支付配置（v2.0），商户：{}，配置类型：{}", merchantName, configType);
        return true;
    }

    /**
     * 检查支付风险（v2.0 去连表设计）
     */
    public Map<String, Object> checkPaymentRiskV2(Long userId, String userName, String orderNo, String payType, String amount) {
        // TODO: 实现完整的风险检查逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("riskLevel", "LOW");
        result.put("riskScore", 10);
        result.put("isBlocked", false);
        result.put("riskReason", "正常交易");
        return result;
    }

    /**
     * 获取支付指标（v2.0 去连表设计）
     */
    public Map<String, Object> getPaymentMetricsV2(String metricType, String timeRange, String payType) {
        // TODO: 实现完整的指标统计逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("metricType", metricType);
        result.put("timeRange", timeRange);
        result.put("payType", payType);
        result.put("totalAmount", BigDecimal.ZERO);
        result.put("totalCount", 0);
        result.put("successRate", "0.00%");
        return result;
    }
} 