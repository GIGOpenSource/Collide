package com.gig.collide.api.category.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分类状态枚举
 * 定义分类的激活状态
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CategoryStatusEnum {

    /**
     * 激活状态
     */
    ACTIVE("ACTIVE", "激活", "分类正常可用"),

    /**
     * 禁用状态
     */
    INACTIVE("INACTIVE", "禁用", "分类被禁用，不可使用");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举值
     *
     * @param code 状态代码
     * @return 对应的枚举值，未找到返回null
     */
    public static CategoryStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CategoryStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为激活状态
     *
     * @return true如果为激活状态
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 判断是否为禁用状态
     *
     * @return true如果为禁用状态
     */
    public boolean isInactive() {
        return this == INACTIVE;
    }

    /**
     * 判断分类是否可用
     *
     * @return true如果分类可用
     */
    public boolean isAvailable() {
        return this == ACTIVE;
    }
} 