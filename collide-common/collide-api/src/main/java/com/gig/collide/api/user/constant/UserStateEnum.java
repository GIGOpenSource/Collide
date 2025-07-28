package com.gig.collide.api.user.constant;

/**
 * 用户状态枚举 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public enum UserStateEnum {
    
    ACTIVE("active", "正常"),
    INACTIVE("inactive", "未激活"),
    SUSPENDED("suspended", "暂停"),
    BANNED("banned", "封禁");

    private final String code;
    private final String desc;

    UserStateEnum(String code, String desc) {
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
