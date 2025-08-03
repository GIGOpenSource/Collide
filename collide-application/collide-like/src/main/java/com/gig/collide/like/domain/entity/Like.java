package com.gig.collide.like.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 点赞实体类 - 简洁版
 * 基于like-simple.sql的t_like表结构
 * 采用无连表设计，包含目标对象和用户信息冗余字段
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("t_like")
public class Like {

    /**
     * 点赞ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 点赞类型：CONTENT、COMMENT、DYNAMIC
     */
    @TableField("like_type")
    private String likeType;

    /**
     * 目标对象ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 点赞用户ID
     */
    @TableField("user_id")
    private Long userId;

    // =================== 目标对象信息（冗余字段，避免连表） ===================
    
    /**
     * 目标对象标题（冗余）
     */
    @TableField("target_title")
    private String targetTitle;

    /**
     * 目标对象作者ID（冗余）
     */
    @TableField("target_author_id")
    private Long targetAuthorId;

    // =================== 用户信息（冗余字段，避免连表） ===================
    
    /**
     * 用户昵称（冗余）
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 用户头像（冗余）
     */
    @TableField("user_avatar")
    private String userAvatar;

    // =================== 状态和时间字段 ===================
    
    /**
     * 状态：active、cancelled
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================
    
    /**
     * 判断是否为活跃状态
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 判断是否已取消
     */
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }

    /**
     * 判断是否为内容点赞
     */
    public boolean isContentLike() {
        return "CONTENT".equals(likeType);
    }

    /**
     * 判断是否为评论点赞
     */
    public boolean isCommentLike() {
        return "COMMENT".equals(likeType);
    }

    /**
     * 判断是否为动态点赞
     */
    public boolean isDynamicLike() {
        return "DYNAMIC".equals(likeType);
    }

    /**
     * 激活点赞
     */
    public void activate() {
        this.status = "active";
    }

    /**
     * 取消点赞
     */
    public void cancel() {
        this.status = "cancelled";
    }
}