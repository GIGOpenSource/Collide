package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 博主申请请求
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
public class UserUnifiedBloggerApplyRequest extends BaseRequest {

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 申请理由（可选）
     */
    @Size(max = 1000, message = "申请理由长度不能超过1000个字符")
    private String applyReason;

    /**
     * 个人作品链接（可选）
     */
    private String portfolioUrl;

    /**
     * 专业领域（可选）
     */
    @Size(max = 100, message = "专业领域长度不能超过100个字符")
    private String speciality;

    /**
     * 社交媒体链接（可选）
     */
    private String socialMediaUrl;
} 