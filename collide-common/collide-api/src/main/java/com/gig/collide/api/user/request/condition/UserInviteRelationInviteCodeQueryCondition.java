package com.gig.collide.api.user.request.condition;

import lombok.*;

import java.util.List;

/**
 * 用户邀请关系-邀请码查询条件
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
public class UserInviteRelationInviteCodeQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请码列表（支持多邀请码查询）
     */
    private List<String> inviteCodes;

    /**
     * 邀请码模糊匹配
     */
    private String inviteCodeLike;
} 