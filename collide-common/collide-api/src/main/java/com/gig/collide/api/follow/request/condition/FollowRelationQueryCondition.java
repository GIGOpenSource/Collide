package com.gig.collide.api.follow.request.condition;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按关注关系查询条件
 * 查询两个用户之间的关注关系
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
public class FollowRelationQueryCondition implements FollowQueryCondition {

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