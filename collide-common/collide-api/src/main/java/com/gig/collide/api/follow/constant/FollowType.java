package com.gig.collide.api.follow.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关注类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum FollowType {

    /**
     * 普通关注
     */
    NORMAL(1, "普通关注"),

    /**
     * 特别关注
     */
    SPECIAL(2, "特别关注");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static FollowType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FollowType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 是否为特别关注
     *
     * @return true-特别关注，false-普通关注
     */
    public boolean isSpecial() {
        return this == SPECIAL;
    }

    /**
     * 是否为普通关注
     *
     * @return true-普通关注，false-特别关注
     */
    public boolean isNormal() {
        return this == NORMAL;
    }
} 