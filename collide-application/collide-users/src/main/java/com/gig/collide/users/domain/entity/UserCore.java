package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户核心实体 - 对应 t_user 表
 * 负责用户基础信息和认证相关功能
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_user")
public class UserCore {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 邮箱（唯一）
     */
    private String email;

    /**
     * 手机号（唯一）
     */  
    private String phone;

    /**
     * 密码哈希
     */
    private String passwordHash;

    /**
     * 用户状态：1-active, 2-inactive, 3-suspended, 4-banned
     */
    private Integer status;

    /**
     * 邀请码（用户专属）
     */
    private String inviteCode;

    /**
     * 邀请人ID
     */
    private Long inviterId;

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
            this.status = 1; // 默认正常状态
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 检查用户是否正常状态（active）
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 检查用户是否未激活（inactive）
     */
    public boolean isInactive() {
        return Integer.valueOf(2).equals(this.status);
    }

    /**
     * 检查用户是否被暂停（suspended）
     */
    public boolean isSuspended() {
        return Integer.valueOf(3).equals(this.status);
    }

    /**
     * 检查用户是否被封禁（banned）
     */
    public boolean isBanned() {
        return Integer.valueOf(4).equals(this.status);
    }

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (this.status == null) return "未知";
        switch (this.status) {
            case 1: return "正常";
            case 2: return "未激活";  
            case 3: return "暂停";
            case 4: return "封禁";
            default: return "未知";
        }
    }

    public void updateModifyTime(){
        this.updateTime = LocalDateTime.now();
    }
}