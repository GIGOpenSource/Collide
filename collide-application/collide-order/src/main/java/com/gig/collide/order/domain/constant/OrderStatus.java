package com.gig.collide.order.domain.constant;

/**
 * 订单状态枚举
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public enum OrderStatus {
    
    /**
     * 创建订单状态
     */
    CREATE("CREATE", "创建订单"),
    
    /**
     * 订单未付款状态
     */
    UNPAID("UNPAID", "未付款"),
    
    /**
     * 订单已支付状态
     */
    PAID("PAID", "已支付");
    
    private final String code;
    private final String description;
    
    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态: " + code);
    }
} 