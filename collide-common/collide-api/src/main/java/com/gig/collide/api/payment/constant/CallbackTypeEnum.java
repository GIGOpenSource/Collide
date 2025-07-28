package com.gig.collide.api.payment.constant;

/**
 * 回调类型枚举
 * 与payment模块保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum CallbackTypeEnum {

    /**
     * 支付回调
     */
    PAYMENT("支付回调"),
    
    /**
     * 退款回调
     */
    REFUND("退款回调"),
    
    /**
     * 取消回调
     */
    CANCEL("取消回调");

    private final String description;

    CallbackTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为支付相关回调
     */
    public boolean isPaymentRelated() {
        return this == PAYMENT;
    }

    /**
     * 检查是否为退款相关回调
     */
    public boolean isRefundRelated() {
        return this == REFUND;
    }
} 