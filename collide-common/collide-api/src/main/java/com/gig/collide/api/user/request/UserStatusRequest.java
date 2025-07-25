package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

/**
 * 用户状态更新请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserStatusRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean active;

    /**
     * 备注信息
     */
    private String remark;
} 