package com.gig.collide.api.user.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户拉黑查询请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserBlockQueryRequest extends PageRequest {

    /**
     * 拉黑者用户ID
     */
    private Long userId;

    /**
     * 被拉黑用户ID
     */
    private Long blockedUserId;

    /**
     * 拉黑者用户名（模糊搜索）
     */
    private String userUsername;

    /**
     * 被拉黑用户名（模糊搜索）
     */
    private String blockedUsername;

    /**
     * 拉黑状态：active、cancelled
     */
    private String status;
}