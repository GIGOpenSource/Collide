package com.gig.collide.api.user.request.users.stats;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户统计查询请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 粉丝数最小�?
     */
    private Integer followerCountMin;

    /**
     * 粉丝数最大�?
     */
    private Integer followerCountMax;

    /**
     * 关注数最小�?
     */
    private Integer followingCountMin;

    /**
     * 关注数最大�?
     */
    private Integer followingCountMax;

    /**
     * 内容数最小�?
     */
    private Integer contentCountMin;

    /**
     * 内容数最大�?
     */
    private Integer contentCountMax;

    /**
     * 点赞数最小�?
     */
    private Integer likeCountMin;

    /**
     * 点赞数最大�?
     */
    private Integer likeCountMax;

    /**
     * 登录次数最小�?
     */
    private Integer loginCountMin;

    /**
     * 登录次数最大�?
     */
    private Integer loginCountMax;

    /**
     * 排序字段：follower_count、following_count、content_count、like_count、login_count
     */
    private String sortField = "follower_count";

    /**
     * 排序方向：asc、desc
     */
    private String sortDirection = "desc";

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 20;

}
