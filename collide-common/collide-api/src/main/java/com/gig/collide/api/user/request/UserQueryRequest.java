package com.gig.collide.api.user.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest {

    /**
     * 用户名（模糊搜索）
     */
    private String username;

    /**
     * 昵称（模糊搜索）
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色：user、blogger、admin、vip
     */
    private String role;

    /**
     * 用户状态：active、inactive、suspended、banned
     */
    private String status;
} 