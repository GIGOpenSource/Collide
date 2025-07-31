package com.gig.collide.api.user.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户拉黑响应 - 简洁版
 * 支持Dubbo序列化传输
 * 
 * @author GIG Team
 * @version 2.0.0 (支持Dubbo序列化)
 */
@Data
public class UserBlockResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 拉黑者用户ID
     */
    private Long userId;

    /**
     * 被拉黑用户ID
     */
    private Long blockedUserId;

    /**
     * 拉黑者用户名
     */
    private String userUsername;

    /**
     * 被拉黑用户名
     */
    private String blockedUsername;

    /**
     * 拉黑状态：active、cancelled
     */
    private String status;

    /**
     * 拉黑原因
     */
    private String reason;

    /**
     * 拉黑时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}