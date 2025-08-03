package com.gig.collide.message.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 消息设置实体类 - 简洁版
 * 基于message-simple.sql的t_message_setting表结构
 * 用户消息相关的设置配置
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
@TableName("t_message_setting")
public class MessageSetting {

    /**
     * 设置ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID - 唯一索引
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否允许陌生人发消息
     */
    @TableField("allow_stranger_msg")
    private Boolean allowStrangerMsg;

    /**
     * 是否自动发送已读回执
     */
    @TableField("auto_read_receipt")
    private Boolean autoReadReceipt;

    /**
     * 是否开启消息通知
     */
    @TableField("message_notification")
    private Boolean messageNotification;

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
     * 设置默认值
     */
    public void initDefaults() {
        if (this.allowStrangerMsg == null) {
            this.allowStrangerMsg = true;
        }
        if (this.autoReadReceipt == null) {
            this.autoReadReceipt = true;
        }
        if (this.messageNotification == null) {
            this.messageNotification = true;
        }
    }

    /**
     * 更新陌生人消息设置
     */
    public void updateStrangerMessageSetting(boolean allow) {
        this.allowStrangerMsg = allow;
    }

    /**
     * 更新已读回执设置
     */
    public void updateReadReceiptSetting(boolean auto) {
        this.autoReadReceipt = auto;
    }

    /**
     * 更新消息通知设置
     */
    public void updateNotificationSetting(boolean enable) {
        this.messageNotification = enable;
    }
}