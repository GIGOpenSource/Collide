package com.gig.collide.api.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 评论创建请求 - 简洁版
 * 基于comment-simple.sql的设计，支持多级评论和回复功能
 * 包含冗余用户信息以避免连表查询
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
public class CommentCreateRequest {

    /**
     * 评论类型：CONTENT、DYNAMIC
     */
    @NotBlank(message = "评论类型不能为空")
    private String commentType;

    /**
     * 目标对象ID
     */
    @NotNull(message = "目标对象ID不能为空")
    private Long targetId;

    /**
     * 父评论ID，0表示根评论
     */
    private Long parentCommentId = 0L;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    // =================== 用户信息（冗余字段） ===================

    /**
     * 评论用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户昵称（冗余）
     */
    private String userNickname;

    /**
     * 用户头像（冗余）
     */
    private String userAvatar;

    // =================== 回复相关 ===================

    /**
     * 回复目标用户ID
     */
    private Long replyToUserId;

    /**
     * 回复目标用户昵称（冗余）
     */
    private String replyToUserNickname;

    /**
     * 回复目标用户头像（冗余）
     */
    private String replyToUserAvatar;

    /**
     * 状态（默认为NORMAL）
     */
    private String status = "NORMAL";

    // =================== 业务方法 ===================

    /**
     * 是否为根评论
     */
    public boolean isRootComment() {
        return parentCommentId == null || parentCommentId == 0;
    }

    /**
     * 是否为回复评论
     */
    public boolean isReplyComment() {
        return !isRootComment();
    }

    /**
     * 是否为内容评论
     */
    public boolean isContentComment() {
        return "CONTENT".equals(commentType);
    }

    /**
     * 是否为动态评论
     */
    public boolean isDynamicComment() {
        return "DYNAMIC".equals(commentType);
    }

    /**
     * 验证评论类型
     */
    public boolean isValidCommentType() {
        return "CONTENT".equals(commentType) || "DYNAMIC".equals(commentType);
    }

    /**
     * 验证状态
     */
    public boolean isValidStatus() {
        return "NORMAL".equals(status) || "HIDDEN".equals(status);
    }

    /**
     * 获取评论内容长度
     */
    public int getContentLength() {
        return content != null ? content.trim().length() : 0;
    }

    /**
     * 是否为长评论
     */
    public boolean isLongComment() {
        return getContentLength() > 100;
    }
}