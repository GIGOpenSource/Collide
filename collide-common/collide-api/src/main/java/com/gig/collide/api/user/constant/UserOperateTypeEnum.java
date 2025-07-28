package com.gig.collide.api.user.constant;

/**
 * 用户操作类型枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum UserOperateTypeEnum {

    /**
     * 用户注册
     */
    REGISTER("用户注册"),

    /**
     * 用户登录
     */
    LOGIN("用户登录"),

    /**
     * 用户登出
     */
    LOGOUT("用户登出"),

    /**
     * 用户激活
     */
    ACTIVATE("用户激活"),

    /**
     * 修改个人信息
     */
    MODIFY_PROFILE("修改个人信息"),

    /**
     * 修改密码
     */
    CHANGE_PASSWORD("修改密码"),

    /**
     * 申请博主认证
     */
    APPLY_BLOGGER("申请博主认证"),

    /**
     * 博主认证审批
     */
    APPROVE_BLOGGER("博主认证审批"),

    /**
     * 升级VIP
     */
    UPGRADE_VIP("升级VIP"),

    /**
     * 邀请用户
     */
    INVITE_USER("邀请用户"),

    /**
     * 冻结用户
     */
    FREEZE("冻结用户"),

    /**
     * 解冻用户
     */
    UNFREEZE("解冻用户"),

    /**
     * 封禁用户
     */
    BAN("封禁用户"),

    /**
     * 解封用户
     */
    UNBAN("解封用户");

    private final String description;

    UserOperateTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否为管理员操作
     */
    public boolean isAdminOperation() {
        return this == APPROVE_BLOGGER || this == FREEZE || this == UNFREEZE 
                || this == BAN || this == UNBAN;
    }

    /**
     * 检查是否为用户自主操作
     */
    public boolean isUserSelfOperation() {
        return this == REGISTER || this == LOGIN || this == LOGOUT 
                || this == MODIFY_PROFILE || this == CHANGE_PASSWORD 
                || this == APPLY_BLOGGER || this == UPGRADE_VIP || this == INVITE_USER;
    }
}
