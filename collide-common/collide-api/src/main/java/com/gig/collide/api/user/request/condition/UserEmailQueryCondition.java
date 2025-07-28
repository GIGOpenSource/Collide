package com.gig.collide.api.user.request.condition;

import lombok.*;

/**
 * 邮箱查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 邮箱地址
     */
    private String email;
} 