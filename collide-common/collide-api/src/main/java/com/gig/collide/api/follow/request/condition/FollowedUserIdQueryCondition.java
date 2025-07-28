package com.gig.collide.api.follow.request.condition;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按被关注者用户ID查询条件
 * 查询哪些人关注了某个用户（粉丝列表）
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
public class FollowedUserIdQueryCondition implements FollowQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 被关注者用户ID
     */
    @NotNull(message = "被关注者用户ID不能为空")
    private Long followedUserId;
} 