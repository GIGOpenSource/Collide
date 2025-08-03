package com.gig.collide.api.user.constant;

/**
 * 用户钱包状态常量定义
 * 与数据库t_user_wallet表的status字段对应
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
public final class WalletStatusConstant {

    /**
     * 钱包正常状态 - 可以正常使用
     */
    public static final String ACTIVE = "active";

    /**
     * 钱包冻结状态 - 钱包被冻结，不能使用
     */
    public static final String FROZEN = "frozen";

    /**
     * 获取状态描述
     * 
     * @param status 状态值
     * @return 状态描述
     */
    public static String getStatusDesc(String status) {
        if (status == null) return "未知";
        switch (status.toLowerCase()) {
            case "active": return "正常";
            case "frozen": return "冻结";
            default: return "未知";
        }
    }

    /**
     * 检查是否为正常状态
     * 
     * @param status 状态值
     * @return true-钱包正常，false-钱包异常
     */
    public static boolean isActiveStatus(String status) {
        return ACTIVE.equals(status);
    }

    /**
     * 检查是否为冻结状态
     * 
     * @param status 状态值
     * @return true-已冻结，false-非冻结状态
     */
    public static boolean isFrozenStatus(String status) {
        return FROZEN.equals(status);
    }

    // 私有构造函数，防止实例化
    private WalletStatusConstant() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}