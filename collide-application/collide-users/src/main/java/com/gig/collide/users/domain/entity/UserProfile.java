package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户扩展信息实体
 *
 * @author GIG
 */
@Data
@TableName("t_user_profile")
public class UserProfile {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 性别
     */
    @TableField("gender")
    private Gender gender;

    /**
     * 所在地
     */
    @TableField("location")
    private String location;

    /**
     * 粉丝数
     */
    @TableField("follower_count")
    private Long followerCount = 0L;

    /**
     * 关注数
     */
    @TableField("following_count")
    private Long followingCount = 0L;

    /**
     * 内容数
     */
    @TableField("content_count")
    private Long contentCount = 0L;

    /**
     * 获得点赞数
     */
    @TableField("like_count")
    private Long likeCount = 0L;

    /**
     * VIP过期时间
     */
    @TableField("vip_expire_time")
    private LocalDateTime vipExpireTime;

    /**
     * 博主认证状态
     */
    @TableField("blogger_status")
    private BloggerStatus bloggerStatus = BloggerStatus.none;

    /**
     * 博主申请时间
     */
    @TableField("blogger_apply_time")
    private LocalDateTime bloggerApplyTime;

    /**
     * 博主认证时间
     */
    @TableField("blogger_approve_time")
    private LocalDateTime bloggerApproveTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 性别枚举
     */
    public enum Gender {
        /** 男性 */
        male,
        /** 女性 */
        female,
        /** 未知 */
        unknown
    }

    /**
     * 博主认证状态枚举
     */
    public enum BloggerStatus {
        /** 无 */
        none,
        /** 申请中 */
        applying,
        /** 已通过 */
        approved,
        /** 已拒绝 */
        rejected
    }
} 