package com.gig.collide.task.domain.constant;

/**
 * 任务分类常量
 * 与优化后的 task-simple.sql 保持一致
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public class TaskCategoryConstant {

    /**
     * 登录类任务
     */
    public static final Integer LOGIN = 1;

    /**
     * 内容类任务
     */
    public static final Integer CONTENT = 2;

    /**
     * 社交类任务
     */
    public static final Integer SOCIAL = 3;

    /**
     * 消费类任务
     */
    public static final Integer CONSUME = 4;

    /**
     * 邀请类任务
     */
    public static final Integer INVITE = 5;

    /**
     * 获取任务分类名称
     */
    public static String getCategoryName(Integer category) {
        if (category == null) return "未知";
        switch (category) {
            case 1: return "登录类";
            case 2: return "内容类";
            case 3: return "社交类";
            case 4: return "消费类";
            case 5: return "邀请类";
            default: return "未知";
        }
    }

    /**
     * 获取任务分类英文名称
     */
    public static String getCategoryKey(Integer category) {
        if (category == null) return "unknown";
        switch (category) {
            case 1: return "login";
            case 2: return "content";
            case 3: return "social";
            case 4: return "consume";
            case 5: return "invite";
            default: return "unknown";
        }
    }

    /**
     * 验证任务分类是否有效
     */
    public static boolean isValidCategory(Integer category) {
        return category != null && category >= 1 && category <= 5;
    }

    /**
     * 判断是否为登录类任务
     */
    public static boolean isLoginTask(Integer category) {
        return LOGIN.equals(category);
    }

    /**
     * 判断是否为内容类任务
     */
    public static boolean isContentTask(Integer category) {
        return CONTENT.equals(category);
    }

    /**
     * 判断是否为社交类任务
     */
    public static boolean isSocialTask(Integer category) {
        return SOCIAL.equals(category);
    }

    /**
     * 判断是否为消费类任务
     */
    public static boolean isConsumeTask(Integer category) {
        return CONSUME.equals(category);
    }

    /**
     * 判断是否为邀请类任务
     */
    public static boolean isInviteTask(Integer category) {
        return INVITE.equals(category);
    }
}