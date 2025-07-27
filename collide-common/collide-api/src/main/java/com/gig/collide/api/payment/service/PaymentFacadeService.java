package com.gig.collide.api.payment.service;

import com.gig.collide.api.payment.request.CreatePaymentRequest;
import com.gig.collide.api.payment.request.PaymentCallbackRequest;
import com.gig.collide.base.response.SingleResponse;

import java.util.Map;

/**
 * 支付服务 Facade 接口（去连表设计 v2.0.0）
 * 
 * @author Collide
 * @since 2.0.0
 */
public interface PaymentFacadeService {

    // =============================================
    // 去连表化支付接口 v2.0.0
    // =============================================
    
    /**
     * 创建支付订单（去连表设计）
     * 传递完整的用户和订单信息，避免联表查询
     * 
     * @param createRequest 创建支付请求（包含完整的用户和订单信息）
     * @return 支付创建结果
     */
    SingleResponse<Map<String, Object>> createPayment(CreatePaymentRequest createRequest);

    /**
     * 处理支付回调（去连表设计）
     * 传递完整的支付和用户信息，避免联表查询
     * 
     * @param callbackRequest 回调请求（包含完整的支付和用户信息）
     * @return 回调处理结果
     */
    SingleResponse<Boolean> processPaymentCallback(PaymentCallbackRequest callbackRequest);

    /**
     * 获取支付状态（通过完整信息查询）
     * 
     * @param orderNo 订单号
     * @param userId 用户ID
     * @param userName 用户名称（用于验证和日志记录）
     * @return 支付状态信息
     */
    SingleResponse<Map<String, Object>> getPaymentStatus(String orderNo, Long userId, String userName);
    
    /**
     * 查询支付记录（通过用户信息查询）
     * 
     * @param userId 用户ID
     * @param userName 用户名称（用于验证）
     * @param pageNo 页码
     * @param pageSize 页面大小
     * @return 支付记录列表
     */
    SingleResponse<Map<String, Object>> queryPaymentRecords(Long userId, String userName, 
                                                           Integer pageNo, Integer pageSize);
    
    /**
     * 取消支付（通过完整信息操作）
     * 
     * @param orderNo 订单号
     * @param userId 用户ID
     * @param userName 用户名称
     * @param cancelReason 取消原因
     * @return 取消结果
     */
    SingleResponse<Boolean> cancelPayment(String orderNo, Long userId, String userName, String cancelReason);
    
    /**
     * 查询商户支付统计（通过商户信息查询）
     * 
     * @param merchantId 商户ID
     * @param merchantName 商户名称
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 商户支付统计
     */
    SingleResponse<Map<String, Object>> queryMerchantPaymentStats(Long merchantId, String merchantName, 
                                                                String startDate, String endDate);
    
    /**
     * 支付配置管理（商户维度）
     * 
     * @param merchantId 商户ID
     * @param merchantName 商户名称
     * @param configType 配置类型
     * @param configData 配置数据
     * @return 配置结果
     */
    SingleResponse<Boolean> updatePaymentConfig(Long merchantId, String merchantName, 
                                              String configType, Map<String, Object> configData);
    
    /**
     * 风险检查（基于完整用户和订单信息）
     * 
     * @param userId 用户ID
     * @param userName 用户名称
     * @param orderNo 订单号
     * @param payAmount 支付金额
     * @param payType 支付方式
     * @return 风险检查结果
     */
    SingleResponse<Map<String, Object>> checkPaymentRisk(Long userId, String userName, 
                                                        String orderNo, String payAmount, String payType);
    
    /**
     * 获取支付指标（运营统计）
     * 
     * @param metricType 指标类型
     * @param timeRange 时间范围
     * @param payType 支付方式（可选）
     * @return 支付指标数据
     */
    SingleResponse<Map<String, Object>> getPaymentMetrics(String metricType, String timeRange, String payType);
} 