package com.gig.collide.payment.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.payment.domain.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付服务接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface PaymentService {

    /**
     * 创建支付订单
     */
    Payment createPayment(Payment payment);

    /**
     * 根据ID查询支付详情
     */
    Payment getPaymentById(Long paymentId);

    /**
     * 根据支付单号查询
     */
    Payment getPaymentByNo(String paymentNo);

    /**
     * 分页查询支付记录
     */
    IPage<Payment> queryPayments(Long userId, String payMethod, String status, 
                                String paymentNo, String orderNo, Long orderId,
                                String thirdPartyNo, BigDecimal minAmount, BigDecimal maxAmount,
                                LocalDateTime startTime, LocalDateTime endTime,
                                int pageNum, int pageSize, String sortBy, String sortDirection);

    /**
     * 处理支付回调
     */
    void handlePaymentCallback(String paymentNo, String status, String thirdPartyNo,
                              LocalDateTime payTime, LocalDateTime notifyTime);

    /**
     * 取消支付
     */
    void cancelPayment(Long paymentId);

    /**
     * 同步支付状态
     */
    Payment syncPaymentStatus(String paymentNo);

    /**
     * 获取用户支付记录
     */
    List<Payment> getUserPayments(Long userId, Integer limit);

    /**
     * 根据订单ID查询支付记录
     */
    List<Payment> getPaymentsByOrderId(Long orderId);

    /**
     * 验证支付状态
     */
    boolean verifyPaymentStatus(String paymentNo);

    /**
     * 生成支付单号
     */
    String generatePaymentNo();

    /**
     * 检查支付是否可以取消
     */
    boolean canCancelPayment(Long paymentId);

    /**
     * 获取用户支付统计
     */
    Payment getUserPaymentStatistics(Long userId);

    /**
     * 更新过期支付订单
     */
    void updateExpiredPayments();
} 