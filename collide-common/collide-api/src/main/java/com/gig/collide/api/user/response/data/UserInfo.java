package com.gig.collide.api.user.response.data;

import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应对象
 * 简化并现代化，移除过时的区块链相关字段
 *
 * @author Collide Team
 * @version 1.0  
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfo extends BasicUserInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 性别
     */
    private String gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 所在地
     */
    private String location;

    /**
     * 用户状态
     */
    private UserStateEnum status;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 判断用户是否为博主
     *
     * @return true if user is blogger
     */
    public boolean isBlogger() {
        return UserRole.blogger.equals(this.role);
    }

    /**
     * 判断用户是否为管理员
     *
     * @return true if user is admin
     */
    public boolean isAdmin() {
        return UserRole.admin.equals(this.role);
    }

    /**
     * 判断用户状态是否正常
     *
     * @return true if user is active
     */
    public boolean isActive() {
        return UserStateEnum.ACTIVE.equals(this.status);
    }
}
