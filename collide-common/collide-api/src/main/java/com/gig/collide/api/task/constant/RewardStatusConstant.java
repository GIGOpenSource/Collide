package com.gig.collide.api.task.constant;

/**
 * 奖励状态常量
 * 与优化后的 task-simple.sql 保持一致
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public class RewardStatusConstant {

    /**
     * 待发放
     */
    public static final Integer PENDING = 1;

    /**
     * 发放成功
     */
    public static final Integer SUCCESS = 2;

    /**
     * 发放失败
     */
    public static final Integer FAILED = 3;

    /**
     * 已过期
     */
    public static final Integer EXPIRED = 4;

    /**
     * 获取奖励状态名称
     */
    public static String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "待发放";
            case 2: return "发放成功";
            case 3: return "发放失败";
            case 4: return "已过期";
            default: return "未知";
        }
    }

    /**
     * 获取奖励状态英文名称
     */
    public static String getStatusKey(Integer status) {
        if (status == null) return "unknown";
        switch (status) {
            case 1: return "pending";
            case 2: return "success";
            case 3: return "failed";
            case 4: return "expired";
            default: return "unknown";
        }
    }

    /**
     * 验证奖励状态是否有效
     */
    public static boolean isValidStatus(Integer status) {
        return status != null && status >= 1 && status <= 4;
    }

    /**
     * 判断是否为待发放状态
     */
    public static boolean isPending(Integer status) {
        return PENDING.equals(status);
    }

    /**
     * 判断是否为成功状态
     */
    public static boolean isSuccess(Integer status) {
        return SUCCESS.equals(status);
    }

    /**
     * 判断是否为失败状态
     */
    public static boolean isFailed(Integer status) {
        return FAILED.equals(status);
    }

    /**
     * 判断是否为过期状态
     */
    public static boolean isExpired(Integer status) {
        return EXPIRED.equals(status);
    }

    /**
     * 判断奖励是否可以发放
     */
    public static boolean canBeGranted(Integer status) {
        return PENDING.equals(status);
    }

    /**
     * 判断奖励是否已完成（成功或失败）
     */
    public static boolean isCompleted(Integer status) {
        return SUCCESS.equals(status) || FAILED.equals(status);
    }
}