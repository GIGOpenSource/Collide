package com.gig.collide.api.goods.constant;

/**
 * 商品排序类型枚举
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum GoodsSortType {
    
    /**
     * 按创建时间排序
     */
    CREATE_TIME("CREATE_TIME", "创建时间", "create_time"),
    
    /**
     * 按更新时间排序
     */
    UPDATE_TIME("UPDATE_TIME", "更新时间", "update_time"),
    
    /**
     * 按价格排序
     */
    PRICE("PRICE", "价格", "price"),
    
    /**
     * 按销量排序
     */
    SOLD_COUNT("SOLD_COUNT", "销量", "sold_count"),
    
    /**
     * 按推荐优先级排序
     */
    RECOMMENDED("RECOMMENDED", "推荐", "recommended"),
    
    /**
     * 按热门优先级排序
     */
    HOT("HOT", "热门", "hot");
    
    private final String code;
    private final String name;
    private final String fieldName;
    
    GoodsSortType(String code, String name, String fieldName) {
        this.code = code;
        this.name = name;
        this.fieldName = fieldName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * 根据代码获取枚举
     */
    public static GoodsSortType fromCode(String code) {
        for (GoodsSortType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return CREATE_TIME; // 默认按创建时间排序
    }
} 