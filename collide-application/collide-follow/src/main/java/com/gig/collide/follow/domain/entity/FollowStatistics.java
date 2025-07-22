package com.gig.collide.follow.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 关注统计实体
 * 对应 t_follow_statistics 表
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("t_follow_statistics")
public class FollowStatistics {

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    /**
     * 关注数（我关注的人数）
     */
    @TableField("following_count")
    private Integer followingCount;

    /**
     * 粉丝数（关注我的人数）
     */
    @TableField("follower_count")
    private Integer followerCount;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 