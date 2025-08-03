package com.gig.collide.api.task.constant;

/**
 * 任务状态常量 - 签到专用版
 * 对应 t_task_template 表的 status 字段
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
public final class TaskStatusConstant {

    private TaskStatusConstant() {
        // 私有构造函数，防止实例化
    }

    /** 启用状态 */
    public static final Integer ENABLED = 1;

    /** 禁用状态 */
    public static final Integer DISABLED = 0;

    /**
     * 获取状态描述
     */
    public static String getStatusDesc(Integer status) {
        if (ENABLED.equals(status)) {
            return "启用";
        } else if (DISABLED.equals(status)) {
            return "禁用";
        }
        return "未知状态";
    }

    /**
     * 验证状态是否有效
     */
    public static boolean isValidStatus(Integer status) {
        return ENABLED.equals(status) || DISABLED.equals(status);
    }

    /**
     * 检查任务是否启用
     */
    public static boolean isEnabled(Integer status) {
        return ENABLED.equals(status);
    }
}