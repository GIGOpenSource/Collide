package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 博主审批请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUnifiedBloggerApproveRequest extends BaseRequest {

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 审批结果（必填）：approved-通过，rejected-拒绝
     */
    @NotNull(message = "审批结果不能为空")
    private String approvalResult;

    /**
     * 审批意见（可选）
     */
    @Size(max = 500, message = "审批意见长度不能超过500个字符")
    private String approvalComment;

    /**
     * 审批人ID（必填）
     */
    @NotNull(message = "审批人ID不能为空")
    private Long approverId;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;
} 