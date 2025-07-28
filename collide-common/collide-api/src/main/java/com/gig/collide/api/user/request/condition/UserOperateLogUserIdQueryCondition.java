package com.gig.collide.api.user.request.condition;

import lombok.*;

import java.util.List;

/**
 * 用户操作日志-用户ID查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserOperateLogUserIdQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户ID列表（支持多用户查询）
     */
    private List<Long> userIds;
} 