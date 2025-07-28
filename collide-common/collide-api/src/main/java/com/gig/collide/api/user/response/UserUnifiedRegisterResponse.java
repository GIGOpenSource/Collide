package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.BasicUserUnifiedInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户统一注册响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserUnifiedRegisterResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 注册用户信息
     */
    private BasicUserUnifiedInfo userInfo;

    /**
     * 是否需要激活
     */
    private Boolean needActivation;

    /**
     * 激活方式（email、phone、admin）
     */
    private String activationType;

    /**
     * 提示信息
     */
    private String message;
} 