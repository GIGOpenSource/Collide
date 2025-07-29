package com.gig.collide.api.follow.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 取消关注请求 - 简洁版
 * 逻辑删除，将状态更新为cancelled
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FollowDeleteRequest {

    /**
     * 关注者用户ID
     */
    @NotNull(message = "关注者ID不能为空")
    private Long followerId;

    /**
     * 被关注者用户ID
     */
    @NotNull(message = "被关注者ID不能为空")
    private Long followeeId;

    /**
     * 取消关注原因
     */
    private String cancelReason;

    /**
     * 操作人ID（一般与关注者ID相同）
     */
    private Long operatorId;
} 