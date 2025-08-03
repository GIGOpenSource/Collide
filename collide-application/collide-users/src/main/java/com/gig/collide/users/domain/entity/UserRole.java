package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gig.collide.api.user.constant.RoleStatusConstant;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户角色实体 - 对应 t_user_role 表
 * 负责用户角色权限管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_user_role")
public class UserRole {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID（关联t_user表）
     */
    private Long userId;

    /**
     * 角色类型：user、blogger、admin、vip
     */
    private String role;

    /**
     * 角色过期时间（可选，VIP角色必填）
     */
    private LocalDateTime expireTime;

    /**
     * 角色状态：active, revoked, expired
     */
    private String status;

    /**
     * 分配时间
     */
    private LocalDateTime assignTime;

    /**
     * 分配人ID
     */
    private Long assignBy;

    /**
     * 撤销时间
     */
    private LocalDateTime revokeTime;

    /**
     * 撤销人ID
     */
    private Long revokeBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.status == null) {
            this.status = RoleStatusConstant.ACTIVE;
        }
        if (this.assignTime == null) {
            this.assignTime = LocalDateTime.now();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 获取角色描述
     */
    public String getRoleDesc() {
        if (this.role == null) return "未知角色";
        switch (this.role.toLowerCase()) {
            case "user": return "普通用户";
            case "blogger": return "博主";
            case "admin": return "管理员";
            case "vip": return "VIP用户";
            default: return "未知角色";
        }
    }

    /**
     * 检查角色是否过期
     */
    public boolean isExpired() {
        if (this.expireTime == null) {
            return false; // 永久角色不过期
        }
        return LocalDateTime.now().isAfter(this.expireTime);
    }

    /**
     * 检查角色是否有效（未过期且状态为active）
     */
    public boolean isValid() {
        return RoleStatusConstant.isActiveStatus(this.status) && !isExpired();
    }

    /**
     * 检查角色是否被撤销
     */
    public boolean isRevoked() {
        return RoleStatusConstant.isRevokedStatus(this.status);
    }

    /**
     * 检查角色是否已过期状态
     */
    public boolean isExpiredStatus() {
        return RoleStatusConstant.isExpiredStatus(this.status);
    }

    /**
     * 撤销角色
     */
    public void revokeRole(Long revokeBy) {
        this.status = RoleStatusConstant.REVOKED;
        this.revokeTime = LocalDateTime.now();
        this.revokeBy = revokeBy;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 标记角色为过期
     */
    public void markExpired() {
        this.status = RoleStatusConstant.EXPIRED;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 检查是否即将过期（7天内）
     */
    public boolean isExpiringSoon() {
        if (this.expireTime == null) {
            return false;
        }
        LocalDateTime sevenDaysLater = LocalDateTime.now().plusDays(7);
        return this.expireTime.isBefore(sevenDaysLater) && !isExpired();
    }

    /**
     * 获取剩余有效天数
     */
    public Long getRemainingDays() {
        if (this.expireTime == null) {
            return null; // 永久有效
        }
        if (isExpired()) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), this.expireTime);
    }

    /**
     * 延长角色有效期
     */
    public void extendExpireTime(int days) {
        if (this.expireTime == null) {
            this.expireTime = LocalDateTime.now().plusDays(days);
        } else {
            // 如果已过期，从当前时间开始延长；否则从原过期时间延长
            LocalDateTime baseTime = isExpired() ? LocalDateTime.now() : this.expireTime;
            this.expireTime = baseTime.plusDays(days);
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 设置为永久角色
     */
    public void setPermanent() {
        this.expireTime = null;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 检查是否为管理员角色
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }

    /**
     * 检查是否为VIP角色
     */
    public boolean isVip() {
        return "vip".equalsIgnoreCase(this.role) && isValid();
    }

    /**
     * 检查是否为博主角色
     */
    public boolean isBlogger() {
        return "blogger".equalsIgnoreCase(this.role) && isValid();
    }

    /**
     * 更新角色修改时间
     */
    public void updateModifyTime() {
        this.updateTime = LocalDateTime.now();
    }
}