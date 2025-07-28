package com.gig.collide.api.payment.constant;

/**
 * 支付状态枚举
 * 与payment模块保持一致
 * 
 * @author Collide Team  
 * @version 2.0
 * @since 2024-01-01
 */
public enum PaymentStatusEnum {

    /**
     * 待支付
     */
    PENDING("待支付"),
    
    /**
     * 支付成功
     */
    SUCCESS("支付成功"),
    
    /**
     * 支付失败
     */
    FAILED("支付失败"),
    
    /**
     * 已取消
     */
    CANCELLED("已取消"),
    
    /**
     * 退款中
     */
    REFUNDING("退款中"),
    
    /**
     * 已退款
     */
    REFUNDED("已退款");

    private final String description;

    PaymentStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为最终状态
     */
    public boolean isFinalStatus() {
        return this == SUCCESS || this == FAILED || this == CANCELLED || this == REFUNDED;
    }

    /**
     * 检查是否为成功状态
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * 检查是否为失败状态
     */
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }

    /**
     * 检查是否需要退款处理
     */
    public boolean needsRefund() {
        return this == REFUNDING;
    }
} 