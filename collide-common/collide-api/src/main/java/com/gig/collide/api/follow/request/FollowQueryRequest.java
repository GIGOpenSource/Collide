package com.gig.collide.api.follow.request;

import com.gig.collide.api.follow.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 关注查询请求
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
public class FollowQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 查询条件
     */
    private FollowQueryCondition followQueryCondition;

    // ===================== 便捷构造器 =====================

    /**
     * 根据关注ID查询
     *
     * @param followId 关注ID
     */
    public FollowQueryRequest(Long followId) {
        FollowIdQueryCondition condition = new FollowIdQueryCondition();
        condition.setFollowId(followId);
        this.followQueryCondition = condition;
    }

    /**
     * 查询某个用户关注了哪些人
     *
     * @param followerUserId 关注者用户ID
     */
    public static FollowQueryRequest byFollowerUserId(Long followerUserId) {
        FollowQueryRequest request = new FollowQueryRequest();
        FollowUserIdQueryCondition condition = new FollowUserIdQueryCondition();
        condition.setFollowerUserId(followerUserId);
        request.setFollowQueryCondition(condition);
        return request;
    }

    /**
     * 查询哪些人关注了某个用户（粉丝列表）
     *
     * @param followedUserId 被关注者用户ID
     */
    public static FollowQueryRequest byFollowedUserId(Long followedUserId) {
        FollowQueryRequest request = new FollowQueryRequest();
        FollowedUserIdQueryCondition condition = new FollowedUserIdQueryCondition();
        condition.setFollowedUserId(followedUserId);
        request.setFollowQueryCondition(condition);
        return request;
    }

    /**
     * 查询两个用户之间的关注关系
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     */
    public static FollowQueryRequest byRelation(Long followerUserId, Long followedUserId) {
        FollowQueryRequest request = new FollowQueryRequest();
        FollowRelationQueryCondition condition = new FollowRelationQueryCondition();
        condition.setFollowerUserId(followerUserId);
        condition.setFollowedUserId(followedUserId);
        request.setFollowQueryCondition(condition);
        return request;
    }
} 