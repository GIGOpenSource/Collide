package com.gig.collide.api.follow.response;

import com.gig.collide.api.follow.constant.FollowStatusEnum;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 关注状态响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class FollowStatusResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 关注状态
     */
    private FollowStatusEnum followStatus;

    /**
     * 是否互相关注
     */
    private Boolean isMutualFollow;

    public FollowStatusResponse() {}

    public FollowStatusResponse(FollowStatusEnum followStatus) {
        this.followStatus = followStatus;
        this.isMutualFollow = followStatus == FollowStatusEnum.MUTUAL_FOLLOWED;
    }
} 