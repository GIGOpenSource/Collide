package com.gig.collide.api.message.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息会话响应对象 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
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
    
    private static final long serialVersionUID = 1L;

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

    // =================== 扩展字段（冗余信息） ===================

    /**
     * 对方用户昵称
     */
    private String otherUserNickname;

    /**
     * 对方用户头像
     */
    private String otherUserAvatar;

    /**
     * 最后一条消息内容
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
}