package com.gig.collide.api.follow.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 关注查询响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class FollowQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 查询数据
     */
    private T data;
} 