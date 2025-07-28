package com.gig.collide.api.follow.request.condition;

import com.gig.collide.api.follow.constant.FollowStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按关注状态查询条件
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatusQueryCondition implements FollowQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 关注状态
     */
    @NotNull(message = "关注状态不能为空")
    private FollowStatus status;

    /**
     * 可选：关注者用户ID（配合状态查询）
     */
    private Long followerUserId;

    /**
     * 可选：被关注者用户ID（配合状态查询）
     */
    private Long followedUserId;
} 