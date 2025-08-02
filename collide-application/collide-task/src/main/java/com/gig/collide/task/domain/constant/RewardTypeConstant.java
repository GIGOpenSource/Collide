package com.gig.collide.task.domain.constant;

/**
 * 奖励类型常量
 * 与优化后的 task-simple.sql 保持一致
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public class RewardTypeConstant {

    /**
     * 金币奖励
     */
    public static final Integer COIN = 1;

    /**
     * 道具奖励
     */
    public static final Integer ITEM = 2;

    /**
     * VIP奖励
     */
    public static final Integer VIP = 3;

    /**
     * 经验值奖励
     */
    public static final Integer EXPERIENCE = 4;

    /**
     * 徽章奖励
     */
    public static final Integer BADGE = 5;

    /**
     * 获取奖励类型名称
     */
    public static String getTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "金币";
            case 2: return "道具";
            case 3: return "VIP";
            case 4: return "经验值";
            case 5: return "徽章";
            default: return "未知";
        }
    }

    /**
     * 获取奖励类型英文名称
     */
    public static String getTypeKey(Integer type) {
        if (type == null) return "unknown";
        switch (type) {
            case 1: return "coin";
            case 2: return "item";
            case 3: return "vip";
            case 4: return "experience";
            case 5: return "badge";
            default: return "unknown";
        }
    }

    /**
     * 验证奖励类型是否有效
     */
    public static boolean isValidType(Integer type) {
        return type != null && type >= 1 && type <= 5;
    }

    /**
     * 判断是否为金币奖励
     */
    public static boolean isCoinReward(Integer type) {
        return COIN.equals(type);
    }

    /**
     * 判断是否为道具奖励
     */
    public static boolean isItemReward(Integer type) {
        return ITEM.equals(type);
    }

    /**
     * 判断是否为VIP奖励
     */
    public static boolean isVipReward(Integer type) {
        return VIP.equals(type);
    }

    /**
     * 判断是否为经验值奖励
     */
    public static boolean isExperienceReward(Integer type) {
        return EXPERIENCE.equals(type);
    }

    /**
     * 判断是否为徽章奖励
     */
    public static boolean isBadgeReward(Integer type) {
        return BADGE.equals(type);
    }

    /**
     * 判断是否需要立即发放到钱包
     * 金币奖励需要立即发放到用户钱包
     */
    public static boolean requiresImmediateWalletSync(Integer type) {
        return COIN.equals(type);
    }
}