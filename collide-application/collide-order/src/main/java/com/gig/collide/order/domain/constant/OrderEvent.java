package com.gig.collide.order.domain.constant;

/**
 * 订单事件枚举
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public enum OrderEvent {
    
    /**
     * 确认订单
     */
    CONFIRM("CONFIRM", "确认订单"),
    
    /**
     * 支付订单
     */
    PAY("PAY", "支付订单"),
    
    /**
     * 取消订单
     */
    CANCEL("CANCEL", "取消订单");
    
    private final String code;
    private final String description;
    
    OrderEvent(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OrderEvent fromCode(String code) {
        for (OrderEvent event : values()) {
            if (event.code.equals(code)) {
                return event;
            }
        }
        throw new IllegalArgumentException("未知的订单事件: " + code);
    }
} 