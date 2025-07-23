package com.gig.collide.comment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体
 * 对应 t_comment 表
 * 完全去连表化设计，统计字段冗余存储
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("t_comment")
public class Comment {

    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论类型
     */
    @TableField("comment_type")
    private CommentType commentType;

    /**
     * 目标ID（内容ID等）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 父评论ID
     */
    @TableField("parent_comment_id")
    private Long parentCommentId;

    /**
     * 根评论ID（用于树形结构）
     */
    @TableField("root_comment_id")
    private Long rootCommentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 回复目标用户ID
     */
    @TableField("reply_to_user_id")
    private Long replyToUserId;

    /**
     * 评论状态
     */
    @TableField("status")
    private CommentStatus status;

    /**
     * 点赞数（冗余字段，避免连表查询）
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 回复数（冗余字段，避免连表查询）
     */
    @TableField("reply_count")
    private Integer replyCount;

    /**
     * 是否置顶
     */
    @TableField("is_pinned")
    private Boolean isPinned;

    /**
     * 是否热门评论
     */
    @TableField("is_hot")
    private Boolean isHot;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

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
    @TableField("is_deleted")
    private Integer isDeleted;
} 