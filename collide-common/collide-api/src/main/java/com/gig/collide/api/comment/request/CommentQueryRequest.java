package com.gig.collide.api.comment.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 评论查询请求 - 简洁版
 * 基于comment-simple.sql的字段，支持多种查询条件
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
public class CommentQueryRequest implements Serializable {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论类型：CONTENT、DYNAMIC
     */
    private String commentType;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 父评论ID
     */
    private Long parentCommentId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 用户昵称关键词
     */
    private String userNickname;

    /**
     * 回复目标用户ID
     */
    private Long replyToUserId;

    /**
     * 状态：NORMAL、HIDDEN、DELETED
     */
    private String status;

    /**
     * 评论内容关键词
     */
    private String contentKeyword;

    /**
     * 查询类型：target、user、replies、tree、search、popular、latest
     */
    private String queryType;

    /**
     * 最小点赞数
     */
    private Integer minLikeCount;

    /**
     * 最小回复数
     */
    private Integer minReplyCount;

    /**
     * 时间范围（天），用于热门评论查询
     */
    private Integer timeRange;

    /**
     * 最大层级深度（用于树形查询）
     */
    private Integer maxDepth;

    // =================== 分页参数 ===================

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    @Min(value = 1, message = "页面大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 排序字段：create_time、like_count、reply_count
     */
    private String orderBy = "create_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "DESC";

    /**
     * 是否包含已删除评论
     */
    private Boolean includeDeleted = false;

    /**
     * 是否只查询根评论
     */
    private Boolean rootOnly = false;

    // =================== 业务方法 ===================

    /**
     * 是否查询根评论
     */
    public boolean isQueryRootComments() {
        return rootOnly != null && rootOnly;
    }

    /**
     * 是否查询回复评论
     */
    public boolean isQueryReplyComments() {
        return parentCommentId != null && parentCommentId > 0;
    }

    /**
     * 是否内容评论查询
     */
    public boolean isContentCommentQuery() {
        return "CONTENT".equals(commentType);
    }

    /**
     * 是否动态评论查询
     */
    public boolean isDynamicCommentQuery() {
        return "DYNAMIC".equals(commentType);
    }

    /**
     * 是否用户评论查询
     */
    public boolean isUserCommentQuery() {
        return "user".equals(queryType) || userId != null;
    }

    /**
     * 是否目标对象评论查询
     */
    public boolean isTargetCommentQuery() {
        return "target".equals(queryType) || targetId != null;
    }

    /**
     * 是否搜索查询
     */
    public boolean isSearchQuery() {
        return "search".equals(queryType) || contentKeyword != null;
    }

    /**
     * 是否热门评论查询
     */
    public boolean isPopularQuery() {
        return "popular".equals(queryType);
    }

    /**
     * 是否最新评论查询
     */
    public boolean isLatestQuery() {
        return "latest".equals(queryType);
    }

    /**
     * 是否树形查询
     */
    public boolean isTreeQuery() {
        return "tree".equals(queryType);
    }

    /**
     * 验证排序字段
     */
    public boolean isValidOrderBy() {
        if (orderBy == null) {
            return true;
        }
        return "create_time".equals(orderBy) || "like_count".equals(orderBy) 
               || "reply_count".equals(orderBy) || "update_time".equals(orderBy);
    }

    /**
     * 验证排序方向
     */
    public boolean isValidOrderDirection() {
        if (orderDirection == null) {
            return true;
        }
        return "ASC".equals(orderDirection) || "DESC".equals(orderDirection);
    }

    /**
     * 获取实际的父评论ID
     */
    public Long getActualParentCommentId() {
        if (isQueryRootComments()) {
            return 0L;
        }
        return parentCommentId;
    }
}