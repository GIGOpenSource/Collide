package com.gig.collide.api.comment.request;

import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.base.request.PageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 评论查询请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentQueryRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID（查询单个评论时使用）
     */
    private Long commentId;

    /**
     * 评论类型
     */
    private CommentType commentType;

    /**
     * 目标ID（内容ID或父评论ID等）
     */
    private Long targetId;

    /**
     * 父评论ID（查询子评论时使用）
     */
    private Long parentCommentId;

    /**
     * 根评论ID（用于树形结构查询）
     */
    private Long rootCommentId;

    /**
     * 评论者用户ID
     */
    private Long userId;

    /**
     * 查询用户ID（用于判断点赞状态等）
     */
    private Long currentUserId;

    /**
     * 是否包含子评论
     */
    private Boolean includeChildren = false;

    /**
     * 排序方式（time: 时间排序, hot: 热度排序）
     */
    private String sortBy = "time";

    /**
     * 排序方向（asc: 升序, desc: 降序）
     */
    private String sortOrder = "desc";
} 