package com.gig.collide.follow.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 关注统计实体
 * @author GIG
 */
@Data
@TableName("t_follow_statistics")
public class FollowStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    /**
     * 关注数量
     */
    @TableField("following_count")
    private Integer followingCount;

    /**
     * 粉丝数量
     */
    @TableField("follower_count")
    private Integer followerCount;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;
} 