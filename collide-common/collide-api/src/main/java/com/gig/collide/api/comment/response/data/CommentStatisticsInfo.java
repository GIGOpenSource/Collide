package com.gig.collide.api.comment.response.data;

import com.gig.collide.api.comment.enums.CommentTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论统计信息
 * 包含评论的各项统计数据
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 评论类型
     */
    private CommentTypeEnum commentType;

    /**
     * 总评论数
     */
    private Long totalCount;

    /**
     * 根评论数
     */
    private Long rootCount;

    /**
     * 回复评论数
     */
    private Long replyCount;

    /**
     * 今日评论数
     */
    private Long todayCount;

    /**
     * 热门评论数
     */
    private Long hotCount;

    /**
     * 精华评论数
     */
    private Long essenceCount;

    /**
     * 置顶评论数
     */
    private Long pinnedCount;

    /**
     * 待审核评论数
     */
    private Long pendingAuditCount;

    /**
     * 已删除评论数
     */
    private Long deletedCount;

    /**
     * 举报评论数
     */
    private Long reportedCount;

    /**
     * 总点赞数
     */
    private Long totalLikes;

    /**
     * 总点踩数
     */
    private Long totalDislikes;

    /**
     * 总举报数
     */
    private Long totalReports;

    /**
     * 最后评论时间
     */
    private LocalDateTime lastCommentTime;

    /**
     * 最后评论用户ID
     */
    private Long lastCommentUserId;

    /**
     * 最后评论用户昵称
     */
    private String lastCommentUserNickname;

    /**
     * 平均质量分数
     */
    private Double averageQualityScore;

    /**
     * 活跃度分数
     */
    private Double activityScore;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 计算评论活跃度
     *
     * @return 活跃度分数
     */
    public double calculateActivityScore() {
        long total = totalCount != null ? totalCount : 0;
        long today = todayCount != null ? todayCount : 0;
        long hot = hotCount != null ? hotCount : 0;

        if (total == 0) {
            return 0.0;
        }

        // 活跃度计算：今日评论占比 + 热门评论占比
        double todayRatio = (double) today / total;
        double hotRatio = (double) hot / total;
        
        return (todayRatio * 0.6 + hotRatio * 0.4) * 100;
    }

    /**
     * 计算评论质量指标
     *
     * @return 质量指标分数
     */
    public double calculateQualityScore() {
        long total = totalCount != null ? totalCount : 0;
        long reported = reportedCount != null ? reportedCount : 0;
        long deleted = deletedCount != null ? deletedCount : 0;
        double avgQuality = averageQualityScore != null ? averageQualityScore : 0.0;

        if (total == 0) {
            return avgQuality;
        }

        // 质量分数 = 平均质量分数 - 举报率*20 - 删除率*30
        double reportRate = (double) reported / total;
        double deleteRate = (double) deleted / total;
        
        return Math.max(0, avgQuality - reportRate * 20 - deleteRate * 30);
    }

    /**
     * 计算互动率
     *
     * @return 互动率（百分比）
     */
    public double calculateEngagementRate() {
        long total = totalCount != null ? totalCount : 0;
        long likes = totalLikes != null ? totalLikes : 0;
        long dislikes = totalDislikes != null ? totalDislikes : 0;

        if (total == 0) {
            return 0.0;
        }

        long totalEngagement = likes + dislikes;
        return (double) totalEngagement / total * 100;
    }

    /**
     * 计算点赞率
     *
     * @return 点赞率（百分比）
     */
    public double calculateLikeRate() {
        long total = totalCount != null ? totalCount : 0;
        long likes = totalLikes != null ? totalLikes : 0;

        if (total == 0) {
            return 0.0;
        }

        return (double) likes / total * 100;
    }

    /**
     * 计算举报率
     *
     * @return 举报率（百分比）
     */
    public double calculateReportRate() {
        long total = totalCount != null ? totalCount : 0;
        long reports = totalReports != null ? totalReports : 0;

        if (total == 0) {
            return 0.0;
        }

        return (double) reports / total * 100;
    }

    /**
     * 判断是否有评论
     *
     * @return true如果有评论
     */
    public boolean hasComments() {
        return totalCount != null && totalCount > 0;
    }

    /**
     * 判断是否活跃
     *
     * @return true如果今日有评论
     */
    public boolean isActive() {
        return todayCount != null && todayCount > 0;
    }
} 