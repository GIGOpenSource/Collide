package com.gig.collide.api.user.constant;

/**
 * 用户角色枚举 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public enum UserRole {
    
    USER("user", "普通用户"),
    BLOGGER("blogger", "博主"),
    ADMIN("admin", "管理员"),
    VIP("vip", "VIP用户");

    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
