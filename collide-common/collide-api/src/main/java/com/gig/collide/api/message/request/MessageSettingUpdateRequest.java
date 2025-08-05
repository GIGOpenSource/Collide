package com.gig.collide.api.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 消息设置更新请求 - 简洁版
 * 支持单项或批量更新用户的消息设置
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
public class MessageSettingUpdateRequest implements Serializable {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 操作用户ID（用于权限验证，通常与userId相同）
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorId;

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
     * 更新类型
     * stranger_msg、read_receipt、notification、batch_update、reset_default
     */
    @NotBlank(message = "更新类型不能为空")
    private String updateType;

    /**
     * 更新原因（可选，用于审计）
     */
    private String updateReason;

    // =================== 业务验证方法 ===================

    /**
     * 判断是否为陌生人消息设置更新
     */
    public boolean isStrangerMessageUpdate() {
        return "stranger_msg".equals(updateType);
    }

    /**
     * 判断是否为已读回执设置更新
     */
    public boolean isReadReceiptUpdate() {
        return "read_receipt".equals(updateType);
    }

    /**
     * 判断是否为消息通知设置更新
     */
    public boolean isNotificationUpdate() {
        return "notification".equals(updateType);
    }

    /**
     * 判断是否为批量更新
     */
    public boolean isBatchUpdate() {
        return "batch_update".equals(updateType);
    }

    /**
     * 判断是否为重置为默认设置
     */
    public boolean isResetToDefault() {
        return "reset_default".equals(updateType);
    }

    /**
     * 验证操作权限
     */
    public boolean hasValidPermission() {
        // 只允许用户自己或系统管理员修改设置
        return userId != null && operatorId != null && userId.equals(operatorId);
    }

    /**
     * 验证更新请求的有效性
     */
    public boolean isValidUpdate() {
        if (!hasValidPermission()) {
            return false;
        }

        switch (updateType) {
            case "stranger_msg":
                return allowStrangerMsg != null;
            case "read_receipt":
                return autoReadReceipt != null;
            case "notification":
                return messageNotification != null;
            case "batch_update":
                return hasAnySettingValue();
            case "reset_default":
                return true; // 重置不需要额外参数
            default:
                return false;
        }
    }

    /**
     * 检查是否有任何设置值
     */
    private boolean hasAnySettingValue() {
        return allowStrangerMsg != null || autoReadReceipt != null || messageNotification != null;
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
     * 创建单项更新请求
     */
    public static MessageSettingUpdateRequest createStrangerMsgUpdate(Long userId, Boolean allow, String reason) {
        MessageSettingUpdateRequest request = new MessageSettingUpdateRequest();
        request.setUserId(userId);
        request.setOperatorId(userId);
        request.setAllowStrangerMsg(allow);
        request.setUpdateType("stranger_msg");
        request.setUpdateReason(reason);
        return request;
    }

    /**
     * 创建已读回执更新请求
     */
    public static MessageSettingUpdateRequest createReadReceiptUpdate(Long userId, Boolean auto, String reason) {
        MessageSettingUpdateRequest request = new MessageSettingUpdateRequest();
        request.setUserId(userId);
        request.setOperatorId(userId);
        request.setAutoReadReceipt(auto);
        request.setUpdateType("read_receipt");
        request.setUpdateReason(reason);
        return request;
    }

    /**
     * 创建通知设置更新请求
     */
    public static MessageSettingUpdateRequest createNotificationUpdate(Long userId, Boolean enable, String reason) {
        MessageSettingUpdateRequest request = new MessageSettingUpdateRequest();
        request.setUserId(userId);
        request.setOperatorId(userId);
        request.setMessageNotification(enable);
        request.setUpdateType("notification");
        request.setUpdateReason(reason);
        return request;
    }

    /**
     * 创建批量更新请求
     */
    public static MessageSettingUpdateRequest createBatchUpdate(Long userId, Boolean allowStranger, 
                                                              Boolean autoReceipt, Boolean notification, String reason) {
        MessageSettingUpdateRequest request = new MessageSettingUpdateRequest();
        request.setUserId(userId);
        request.setOperatorId(userId);
        request.setAllowStrangerMsg(allowStranger);
        request.setAutoReadReceipt(autoReceipt);
        request.setMessageNotification(notification);
        request.setUpdateType("batch_update");
        request.setUpdateReason(reason);
        return request;
    }

    /**
     * 创建重置为默认设置的请求
     */
    public static MessageSettingUpdateRequest createResetToDefault(Long userId, String reason) {
        MessageSettingUpdateRequest request = new MessageSettingUpdateRequest();
        request.setUserId(userId);
        request.setOperatorId(userId);
        request.setUpdateType("reset_default");
        request.setUpdateReason(reason);
        return request;
    }

    /**
     * 获取更新的设置项数量
     */
    public int getUpdateCount() {
        int count = 0;
        if (allowStrangerMsg != null) count++;
        if (autoReadReceipt != null) count++;
        if (messageNotification != null) count++;
        return count;
    }
}