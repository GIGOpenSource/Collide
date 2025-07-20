package com.gig.collide.api.follow.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 关注操作响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class FollowOperatorResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 操作时间
     */
    private Long timestamp;

    /**
     * 消息
     */
    private String message;

    public FollowOperatorResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public FollowOperatorResponse(Boolean success) {
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }
}