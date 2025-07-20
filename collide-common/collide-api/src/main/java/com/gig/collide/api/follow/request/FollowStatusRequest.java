package com.gig.collide.api.follow.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 关注状态查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowStatusRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 关注者用户ID
     */
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    private Long followedUserId;
} 