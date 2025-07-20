package com.gig.collide.api.content.response.data;

import com.gig.collide.api.content.constant.ContentReviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容审核信息
 */
@Data
public class ContentReviewInfo {

    /**
     * 审核ID
     */
    private Long reviewId;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 审核状态
     */
    private ContentReviewStatus reviewStatus;

    /**
     * 审核员用户ID
     */
    private Long reviewerUserId;

    /**
     * 审核员用户名
     */
    private String reviewerUsername;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 审核原因
     */
    private String reviewReason;

    /**
     * 违规类型
     */
    private String violationType;

    /**
     * 审核轮次
     */
    private Integer reviewRound;

    /**
     * 是否需要人工审核
     */
    private Boolean needManualReview;

    /**
     * 机器审核结果
     */
    private String aiReviewResult;

    /**
     * 机器审核置信度
     */
    private Double aiConfidence;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 提交审核时间
     */
    private LocalDateTime submitTime;

    /**
     * 开始审核时间
     */
    private LocalDateTime startTime;

    /**
     * 完成审核时间
     */
    private LocalDateTime finishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 