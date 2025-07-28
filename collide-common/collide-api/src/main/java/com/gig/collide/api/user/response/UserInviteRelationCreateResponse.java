package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.UserInviteRelationInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户邀请关系创建响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserInviteRelationCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 创建的邀请关系信息
     */
    private UserInviteRelationInfo relationInfo;

    /**
     * 关系ID
     */
    private Long relationId;

    /**
     * 奖励积分
     */
    private Integer rewardPoints;

    /**
     * 邀请链长度
     */
    private Integer chainLength;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 操作提示信息
     */
    private String message;
} 