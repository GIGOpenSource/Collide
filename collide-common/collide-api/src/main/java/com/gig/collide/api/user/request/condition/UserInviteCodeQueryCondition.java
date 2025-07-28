package com.gig.collide.api.user.request.condition;

import lombok.*;

/**
 * 邀请码查询条件
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
public class UserInviteCodeQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 邀请码
     */
    private String inviteCode;
} 