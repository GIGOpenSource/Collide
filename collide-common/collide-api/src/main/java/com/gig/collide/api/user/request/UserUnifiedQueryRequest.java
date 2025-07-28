package com.gig.collide.api.user.request;

import com.gig.collide.api.user.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

/**
 * 用户统一信息查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUnifiedQueryRequest extends BaseRequest {

    /**
     * 查询条件
     */
    private UserQueryCondition userQueryCondition;

    // ===================== 便捷构造器 =====================

    /**
     * 根据用户ID查询
     */
    public UserUnifiedQueryRequest(Long userId) {
        UserIdQueryCondition condition = new UserIdQueryCondition();
        condition.setUserId(userId);
        this.userQueryCondition = condition;
    }

    /**
     * 根据用户名查询
     */
    public UserUnifiedQueryRequest(String username) {
        UserUsernameQueryCondition condition = new UserUsernameQueryCondition();
        condition.setUsername(username);
        this.userQueryCondition = condition;
    }

    /**
     * 根据邮箱查询
     */
    public static UserUnifiedQueryRequest byEmail(String email) {
        UserUnifiedQueryRequest request = new UserUnifiedQueryRequest();
        UserEmailQueryCondition condition = new UserEmailQueryCondition();
        condition.setEmail(email);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 根据手机号查询
     */
    public static UserUnifiedQueryRequest byPhone(String phone) {
        UserUnifiedQueryRequest request = new UserUnifiedQueryRequest();
        UserPhoneQueryCondition condition = new UserPhoneQueryCondition();
        condition.setPhone(phone);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 根据邀请码查询
     */
    public static UserUnifiedQueryRequest byInviteCode(String inviteCode) {
        UserUnifiedQueryRequest request = new UserUnifiedQueryRequest();
        UserInviteCodeQueryCondition condition = new UserInviteCodeQueryCondition();
        condition.setInviteCode(inviteCode);
        request.setUserQueryCondition(condition);
        return request;
    }
} 