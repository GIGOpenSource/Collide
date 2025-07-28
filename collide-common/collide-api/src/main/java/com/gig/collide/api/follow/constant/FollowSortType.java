package com.gig.collide.api.follow.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 关注排序类型枚举
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum FollowSortType {

    /**
     * 按创建时间排序
     */
    CREATE_TIME("created_time", "按创建时间排序"),

    /**
     * 按更新时间排序
     */
    UPDATE_TIME("updated_time", "按更新时间排序"),

    /**
     * 按关注者ID排序
     */
    FOLLOWER_USER_ID("follower_user_id", "按关注者ID排序"),

    /**
     * 按被关注者ID排序
     */
    FOLLOWED_USER_ID("followed_user_id", "按被关注者ID排序"),

    /**
     * 按关注类型排序
     */
    FOLLOW_TYPE("follow_type", "按关注类型排序"),

    /**
     * 按状态排序
     */
    STATUS("status", "按状态排序"),

    /**
     * 按粉丝数排序（用于统计排序）
     */
    FOLLOWER_COUNT("follower_count", "按粉丝数排序"),

    /**
     * 按关注数排序（用于统计排序）
     */
    FOLLOWING_COUNT("following_count", "按关注数排序");

    /**
     * 排序字段
     */
    private final String field;

    /**
     * 排序描述
     */
    private final String description;

    /**
     * 根据字段名获取枚举
     *
     * @param field 字段名
     * @return 枚举值
     */
    public static FollowSortType getByField(String field) {
        if (field == null) {
            return null;
        }
        for (FollowSortType sortType : values()) {
            if (sortType.getField().equals(field)) {
                return sortType;
            }
        }
        return null;
    }

    /**
     * 是否为时间类型排序
     *
     * @return true-时间排序，false-非时间排序
     */
    public boolean isTimeSort() {
        return this == CREATE_TIME || this == UPDATE_TIME;
    }

    /**
     * 是否为统计类型排序
     *
     * @return true-统计排序，false-非统计排序
     */
    public boolean isStatisticsSort() {
        return this == FOLLOWER_COUNT || this == FOLLOWING_COUNT;
    }
} 