package com.gig.collide.api.payment.constant;

/**
 * 支付状态常量
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class PaymentStatus {
    
    /**
     * 支付创建
     */
    public static final String CREATED = "CREATED";
    
    /**
     * 支付处理中
     */
    public static final String PROCESSING = "PROCESSING";
    
    /**
     * 支付成功
     */
    public static final String SUCCESS = "SUCCESS";
    
    /**
     * 支付失败
     */
    public static final String FAILED = "FAILED";
    
    /**
     * 支付取消
     */
    public static final String CANCELLED = "CANCELLED";
    
    /**
     * 支付超时
     */
    public static final String TIMEOUT = "TIMEOUT";
} 