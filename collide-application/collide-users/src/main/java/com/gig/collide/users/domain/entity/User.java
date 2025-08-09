package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableField;


/**
 * 用户实体 - 简洁版
 * 基于简洁版t_user表设计
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String email;

    private String phone;

    private String passwordHash;

    private String status;

    @TableField(exist = false)
    private List<Role> roles;

    private String bio;

    private LocalDate birthday;

    private String gender;

    private String location;

    // 统计字段（冗余设计，避免连表）
    private Long followerCount;

    private Long followingCount;

    private Long contentCount;

    private Long likeCount;

    // VIP相关
    private LocalDateTime vipExpireTime;

    // 登录相关
    private LocalDateTime lastLoginTime;

    private Long loginCount;

    // 邀请相关
    private String inviteCode;

    private Long inviterId;

    private Long invitedCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 