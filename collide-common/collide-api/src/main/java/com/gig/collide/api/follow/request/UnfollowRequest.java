package com.gig.collide.api.follow.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 取消关注请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UnfollowRequest extends BaseRequest {

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