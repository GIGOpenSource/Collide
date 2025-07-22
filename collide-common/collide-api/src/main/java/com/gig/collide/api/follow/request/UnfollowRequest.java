package com.gig.collide.api.follow.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 取消关注请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UnfollowRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关注者用户ID
     */
    @NotNull(message = "关注者用户ID不能为空")
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    @NotNull(message = "被关注者用户ID不能为空")
    private Long followedUserId;
} 