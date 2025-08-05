package com.gig.collide.api.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 消息创建请求 - 简洁版
 * 基于message-simple.sql的t_message表设计
 * 支持文本、图片、文件等多种消息类型
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
public class MessageCreateRequest implements Serializable {

    /**
     * 发送者ID
     */
    @NotNull(message = "发送者ID不能为空")
    private Long senderId;

    /**
     * 接收者ID
     */
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型：text、image、file、system
     */
    @NotBlank(message = "消息类型不能为空")
    private String messageType;

    /**
     * 扩展数据（图片URL、文件信息等）
     * 可选字段，用于存储多媒体消息的附加信息
     */
    private Map<String, Object> extraData;

    /**
     * 回复的消息ID
     * 如果是回复消息，则需要指定原消息ID
     */
    private Long replyToId;

    /**
     * 是否置顶（留言板功能）
     * 用于留言板消息的置顶功能
     */
    private Boolean isPinned;

    // =================== 业务验证方法 ===================

    /**
     * 判断是否为回复消息
     */
    public boolean isReplyMessage() {
        return replyToId != null && replyToId > 0;
    }

    /**
     * 判断是否为多媒体消息
     */
    public boolean isMultimediaMessage() {
        return extraData != null && !extraData.isEmpty();
    }

    /**
     * 判断是否为文本消息
     */
    public boolean isTextMessage() {
        return "text".equals(messageType);
    }

    /**
     * 判断是否为图片消息
     */
    public boolean isImageMessage() {
        return "image".equals(messageType);
    }

    /**
     * 判断是否为文件消息
     */
    public boolean isFileMessage() {
        return "file".equals(messageType);
    }

    /**
     * 判断是否为系统消息
     */
    public boolean isSystemMessage() {
        return "system".equals(messageType);
    }

    /**
     * 判断是否需要置顶
     */
    public boolean needPin() {
        return Boolean.TRUE.equals(isPinned);
    }

    /**
     * 设置默认值
     */
    public void initDefaults() {
        if (this.messageType == null) {
            this.messageType = "text";
        }
        if (this.isPinned == null) {
            this.isPinned = false;
        }
    }

    /**
     * 验证消息内容长度
     */
    public boolean isContentLengthValid() {
        if (content == null) {
            return false;
        }
        // 文本消息最长1000字符，其他类型可以更短
        int maxLength = isTextMessage() ? 1000 : 200;
        return content.length() <= maxLength;
    }
}