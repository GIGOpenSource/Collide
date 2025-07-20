package com.gig.collide.api.follow.request;

import com.gig.collide.api.follow.constant.FollowTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 关注用户请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

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
    private FollowTypeEnum followType = FollowTypeEnum.NORMAL;

    public FollowRequest(Long followerUserId, Long followedUserId) {
        this.followerUserId = followerUserId;
        this.followedUserId = followedUserId;
        this.followType = FollowTypeEnum.NORMAL;
    }
} 