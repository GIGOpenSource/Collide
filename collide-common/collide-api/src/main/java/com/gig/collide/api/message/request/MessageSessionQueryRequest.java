package com.gig.collide.api.message.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息会话查询请求 - 简洁版
 * 支持多维度条件查询会话列表
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
public class MessageSessionQueryRequest implements Serializable {

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 对方用户ID
     */
    private Long otherUserId;

    /**
     * 是否归档
     */
    private Boolean isArchived;

    /**
     * 是否有未读消息
     */
    private Boolean hasUnread;

    /**
     * 最小未读数
     */
    private Integer minUnreadCount;

    /**
     * 活跃时间起点（查询此时间后有消息的会话）
     */
    private LocalDateTime sinceTime;

    /**
     * 查询类型
     * all、active、unread、archived、recent
     */
    private String queryType;

    /**
     * 排序字段
     * last_message_time、create_time、unread_count
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

    // =================== 业务验证方法 ===================

    /**
     * 判断是否为查询所有会话
     */
    public boolean isAllSessionsQuery() {
        return "all".equals(queryType);
    }

    /**
     * 判断是否为查询活跃会话
     */
    public boolean isActiveSessionsQuery() {
        return "active".equals(queryType);
    }

    /**
     * 判断是否为查询未读会话
     */
    public boolean isUnreadSessionsQuery() {
        return "unread".equals(queryType);
    }

    /**
     * 判断是否为查询归档会话
     */
    public boolean isArchivedSessionsQuery() {
        return "archived".equals(queryType);
    }

    /**
     * 判断是否为查询最近会话
     */
    public boolean isRecentSessionsQuery() {
        return "recent".equals(queryType);
    }

    /**
     * 判断是否为查询特定用户的会话
     */
    public boolean isSpecificUserQuery() {
        return otherUserId != null;
    }

    /**
     * 判断是否有时间范围限制
     */
    public boolean hasTimeLimit() {
        return sinceTime != null;
    }

    /**
     * 判断是否有未读数限制
     */
    public boolean hasUnreadLimit() {
        return minUnreadCount != null && minUnreadCount > 0;
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
            this.orderBy = "last_message_time";
        }
        if (this.orderDirection == null) {
            this.orderDirection = "DESC";
        }
        if (this.queryType == null) {
            this.queryType = "all";
        }
    }

    /**
     * 验证查询参数的有效性
     */
    public boolean isValidQuery() {
        if (userId == null) {
            return false;
        }

        // 根据查询类型验证特定参数
        switch (queryType) {
            case "active":
                // 活跃会话查询可以有时间限制
                return true;
            case "unread":
                // 未读会话查询可以有最小未读数限制
                return true;
            case "archived":
                // 归档会话查询
                return true;
            case "recent":
                // 最近会话查询
                return true;
            case "all":
            default:
                return true;
        }
    }

    /**
     * 自动设置查询类型相关的字段
     */
    public void autoSetQueryFields() {
        switch (queryType) {
            case "unread":
                this.hasUnread = true;
                this.isArchived = false; // 未读查询排除归档
                break;
            case "archived":
                this.isArchived = true;
                break;
            case "active":
                this.isArchived = false; // 活跃查询排除归档
                if (sinceTime == null) {
                    // 默认查询最近7天的活跃会话
                    this.sinceTime = LocalDateTime.now().minusDays(7);
                }
                break;
            case "recent":
                this.isArchived = false; // 最近查询排除归档
                this.orderBy = "last_message_time";
                this.orderDirection = "DESC";
                break;
        }
    }

    /**
     * 获取有效的页面大小（防止过大的查询）
     */
    public int getValidPageSize() {
        int maxPageSize = 100;
        return Math.min(pageSize, maxPageSize);
    }
}