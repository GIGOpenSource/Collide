package com.gig.collide.api.user.request.condition;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUserNameAndPasswordQueryCondition implements UserQueryCondition{


    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;
}
