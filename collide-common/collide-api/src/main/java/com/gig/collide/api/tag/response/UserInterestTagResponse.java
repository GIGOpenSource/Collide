package com.gig.collide.api.tag.response;

import com.gig.collide.api.tag.response.data.UserInterestTagInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户兴趣标签响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserInterestTagResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 兴趣标签信息
     */
    private UserInterestTagInfo interestTagInfo;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 兴趣分数变化
     */
    private String scoreChange;

    /**
     * 操作提示信息
     */
    private String message;
} 