package com.gig.collide.api.permission.constant;

/**
 * 权限类型常量
 * 定义用户对内容的观看权限类型
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class PermissionType {
    
    /**
     * 普通用户付费观看权限
     * - 普通用户购买金币或订阅后可观看付费内容
     */
    public static final String NORMAL_USER_PAID = "NORMAL_USER_PAID";
    
    /**
     * 普通用户免费观看权限
     * - 普通用户可观看免费内容
     */
    public static final String NORMAL_USER_FREE = "NORMAL_USER_FREE";
    
    /**
     * VIP用户付费观看权限
     * - VIP用户可观看所有付费内容（可能有折扣或优惠）
     */
    public static final String VIP_USER_PAID = "VIP_USER_PAID";
    
    /**
     * VIP用户免费观看权限
     * - VIP用户可免费观看部分或全部内容
     */
    public static final String VIP_USER_FREE = "VIP_USER_FREE";
}

/**
 * 用户类型枚举
 */
enum UserType {
    /**
     * 普通用户
     */
    NORMAL("NORMAL", "普通用户"),
    
    /**
     * VIP用户
     */
    VIP("VIP", "VIP用户");
    
    private final String code;
    private final String description;
    
    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getDescription() { return description; }
}

/**
 * 内容类型枚举
 */
enum ContentType {
    /**
     * 免费内容
     */
    FREE("FREE", "免费内容"),
    
    /**
     * 付费内容
     */
    PAID("PAID", "付费内容");
    
    private final String code;
    private final String description;
    
    ContentType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getDescription() { return description; }
} 