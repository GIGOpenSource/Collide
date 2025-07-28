package com.gig.collide.api.user.request.condition;

import lombok.*;

import java.util.List;

/**
 * 用户邀请关系-邀请层级查询条件
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
public class UserInviteRelationInviteLevelQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 邀请层级（1-直接邀请，2-二级邀请，以此类推）
     */
    private Integer inviteLevel;

    /**
     * 邀请层级列表（支持多层级查询）
     */
    private List<Integer> inviteLevels;

    /**
     * 最小邀请层级
     */
    private Integer minInviteLevel;

    /**
     * 最大邀请层级
     */
    private Integer maxInviteLevel;
} 