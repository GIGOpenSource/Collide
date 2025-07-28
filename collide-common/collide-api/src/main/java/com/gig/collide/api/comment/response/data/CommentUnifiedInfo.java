package com.gig.collide.api.comment.response.data;

import com.gig.collide.api.comment.enums.AuditStatusEnum;
import com.gig.collide.api.comment.enums.CommentStatusEnum;
import com.gig.collide.api.comment.enums.CommentTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论统一信息传输对象
 * 包含完整的评论信息和统计数据
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentUnifiedInfo extends BasicCommentInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 评论类型
     */
    private CommentTypeEnum commentType;

    /**
     * 目标对象ID（内容ID、动态ID等）
     */
    private Long targetId;

    /**
     * 父评论ID（0表示根评论）
     */
    private Long parentCommentId;

    /**
     * 根评论ID（0表示本身就是根评论）
     */
    private Long rootCommentId;

    /**
     * 回复目标用户ID
     */
    private Long replyToUserId;

    /**
     * 回复目标用户昵称
     */
    private String replyToUserNickname;

    /**
     * 评论状态
     */
    private CommentStatusEnum status;

    /**
     * 举报数
     */
    private Integer reportCount;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 提及的用户ID列表
     */
    private List<Long> mentionUserIds;

    /**
     * 评论图片列表
     */
    private List<String> images;

    /**
     * 扩展数据
     */
    private String extraData;

    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatus;

    /**
     * 审核原因
     */
    private String auditReason;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 评论层级（0-根评论，1-回复评论）
     */
    private Integer commentLevel;

    /**
     * 热度分数
     */
    private Double hotScore;

    /**
     * 子评论列表（用于树形结构展示）
     */
    private List<CommentUnifiedInfo> replies;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 当前用户是否已点踩
     */
    private Boolean isDisliked;

    /**
     * 当前用户是否已举报
     */
    private Boolean isReported;

    /**
     * 当前用户是否已关注评论者
     */
    private Boolean isFollowed;

    /**
     * 判断是否为根评论
     *
     * @return true如果是根评论
     */
    public boolean isRootComment() {
        return parentCommentId != null && parentCommentId == 0L;
    }

    /**
     * 判断是否为回复评论
     *
     * @return true如果是回复评论
     */
    public boolean isReplyComment() {
        return parentCommentId != null && parentCommentId > 0L;
    }

    /**
     * 判断评论是否可见
     *
     * @return true如果可见
     */
    public boolean isVisible() {
        return status != null && status.isVisible();
    }

    /**
     * 判断评论是否已删除
     *
     * @return true如果已删除
     */
    public boolean isDeleted() {
        return status != null && status.isDeleted();
    }

    /**
     * 判断审核是否通过
     *
     * @return true如果审核通过
     */
    public boolean isAuditPass() {
        return auditStatus != null && auditStatus.isPass();
    }

    /**
     * 判断是否待审核
     *
     * @return true如果待审核
     */
    public boolean isPendingAudit() {
        return auditStatus != null && auditStatus.isPending();
    }

    /**
     * 计算评论热度分数
     * 基于点赞数、回复数、置顶等因素
     *
     * @return 热度分数
     */
    public double calculateHotScore() {
        int likes = getLikeCount() != null ? getLikeCount() : 0;
        int replies = getReplyCount() != null ? getReplyCount() : 0;
        int reports = reportCount != null ? reportCount : 0;
        double quality = getQualityScore() != null ? getQualityScore() : 0.0;

        // 热度计算公式：点赞*2 + 回复*3 + 质量分数*5 + 置顶权重 + 热门权重 + 精华权重 - 举报数*2
        double score = likes * 2.0 + replies * 3.0 + quality * 5.0;
        
        if (Boolean.TRUE.equals(getIsPinned())) {
            score += 100.0;
        }
        if (Boolean.TRUE.equals(getIsHot())) {
            score += 50.0;
        }
        if (Boolean.TRUE.equals(getIsEssence())) {
            score += 30.0;
        }
        
        score -= reports * 2.0;

        // 时间衰减（创建时间越久，分数越低）
        if (getCreateTime() != null) {
            long hoursAgo = java.time.Duration.between(getCreateTime(), LocalDateTime.now()).toHours();
            score += hoursAgo * (-0.1);
        }

        return Math.max(0, score);
    }

    /**
     * 判断是否有回复
     *
     * @return true如果有回复
     */
    public boolean hasReplies() {
        return replies != null && !replies.isEmpty();
    }

    /**
     * 获取回复数量
     *
     * @return 回复数量
     */
    public int getRepliesCount() {
        return replies != null ? replies.size() : 0;
    }
} 