package com.gig.collide.message.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 消息会话实体类 - 简洁版
 * 基于message-simple.sql的t_message_session表结构
 * 用于优化会话列表查询性能
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
@TableName("t_message_session")
public class MessageSession {

    /**
     * 会话ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 对方用户ID
     */
    @TableField("other_user_id")
    private Long otherUserId;

    /**
     * 最后一条消息ID
     */
    @TableField("last_message_id")
    private Long lastMessageId;

    /**
     * 最后消息时间
     */
    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数
     */
    @TableField("unread_count")
    private Integer unreadCount;

    /**
     * 是否归档
     */
    @TableField("is_archived")
    private Boolean isArchived;

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
     * 更新最后消息信息
     */
    public void updateLastMessage(Long messageId, LocalDateTime messageTime) {
        this.lastMessageId = messageId;
        this.lastMessageTime = messageTime;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加未读消息数
     */
    public void incrementUnreadCount() {
        if (this.unreadCount == null) {
            this.unreadCount = 1;
        } else {
            this.unreadCount++;
        }
    }

    /**
     * 清零未读消息数
     */
    public void clearUnreadCount() {
        this.unreadCount = 0;
    }

    /**
     * 设置归档状态
     */
    public void setArchivedStatus(boolean archived) {
        this.isArchived = archived;
    }

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.unreadCount == null) {
            this.unreadCount = 0;
        }
        if (this.isArchived == null) {
            this.isArchived = false;
        }
    }
}