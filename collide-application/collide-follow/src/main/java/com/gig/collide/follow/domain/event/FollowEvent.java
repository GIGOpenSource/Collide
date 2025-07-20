package com.gig.collide.follow.domain.event;

import com.gig.collide.api.follow.constant.FollowTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 关注事件消息体
 * @author GIG
 */
@Data
@Accessors(chain = true)
public class FollowEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private FollowEventType eventType;

    /**
     * 关注者用户ID
     */
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    private Long followedUserId;

    /**
     * 关注类型
     */
    private FollowTypeEnum followType;

    /**
     * 事件时间
     */
    private Date eventTime;

    /**
     * 事件ID（幂等性保证）
     */
    private String eventId;

    /**
     * 扩展信息
     */
    private String extendInfo;

    /**
     * 关注事件类型枚举
     */
    public enum FollowEventType {
        /**
         * 关注事件
         */
        FOLLOW,
        
        /**
         * 取消关注事件
         */
        UNFOLLOW,
        
        /**
         * 统计更新事件
         */
        STATISTICS_UPDATE
    }
} 