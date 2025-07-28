package com.gig.collide.api.user.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 博主申请响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserUnifiedBloggerApplyResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    private Long applyId;

    /**
     * 申请状态（applying-申请中，approved-已通过，rejected-已拒绝）
     */
    private String applyStatus;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 预计审核时间（工作日）
     */
    private Integer estimatedReviewDays;

    /**
     * 操作提示信息
     */
    private String message;
} 