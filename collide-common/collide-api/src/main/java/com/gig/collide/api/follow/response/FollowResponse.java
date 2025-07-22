package com.gig.collide.api.follow.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 关注操作响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class FollowResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
     */
    private Long followId;

    /**
     * 是否为新的关注关系
     */
    private Boolean newFollow;

    /**
     * 是否形成相互关注
     */
    private Boolean mutualFollow;
} 