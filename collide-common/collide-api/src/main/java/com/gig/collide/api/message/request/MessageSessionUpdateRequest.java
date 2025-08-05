package com.gig.collide.api.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息会话更新请求 - 简洁版
 * 支持更新会话的各种状态信息
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
public class MessageSessionUpdateRequest implements Serializable {

    /**
     * 会话ID（可选，如果提供userId和otherUserId则可为空）
     */
    private Long sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 对方用户ID
     */
    private Long otherUserId;

    /**
     * 操作用户ID（用于权限验证）
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorId;

    /**
     * 最后一条消息ID
     */
    private Long lastMessageId;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 是否归档
     */
    private Boolean isArchived;

    /**
     * 更新类型
     * last_message、unread_count、archive_status、clear_unread、increment_unread
     */
    @NotBlank(message = "更新类型不能为空")
    private String updateType;

    // =================== 业务验证方法 ===================

    /**
     * 判断是否为最后消息更新
     */
    public boolean isLastMessageUpdate() {
        return "last_message".equals(updateType);
    }

    /**
     * 判断是否为未读数更新
     */
    public boolean isUnreadCountUpdate() {
        return "unread_count".equals(updateType);
    }

    /**
     * 判断是否为归档状态更新
     */
    public boolean isArchiveStatusUpdate() {
        return "archive_status".equals(updateType);
    }

    /**
     * 判断是否为清零未读数
     */
    public boolean isClearUnreadUpdate() {
        return "clear_unread".equals(updateType);
    }

    /**
     * 判断是否为增加未读数
     */
    public boolean isIncrementUnreadUpdate() {
        return "increment_unread".equals(updateType);
    }

    /**
     * 验证会话标识的有效性
     */
    public boolean hasValidSessionIdentifier() {
        return sessionId != null || (userId != null && otherUserId != null);
    }

    /**
     * 验证操作权限
     */
    public boolean hasValidPermission() {
        if (operatorId == null) {
            return false;
        }
        // 操作者必须是会话的参与者之一
        return operatorId.equals(userId) || operatorId.equals(otherUserId);
    }

    /**
     * 验证更新请求的有效性
     */
    public boolean isValidUpdate() {
        if (!hasValidSessionIdentifier() || !hasValidPermission()) {
            return false;
        }

        switch (updateType) {
            case "last_message":
                return lastMessageId != null && lastMessageTime != null;
            case "unread_count":
                return unreadCount != null && unreadCount >= 0;
            case "archive_status":
                return isArchived != null;
            case "clear_unread":
            case "increment_unread":
                return true; // 这两种操作不需要额外参数
            default:
                return false;
        }
    }

    /**
     * 设置默认值
     */
    public void initDefaults() {
        if (this.unreadCount != null && this.unreadCount < 0) {
            this.unreadCount = 0;
        }
    }

    /**
     * 生成对方会话的更新请求
     * 用于同步更新双方的会话状态
     */
    public MessageSessionUpdateRequest createReverseUpdate() {
        MessageSessionUpdateRequest reverse = new MessageSessionUpdateRequest();
        reverse.setUserId(this.otherUserId);
        reverse.setOtherUserId(this.userId);
        reverse.setOperatorId(this.operatorId);
        reverse.setUpdateType(this.updateType);

        // 根据更新类型设置不同的值
        if (isLastMessageUpdate()) {
            reverse.setLastMessageId(this.lastMessageId);
            reverse.setLastMessageTime(this.lastMessageTime);
            // 对方的未读数需要增加（除非是自己发送的消息）
            if (!operatorId.equals(otherUserId)) {
                reverse.setUnreadCount(1); // 简化处理，实际应该获取当前未读数+1
            }
        }

        return reverse;
    }
}