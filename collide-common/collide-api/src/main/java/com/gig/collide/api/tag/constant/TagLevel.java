package com.gig.collide.api.tag.constant;

/**
 * 标签层级枚举
 * @author GIG
 */
public enum TagLevel {

    /**
     * 一级标签（根标签）
     */
    LEVEL_1(1, "一级标签"),

    /**
     * 二级标签
     */
    LEVEL_2(2, "二级标签"),

    /**
     * 三级标签
     */
    LEVEL_3(3, "三级标签");

    private final int level;
    private final String desc;

    TagLevel(int level, String desc) {
        this.level = level;
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据数字级别获取枚举
     */
    public static TagLevel fromLevel(int level) {
        for (TagLevel tagLevel : values()) {
            if (tagLevel.level == level) {
                return tagLevel;
            }
        }
        throw new IllegalArgumentException("不支持的标签层级: " + level);
    }

    /**
     * 是否是根级标签
     */
    public boolean isRoot() {
        return this == LEVEL_1;
    }

    /**
     * 是否是叶子级标签
     */
    public boolean isLeaf() {
        return this == LEVEL_3;
    }

    /**
     * 获取下一级别
     */
    public TagLevel getNextLevel() {
        switch (this) {
            case LEVEL_1:
                return LEVEL_2;
            case LEVEL_2:
                return LEVEL_3;
            case LEVEL_3:
                throw new IllegalStateException("三级标签已是最大层级");
            default:
                throw new IllegalStateException("未知标签层级");
        }
    }
} 