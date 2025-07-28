package com.gig.collide.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户邀请统计信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInviteStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计时间范围开始
     */
    private LocalDateTime startTime;

    /**
     * 统计时间范围结束
     */
    private LocalDateTime endTime;

    /**
     * 总邀请人数
     */
    private Long totalInviteCount;

    /**
     * 今日邀请人数
     */
    private Long todayInviteCount;

    /**
     * 有效邀请人数（已激活）
     */
    private Long validInviteCount;

    /**
     * 邀请转化率
     */
    private Double conversionRate;

    /**
     * 按层级统计
     */
    private Map<Integer, Long> levelStats;

    /**
     * 按时间段统计（日级别）
     */
    private Map<String, Long> dailyStats;

    /**
     * 邀请排行榜
     */
    private List<UserInviteRankingInfo> rankingList;

    /**
     * 邀请链统计
     */
    private List<InviteChainInfo> inviteChainStats;

    /**
     * 邀请码使用统计
     */
    private Map<String, Long> inviteCodeStats;

    /**
     * 邀请链信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class InviteChainInfo implements Serializable {
        /**
         * 根邀请人ID
         */
        private Long rootInviterId;
        
        /**
         * 根邀请人用户名
         */
        private String rootInviterUsername;
        
        /**
         * 链长度
         */
        private Integer chainLength;
        
        /**
         * 链中总人数
         */
        private Integer totalUsers;
        
        /**
         * 链中活跃用户数
         */
        private Integer activeUsers;
    }
} 