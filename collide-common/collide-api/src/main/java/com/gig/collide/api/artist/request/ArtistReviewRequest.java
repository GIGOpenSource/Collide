package com.gig.collide.api.artist.request;

import com.gig.collide.api.artist.constant.ArtistReviewResult;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 博主审核请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArtistReviewRequest extends BaseRequest {

    /**
     * 申请ID
     */
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    /**
     * 审核员ID
     */
    @NotNull(message = "审核员ID不能为空")
    private Long reviewerId;

    /**
     * 审核结果
     */
    @NotNull(message = "审核结果不能为空")
    private ArtistReviewResult reviewResult;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 审核评分（1-10分）
     */
    private Integer reviewScore;

    /**
     * 风险评估
     */
    private String riskAssessment;

    /**
     * 建议等级
     */
    private String suggestedLevel;

    /**
     * 内部备注
     */
    private String internalNote;
} 