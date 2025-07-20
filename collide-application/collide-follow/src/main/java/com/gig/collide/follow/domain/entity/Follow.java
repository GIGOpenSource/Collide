package com.gig.collide.follow.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.follow.constant.FollowTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 关注实体
 * @author GIG
 */
@Data
@TableName("t_follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private FollowTypeEnum followType;

    /**
     * 状态：1-正常，0-已取消
     */
    @TableField("status")
    private Integer status;

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