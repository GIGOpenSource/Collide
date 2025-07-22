package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 用户激活请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserActiveRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 激活码
     */
    @NotBlank(message = "激活码不能为空")
    private String activationCode;

    /**
     * 激活类型：EMAIL-邮箱激活，SMS-短信激活
     */
    private String activationType = "EMAIL";

    /**
     * 备注信息
     */
    private String remark;
}
