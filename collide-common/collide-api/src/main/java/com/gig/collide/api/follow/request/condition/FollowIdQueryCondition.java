package com.gig.collide.api.follow.request.condition;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按关注ID查询条件
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
public class FollowIdQueryCondition implements FollowQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
     */
    @NotNull(message = "关注ID不能为空")
    private Long followId;
} 