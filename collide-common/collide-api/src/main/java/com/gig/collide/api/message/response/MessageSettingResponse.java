package com.gig.collide.api.message.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息设置统一响应对象 - 简洁版
 * 基于message-simple.sql的t_message_setting表结构
 * 包含用户消息设置的完整信息和扩展属性
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageSettingResponse implements Serializable {

    /**
     * 设置ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否允许陌生人发消息
     */
    private Boolean allowStrangerMsg;

    /**
     * 是否自动发送已读回执
     */
    private Boolean autoReadReceipt;

    /**
     * 是否开启消息通知
     */
    private Boolean messageNotification;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 扩展字段 ===================

    /**
     * 用户昵称（冗余字段，避免连表查询）
     */
    private String userNickname;

    /**
     * 设置来源
     * default、user_init、admin_init、import
     */
    private String settingSource;

    /**
     * 最后更新操作者ID
     */
    private Long lastUpdatedBy;

    /**
     * 设置版本号（用于并发控制）
     */
    private Integer version;

    /**
     * 是否为默认设置
     */
    private Boolean isDefaultSetting;

    /**
     * 设置生效时间
     */
    private LocalDateTime effectiveTime;

    /**
     * 设置失效时间（如果有的话）
     */
    private LocalDateTime expireTime;

    /**
     * 隐私级别
     * open（开放）、normal（普通）、private（私密）、strict（严格）
     */
    private String privacyLevel;

    /**
     * 消息过滤级别
     * none（无过滤）、basic（基础过滤）、strict（严格过滤）
     */
    private String filterLevel;

    /**
     * 是否启用消息加密
     */
    private Boolean messageEncryption;

    /**
     * 通知时间段设置（JSON格式）
     */
    private String notificationSchedule;

    // =================== 业务状态方法 ===================

    /**
     * 判断是否允许陌生人发消息
     */
    public boolean isAllowStrangerMessage() {
        return Boolean.TRUE.equals(allowStrangerMsg);
    }

    /**
     * 判断是否自动已读回执
     */
    public boolean isAutoReadReceipt() {
        return Boolean.TRUE.equals(autoReadReceipt);
    }

    /**
     * 判断是否开启消息通知
     */
    public boolean isMessageNotificationEnabled() {
        return Boolean.TRUE.equals(messageNotification);
    }

    /**
     * 判断是否为默认设置
     */
    public boolean isDefaultSetting() {
        return Boolean.TRUE.equals(isDefaultSetting);
    }

    /**
     * 判断是否启用消息加密
     */
    public boolean isMessageEncryptionEnabled() {
        return Boolean.TRUE.equals(messageEncryption);
    }

    /**
     * 判断设置是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }

    /**
     * 判断设置是否生效
     */
    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        boolean afterEffective = effectiveTime == null || effectiveTime.isBefore(now) || effectiveTime.isEqual(now);
        boolean beforeExpire = expireTime == null || expireTime.isAfter(now);
        return afterEffective && beforeExpire;
    }

    /**
     * 判断是否为开放型设置
     */
    public boolean isOpenSettings() {
        return isAllowStrangerMessage() && isAutoReadReceipt() && isMessageNotificationEnabled();
    }

    /**
     * 判断是否为保守型设置
     */
    public boolean isPrivateSettings() {
        return !isAllowStrangerMessage() && !isAutoReadReceipt();
    }

    /**
     * 判断是否为严格隐私设置
     */
    public boolean isStrictPrivacySettings() {
        return "strict".equals(privacyLevel) || "private".equals(privacyLevel);
    }

    /**
     * 获取隐私级别的中文描述
     */
    public String getPrivacyLevelDescription() {
        if (privacyLevel == null) {
            return "普通";
        }
        switch (privacyLevel) {
            case "open":
                return "开放";
            case "normal":
                return "普通";
            case "private":
                return "私密";
            case "strict":
                return "严格";
            default:
                return "未知";
        }
    }

    /**
     * 获取过滤级别的中文描述
     */
    public String getFilterLevelDescription() {
        if (filterLevel == null) {
            return "无过滤";
        }
        switch (filterLevel) {
            case "none":
                return "无过滤";
            case "basic":
                return "基础过滤";
            case "strict":
                return "严格过滤";
            default:
                return "未知";
        }
    }

    /**
     * 获取设置来源的中文描述
     */
    public String getSettingSourceDescription() {
        if (settingSource == null) {
            return "默认";
        }
        switch (settingSource) {
            case "default":
                return "系统默认";
            case "user_init":
                return "用户设置";
            case "admin_init":
                return "管理员设置";
            case "import":
                return "导入设置";
            default:
                return "未知来源";
        }
    }

    /**
     * 计算设置的开放度评分（0-100）
     */
    public int getOpenessScore() {
        int score = 0;
        if (isAllowStrangerMessage()) score += 40;
        if (isAutoReadReceipt()) score += 30;
        if (isMessageNotificationEnabled()) score += 30;
        return score;
    }

    /**
     * 计算隐私保护评分（0-100）
     */
    public int getPrivacyScore() {
        int score = 0;
        if (!isAllowStrangerMessage()) score += 40;
        if (!isAutoReadReceipt()) score += 30;
        if (isMessageEncryptionEnabled()) score += 20;
        if ("strict".equals(filterLevel)) score += 10;
        return Math.min(score, 100);
    }

    /**
     * 获取设置摘要
     */
    public String getSettingSummary() {
        StringBuilder summary = new StringBuilder();
        if (isAllowStrangerMessage()) {
            summary.append("允许陌生人消息 ");
        }
        if (isAutoReadReceipt()) {
            summary.append("自动已读回执 ");
        }
        if (isMessageNotificationEnabled()) {
            summary.append("消息通知 ");
        }
        if (isMessageEncryptionEnabled()) {
            summary.append("消息加密 ");
        }
        return summary.toString().trim();
    }

    // =================== 隐私保护方法 ===================

    /**
     * 隐藏敏感设置信息（用于非本人查看）
     */
    @JsonIgnore
    public void hideSensitiveSettings() {
        this.notificationSchedule = null;
        this.lastUpdatedBy = null;
        this.version = null;
    }

    /**
     * 应用隐私过滤
     */
    @JsonIgnore
    public void applyPrivacyFilter() {
        if (isStrictPrivacySettings()) {
            hideSensitiveSettings();
        }
    }
}