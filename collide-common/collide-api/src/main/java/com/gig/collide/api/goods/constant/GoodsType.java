package com.gig.collide.api.goods.constant;

/**
 * 商品类型枚举
 * 对应数据库goods表的type字段
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum GoodsType {
    
    /**
     * 金币类商品
     * 用户购买后可获得对应数量的金币
     */
    COIN("COIN", "金币类", "用户购买后可获得对应数量的金币"),
    
    /**
     * 订阅类商品
     * 用户购买后可获得对应天数的会员权限
     */
    SUBSCRIPTION("SUBSCRIPTION", "订阅类", "用户购买后可获得对应天数的会员权限");
    
    private final String code;
    private final String name;
    private final String description;
    
    GoodsType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static GoodsType fromCode(String code) {
        for (GoodsType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的商品类型: " + code);
    }
    
    /**
     * 是否为金币类商品
     */
    public boolean isCoinType() {
        return this == COIN;
    }
    
    /**
     * 是否为订阅类商品
     */
    public boolean isSubscriptionType() {
        return this == SUBSCRIPTION;
    }
} 