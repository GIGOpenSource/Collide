package com.gig.collide.api.message.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息统一响应对象 - 简洁版
 * 基于message-simple.sql的t_message表结构
 * 包含消息的完整信息和扩展属性
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
     * 是否置顶（留言板功能）
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

    // =================== 扩展字段 ===================

    /**
     * 发送者昵称（冗余字段，避免连表查询）
     */
    private String senderNickname;

    /**
     * 发送者头像（冗余字段）
     */
    private String senderAvatar;

    /**
     * 接收者昵称（冗余字段）
     */
    private String receiverNickname;

    /**
     * 接收者头像（冗余字段）
     */
    private String receiverAvatar;

    /**
     * 回复的消息内容（预览）
     */
    private String replyToContent;

    /**
     * 回复的消息发送者昵称
     */
    private String replyToSenderNickname;

    /**
     * 是否为当前用户发送的消息
     */
    private Boolean isSentByCurrentUser;

    /**
     * 消息距离现在的时间描述（如：刚刚、5分钟前）
     */
    private String timeAgo;

    /**
     * 消息大小（主要用于文件消息）
     */
    private Long messageSize;

    /**
     * 文件名（用于文件消息）
     */
    private String fileName;

    /**
     * 图片缩略图URL（用于图片消息）
     */
    private String thumbnailUrl;

    /**
     * 消息摘要（用于长文本消息的预览）
     */
    private String summary;

    // =================== 业务状态方法 ===================

    /**
     * 判断是否已读
     */
    public boolean isRead() {
        return "read".equals(status);
    }

    /**
     * 判断是否已发送
     */
    public boolean isSent() {
        return "sent".equals(status) || "delivered".equals(status) || "read".equals(status);
    }

    /**
     * 判断是否已删除
     */
    public boolean isDeleted() {
        return "deleted".equals(status);
    }

    /**
     * 判断是否为回复消息
     */
    public boolean isReplyMessage() {
        return replyToId != null && replyToId > 0;
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
     * 判断是否置顶
     */
    public boolean isPinned() {
        return Boolean.TRUE.equals(isPinned);
    }

    /**
     * 判断是否为多媒体消息
     */
    public boolean isMultimediaMessage() {
        return extraData != null && !extraData.isEmpty();
    }

    /**
     * 获取消息内容摘要
     */
    public String getContentSummary() {
        if (summary != null) {
            return summary;
        }
        if (content == null) {
            return "";
        }
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    /**
     * 获取消息类型的中文描述
     */
    public String getMessageTypeDescription() {
        switch (messageType) {
            case "text":
                return "文本消息";
            case "image":
                return "图片消息";
            case "file":
                return "文件消息";
            case "system":
                return "系统消息";
            default:
                return "未知类型";
        }
    }

    /**
     * 获取消息状态的中文描述
     */
    public String getStatusDescription() {
        switch (status) {
            case "sent":
                return "已发送";
            case "delivered":
                return "已送达";
            case "read":
                return "已读";
            case "deleted":
                return "已删除";
            default:
                return "未知状态";
        }
    }

    // =================== 隐藏敏感信息 ===================

    /**
     * 隐藏已删除消息的敏感内容
     */
    @JsonIgnore
    public void hideSensitiveContent() {
        if (isDeleted()) {
            this.content = "[该消息已删除]";
            this.extraData = null;
        }
    }

    /**
     * 为非参与者隐藏私密信息
     */
    @JsonIgnore
    public void hidePrivateInfo() {
        this.extraData = null; // 隐藏扩展数据中的敏感信息
    }
}