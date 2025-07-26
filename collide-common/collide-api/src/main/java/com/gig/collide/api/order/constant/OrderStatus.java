package com.gig.collide.api.order.constant;

/**
 * 订单状态常量
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class OrderStatus {
    
    /**
     * 创建订单状态
     */
    public static final String CREATE = "CREATE";
    
    /**
     * 订单未付款状态
     */
    public static final String UNPAID = "UNPAID";
    
    /**
     * 订单已支付状态
     */
    public static final String PAID = "PAID";
    
    /**
     * 订单已取消状态
     */
    public static final String CANCELLED = "CANCELLED";
    
    /**
     * 订单已退款状态
     */
    public static final String REFUNDED = "REFUNDED";
} 