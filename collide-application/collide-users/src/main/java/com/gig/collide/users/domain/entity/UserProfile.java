package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户资料实体 - 对应 t_user_profile 表
 * 负责用户个人资料信息管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_user_profile")
public class UserProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关联t_user表）
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 性别：0-unknown, 1-male, 2-female
     */
    private Integer gender;

    /**
     * 所在地
     */
    private String location;

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
        if (this.gender == null) {
            this.gender = 0; // 默认未知
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 获取性别描述
     */
    public String getGenderDesc() {
        if (this.gender == null) return "未知";
        switch (this.gender) {
            case 0: return "未知";
            case 1: return "男";
            case 2: return "女";
            default: return "未知";
        }
    }

    /**
     * 检查是否有完整资料
     */
    public boolean hasCompleteProfile() {
        return nickname != null && !nickname.trim().isEmpty() &&
               avatar != null && !avatar.trim().isEmpty() &&
               gender != null && gender >= 0;
    }

    /**
     * 检查是否为男性
     */
    public boolean isMale() {
        return Integer.valueOf(1).equals(this.gender);
    }

    /**
     * 检查是否为女性
     */
    public boolean isFemale() {
        return Integer.valueOf(2).equals(this.gender);
    }

    /**
     * 更新资料修改时间
     */
    public void updateModifyTime() {
        this.updateTime = LocalDateTime.now();
    }
}