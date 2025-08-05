package com.gig.collide.api.message.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息会话创建请求 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
 * 用于创建或更新用户间的会话状态
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
public class MessageSessionCreateRequest implements Serializable {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 对方用户ID
     */
    @NotNull(message = "对方用户ID不能为空")
    private Long otherUserId;

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

    // =================== 业务验证方法 ===================

    /**
     * 验证用户ID的有效性
     */
    public boolean isValidUserIds() {
        return userId != null && otherUserId != null && !userId.equals(otherUserId);
    }

    /**
     * 判断是否有最后消息信息
     */
    public boolean hasLastMessage() {
        return lastMessageId != null && lastMessageTime != null;
    }

    /**
     * 判断是否有未读消息
     */
    public boolean hasUnreadMessages() {
        return unreadCount != null && unreadCount > 0;
    }

    /**
     * 判断是否为归档会话
     */
    public boolean isArchived() {
        return Boolean.TRUE.equals(isArchived);
    }

    /**
     * 设置默认值
     */
    public void initDefaults() {
        if (this.unreadCount == null) {
            this.unreadCount = 0;
        }
        if (this.isArchived == null) {
            this.isArchived = false;
        }
        if (this.lastMessageTime == null) {
            this.lastMessageTime = LocalDateTime.now();
        }
    }

    /**
     * 创建会话的反向关系
     * 生成对方用户的会话记录请求
     */
    public MessageSessionCreateRequest createReverseSession() {
        MessageSessionCreateRequest reverse = new MessageSessionCreateRequest();
        reverse.setUserId(this.otherUserId);
        reverse.setOtherUserId(this.userId);
        reverse.setLastMessageId(this.lastMessageId);
        reverse.setLastMessageTime(this.lastMessageTime);
        // 对方的未读数和归档状态可能不同，需要单独设置
        reverse.setUnreadCount(0);
        reverse.setIsArchived(false);
        return reverse;
    }

    /**
     * 验证请求的完整性
     */
    public boolean isValidRequest() {
        if (!isValidUserIds()) {
            return false;
        }
        if (unreadCount != null && unreadCount < 0) {
            return false;
        }
        return true;
    }
}