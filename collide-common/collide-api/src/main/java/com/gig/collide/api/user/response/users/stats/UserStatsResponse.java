package com.gig.collide.api.user.response.users.stats;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户统计响应 - 对应 t_user_stats 表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 粉丝数
     */
    private Integer followerCount;

    /**
     * 关注数
     */
    private Integer followingCount;

    /**
     * 内容数
     */
    private Integer contentCount;

    /**
     * 获得点赞数
     */
    private Integer likeCount;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 活跃度评分（计算字段）
     */
    private Double activityScore;

    /**
     * 影响力评分（计算字段）
     */
    private Double influenceScore;

    /**
     * 计算活跃度评分
     * 基于登录次数、内容数、点赞数等指标
     */
    public void calculateActivityScore() {
        if (loginCount != null && contentCount != null && likeCount != null) {
            this.activityScore = (loginCount * 0.3 + contentCount * 0.5 + likeCount * 0.2);
        }
    }

    /**
     * 计算影响力评分
     * 基于粉丝数、内容数、获得点赞数等指标
     */
    public void calculateInfluenceScore() {
        if (followerCount != null && contentCount != null && likeCount != null) {
            this.influenceScore = (followerCount * 0.4 + contentCount * 0.3 + likeCount * 0.3);
        }
    }
}