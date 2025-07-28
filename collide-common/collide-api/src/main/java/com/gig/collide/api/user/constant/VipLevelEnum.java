package com.gig.collide.api.user.constant;

/**
 * VIP等级枚举
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public enum VipLevelEnum {

    /**
     * 普通用户（非VIP）
     */
    NONE(0, "普通用户"),

    /**
     * 青铜VIP
     */
    BRONZE(1, "青铜VIP"),

    /**
     * 白银VIP
     */
    SILVER(2, "白银VIP"),

    /**
     * 黄金VIP
     */
    GOLD(3, "黄金VIP"),

    /**
     * 白金VIP
     */
    PLATINUM(4, "白金VIP"),

    /**
     * 钻石VIP
     */
    DIAMOND(5, "钻石VIP"),

    /**
     * 王者VIP
     */
    KING(6, "王者VIP");

    private final int level;
    private final String description;

    VipLevelEnum(int level, String description) {
        this.level = level;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据等级数字获取VIP枚举
     */
    public static VipLevelEnum fromLevel(Integer level) {
        if (level == null || level < 0) {
            return NONE;
        }
        
        for (VipLevelEnum vipLevel : values()) {
            if (vipLevel.level == level) {
                return vipLevel;
            }
        }
        
        // 如果等级超过最高级，返回最高级
        return level > KING.level ? KING : NONE;
    }

    /**
     * 检查是否为VIP用户
     */
    public boolean isVip() {
        return this != NONE;
    }

    /**
     * 检查是否为高级VIP（黄金及以上）
     */
    public boolean isPremiumVip() {
        return level >= GOLD.level && level > 0;
    }

    /**
     * 检查是否为顶级VIP（钻石及以上）
     */
    public boolean isTopVip() {
        return level >= DIAMOND.level && level > 0;
    }

    /**
     * 获取下一级VIP等级
     */
    public VipLevelEnum getNextLevel() {
        if (this == KING) {
            return KING; // 已经是最高级
        }
        
        for (VipLevelEnum vipLevel : values()) {
            if (vipLevel.level == this.level + 1) {
                return vipLevel;
            }
        }
        
        return this;
    }
} 