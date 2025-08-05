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
 * 消息会话统一响应对象 - 简洁版
 * 基于message-simple.sql的t_message_session表结构
 * 包含会话的完整信息和扩展属性
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
public class MessageSessionResponse implements Serializable {

    /**
     * 会话ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 对方用户ID
     */
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
     * 对方用户昵称（冗余字段，避免连表查询）
     */
    private String otherUserNickname;

    /**
     * 对方用户头像（冗余字段）
     */
    private String otherUserAvatar;

    /**
     * 对方用户在线状态
     */
    private String otherUserOnlineStatus;

    /**
     * 最后一条消息内容预览
     */
    private String lastMessageContent;

    /**
     * 最后一条消息类型
     */
    private String lastMessageType;

    /**
     * 最后一条消息发送者ID
     */
    private Long lastMessageSenderId;

    /**
     * 会话类型（私聊、群聊等，当前主要是私聊）
     */
    private String sessionType;

    /**
     * 会话状态（正常、已屏蔽等）
     */
    private String sessionStatus;

    /**
     * 是否免打扰
     */
    private Boolean isDoNotDisturb;

    /**
     * 会话置顶状态
     */
    private Boolean isSessionPinned;

    /**
     * 最后活跃时间描述（如：刚刚、5分钟前）
     */
    private String lastActiveTimeAgo;

    /**
     * 会话的总消息数
     */
    private Long totalMessageCount;

    /**
     * 本周消息数
     */
    private Integer weeklyMessageCount;

    // =================== 业务状态方法 ===================

    /**
     * 判断是否有未读消息
     */
    public boolean hasUnreadMessages() {
        return unreadCount != null && unreadCount > 0;
    }

    /**
     * 判断是否已归档
     */
    public boolean isArchived() {
        return Boolean.TRUE.equals(isArchived);
    }

    /**
     * 判断是否置顶
     */
    public boolean isPinned() {
        return Boolean.TRUE.equals(isSessionPinned);
    }

    /**
     * 判断是否免打扰
     */
    public boolean isDoNotDisturb() {
        return Boolean.TRUE.equals(isDoNotDisturb);
    }

    /**
     * 判断最后一条消息是否由当前用户发送
     */
    public boolean isLastMessageSentByUser() {
        return userId != null && userId.equals(lastMessageSenderId);
    }

    /**
     * 判断对方用户是否在线
     */
    public boolean isOtherUserOnline() {
        return "online".equals(otherUserOnlineStatus);
    }

    /**
     * 判断会话是否活跃（最近有消息）
     */
    public boolean isActiveSession() {
        if (lastMessageTime == null) {
            return false;
        }
        // 7天内有消息认为是活跃会话
        return lastMessageTime.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * 获取未读消息数的显示文本
     */
    public String getUnreadCountDisplay() {
        if (unreadCount == null || unreadCount <= 0) {
            return "";
        }
        if (unreadCount > 99) {
            return "99+";
        }
        return unreadCount.toString();
    }

    /**
     * 获取最后消息内容的预览
     */
    public String getLastMessagePreview() {
        if (lastMessageContent == null || lastMessageContent.isEmpty()) {
            return getMessageTypePreview();
        }
        
        // 根据消息类型返回不同的预览
        if ("text".equals(lastMessageType)) {
            return lastMessageContent.length() > 30 ? 
                   lastMessageContent.substring(0, 30) + "..." : lastMessageContent;
        } else {
            return getMessageTypePreview();
        }
    }

    /**
     * 根据消息类型获取预览文本
     */
    private String getMessageTypePreview() {
        if (lastMessageType == null) {
            return "[未知消息]";
        }
        switch (lastMessageType) {
            case "image":
                return "[图片]";
            case "file":
                return "[文件]";
            case "system":
                return "[系统消息]";
            default:
                return "[消息]";
        }
    }

    /**
     * 获取会话状态的中文描述
     */
    public String getSessionStatusDescription() {
        if (sessionStatus == null) {
            return "正常";
        }
        switch (sessionStatus) {
            case "normal":
                return "正常";
            case "blocked":
                return "已屏蔽";
            case "deleted":
                return "已删除";
            default:
                return "未知";
        }
    }

    /**
     * 获取对方用户的显示名称
     */
    public String getOtherUserDisplayName() {
        return otherUserNickname != null && !otherUserNickname.isEmpty() ? 
               otherUserNickname : "用户" + otherUserId;
    }

    // =================== 隐私保护方法 ===================

    /**
     * 隐藏敏感信息（用于非参与者查看）
     */
    @JsonIgnore
    public void hideSensitiveInfo() {
        this.lastMessageContent = "[隐私消息]";
        this.otherUserAvatar = null;
    }

    /**
     * 为已屏蔽会话隐藏详细信息
     */
    @JsonIgnore
    public void hideBlockedSessionInfo() {
        if ("blocked".equals(sessionStatus)) {
            this.lastMessageContent = "[会话已屏蔽]";
            this.unreadCount = 0;
        }
    }
}