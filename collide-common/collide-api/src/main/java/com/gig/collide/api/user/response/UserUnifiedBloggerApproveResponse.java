package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.UserBloggerInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 博主审批响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserUnifiedBloggerApproveResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 审批后的博主信息
     */
    private UserBloggerInfo bloggerInfo;

    /**
     * 审批结果
     */
    private String approvalResult;

    /**
     * 审批时间
     */
    private String approvalTime;

    /**
     * 审批人信息
     */
    private String approverInfo;

    /**
     * 操作提示信息
     */
    private String message;
} 