package com.gig.collide.api.goods.constant;

/**
 * 商品状态枚举
 * 对应数据库goods表的status字段
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum GoodsStatus {
    
    /**
     * 草稿状态
     * 商品还在编辑中，未发布
     */
    DRAFT("DRAFT", "草稿", "商品还在编辑中，未发布"),
    
    /**
     * 销售中
     * 商品正在销售，用户可以购买
     */
    ON_SALE("ON_SALE", "销售中", "商品正在销售，用户可以购买"),
    
    /**
     * 下架
     * 商品已下架，用户不能购买
     */
    OFF_SALE("OFF_SALE", "下架", "商品已下架，用户不能购买"),
    
    /**
     * 售罄
     * 商品库存为0，暂时不能购买
     */
    SOLD_OUT("SOLD_OUT", "售罄", "商品库存为0，暂时不能购买"),
    
    /**
     * 禁用
     * 商品被管理员禁用，不能销售
     */
    DISABLED("DISABLED", "禁用", "商品被管理员禁用，不能销售");
    
    private final String code;
    private final String name;
    private final String description;
    
    GoodsStatus(String code, String name, String description) {
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
    public static GoodsStatus fromCode(String code) {
        for (GoodsStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的商品状态: " + code);
    }
    
    /**
     * 是否为可购买状态
     */
    public boolean isPurchasable() {
        return this == ON_SALE;
    }
    
    /**
     * 是否为可见状态（用户可以看到商品信息）
     */
    public boolean isVisible() {
        return this == ON_SALE || this == SOLD_OUT;
    }
    
    /**
     * 是否为管理状态（需要管理员权限才能查看）
     */
    public boolean isManagementStatus() {
        return this == DRAFT || this == OFF_SALE || this == DISABLED;
    }
} 