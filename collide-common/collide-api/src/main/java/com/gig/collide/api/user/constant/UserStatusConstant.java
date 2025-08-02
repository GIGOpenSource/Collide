package com.gig.collide.api.user.constant;

/**
 * 用户状态常量定义
 * 与数据库t_user表的status字段(TINYINT)对应
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
public final class UserStatusConstant {

    /**
     * 正常状态 - 用户可以正常使用所有功能
     */
    public static final Integer ACTIVE = 1;

    /**
     * 未激活状态 - 用户注册后未完成激活流程
     */
    public static final Integer INACTIVE = 2;

    /**
     * 暂停状态 - 用户被临时暂停使用，可恢复
     */
    public static final Integer SUSPENDED = 3;

    /**
     * 封禁状态 - 用户被永久或长期封禁
     */
    public static final Integer BANNED = 4;

    /**
     * 状态值转字符串（用于兼容旧系统）
     */
    public static final String ACTIVE_STR = "active";
    public static final String INACTIVE_STR = "inactive";
    public static final String SUSPENDED_STR = "suspended";
    public static final String BANNED_STR = "banned";

    /**
     * 获取状态描述
     * 
     * @param status 状态值
     * @return 状态描述
     */
    public static String getStatusDesc(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "正常";
            case 2: return "未激活";
            case 3: return "暂停";
            case 4: return "封禁";
            default: return "未知";
        }
    }

    /**
     * 状态值转字符串
     * 
     * @param status 状态值
     * @return 状态字符串
     */
    public static String getStatusString(Integer status) {
        if (status == null) return INACTIVE_STR;
        switch (status) {
            case 1: return ACTIVE_STR;
            case 2: return INACTIVE_STR;
            case 3: return SUSPENDED_STR;
            case 4: return BANNED_STR;
            default: return INACTIVE_STR;
        }
    }

    /**
     * 字符串转状态值
     * 
     * @param statusStr 状态字符串
     * @return 状态值
     */
    public static Integer getStatusValue(String statusStr) {
        if (statusStr == null || statusStr.isEmpty()) return INACTIVE;
        switch (statusStr.toLowerCase()) {
            case "active": return ACTIVE;
            case "inactive": return INACTIVE;
            case "suspended": return SUSPENDED;
            case "banned": return BANNED;
            default: return INACTIVE;
        }
    }

    /**
     * 检查是否为有效状态（可以使用系统功能）
     * 
     * @param status 状态值
     * @return true-有效状态，false-无效状态
     */
    public static boolean isValidStatus(Integer status) {
        return ACTIVE.equals(status);
    }

    /**
     * 检查字符串状态是否有效
     * 
     * @param statusStr 状态字符串
     * @return true-有效状态，false-无效状态
     */
    public static boolean isValidStatusString(String statusStr) {
        return ACTIVE_STR.equals(statusStr);
    }

    /**
     * 检查是否为可恢复状态（可以通过管理操作恢复）
     * 
     * @param status 状态值
     * @return true-可恢复，false-不可恢复
     */
    public static boolean isRecoverableStatus(Integer status) {
        return INACTIVE.equals(status) || SUSPENDED.equals(status);
    }

    // 私有构造函数，防止实例化
    private UserStatusConstant() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}