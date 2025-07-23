package com.gig.collide.comment.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体
 * 对应 t_comment 表
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
     * 目标ID（内容ID或父评论ID等）
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
     * 评论者用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 被回复用户ID
     */
    @TableField("reply_to_user_id")
    private Long replyToUserId;

    /**
     * 评论状态
     */
    @TableField("status")
    private CommentStatus status;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 回复数
     */
    @TableField("reply_count")
    private Integer replyCount;

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
     * 是否已删除（0: 否, 1: 是）
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 检查是否可以删除
     * 
     * @param currentUserId 当前用户ID
     * @param isAdmin 是否为管理员
     * @return 是否可删除
     */
    public boolean isDeletable(Long currentUserId, boolean isAdmin) {
        // 管理员可以删除任何评论
        if (isAdmin) {
            return true;
        }
        // 用户只能删除自己的评论
        return this.userId != null && this.userId.equals(currentUserId);
    }
} 