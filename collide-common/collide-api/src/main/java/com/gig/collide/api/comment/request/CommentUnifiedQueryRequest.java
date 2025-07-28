package com.gig.collide.api.comment.request;

import com.gig.collide.api.comment.enums.AuditStatusEnum;
import com.gig.collide.api.comment.enums.CommentStatusEnum;
import com.gig.collide.api.comment.enums.CommentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论统一查询请求
 * 支持多种查询条件和排序方式
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentUnifiedQueryRequest extends BaseRequest {

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 评论ID列表（批量查询）
     */
    private List<Long> commentIds;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 目标对象ID列表
     */
    private List<Long> targetIds;

    /**
     * 评论类型
     */
    private CommentTypeEnum commentType;

    /**
     * 评论类型列表
     */
    private List<CommentTypeEnum> commentTypes;

    /**
     * 父评论ID
     */
    private Long parentCommentId;

    /**
     * 根评论ID
     */
    private Long rootCommentId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 评论用户ID列表
     */
    private List<Long> userIds;

    /**
     * 回复目标用户ID
     */
    private Long replyToUserId;

    /**
     * 评论状态
     */
    private CommentStatusEnum status;

    /**
     * 评论状态列表
     */
    private List<CommentStatusEnum> statuses;

    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatus;

    /**
     * 审核状态列表
     */
    private List<AuditStatusEnum> auditStatuses;

    /**
     * 内容关键词搜索
     */
    private String contentKeyword;

    /**
     * 是否置顶
     */
    private Boolean isPinned;

    /**
     * 是否热门
     */
    private Boolean isHot;

    /**
     * 是否精华
     */
    private Boolean isEssence;

    /**
     * 最小点赞数
     */
    private Integer minLikeCount;

    /**
     * 最小回复数
     */
    private Integer minReplyCount;

    /**
     * 最小质量分数
     */
    private Double minQualityScore;

    /**
     * 创建时间范围 - 开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间范围 - 结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 更新时间范围 - 开始
     */
    private LocalDateTime updateTimeStart;

    /**
     * 更新时间范围 - 结束
     */
    private LocalDateTime updateTimeEnd;

    /**
     * 排序字段
     * 可选值：create_time, update_time, like_count, reply_count, hot_score, quality_score
     */
    private String sortBy;

    /**
     * 排序方向（ASC/DESC）
     */
    private String sortDirection;

    /**
     * 是否只查询根评论
     */
    private Boolean rootCommentsOnly;

    /**
     * 是否包含回复
     */
    private Boolean includeReplies;

    /**
     * 是否包含统计信息
     */
    private Boolean includeStatistics;

    /**
     * 是否包含用户交互信息（点赞、举报状态等）
     */
    private Boolean includeUserInteraction;

    /**
     * 当前用户ID（用于查询用户交互信息）
     */
    private Long currentUserId;

    /**
     * 树形查询深度（0-只查询根评论，1-查询根评论和第一层回复，-1-查询所有层级）
     */
    private Integer treeDepth;

    // ===================== 便捷构造器 =====================

    /**
     * 根据评论ID查询
     */
    public CommentUnifiedQueryRequest(Long commentId) {
        this.commentId = commentId;
    }

    /**
     * 根据目标对象ID查询
     */
    public static CommentUnifiedQueryRequest byTarget(Long targetId, CommentTypeEnum commentType) {
        CommentUnifiedQueryRequest request = new CommentUnifiedQueryRequest();
        request.setTargetId(targetId);
        request.setCommentType(commentType);
        request.setStatus(CommentStatusEnum.NORMAL);
        return request;
    }

    /**
     * 根据用户ID查询
     */
    public static CommentUnifiedQueryRequest byUser(Long userId) {
        CommentUnifiedQueryRequest request = new CommentUnifiedQueryRequest();
        request.setUserId(userId);
        request.setStatus(CommentStatusEnum.NORMAL);
        return request;
    }

    /**
     * 查询根评论
     */
    public static CommentUnifiedQueryRequest rootComments(Long targetId, CommentTypeEnum commentType) {
        CommentUnifiedQueryRequest request = byTarget(targetId, commentType);
        request.setRootCommentsOnly(true);
        request.setParentCommentId(0L);
        return request;
    }

    /**
     * 查询回复评论
     */
    public static CommentUnifiedQueryRequest replies(Long parentCommentId) {
        CommentUnifiedQueryRequest request = new CommentUnifiedQueryRequest();
        request.setParentCommentId(parentCommentId);
        request.setStatus(CommentStatusEnum.NORMAL);
        return request;
    }

    /**
     * 查询热门评论
     */
    public static CommentUnifiedQueryRequest hot(Long targetId, CommentTypeEnum commentType) {
        CommentUnifiedQueryRequest request = byTarget(targetId, commentType);
        request.setSortBy("hot_score");
        request.setSortDirection("DESC");
        request.setMinLikeCount(1);
        return request;
    }

    /**
     * 查询最新评论
     */
    public static CommentUnifiedQueryRequest latest(Long targetId, CommentTypeEnum commentType) {
        CommentUnifiedQueryRequest request = byTarget(targetId, commentType);
        request.setSortBy("create_time");
        request.setSortDirection("DESC");
        return request;
    }

    /**
     * 查询精华评论
     */
    public static CommentUnifiedQueryRequest essence(Long targetId, CommentTypeEnum commentType) {
        CommentUnifiedQueryRequest request = byTarget(targetId, commentType);
        request.setIsEssence(true);
        request.setSortBy("quality_score");
        request.setSortDirection("DESC");
        return request;
    }

    /**
     * 查询置顶评论
     */
    public static CommentUnifiedQueryRequest pinned(Long targetId, CommentTypeEnum commentType) {
        CommentUnifiedQueryRequest request = byTarget(targetId, commentType);
        request.setIsPinned(true);
        request.setSortBy("create_time");
        request.setSortDirection("DESC");
        return request;
    }

    /**
     * 查询待审核评论
     */
    public static CommentUnifiedQueryRequest pendingAudit() {
        CommentUnifiedQueryRequest request = new CommentUnifiedQueryRequest();
        request.setAuditStatus(AuditStatusEnum.PENDING);
        request.setSortBy("create_time");
        request.setSortDirection("ASC");
        return request;
    }

    /**
     * 查询评论树（包含回复）
     */
    public static CommentUnifiedQueryRequest tree(Long targetId, CommentTypeEnum commentType, Integer depth) {
        CommentUnifiedQueryRequest request = byTarget(targetId, commentType);
        request.setIncludeReplies(true);
        request.setTreeDepth(depth);
        request.setSortBy("create_time");
        request.setSortDirection("ASC");
        return request;
    }
} 