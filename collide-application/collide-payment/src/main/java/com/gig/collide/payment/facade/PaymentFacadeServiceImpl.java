package com.gig.collide.payment.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.payment.PaymentFacadeService;
import com.gig.collide.api.payment.request.PaymentCreateRequest;
import com.gig.collide.api.payment.request.PaymentQueryRequest;
import com.gig.collide.api.payment.request.PaymentCallbackRequest;
import com.gig.collide.api.payment.response.PaymentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.payment.domain.entity.Payment;
import com.gig.collide.payment.domain.service.PaymentService;
import com.gig.collide.payment.infrastructure.cache.PaymentCacheConstant;
import com.gig.collide.web.vo.Result;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 支付门面服务实现类 - 缓存增强版
 * 对齐search模块设计风格，提供完整的支付服务
 * 包含缓存功能、跨模块集成、错误处理、数据转换
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class PaymentFacadeServiceImpl implements PaymentFacadeService {

    private final PaymentService paymentService;
    
    // =================== 跨模块服务注入 ===================
    @Autowired
    private OrderFacadeService orderFacadeService;
    
    @Autowired
    private UserFacadeService userFacadeService;

    // =================== 支付核心功能 ===================

    @Override
    @CacheInvalidate(name = PaymentCacheConstant.USER_PAYMENT_CACHE)
    @CacheInvalidate(name = PaymentCacheConstant.PAYMENT_STATISTICS_CACHE)
    public Result<PaymentResponse> createPayment(PaymentCreateRequest request) {
        try {
            log.info("创建支付订单: 用户={}, 订单={}, 金额={}", request.getUserId(), request.getOrderId(), request.getAmount());
            long startTime = System.currentTimeMillis();
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法创建支付订单: userId={}", request.getUserId());
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            Payment payment = new Payment();
            BeanUtils.copyProperties(request, payment);
            
            Payment createdPayment = paymentService.createPayment(payment);
            PaymentResponse response = convertToResponse(createdPayment);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("支付订单创建成功: 支付单号={}, 用户={}, 耗时={}ms", 
                    response.getPaymentNo(), userResult.getData().getNickname(), duration);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建支付订单失败: 用户={}, 订单={}", request.getUserId(), request.getOrderId(), e);
            return Result.error("PAYMENT_CREATE_ERROR", "创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = PaymentCacheConstant.PAYMENT_DETAIL_CACHE, key = PaymentCacheConstant.PAYMENT_DETAIL_BY_ID_KEY,
            expire = PaymentCacheConstant.PAYMENT_DETAIL_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PaymentResponse> getPaymentById(Long paymentId) {
        try {
            log.debug("查询支付详情: ID={}", paymentId);
            
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                log.warn("支付记录不存在: ID={}", paymentId);
                return Result.error("PAYMENT_NOT_FOUND", "支付记录不存在");
            }
            
            PaymentResponse response = convertToResponse(payment);
            log.debug("支付详情查询成功: ID={}, 状态={}", paymentId, response.getStatus());
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询支付详情失败: ID={}", paymentId, e);
            return Result.error("PAYMENT_QUERY_ERROR", "查询支付详情失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = PaymentCacheConstant.PAYMENT_DETAIL_CACHE, key = PaymentCacheConstant.PAYMENT_DETAIL_BY_NO_KEY,
            expire = PaymentCacheConstant.PAYMENT_DETAIL_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PaymentResponse> getPaymentByNo(String paymentNo) {
        try {
            log.debug("根据支付单号查询: 支付单号={}", paymentNo);
            
            Payment payment = paymentService.getPaymentByNo(paymentNo);
            if (payment == null) {
                log.warn("支付记录不存在: 支付单号={}", paymentNo);
                return Result.error("PAYMENT_NOT_FOUND", "支付记录不存在");
            }
            
            PaymentResponse response = convertToResponse(payment);
            log.debug("支付单号查询成功: 支付单号={}, 状态={}", paymentNo, response.getStatus());
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据支付单号查询失败: 支付单号={}", paymentNo, e);
            return Result.error("PAYMENT_QUERY_ERROR", "根据支付单号查询失败: " + e.getMessage());
        }
    }

    // =================== 支付查询功能 ===================

    @Override
    @Cached(name = PaymentCacheConstant.USER_PAYMENT_CACHE, key = PaymentCacheConstant.USER_PAYMENT_KEY,
            expire = PaymentCacheConstant.USER_PAYMENT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<PaymentResponse>> queryPayments(PaymentQueryRequest request) {
        try {
            log.debug("分页查询支付记录: 用户={}, 页码={}", request.getUserId(), request.getCurrentPage());
            long startTime = System.currentTimeMillis();
            
            // 验证用户是否存在（如果指定了用户ID）
            if (request.getUserId() != null) {
                Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
                if (userResult == null || !userResult.getSuccess()) {
                    log.warn("用户不存在，无法查询支付记录: userId={}", request.getUserId());
                    return Result.error("USER_NOT_FOUND", "用户不存在");
                }
            }
            
            IPage<Payment> page = paymentService.queryPayments(
                request.getUserId(),
                request.getPayMethod(),
                request.getStatus(),
                request.getPaymentNo(),
                request.getOrderNo(),
                request.getOrderId(),
                request.getThirdPartyNo(),
                request.getMinAmount(),
                request.getMaxAmount(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getSortBy(),
                request.getSortDirection()
            );
            
            List<PaymentResponse> responses = page.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            PageResponse<PaymentResponse> result = convertToPageResponse(page, responses);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("支付记录查询完成: 用户={}, 记录数={}, 耗时={}ms", 
                    request.getUserId(), result.getTotal(), duration);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询支付记录失败: 用户={}", request.getUserId(), e);
            return Result.error("PAYMENT_QUERY_ERROR", "分页查询支付记录失败: " + e.getMessage());
        }
    }

    // =================== 支付回调处理 ===================

    @Override
    @CacheInvalidate(name = PaymentCacheConstant.PAYMENT_DETAIL_CACHE)
    @CacheInvalidate(name = PaymentCacheConstant.USER_PAYMENT_CACHE)
    @CacheInvalidate(name = PaymentCacheConstant.ORDER_PAYMENT_STATUS_CACHE)
    public Result<Void> handlePaymentCallback(PaymentCallbackRequest request) {
        try {
            log.info("处理支付回调: 支付单号={}, 状态={}, 第三方单号={}", 
                    request.getPaymentNo(), request.getStatus(), request.getThirdPartyNo());
            long startTime = System.currentTimeMillis();
            
            paymentService.handlePaymentCallback(
                request.getPaymentNo(),
                request.getStatus(),
                request.getThirdPartyNo(),
                request.getPayTime(),
                request.getNotifyTime()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("支付回调处理成功: 支付单号={}, 耗时={}ms", request.getPaymentNo(), duration);
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("支付回调处理失败: 支付单号={}", request.getPaymentNo(), e);
            return Result.error("PAYMENT_CALLBACK_ERROR", "支付回调处理失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = PaymentCacheConstant.PAYMENT_DETAIL_CACHE)
    @CacheInvalidate(name = PaymentCacheConstant.USER_PAYMENT_CACHE)
    public Result<Void> cancelPayment(Long paymentId) {
        try {
            log.info("取消支付: ID={}", paymentId);
            paymentService.cancelPayment(paymentId);
            log.info("支付取消成功: ID={}", paymentId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("取消支付失败: ID={}", paymentId, e);
            return Result.error("PAYMENT_CANCEL_ERROR", "取消支付失败: " + e.getMessage());
        }
    }

    // =================== 支付状态管理 ===================

    @Override
    @CacheInvalidate(name = PaymentCacheConstant.PAYMENT_DETAIL_CACHE)
    @CacheInvalidate(name = PaymentCacheConstant.THIRD_PARTY_PAYMENT_CACHE)
    public Result<PaymentResponse> syncPaymentStatus(String paymentNo) {
        try {
            log.info("同步支付状态: 支付单号={}", paymentNo);
            Payment payment = paymentService.syncPaymentStatus(paymentNo);
            if (payment == null) {
                log.warn("支付记录不存在: 支付单号={}", paymentNo);
                return Result.error("PAYMENT_NOT_FOUND", "支付记录不存在");
            }
            
            PaymentResponse response = convertToResponse(payment);
            log.info("支付状态同步成功: 支付单号={}, 状态={}", paymentNo, response.getStatus());
            return Result.success(response);
        } catch (Exception e) {
            log.error("同步支付状态失败: 支付单号={}", paymentNo, e);
            return Result.error("PAYMENT_SYNC_ERROR", "同步支付状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = PaymentCacheConstant.USER_PAYMENT_CACHE, key = "'user:payments:' + #userId + ':' + #limit",
            expire = PaymentCacheConstant.USER_PAYMENT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<PaymentResponse>> getUserPayments(Long userId, Integer limit) {
        try {
            log.debug("获取用户支付记录: 用户={}, 限制数量={}", userId, limit);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取用户支付记录: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            List<Payment> payments = paymentService.getUserPayments(userId, limit);
            
            if (CollectionUtils.isEmpty(payments)) {
                log.debug("用户暂无支付记录: 用户={}({})", userId, userResult.getData().getNickname());
                return Result.success(List.of());
            }
            
            List<PaymentResponse> responses = payments.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            log.debug("用户支付记录查询成功: 用户={}({}), 记录数={}", 
                    userId, userResult.getData().getNickname(), responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户支付记录失败: 用户={}", userId, e);
            return Result.error("PAYMENT_QUERY_ERROR", "获取用户支付记录失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = PaymentCacheConstant.ORDER_PAYMENT_STATUS_CACHE, key = PaymentCacheConstant.ORDER_PAYMENT_STATUS_KEY,
            expire = PaymentCacheConstant.ORDER_PAYMENT_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<PaymentResponse>> getPaymentsByOrderId(Long orderId) {
        try {
            log.debug("根据订单ID查询支付记录: 订单ID={}", orderId);
            List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
            
            if (CollectionUtils.isEmpty(payments)) {
                log.debug("订单暂无支付记录: 订单ID={}", orderId);
                return Result.success(List.of());
            }
            
            List<PaymentResponse> responses = payments.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            log.debug("订单支付记录查询成功: 订单ID={}, 记录数={}", orderId, responses.size());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据订单ID查询支付记录失败: 订单ID={}", orderId, e);
            return Result.error("PAYMENT_QUERY_ERROR", "根据订单ID查询支付记录失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = PaymentCacheConstant.THIRD_PARTY_PAYMENT_CACHE, key = PaymentCacheConstant.THIRD_PARTY_PAYMENT_KEY,
            expire = PaymentCacheConstant.THIRD_PARTY_PAYMENT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Boolean> verifyPaymentStatus(String paymentNo) {
        try {
            log.debug("验证支付状态: 支付单号={}", paymentNo);
            boolean isSuccess = paymentService.verifyPaymentStatus(paymentNo);
            log.debug("支付状态验证完成: 支付单号={}, 结果={}", paymentNo, isSuccess);
            return Result.success(isSuccess);
        } catch (Exception e) {
            log.error("验证支付状态失败: 支付单号={}", paymentNo, e);
            return Result.error("PAYMENT_VERIFY_ERROR", "验证支付状态失败: " + e.getMessage());
        }
    }

    // =================== 支付统计功能 ===================

    @Override
    @Cached(name = PaymentCacheConstant.PAYMENT_STATISTICS_CACHE, key = PaymentCacheConstant.USER_PAYMENT_STATS_KEY,
            expire = PaymentCacheConstant.PAYMENT_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PaymentResponse> getPaymentStatistics(Long userId) {
        try {
            log.debug("获取用户支付统计: 用户={}", userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取用户支付统计: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            Payment statistics = paymentService.getUserPaymentStatistics(userId);
            PaymentResponse response = convertToResponse(statistics);
            log.debug("用户支付统计查询成功: 用户={}({})", userId, userResult.getData().getNickname());
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取支付统计信息失败: 用户={}", userId, e);
            return Result.error("PAYMENT_STATISTICS_ERROR", "获取支付统计信息失败: " + e.getMessage());
        }
    }

    // =================== 数据转换工具方法 ===================

    /**
     * 转换为支付响应对象
     * 对齐search模块数据转换风格
     */
    private PaymentResponse convertToResponse(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        PaymentResponse response = new PaymentResponse();
        BeanUtils.copyProperties(payment, response);
        
        // 设置状态描述
        response.setStatusDesc(getStatusDesc(payment.getStatus()));
        
        // 设置是否可以取消
        response.setCancellable(paymentService.canCancelPayment(payment.getId()));
        
        return response;
    }

    /**
     * 转换为分页响应对象
     * 对齐search模块分页转换风格
     */
    private PageResponse<PaymentResponse> convertToPageResponse(IPage<Payment> page, List<PaymentResponse> responses) {
        PageResponse<PaymentResponse> result = new PageResponse<>();
        result.setDatas(responses);
        result.setTotal((int) page.getTotal());
        result.setCurrentPage((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        return result;
    }

    /**
     * 获取状态描述
     * 对齐search模块描述风格
     */
    private String getStatusDesc(String status) {
        if (status == null) {
            return "未知状态";
        }
        
        return switch (status.toLowerCase()) {
            case "pending" -> "待支付";
            case "success" -> "支付成功";
            case "failed" -> "支付失败";
            case "cancelled" -> "已取消";
            case "refunding" -> "退款中";
            case "refunded" -> "已退款";
            default -> "未知状态";
        };
    }


} 