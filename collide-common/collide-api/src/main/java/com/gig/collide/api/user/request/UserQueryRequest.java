package com.gig.collide.api.user.request;

import com.gig.collide.api.user.request.condition.UserIdQueryCondition;
import com.gig.collide.api.user.request.condition.UserPhoneQueryCondition;
import com.gig.collide.api.user.request.condition.UserUserNameQueryCondition;
import com.gig.collide.base.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户查询请求
 * 参考nft-turbo的UserQueryRequest设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserQueryRequest extends BaseRequest {

    /**
     * 用户ID查询条件
     */
    private UserIdQueryCondition userIdQueryCondition;

    /**
     * 用户名查询条件
     */
    private UserUserNameQueryCondition userUserNameQueryCondition;

    /**
     * 手机号查询条件
     */
    private UserPhoneQueryCondition userPhoneQueryCondition;
}
