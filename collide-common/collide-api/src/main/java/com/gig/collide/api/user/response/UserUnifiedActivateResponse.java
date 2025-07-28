package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.BasicUserUnifiedInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户激活响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserUnifiedActivateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 激活后的用户信息
     */
    private BasicUserUnifiedInfo userInfo;

    /**
     * 激活状态
     */
    private Boolean activated;

    /**
     * 激活时间
     */
    private String activateTime;

    /**
     * 奖励积分
     */
    private Integer rewardPoints;

    /**
     * 操作提示信息
     */
    private String message;
} 