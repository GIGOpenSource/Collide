package com.gig.collide.api.user.constant;

/**
 * 用户拉黑状态常量定义
 * 与数据库t_user_block表的status字段对应
 * 
 * @author GIG Team
 * @version 2.0.0
 * @since 2024-01-01
 */
public final class BlockStatusConstant {

    /**
     * 拉黑状态 - 拉黑关系生效中
     */
    public static final String ACTIVE = "active";

    /**
     * 取消拉黑状态 - 拉黑关系已取消
     */
    public static final String CANCELLED = "cancelled";

    /**
     * 获取状态描述
     * 
     * @param status 状态值
     * @return 状态描述
     */
    public static String getStatusDesc(String status) {
        if (status == null) return "未知";
        switch (status.toLowerCase()) {
            case "active": return "拉黑中";
            case "cancelled": return "已取消";
            default: return "未知";
        }
    }

    /**
     * 检查是否为生效状态
     * 
     * @param status 状态值
     * @return true-拉黑生效，false-拉黑无效
     */
    public static boolean isActiveStatus(String status) {
        return ACTIVE.equals(status);
    }

    // 私有构造函数，防止实例化
    private BlockStatusConstant() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}