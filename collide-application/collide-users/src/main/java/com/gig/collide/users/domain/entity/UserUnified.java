package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.user.constant.UserRole;
import com.gig.collide.api.user.constant.UserStateEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户统一实体（去连表设计）
 * 参考Code项目设计思想，将User和UserProfile合并为单表
 * 实体内包含业务方法，遵循领域驱动设计原则
 *
 * @author GIG Team
 * @version 2.0 (重构版本)
 * @since 2024-01-01
 */
@Data
@Accessors(chain = true)
@TableName("t_user_unified")
public class UserUnified {

    // ================================ 基础信息字段 ================================
    
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
     * 用户角色
     */
    @TableField("role")
    private UserRole role;

    /**
     * 用户状态
     */
    @TableField("status")
    private UserStateEnum status;

    // ================================ 扩展信息字段（原UserProfile字段） ================================

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 性别
     */
    @TableField("gender")
    private Gender gender;

    /**
     * 所在地
     */
    @TableField("location")
    private String location;

    // ================================ 统计字段（冗余设计，避免连表统计） ================================

    /**
     * 粉丝数
     */
    @TableField("follower_count")
    private Long followerCount = 0L;

    /**
     * 关注数
     */
    @TableField("following_count")
    private Long followingCount = 0L;

    /**
     * 内容数
     */
    @TableField("content_count")
    private Long contentCount = 0L;

    /**
     * 获得点赞数
     */
    @TableField("like_count")
    private Long likeCount = 0L;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Long loginCount = 0L;

    // ================================ VIP相关字段 ================================

    /**
     * VIP过期时间
     */
    @TableField("vip_expire_time")
    private LocalDateTime vipExpireTime;

    // ================================ 博主认证字段 ================================

    /**
     * 博主认证状态
     */
    @TableField("blogger_status")
    private BloggerStatus bloggerStatus = BloggerStatus.none;

    /**
     * 博主申请时间
     */
    @TableField("blogger_apply_time")
    private LocalDateTime bloggerApplyTime;

    /**
     * 博主认证时间
     */
    @TableField("blogger_approve_time")
    private LocalDateTime bloggerApproveTime;

    // ================================ 登录相关字段 ================================

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    // ================================ 邀请相关字段 ================================

    /**
     * 邀请码
     */
    @TableField("invite_code")
    private String inviteCode;

    /**
     * 邀请人ID
     */
    @TableField("inviter_id")
    private Long inviterId;

    /**
     * 邀请人数
     */
    @TableField("invited_count")
    private Long invitedCount = 0L;

    // ================================ 系统字段 ================================

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

    /**
     * 版本号（用于乐观锁和幂等性控制）
     */
    @Version
    @TableField("version")
    private Integer version;

    // ================================ 业务方法（参考Code项目设计） ================================

    /**
     * 用户注册
     * 参考Code项目的register方法设计
     *
     * @param username 用户名
     * @param nickname 昵称
     * @param phone 手机号
     * @param email 邮箱
     * @param passwordHash 密码哈希
     * @param salt 盐值
     * @param inviteCode 邀请码
     * @param inviterId 邀请人ID
     * @return 当前实体
     */
    public UserUnified register(String username, String nickname, String phone, String email,
                               String passwordHash, String salt, String inviteCode, Long inviterId) {
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = UserRole.user;
        this.status = UserStateEnum.inactive;
        this.inviteCode = generateInviteCode();
        this.inviterId = inviterId;
        this.gender = Gender.unknown;
        
        // 初始化统计字段
        this.followerCount = 0L;
        this.followingCount = 0L;
        this.contentCount = 0L;
        this.likeCount = 0L;
        this.loginCount = 0L;
        this.invitedCount = 0L;
        
        return this;
    }

    /**
     * 激活用户账号
     *
     * @return 当前实体
     */
    public UserUnified activate() {
        if (this.status == UserStateEnum.inactive) {
            this.status = UserStateEnum.active;
        }
        return this;
    }

    /**
     * 申请博主认证
     *
     * @return 当前实体
     */
    public UserUnified applyBlogger() {
        if (this.status == UserStateEnum.active && this.bloggerStatus == BloggerStatus.none) {
            this.bloggerStatus = BloggerStatus.applying;
            this.bloggerApplyTime = LocalDateTime.now();
        }
        return this;
    }

    /**
     * 通过博主认证
     *
     * @return 当前实体
     */
    public UserUnified approveBlogger() {
        if (this.bloggerStatus == BloggerStatus.applying) {
            this.bloggerStatus = BloggerStatus.approved;
            this.bloggerApproveTime = LocalDateTime.now();
            this.role = UserRole.blogger;
        }
        return this;
    }

    /**
     * 拒绝博主认证
     *
     * @return 当前实体
     */
    public UserUnified rejectBlogger() {
        if (this.bloggerStatus == BloggerStatus.applying) {
            this.bloggerStatus = BloggerStatus.rejected;
        }
        return this;
    }

    /**
     * 升级为VIP
     *
     * @param expireTime VIP过期时间
     * @return 当前实体
     */
    public UserUnified upgradeToVip(LocalDateTime expireTime) {
        this.role = UserRole.vip;
        this.vipExpireTime = expireTime;
        return this;
    }

    /**
     * 更新最后登录时间并增加登录次数
     *
     * @return 当前实体
     */
    public UserUnified updateLastLogin() {
        this.lastLoginTime = LocalDateTime.now();
        this.loginCount++;
        return this;
    }

    /**
     * 更新个人资料
     *
     * @param nickname 昵称
     * @param avatar 头像
     * @param bio 个人简介
     * @param birthday 生日
     * @param gender 性别
     * @param location 所在地
     * @return 当前实体
     */
    public UserUnified updateProfile(String nickname, String avatar, String bio, 
                                   LocalDate birthday, Gender gender, String location) {
        if (canModifyProfile()) {
            if (nickname != null) this.nickname = nickname;
            if (avatar != null) this.avatar = avatar;
            if (bio != null) this.bio = bio;
            if (birthday != null) this.birthday = birthday;
            if (gender != null) this.gender = gender;
            if (location != null) this.location = location;
        }
        return this;
    }

    /**
     * 增加统计数据
     *
     * @param field 统计字段类型
     * @param increment 增量
     * @return 当前实体
     */
    public UserUnified incrementStatistics(StatisticsField field, long increment) {
        switch (field) {
            case FOLLOWER_COUNT -> this.followerCount += increment;
            case FOLLOWING_COUNT -> this.followingCount += increment;
            case CONTENT_COUNT -> this.contentCount += increment;
            case LIKE_COUNT -> this.likeCount += increment;
            case INVITED_COUNT -> this.invitedCount += increment;
        }
        return this;
    }

    // ================================ 业务判断方法 ================================

    /**
     * 是否可以修改个人资料
     *
     * @return true if can modify
     */
    public boolean canModifyProfile() {
        return status == UserStateEnum.active || status == UserStateEnum.inactive;
    }

    /**
     * 是否为VIP用户
     *
     * @return true if VIP and not expired
     */
    public boolean isVip() {
        return role == UserRole.vip && 
               vipExpireTime != null && 
               vipExpireTime.isAfter(LocalDateTime.now());
    }

    /**
     * 是否为认证博主
     *
     * @return true if approved blogger
     */
    public boolean isBlogger() {
        return role == UserRole.blogger && 
               bloggerStatus == BloggerStatus.approved;
    }

    /**
     * 是否可以申请博主认证
     *
     * @return true if can apply
     */
    public boolean canApplyBlogger() {
        return status == UserStateEnum.active && 
               (bloggerStatus == BloggerStatus.none || 
                (bloggerStatus == BloggerStatus.rejected && 
                 bloggerApplyTime != null && 
                 bloggerApplyTime.isBefore(LocalDateTime.now().minusDays(30))));
    }

    // ================================ 工具方法 ================================

    /**
     * 生成邀请码
     *
     * @return 8位大写字母数字邀请码
     */
    private String generateInviteCode() {
        return UUID.randomUUID().toString()
                   .replace("-", "")
                   .substring(0, 8)
                   .toUpperCase();
    }

    // ================================ 枚举定义 ================================

    /**
     * 性别枚举
     */
    public enum Gender {
        /** 男性 */
        male,
        /** 女性 */
        female,
        /** 未知 */
        unknown
    }

    /**
     * 博主认证状态枚举
     */
    public enum BloggerStatus {
        /** 无 */
        none,
        /** 申请中 */
        applying,
        /** 已通过 */
        approved,
        /** 已拒绝 */
        rejected
    }

    /**
     * 统计字段枚举
     */
    public enum StatisticsField {
        FOLLOWER_COUNT,
        FOLLOWING_COUNT,
        CONTENT_COUNT,
        LIKE_COUNT,
        INVITED_COUNT
    }
} 