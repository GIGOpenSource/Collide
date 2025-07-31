package com.gig.collide.api.message.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    
    private static final long serialVersionUID = 1L;

    /**
     * 发送者ID - 必填
     */
    @NotNull(message = "发送者ID不能为空")
    private Long senderId;

    /**
     * 接收者ID - 必填
     */
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;

    /**
     * 消息内容 - 必填
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000字符")
    private String content;

    /**
     * 消息类型：text、image、file、system
     * 默认为text
     */
    private String messageType = "text";

    /**
     * 扩展数据（图片URL、文件信息等）
     */
    private Map<String, Object> extraData;

    /**
     * 回复的消息ID（可选，回复消息时使用）
     */
    private Long replyToId;

    /**
     * 是否置顶（留言板功能）
     */
    private Boolean isPinned = false;
}