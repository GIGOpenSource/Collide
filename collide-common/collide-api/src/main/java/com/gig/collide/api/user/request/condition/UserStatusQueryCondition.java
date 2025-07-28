package com.gig.collide.api.user.request.condition;

import com.gig.collide.api.user.constant.UserStateEnum;
import lombok.*;

import java.util.List;

/**
 * 用户状态查询条件
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
public class UserStatusQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户状态
     */
    private UserStateEnum status;

    /**
     * 用户状态列表（支持多状态查询）
     */
    private List<UserStateEnum> statuses;
} 