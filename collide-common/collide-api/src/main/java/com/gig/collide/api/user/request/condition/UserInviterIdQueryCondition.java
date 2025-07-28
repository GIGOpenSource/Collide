package com.gig.collide.api.user.request.condition;

import lombok.*;

import java.util.List;

/**
 * 邀请人ID查询条件
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
public class UserInviterIdQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 邀请人ID
     */
    private Long inviterId;

    /**
     * 邀请人ID列表（支持多邀请人查询）
     */
    private List<Long> inviterIds;
} 