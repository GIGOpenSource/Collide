package com.gig.collide.task.domain.constant;

/**
 * 任务类型常量
 * 与优化后的 task-simple.sql 保持一致
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public class TaskTypeConstant {

    /**
     * 每日任务
     */
    public static final Integer DAILY = 1;

    /**
     * 周常任务
     */
    public static final Integer WEEKLY = 2;

    /**
     * 月度任务
     */
    public static final Integer MONTHLY = 3;

    /**
     * 成就任务
     */
    public static final Integer ACHIEVEMENT = 4;

    /**
     * 获取任务类型名称
     */
    public static String getTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "每日任务";
            case 2: return "周常任务";
            case 3: return "月度任务";
            case 4: return "成就任务";
            default: return "未知";
        }
    }

    /**
     * 获取任务类型英文名称
     */
    public static String getTypeKey(Integer type) {
        if (type == null) return "unknown";
        switch (type) {
            case 1: return "daily";
            case 2: return "weekly";
            case 3: return "monthly";
            case 4: return "achievement";
            default: return "unknown";
        }
    }

    /**
     * 验证任务类型是否有效
     */
    public static boolean isValidType(Integer type) {
        return type != null && type >= 1 && type <= 4;
    }

    /**
     * 判断是否为每日任务
     */
    public static boolean isDailyTask(Integer type) {
        return DAILY.equals(type);
    }

    /**
     * 判断是否为周常任务
     */
    public static boolean isWeeklyTask(Integer type) {
        return WEEKLY.equals(type);
    }

    /**
     * 判断是否为月度任务
     */
    public static boolean isMonthlyTask(Integer type) {
        return MONTHLY.equals(type);
    }

    /**
     * 判断是否为成就任务
     */
    public static boolean isAchievementTask(Integer type) {
        return ACHIEVEMENT.equals(type);
    }
}