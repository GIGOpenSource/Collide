package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户基础信息实体
 * 使用collide-api中的统一枚举
 *
 * @author GIG
 */
@Data
@TableName("t_user")
public class User {

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 密码哈希
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 密码盐值
     */
    @TableField("salt")
    private String salt;

    /**
     * 用户角色（使用统一枚举）
     */
    @TableField("role")
    private UserRole role;

    /**
     * 用户状态（使用统一枚举）
     */
    @TableField("status")
    private UserStateEnum status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}