package com.gig.collide.api.artist.constant;

/**
 * 博主等级枚举
 * @author GIG
 */
public enum ArtistLevel {

    /**
     * 新手博主
     */
    NEWCOMER(1, "新手博主", 0, 1000),

    /**
     * 成长博主
     */
    GROWING(2, "成长博主", 1000, 10000),

    /**
     * 优秀博主
     */
    EXCELLENT(3, "优秀博主", 10000, 50000),

    /**
     * 知名博主
     */
    FAMOUS(4, "知名博主", 50000, 200000),

    /**
     * 顶级博主
     */
    TOP(5, "顶级博主", 200000, Integer.MAX_VALUE);

    private final int level;
    private final String desc;
    private final int minFollowers;
    private final int maxFollowers;

    ArtistLevel(int level, String desc, int minFollowers, int maxFollowers) {
        this.level = level;
        this.desc = desc;
        this.minFollowers = minFollowers;
        this.maxFollowers = maxFollowers;
    }

    public int getLevel() {
        return level;
    }

    public String getDesc() {
        return desc;
    }

    public int getMinFollowers() {
        return minFollowers;
    }

    public int getMaxFollowers() {
        return maxFollowers;
    }

    /**
     * 根据粉丝数计算博主等级
     */
    public static ArtistLevel calculateLevel(int followersCount) {
        for (ArtistLevel level : values()) {
            if (followersCount >= level.minFollowers && followersCount < level.maxFollowers) {
                return level;
            }
        }
        return TOP; // 默认返回最高等级
    }

    /**
     * 是否为高级博主
     */
    public boolean isAdvanced() {
        return level >= 3;
    }

    /**
     * 是否为顶级博主
     */
    public boolean isTop() {
        return this == TOP;
    }

    /**
     * 获取下一个等级
     */
    public ArtistLevel getNextLevel() {
        int nextLevel = this.level + 1;
        for (ArtistLevel level : values()) {
            if (level.level == nextLevel) {
                return level;
            }
        }
        return this; // 已是最高等级
    }
} 