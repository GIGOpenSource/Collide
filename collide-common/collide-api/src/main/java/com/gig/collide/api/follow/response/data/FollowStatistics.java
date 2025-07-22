package com.gig.collide.api.follow.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注统计信息响应对象
 * 对应 t_follow_statistics 表结构
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class FollowStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关注数（我关注的人数）
     */
    private Integer followingCount;

    /**
     * 粉丝数（关注我的人数）
     */
    private Integer followerCount;

    /**
     * 相互关注数（互关的人数）
     */
    private Integer mutualFollowCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 计算粉丝关注比
     *
     * @return 粉丝关注比 (followerCount / followingCount)
     */
    public double getFollowerRatio() {
        if (followingCount == null || followingCount == 0) {
            return followerCount == null ? 0.0 : followerCount.doubleValue();
        }
        return (double) followerCount / followingCount;
    }

    /**
     * 判断是否为热门用户（粉丝数超过关注数的2倍）
     *
     * @return true if popular user
     */
    public boolean isPopularUser() {
        return followerCount != null && followingCount != null 
            && followerCount >= followingCount * 2;
    }
} 