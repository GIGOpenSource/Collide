package com.gig.collide.api.category.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分类层级枚举
 * 定义分类的层级级别
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CategoryLevelEnum {

    /**
     * 根分类（一级分类）
     */
    ROOT(1, "根分类", "最顶层的分类"),

    /**
     * 二级分类
     */
    SECOND(2, "二级分类", "二级子分类"),

    /**
     * 三级分类
     */
    THIRD(3, "三级分类", "三级子分类"),

    /**
     * 四级分类
     */
    FOURTH(4, "四级分类", "四级子分类"),

    /**
     * 五级分类
     */
    FIFTH(5, "五级分类", "五级子分类");

    /**
     * 层级级别
     */
    private final int level;

    /**
     * 层级名称
     */
    private final String name;

    /**
     * 层级描述
     */
    private final String description;

    /**
     * 根据级别获取枚举值
     *
     * @param level 层级级别
     * @return 对应的枚举值，未找到返回null
     */
    public static CategoryLevelEnum getByLevel(int level) {
        for (CategoryLevelEnum levelEnum : values()) {
            if (levelEnum.getLevel() == level) {
                return levelEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否为根分类
     *
     * @return true如果为根分类
     */
    public boolean isRoot() {
        return this == ROOT;
    }

    /**
     * 判断是否为叶子分类（最后一级）
     *
     * @return true如果为叶子分类
     */
    public boolean isLeaf() {
        return this == FIFTH;
    }

    /**
     * 判断是否可以添加子分类
     *
     * @return true如果可以添加子分类
     */
    public boolean canHaveChildren() {
        return this.level < FIFTH.level;
    }

    /**
     * 获取下一级别
     *
     * @return 下一级别的枚举值，如果已是最后一级则返回null
     */
    public CategoryLevelEnum getNextLevel() {
        return getByLevel(this.level + 1);
    }

    /**
     * 获取上一级别
     *
     * @return 上一级别的枚举值，如果已是第一级则返回null
     */
    public CategoryLevelEnum getPreviousLevel() {
        return getByLevel(this.level - 1);
    }

    /**
     * 判断是否超过最大层级
     *
     * @param level 要检查的层级
     * @return true如果超过最大层级
     */
    public static boolean exceedsMaxLevel(int level) {
        return level > FIFTH.level;
    }

    /**
     * 判断层级是否有效
     *
     * @param level 要检查的层级
     * @return true如果层级有效
     */
    public static boolean isValidLevel(int level) {
        return level >= ROOT.level && level <= FIFTH.level;
    }
} 