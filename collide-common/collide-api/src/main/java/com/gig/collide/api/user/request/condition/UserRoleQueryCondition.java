package com.gig.collide.api.user.request.condition;

import com.gig.collide.api.user.constant.UserRole;
import lombok.*;

import java.util.List;

/**
 * 用户角色查询条件
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
public class UserRoleQueryCondition implements UserQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 用户角色列表（支持多角色查询）
     */
    private List<UserRole> roles;
} 