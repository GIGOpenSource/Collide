package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 用户激活请求
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
public class UserUnifiedActivateRequest extends BaseRequest {

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 激活码（可选）
     */
    private String activationCode;

    /**
     * 验证码（可选）
     */
    private String verificationCode;

    /**
     * 激活方式（email、phone、admin）
     */
    private String activationType = "admin";
} 