package com.gig.collide.api.message.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 消息设置创建请求 - 简洁版
 * 基于message-simple.sql的t_message_setting表设计
 * 用于创建或初始化用户的消息偏好设置
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
public class MessageSettingCreateRequest implements Serializable {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
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
     * 设置来源（用于追踪设置的创建来源）
     * default、user_init、admin_init、import
     */
    private String settingSource;

    /**
     * 是否使用系统默认设置
     */
    private Boolean useSystemDefault;

    // =================== 业务验证方法 ===================

    /**
     * 验证用户ID的有效性
     */
    public boolean isValidUserId() {
        return userId != null && userId > 0;
    }

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
     * 判断是否使用系统默认设置
     */
    public boolean shouldUseSystemDefault() {
        return Boolean.TRUE.equals(useSystemDefault);
    }

    /**
     * 判断是否为默认设置来源
     */
    public boolean isDefaultSource() {
        return "default".equals(settingSource);
    }

    /**
     * 判断是否为用户主动初始化
     */
    public boolean isUserInitSource() {
        return "user_init".equals(settingSource);
    }

    /**
     * 判断是否为管理员初始化
     */
    public boolean isAdminInitSource() {
        return "admin_init".equals(settingSource);
    }

    /**
     * 判断是否为导入设置
     */
    public boolean isImportSource() {
        return "import".equals(settingSource);
    }

    /**
     * 设置默认值
     */
    public void initDefaults() {
        if (this.allowStrangerMsg == null) {
            this.allowStrangerMsg = true; // 默认允许陌生人发消息
        }
        if (this.autoReadReceipt == null) {
            this.autoReadReceipt = true; // 默认开启自动已读回执
        }
        if (this.messageNotification == null) {
            this.messageNotification = true; // 默认开启消息通知
        }
        if (this.settingSource == null) {
            this.settingSource = "default";
        }
        if (this.useSystemDefault == null) {
            this.useSystemDefault = false;
        }
    }

    /**
     * 应用系统默认设置
     */
    public void applySystemDefaults() {
        this.allowStrangerMsg = true;
        this.autoReadReceipt = true;
        this.messageNotification = true;
        this.settingSource = "default";
        this.useSystemDefault = true;
    }

    /**
     * 应用推荐设置（安全优先）
     */
    public void applySecureDefaults() {
        this.allowStrangerMsg = false; // 安全起见，不允许陌生人发消息
        this.autoReadReceipt = false; // 保护隐私，不自动发送已读回执
        this.messageNotification = true; // 保持通知开启
        this.settingSource = "default";
    }

    /**
     * 从模板用户复制设置
     */
    public void copyFromTemplate(MessageSettingCreateRequest template) {
        if (template != null) {
            this.allowStrangerMsg = template.allowStrangerMsg;
            this.autoReadReceipt = template.autoReadReceipt;
            this.messageNotification = template.messageNotification;
            this.settingSource = "import";
        }
    }

    /**
     * 验证请求的有效性
     */
    public boolean isValidRequest() {
        return isValidUserId() && settingSource != null;
    }

    /**
     * 检查设置是否为开放型（对陌生人友好）
     */
    public boolean isOpenSettings() {
        return isAllowStrangerMessage() && isAutoReadReceipt() && isMessageNotificationEnabled();
    }

    /**
     * 检查设置是否为保守型（隐私优先）
     */
    public boolean isPrivateSettings() {
        return !isAllowStrangerMessage() && !isAutoReadReceipt();
    }
}