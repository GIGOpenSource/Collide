package com.gig.collide.follow.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关注关系实体
 * 对应 t_follow 表
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("t_follow")
public class Follow {

    /**
     * 关注ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关注者用户ID
     */
    @TableField("follower_user_id")
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    @TableField("followed_user_id")
    private Long followedUserId;

    /**
     * 关注类型
     */
    @TableField("follow_type")
    private FollowType followType;

    /**
     * 状态
     */
    @TableField("status")
    private FollowStatus status;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
} 