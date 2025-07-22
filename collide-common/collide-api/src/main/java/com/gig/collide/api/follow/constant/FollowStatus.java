package com.gig.collide.api.follow.constant;

/**
 * 关注状态枚举
 * 对应 t_follow 表的 status 字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum FollowStatus {

    /**
     * 已取消关注
     */
    CANCELLED(0, "已取消"),

    /**
     * 正常关注
     */
    NORMAL(1, "正常");

    private final int code;
    private final String description;

    FollowStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static FollowStatus fromCode(int code) {
        for (FollowStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown follow status code: " + code);
    }
} 