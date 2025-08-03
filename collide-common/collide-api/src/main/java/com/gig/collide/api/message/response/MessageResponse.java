package com.gig.collide.api.message.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息响应对象 - 简洁版
 * 基于message-simple.sql的t_message表设计
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
public class MessageResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：text、image、file、system
     */
    private String messageType;

    /**
     * 扩展数据（图片URL、文件信息等）
     */
    private Map<String, Object> extraData;

    /**
     * 消息状态：sent、delivered、read、deleted
     */
    private String status;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 回复的消息ID
     */
    private Long replyToId;

    /**
     * 是否置顶
     */
    private Boolean isPinned;

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
     * 发送者昵称（可选，避免额外查询）
     */
    private String senderNickname;

    /**
     * 发送者头像（可选，避免额外查询）
     */
    private String senderAvatar;

    /**
     * 接收者昵称（可选，避免额外查询）
     */
    private String receiverNickname;

    /**
     * 接收者头像（可选，避免额外查询）
     */
    private String receiverAvatar;

    /**
     * 回复的消息内容（可选，用于显示引用）
     */
    private String replyToContent;

    /**
     * 回复的消息发送者昵称（可选）
     */
    private String replyToSenderNickname;
}