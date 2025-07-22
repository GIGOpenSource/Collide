package com.gig.collide.api.follow.request;

import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 关注查询请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class FollowQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（用于查询该用户的关注关系）
     */
    private Long userId;

    /**
     * 关注者用户ID
     */
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    private Long followedUserId;

    /**
     * 关注类型
     */
    private FollowType followType;

    /**
     * 关注状态
     */
    private FollowStatus status = FollowStatus.NORMAL;

    /**
     * 批量用户ID列表
     */
    private List<Long> userIds;

    /**
     * 页码，从1开始
     */
    private Integer pageNo = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 是否包含用户详情
     */
    private Boolean includeUserInfo = true;

    /**
     * 是否检查相互关注状态
     */
    private Boolean checkMutualFollow = false;
} 