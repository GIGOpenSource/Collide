package com.gig.collide.api.user.response.users.role;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户角色响应 - 对应 t_user_role 表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色记录ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色：user、blogger、admin、vip
     */
    private String role;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 角色过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否已过期
     */
    private Boolean expired;

    /**
     * 剩余天数（对于有过期时间的角色）
     */
    private Integer remainingDays;

    /**
     * 设置角色并自动计算描述
     */
    public void setRole(String role) {
        this.role = role;
        // 设置角色描述
        switch (role) {
            case "user":
                this.roleDesc = "普通用户";
                break;
            case "blogger":
                this.roleDesc = "博主";
                break;
            case "admin":
                this.roleDesc = "管理员";
                break;
            case "vip":
                this.roleDesc = "VIP用户";
                break;
            default:
                this.roleDesc = "未知角色";
        }
    }

    /**
     * 设置过期时间并自动计算是否过期和剩余天数
     */
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
        // 计算是否过期和剩余天数
        if (expireTime != null) {
            LocalDateTime now = LocalDateTime.now();
            this.expired = expireTime.isBefore(now);
            if (!expired) {
                long days = java.time.Duration.between(now, expireTime).toDays();
                this.remainingDays = (int) days;
            } else {
                this.remainingDays = 0;
            }
        } else {
            this.expired = false;
            this.remainingDays = null;
        }
    }
}