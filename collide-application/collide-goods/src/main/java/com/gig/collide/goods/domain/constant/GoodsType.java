package com.gig.collide.goods.domain.constant;

/**
 * 商品类型枚举
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public enum GoodsType {
    
    /**
     * 金币类商品
     * - 一次性购买
     * - 立即到账
     * - 可用于内容付费消费
     */
    COIN("COIN", "金币类商品"),
    
    /**
     * 订阅类商品
     * - 周期性订阅
     * - 按时间周期收费
     * - 享受VIP特权
     */
    SUBSCRIPTION("SUBSCRIPTION", "订阅类商品");
    
    private final String code;
    private final String description;
    
    GoodsType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static GoodsType fromCode(String code) {
        for (GoodsType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的商品类型: " + code);
    }
} 