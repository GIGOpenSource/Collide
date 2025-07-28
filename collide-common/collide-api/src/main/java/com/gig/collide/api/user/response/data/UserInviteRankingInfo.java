package com.gig.collide.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户邀请排行信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInviteRankingInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（脱敏）
     */
    private String username;

    /**
     * 昵称（脱敏）
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 直接邀请人数
     */
    private Integer directInviteCount;

    /**
     * 总邀请人数（包含间接邀请）
     */
    private Integer totalInviteCount;

    /**
     * 邀请积分
     */
    private Integer invitePoints;

    /**
     * 最近邀请时间
     */
    private LocalDateTime lastInviteTime;

    /**
     * 是否为VIP
     */
    private Boolean isVip;

    /**
     * VIP等级
     */
    private Integer vipLevel;
} 