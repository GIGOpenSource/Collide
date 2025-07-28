package com.gig.collide.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户邀请关系信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInviteRelationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    private Long relationId;

    /**
     * 被邀请用户ID
     */
    private Long userId;

    /**
     * 被邀请用户名
     */
    private String username;

    /**
     * 被邀请用户昵称
     */
    private String nickname;

    /**
     * 被邀请用户头像
     */
    private String avatar;

    /**
     * 邀请人ID
     */
    private Long inviterId;

    /**
     * 邀请人用户名
     */
    private String inviterUsername;

    /**
     * 邀请人昵称
     */
    private String inviterNickname;

    /**
     * 使用的邀请码
     */
    private String inviteCode;

    /**
     * 邀请层级
     */
    private Integer inviteLevel;

    /**
     * 邀请时间
     */
    private LocalDateTime inviteTime;

    /**
     * 邀请状态（pending-待确认，confirmed-已确认，expired-已过期）
     */
    private String inviteStatus;

    /**
     * 邀请奖励积分
     */
    private Integer rewardPoints;

    /**
     * 是否已激活
     */
    private Boolean isActivated;
} 