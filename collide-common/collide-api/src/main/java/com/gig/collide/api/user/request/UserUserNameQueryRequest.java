package com.gig.collide.api.user.request;

import com.gig.collide.api.user.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUserNameQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private UserQueryCondition userUserNameQueryCondition;

    public UserUserNameQueryRequest(Long userId) {
        UserIdQueryCondition userIdQueryCondition = new UserIdQueryCondition();
        userIdQueryCondition.setUserId(userId);
        this.userUserNameQueryCondition = userIdQueryCondition;
    }

    public UserUserNameQueryRequest(String username) {
        UserUserNameQueryCondition condition = new UserUserNameQueryCondition();
        condition.setUserName(username);
        this.userUserNameQueryCondition = condition;
    }

    public UserUserNameQueryRequest(String username, String password) {
        UserUserNameAndPasswordQueryCondition condition = new UserUserNameAndPasswordQueryCondition();
        condition.setUserName(username);
        condition.setPassword(password);
        this.userUserNameQueryCondition = condition;
    }
}
