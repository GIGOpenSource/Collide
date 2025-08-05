package com.gig.collide.api.message.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息查询请求 - 简洁版
 * 支持多维度条件查询和排序
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
public class MessageQueryRequest implements Serializable {

    /**
     * 消息ID
     */
    private Long messageId;

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
     * 回复的消息ID
     */
    private Long replyToId;

    /**
     * 内容关键词搜索
     */
    private String keyword;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 查询类型
     * chat_history、wall_messages、replies、search、user_sent、user_received
     */
    private String queryType;

    /**
     * 排序字段
     * create_time、update_time、read_time
     */
    private String orderBy;

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection;

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    @Min(value = 1, message = "页面大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 是否只查询未读消息
     */
    private Boolean unreadOnly;

    /**
     * 是否排除系统消息
     */
    private Boolean excludeSystem;

    // =================== 业务验证方法 ===================

    /**
     * 判断是否为聊天记录查询
     */
    public boolean isChatHistoryQuery() {
        return "chat_history".equals(queryType);
    }

    /**
     * 判断是否为留言板查询
     */
    public boolean isWallMessageQuery() {
        return "wall_messages".equals(queryType);
    }

    /**
     * 判断是否为回复查询
     */
    public boolean isReplyQuery() {
        return "replies".equals(queryType);
    }

    /**
     * 判断是否为搜索查询
     */
    public boolean isSearchQuery() {
        return "search".equals(queryType);
    }

    /**
     * 判断是否为用户发送消息查询
     */
    public boolean isUserSentQuery() {
        return "user_sent".equals(queryType);
    }

    /**
     * 判断是否为用户接收消息查询
     */
    public boolean isUserReceivedQuery() {
        return "user_received".equals(queryType);
    }

    /**
     * 判断是否有时间范围限制
     */
    public boolean hasTimeRange() {
        return startTime != null || endTime != null;
    }

    /**
     * 判断是否有关键词搜索
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    /**
     * 设置默认值
     */
    public void initDefaults() {
        if (this.currentPage == null || this.currentPage < 1) {
            this.currentPage = 1;
        }
        if (this.pageSize == null || this.pageSize < 1) {
            this.pageSize = 20;
        }
        if (this.orderBy == null) {
            this.orderBy = "create_time";
        }
        if (this.orderDirection == null) {
            this.orderDirection = "DESC";
        }
        if (this.queryType == null) {
            this.queryType = "chat_history";
        }
    }

    /**
     * 验证查询参数的有效性
     */
    public boolean isValidQuery() {
        // 聊天记录查询需要发送者和接收者ID
        if (isChatHistoryQuery()) {
            return senderId != null && receiverId != null;
        }
        // 留言板查询需要接收者ID
        if (isWallMessageQuery()) {
            return receiverId != null;
        }
        // 回复查询需要原消息ID
        if (isReplyQuery()) {
            return replyToId != null;
        }
        // 搜索查询需要用户ID和关键词
        if (isSearchQuery()) {
            return (senderId != null || receiverId != null) && hasKeyword();
        }
        // 用户发送/接收查询需要对应的用户ID
        if (isUserSentQuery()) {
            return senderId != null;
        }
        if (isUserReceivedQuery()) {
            return receiverId != null;
        }
        return true;
    }
}