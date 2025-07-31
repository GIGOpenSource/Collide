package com.gig.collide.api.message.request;

import com.gig.collide.base.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * 消息查询请求 - 简洁版
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
@EqualsAndHashCode(callSuper = true)
public class MessageQueryRequest extends PageRequest {

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 消息类型：text、image、file、system
     */
    private String messageType;

    /**
     * 消息状态：sent、delivered、read、deleted
     */
    private String status;

    /**
     * 是否置顶
     */
    private Boolean isPinned;

    /**
     * 回复的消息ID（查询某条消息的回复）
     */
    private Long replyToId;

    /**
     * 搜索关键词（模糊搜索消息内容）
     */
    private String keyword;

    /**
     * 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String startTime;

    /**
     * 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;

    /**
     * 排序字段：create_time、read_time
     */
    private String orderBy = "create_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "DESC";
}