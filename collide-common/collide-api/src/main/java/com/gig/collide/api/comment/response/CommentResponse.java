package com.gig.collide.api.comment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 评论操作响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 操作结果说明
     */
    private String resultMessage;

    /**
     * 是否为新创建
     */
    private Boolean isNew;

    /**
     * 当前点赞状态（用于点赞操作）
     */
    private Boolean isLiked;

    /**
     * 点赞数量（用于点赞操作）
     */
    private Integer likeCount;

    public CommentResponse(Long commentId) {
        super();
        this.commentId = commentId;
        this.setSuccess(true);
    }

    /**
     * 创建成功响应
     *
     * @param commentId 评论ID
     * @param message 响应消息
     * @return 响应对象
     */
    public static CommentResponse success(Long commentId, String message) {
        CommentResponse response = new CommentResponse(commentId);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建失败响应
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return 响应对象
     */
    public static CommentResponse error(String errorCode, String errorMessage) {
        CommentResponse response = new CommentResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 