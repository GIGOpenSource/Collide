package com.gig.collide.api.content.request;

import com.gig.collide.api.content.constant.ContentReviewStatus;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 内容审核请求
 */
@Data
public class ContentReviewRequest {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 审核状态
     */
    @NotNull(message = "审核状态不能为空")
    private ContentReviewStatus reviewStatus;

    /**
     * 审核意见
     */
    @Size(max = 500, message = "审核意见长度不能超过500字符")
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
     * 是否需要重审
     */
    private Boolean needReReview = false;
} 