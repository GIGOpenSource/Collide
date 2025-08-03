package com.gig.collide.api.task.constant;

/**
 * 奖励来源常量
 * 与优化后的 task-simple.sql 保持一致
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public class RewardSourceConstant {

    /**
     * 任务奖励
     */
    public static final Integer TASK = 1;

    /**
     * 事件奖励
     */
    public static final Integer EVENT = 2;

    /**
     * 系统奖励
     */
    public static final Integer SYSTEM = 3;

    /**
     * 管理员奖励
     */
    public static final Integer ADMIN = 4;

    /**
     * 获取奖励来源名称
     */
    public static String getSourceName(Integer source) {
        if (source == null) return "未知";
        switch (source) {
            case 1: return "任务奖励";
            case 2: return "事件奖励";
            case 3: return "系统奖励";
            case 4: return "管理员奖励";
            default: return "未知";
        }
    }

    /**
     * 获取奖励来源英文名称
     */
    public static String getSourceKey(Integer source) {
        if (source == null) return "unknown";
        switch (source) {
            case 1: return "task";
            case 2: return "event";
            case 3: return "system";
            case 4: return "admin";
            default: return "unknown";
        }
    }

    /**
     * 验证奖励来源是否有效
     */
    public static boolean isValidSource(Integer source) {
        return source != null && source >= 1 && source <= 4;
    }

    /**
     * 判断是否为任务奖励
     */
    public static boolean isTaskReward(Integer source) {
        return TASK.equals(source);
    }

    /**
     * 判断是否为事件奖励
     */
    public static boolean isEventReward(Integer source) {
        return EVENT.equals(source);
    }

    /**
     * 判断是否为系统奖励
     */
    public static boolean isSystemReward(Integer source) {
        return SYSTEM.equals(source);
    }

    /**
     * 判断是否为管理员奖励
     */
    public static boolean isAdminReward(Integer source) {
        return ADMIN.equals(source);
    }
}