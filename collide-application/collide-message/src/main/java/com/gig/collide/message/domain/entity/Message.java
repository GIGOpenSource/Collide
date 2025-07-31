package com.gig.collide.message.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 私信消息实体类 - 简洁版
 * 基于message-simple.sql的t_message表结构
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
@TableName("t_message")
public class Message {

    /**
     * 消息ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 接收者ID
     */
    @TableField("receiver_id")
    private Long receiverId;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型：text、image、file、system
     */
    @TableField("message_type")
    private String messageType;

    /**
     * 扩展数据（图片URL、文件信息等）
     */
    @TableField(value = "extra_data", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> extraData;

    /**
     * 消息状态：sent、delivered、read、deleted
     */
    @TableField("status")
    private String status;

    /**
     * 已读时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 回复的消息ID
     */
    @TableField("reply_to_id")
    private Long replyToId;

    /**
     * 是否置顶（留言板功能）
     */
    @TableField("is_pinned")
    private Boolean isPinned;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 判断是否为已读消息
     */
    public boolean isRead() {
        return "read".equals(status);
    }

    /**
     * 判断是否为已删除消息
     */
    public boolean isDeleted() {
        return "deleted".equals(status);
    }

    /**
     * 判断是否为系统消息
     */
    public boolean isSystemMessage() {
        return "system".equals(messageType);
    }

    /**
     * 判断是否为回复消息
     */
    public boolean isReply() {
        return replyToId != null && replyToId > 0;
    }

    /**
     * 判断是否可以被指定用户查看
     */
    public boolean canBeViewedBy(Long userId) {
        return userId != null && (userId.equals(senderId) || userId.equals(receiverId));
    }

    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.status = "read";
        this.readTime = LocalDateTime.now();
    }

    /**
     * 标记为已删除
     */
    public void markAsDeleted() {
        this.status = "deleted";
    }

    /**
     * 设置置顶状态
     */
    public void setPinnedStatus(boolean pinned) {
        this.isPinned = pinned;
    }
}