package com.gig.collide.payment.facade;

import com.gig.collide.api.payment.request.CreatePaymentRequest;
import com.gig.collide.api.payment.request.PaymentCallbackRequest;
import com.gig.collide.api.payment.service.PaymentFacadeService;
import com.gig.collide.base.response.SingleResponse;
import com.gig.collide.payment.domain.service.PaymentDomainService;
import com.gig.collide.payment.infrastructure.entity.PaymentRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务 Facade 实现（去连表设计 v2.0.0）
 * 
 * 提供支付相关的 RPC 服务接口实现
 * 采用去连表化设计，通过传递完整信息避免联表查询
 * 
 * @author Collide
 * @since 2.0.0
 */
@Slf4j
@Service
@DubboService(version = "2.0.0", timeout = 5000)
@RequiredArgsConstructor
public class PaymentFacadeServiceImpl implements PaymentFacadeService {

    private final PaymentDomainService paymentDomainService;

    @Override
    public SingleResponse<Map<String, Object>> createPayment(CreatePaymentRequest createRequest) {
        try {
            log.info("RPC调用：创建支付订单（v2.0），订单号：{}，用户：{}，支付金额：{}", 
                createRequest.getOrderNo(), createRequest.getUserName(), createRequest.getPayAmount());
            
            // 使用域服务创建支付
            PaymentRecord paymentRecord = paymentDomainService.createPaymentV2(createRequest);
            
            // 转换为响应格式
            Map<String, Object> result = convertPaymentRecordToMap(paymentRecord);
            
            log.info("RPC调用：创建支付订单成功，订单号：{}", createRequest.getOrderNo());
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：创建支付订单失败，订单号：{}", createRequest.getOrderNo(), e);
            return SingleResponse.fail("CREATE_PAYMENT_ERROR", "创建支付失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Boolean> processPaymentCallback(PaymentCallbackRequest callbackRequest) {
        try {
            log.info("RPC调用：处理支付回调（v2.0），订单号：{}，用户：{}，回调类型：{}", 
                callbackRequest.getOrderNo(), callbackRequest.getUserName(), callbackRequest.getCallbackType());
            
            // 使用域服务处理回调
            boolean result = paymentDomainService.processPaymentCallbackV2(callbackRequest);
            
            log.info("RPC调用：处理支付回调成功，订单号：{}，结果：{}", callbackRequest.getOrderNo(), result);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：处理支付回调失败，订单号：{}", callbackRequest.getOrderNo(), e);
            return SingleResponse.fail("CALLBACK_PROCESS_ERROR", "回调处理失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> getPaymentStatus(String orderNo, Long userId, String userName) {
        try {
            log.info("RPC调用：获取支付状态（v2.0），订单号：{}，用户：{}", orderNo, userName);
            
            // 使用域服务查询支付状态
            Map<String, Object> result = paymentDomainService.getPaymentStatusV2(orderNo, userId, userName);
            
            log.info("RPC调用：获取支付状态成功，订单号：{}", orderNo);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：获取支付状态失败，订单号：{}", orderNo, e);
            return SingleResponse.fail("GET_STATUS_ERROR", "获取支付状态失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> queryPaymentRecords(Long userId, String userName, 
                                                                  Integer pageNo, Integer pageSize) {
        try {
            log.info("RPC调用：查询支付记录（v2.0），用户：{}，页码：{}，大小：{}", userName, pageNo, pageSize);
            
            // 使用域服务查询支付记录
            Map<String, Object> result = paymentDomainService.queryPaymentRecordsV2(userId, userName, pageNo, pageSize);
            
            log.info("RPC调用：查询支付记录成功，用户：{}", userName);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：查询支付记录失败，用户：{}", userName, e);
            return SingleResponse.fail("QUERY_RECORDS_ERROR", "查询支付记录失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Boolean> cancelPayment(String orderNo, Long userId, String userName, String cancelReason) {
        try {
            log.info("RPC调用：取消支付（v2.0），订单号：{}，用户：{}，原因：{}", orderNo, userName, cancelReason);
            
            // 使用域服务取消支付
            boolean result = paymentDomainService.cancelPaymentV2(orderNo, userId, userName, cancelReason);
            
            log.info("RPC调用：取消支付成功，订单号：{}，结果：{}", orderNo, result);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：取消支付失败，订单号：{}", orderNo, e);
            return SingleResponse.fail("CANCEL_PAYMENT_ERROR", "取消支付失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> queryMerchantPaymentStats(Long merchantId, String merchantName, 
                                                                        String startDate, String endDate) {
        try {
            log.info("RPC调用：查询商户支付统计（v2.0），商户：{}，时间：{}-{}", merchantName, startDate, endDate);
            
            // 使用域服务查询商户统计
            Map<String, Object> result = paymentDomainService.queryMerchantPaymentStatsV2(
                merchantId, merchantName, startDate, endDate);
            
            log.info("RPC调用：查询商户支付统计成功，商户：{}", merchantName);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：查询商户支付统计失败，商户：{}", merchantName, e);
            return SingleResponse.fail("QUERY_STATS_ERROR", "查询统计失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Boolean> updatePaymentConfig(Long merchantId, String merchantName, 
                                                      String configType, Map<String, Object> configData) {
        try {
            log.info("RPC调用：更新支付配置（v2.0），商户：{}，配置类型：{}", merchantName, configType);
            
            // 使用域服务更新配置
            boolean result = paymentDomainService.updatePaymentConfigV2(
                merchantId, merchantName, configType, configData);
            
            log.info("RPC调用：更新支付配置成功，商户：{}", merchantName);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：更新支付配置失败，商户：{}", merchantName, e);
            return SingleResponse.fail("UPDATE_CONFIG_ERROR", "更新配置失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> checkPaymentRisk(Long userId, String userName, 
                                                               String orderNo, String payAmount, String payType) {
        try {
            log.info("RPC调用：风险检查（v2.0），用户：{}，订单：{}，金额：{}", userName, orderNo, payAmount);
            
            // 使用域服务进行风险检查
            Map<String, Object> result = paymentDomainService.checkPaymentRiskV2(
                userId, userName, orderNo, payAmount, payType);
            
            log.info("RPC调用：风险检查成功，用户：{}", userName);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：风险检查失败，用户：{}", userName, e);
            return SingleResponse.fail("RISK_CHECK_ERROR", "风险检查失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> getPaymentMetrics(String metricType, String timeRange, String payType) {
        try {
            log.info("RPC调用：获取支付指标（v2.0），类型：{}，时间：{}，支付方式：{}", metricType, timeRange, payType);
            
            // 使用域服务获取支付指标
            Map<String, Object> result = paymentDomainService.getPaymentMetricsV2(metricType, timeRange, payType);
            
            log.info("RPC调用：获取支付指标成功，类型：{}", metricType);
            return SingleResponse.of(result);
            
        } catch (Exception e) {
            log.error("RPC调用：获取支付指标失败，类型：{}", metricType, e);
            return SingleResponse.fail("GET_METRICS_ERROR", "获取指标失败：" + e.getMessage());
        }
    }

    // ===== 私有工具方法 =====

    /**
     * 转换PaymentRecord为Map格式
     */
    private Map<String, Object> convertPaymentRecordToMap(PaymentRecord paymentRecord) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", paymentRecord.getId());
        result.put("orderNo", paymentRecord.getOrderNo());
        result.put("internalTransactionNo", paymentRecord.getInternalTransactionNo());
        result.put("transactionNo", paymentRecord.getTransactionNo());
        result.put("userId", paymentRecord.getUserId());
        result.put("userName", paymentRecord.getUserName());
        result.put("payAmount", paymentRecord.getPayAmount());
        result.put("actualPayAmount", paymentRecord.getActualPayAmount());
        result.put("payType", paymentRecord.getPayType());
        result.put("payStatus", paymentRecord.getPayStatus());
        result.put("payTime", paymentRecord.getPayTime());
        result.put("completeTime", paymentRecord.getCompleteTime());
        result.put("expireTime", paymentRecord.getExpireTime());
        result.put("notifyUrl", paymentRecord.getNotifyUrl());
        result.put("message", getStatusMessage(paymentRecord.getPayStatus()));
        return result;
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
}