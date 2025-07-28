package com.gig.collide.api.follow.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关注状态枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum FollowStatus {

    /**
     * 已取消
     */
    CANCELLED(0, "已取消"),

    /**
     * 正常
     */
    NORMAL(1, "正常"),

    /**
     * 已屏蔽
     */
    BLOCKED(2, "已屏蔽");

    /**
     * 状态编码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static FollowStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (FollowStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否为正常状态
     *
     * @return true-正常，false-非正常
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * 是否为已取消状态
     *
     * @return true-已取消，false-非已取消
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }

    /**
     * 是否为已屏蔽状态
     *
     * @return true-已屏蔽，false-非已屏蔽
     */
    public boolean isBlocked() {
        return this == BLOCKED;
    }

    /**
     * 是否为有效状态（正常或已屏蔽）
     *
     * @return true-有效，false-无效
     */
    public boolean isActive() {
        return this == NORMAL || this == BLOCKED;
    }
} 