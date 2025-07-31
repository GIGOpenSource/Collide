package com.gig.collide.api.message.request;

import com.gig.collide.base.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

/**
 * 消息会话查询请求 - 简洁版
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
@EqualsAndHashCode(callSuper = true)
public class MessageSessionQueryRequest extends PageRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 对方用户ID
     */
    private Long otherUserId;

    /**
     * 是否归档
     */
    private Boolean isArchived = false;

    /**
     * 是否有未读消息
     */
    private Boolean hasUnread;

    /**
     * 排序字段：last_message_time、create_time
     */
    private String orderBy = "last_message_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "DESC";
}