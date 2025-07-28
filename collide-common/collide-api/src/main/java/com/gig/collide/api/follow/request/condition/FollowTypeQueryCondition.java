package com.gig.collide.api.follow.request.condition;

import com.gig.collide.api.follow.constant.FollowType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 按关注类型查询条件
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
public class FollowTypeQueryCondition implements FollowQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 关注类型
     */
    @NotNull(message = "关注类型不能为空")
    private FollowType followType;

    /**
     * 可选：关注者用户ID（配合类型查询）
     */
    private Long followerUserId;

    /**
     * 可选：被关注者用户ID（配合类型查询）
     */
    private Long followedUserId;
} 