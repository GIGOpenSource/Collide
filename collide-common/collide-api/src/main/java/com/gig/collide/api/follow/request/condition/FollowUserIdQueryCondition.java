package com.gig.collide.api.follow.request.condition;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按关注者用户ID查询条件
 * 查询某个用户关注了哪些人
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
public class FollowUserIdQueryCondition implements FollowQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 关注者用户ID
     */
    @NotNull(message = "关注者用户ID不能为空")
    private Long followerUserId;
} 