package com.gig.collide.api.user.constant;

/**
 * 用户角色状态常量定义
 * 与数据库t_user_role表的status字段对应
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
public final class RoleStatusConstant {

    /**
     * 角色生效状态 - 角色正常生效中
     */
    public static final String ACTIVE = "active";

    /**
     * 角色撤销状态 - 角色已被撤销
     */
    public static final String REVOKED = "revoked";

    /**
     * 角色过期状态 - 角色已过期
     */
    public static final String EXPIRED = "expired";

    /**
     * 获取状态描述
     * 
     * @param status 状态值
     * @return 状态描述
     */
    public static String getStatusDesc(String status) {
        if (status == null) return "未知";
        switch (status.toLowerCase()) {
            case "active": return "生效中";
            case "revoked": return "已撤销";
            case "expired": return "已过期";
            default: return "未知";
        }
    }

    /**
     * 检查是否为生效状态
     * 
     * @param status 状态值
     * @return true-角色生效，false-角色无效
     */
    public static boolean isActiveStatus(String status) {
        return ACTIVE.equals(status);
    }

    /**
     * 检查是否为已撤销状态
     * 
     * @param status 状态值
     * @return true-已撤销，false-非撤销状态
     */
    public static boolean isRevokedStatus(String status) {
        return REVOKED.equals(status);
    }

    /**
     * 检查是否为已过期状态
     * 
     * @param status 状态值
     * @return true-已过期，false-非过期状态
     */
    public static boolean isExpiredStatus(String status) {
        return EXPIRED.equals(status);
    }

    // 私有构造函数，防止实例化
    private RoleStatusConstant() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}