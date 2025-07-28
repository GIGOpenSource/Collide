package com.gig.collide.api.follow.response.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注统计信息传输对象
 * 对应 t_follow_statistics 表结构
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Schema(description = "关注统计信息")
public class FollowStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 关注数（我关注的人数）
     */
    @Schema(description = "关注数")
    private Integer followingCount;

    /**
     * 粉丝数（关注我的人数）
     */
    @Schema(description = "粉丝数")
    private Integer followerCount;

    /**
     * 相互关注数
     */
    @Schema(description = "相互关注数")
    private Integer mutualFollowCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedTime;

    /**
     * 逻辑删除标记
     */
    @Schema(description = "是否已删除")
    private Boolean deleted;

    // ===================== 业务方法 =====================

    /**
     * 获取关注比率（关注数/粉丝数）
     *
     * @return 关注比率，粉丝数为0时返回null
     */
    public Double getFollowingRatio() {
        if (followerCount == null || followerCount == 0) {
            return null;
        }
        return followingCount != null ? (double) followingCount / followerCount : null;
    }

    /**
     * 获取粉丝比率（粉丝数/关注数）
     *
     * @return 粉丝比率，关注数为0时返回null
     */
    public Double getFollowerRatio() {
        if (followingCount == null || followingCount == 0) {
            return null;
        }
        return followerCount != null ? (double) followerCount / followingCount : null;
    }

    /**
     * 获取相互关注比率（相互关注数/关注数）
     *
     * @return 相互关注比率
     */
    public Double getMutualFollowRatio() {
        if (followingCount == null || followingCount == 0) {
            return null;
        }
        return mutualFollowCount != null ? (double) mutualFollowCount / followingCount : null;
    }

    /**
     * 是否为活跃用户（关注数或粉丝数大于100）
     *
     * @return true-活跃用户，false-普通用户
     */
    public boolean isActiveUser() {
        int following = followingCount != null ? followingCount : 0;
        int follower = followerCount != null ? followerCount : 0;
        return following > 100 || follower > 100;
    }

    /**
     * 是否为热门用户（粉丝数大于1000）
     *
     * @return true-热门用户，false-普通用户
     */
    public boolean isPopularUser() {
        return followerCount != null && followerCount > 1000;
    }

    /**
     * 是否为大V用户（粉丝数大于10000）
     *
     * @return true-大V用户，false-普通用户
     */
    public boolean isBigVUser() {
        return followerCount != null && followerCount > 10000;
    }

    /**
     * 获取用户等级（基于粉丝数）
     *
     * @return 用户等级描述
     */
    public String getUserLevel() {
        if (followerCount == null) {
            return "新用户";
        }
        if (followerCount >= 10000) {
            return "大V用户";
        } else if (followerCount >= 1000) {
            return "活跃用户";
        } else if (followerCount >= 100) {
            return "普通用户";
        } else {
            return "新用户";
        }
    }

    // ===================== 静态工厂方法 =====================

    /**
     * 创建空统计信息
     *
     * @param userId 用户ID
     * @return 空统计信息
     */
    public static FollowStatisticsInfo createEmpty(Long userId) {
        FollowStatisticsInfo info = new FollowStatisticsInfo();
        info.setUserId(userId);
        info.setFollowingCount(0);
        info.setFollowerCount(0);
        info.setMutualFollowCount(0);
        info.setDeleted(false);
        return info;
    }
} 