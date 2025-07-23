package com.gig.collide.api.comment.request;

import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 评论创建请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentCreateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 评论类型
     */
    @NotNull(message = "评论类型不能为空")
    private CommentType commentType;

    /**
     * 目标ID（内容ID或父评论ID等）
     */
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    /**
     * 父评论ID（回复评论时必填）
     */
    private Long parentCommentId;

    /**
     * 根评论ID（用于树形结构）
     */
    private Long rootCommentId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 2000, message = "评论内容长度必须在1-2000字符之间")
    private String content;

    /**
     * 评论者用户ID
     */
    @NotNull(message = "评论者用户ID不能为空")
    private Long userId;

    /**
     * 被回复用户ID（回复评论时使用）
     */
    private Long replyToUserId;
} 