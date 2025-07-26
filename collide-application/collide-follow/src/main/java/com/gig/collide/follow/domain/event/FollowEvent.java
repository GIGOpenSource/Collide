package com.gig.collide.follow.domain.event;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 关注事件
 * 用于异步处理关注相关的业务逻辑
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Builder
public class FollowEvent {

    /**
     * 关注者用户ID
     */
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    private Long followedUserId;

    /**
     * 关注ID
     */
    private Long followId;

    /**
     * 是否为关注操作（true: 关注, false: 取消关注）
     */
    private Boolean isFollow;

    /**
     * 事件时间
     */
    private LocalDateTime eventTime;

    /**
     * 事件类型
     */
    public String getEventType() {
        return isFollow ? "USER_FOLLOWED" : "USER_UNFOLLOWED";
    }

    @Override
    public String toString() {
        return String.format("FollowEvent{followerUserId=%d, followedUserId=%d, isFollow=%s, eventTime=%s}",
                followerUserId, followedUserId, isFollow, eventTime);
    }
} 