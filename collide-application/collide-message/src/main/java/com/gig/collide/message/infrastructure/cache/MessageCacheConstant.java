package com.gig.collide.message.infrastructure.cache;

/**
 * 消息模块缓存常量 - 简洁版
 * 定义消息、会话、设置相关的缓存配置
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public final class MessageCacheConstant {

    // =================== 消息缓存 ===================

    /**
     * 消息详情缓存
     * Key: messageId
     * Value: MessageResponse
     * TTL: 30分钟
     */
    public static final String MESSAGE_DETAIL_CACHE = "message:detail";

    /**
     * 用户未读消息数缓存
     * Key: userId
     * Value: Long
     * TTL: 5分钟
     */
    public static final String MESSAGE_UNREAD_COUNT_CACHE = "message:unread:count";

    /**
     * 两用户未读消息数缓存
     * Key: receiverId:senderId
     * Value: Long
     * TTL: 10分钟
     */
    public static final String MESSAGE_UNREAD_WITH_USER_CACHE = "message:unread:with:user";

    /**
     * 用户聊天记录缓存
     * Key: userId1:userId2:page:size
     * Value: PageResponse<MessageResponse>
     * TTL: 15分钟
     */
    public static final String MESSAGE_CHAT_HISTORY_CACHE = "message:chat:history";

    /**
     * 用户最近聊天用户缓存
     * Key: userId:limit
     * Value: List<Long>
     * TTL: 10分钟
     */
    public static final String MESSAGE_RECENT_CHAT_USERS_CACHE = "message:recent:chat:users";

    /**
     * 两用户最新消息缓存
     * Key: userId1:userId2
     * Value: MessageResponse
     * TTL: 30分钟
     */
    public static final String MESSAGE_LATEST_CACHE = "message:latest";

    // =================== 会话缓存 ===================

    /**
     * 用户会话详情缓存
     * Key: userId:otherUserId
     * Value: MessageSessionResponse
     * TTL: 20分钟
     */
    public static final String SESSION_DETAIL_CACHE = "session:detail";

    /**
     * 用户会话列表缓存
     * Key: userId:isArchived:hasUnread:page:size
     * Value: PageResponse<MessageSessionResponse>
     * TTL: 10分钟
     */
    public static final String SESSION_USER_LIST_CACHE = "session:user:list";

    /**
     * 用户未读会话数缓存
     * Key: userId
     * Value: Long
     * TTL: 5分钟
     */
    public static final String SESSION_UNREAD_COUNT_CACHE = "session:unread:count";

    /**
     * 用户会话总数缓存
     * Key: userId:isArchived
     * Value: Long
     * TTL: 15分钟
     */
    public static final String SESSION_USER_COUNT_CACHE = "session:user:count";

    /**
     * 用户活跃会话缓存
     * Key: userId:sinceTime:page:size
     * Value: PageResponse<MessageSessionResponse>
     * TTL: 10分钟
     */
    public static final String SESSION_ACTIVE_CACHE = "session:active";

    // =================== 设置缓存 ===================

    /**
     * 用户消息设置缓存
     * Key: userId
     * Value: MessageSettingResponse
     * TTL: 60分钟
     */
    public static final String SETTING_USER_CACHE = "setting:user";

    /**
     * 用户消息权限缓存
     * Key: senderId:receiverId
     * Value: Boolean
     * TTL: 30分钟
     */
    public static final String SETTING_PERMISSION_CACHE = "setting:permission";

    /**
     * 陌生人消息权限缓存
     * Key: userId
     * Value: Boolean
     * TTL: 30分钟
     */
    public static final String SETTING_STRANGER_MSG_CACHE = "setting:stranger:msg";

    /**
     * 自动已读回执设置缓存
     * Key: userId
     * Value: Boolean
     * TTL: 30分钟
     */
    public static final String SETTING_AUTO_READ_RECEIPT_CACHE = "setting:auto:read:receipt";

    /**
     * 消息通知设置缓存
     * Key: userId
     * Value: Boolean
     * TTL: 30分钟
     */
    public static final String SETTING_NOTIFICATION_CACHE = "setting:notification";

    /**
     * 默认消息设置缓存
     * Key: default
     * Value: MessageSettingResponse
     * TTL: 24小时
     */
    public static final String SETTING_DEFAULT_CACHE = "setting:default";

    // =================== 统计缓存 ===================

    /**
     * 用户发送消息数缓存
     * Key: userId:startTime:endTime
     * Value: Long
     * TTL: 60分钟
     */
    public static final String STATISTICS_SENT_COUNT_CACHE = "statistics:sent:count";

    /**
     * 用户接收消息数缓存
     * Key: userId:startTime:endTime
     * Value: Long
     * TTL: 60分钟
     */
    public static final String STATISTICS_RECEIVED_COUNT_CACHE = "statistics:received:count";

    /**
     * 设置统计信息缓存
     * Key: statistics
     * Value: Map<String, Object>
     * TTL: 2小时
     */
    public static final String SETTING_STATISTICS_CACHE = "setting:statistics";

    // =================== 缓存失效组 ===================

    /**
     * 消息相关缓存失效组
     * 包含消息的详情、列表、统计等缓存
     */
    public static final String[] MESSAGE_CACHE_GROUP = {
            MESSAGE_DETAIL_CACHE,
            MESSAGE_UNREAD_COUNT_CACHE,
            MESSAGE_UNREAD_WITH_USER_CACHE,
            MESSAGE_CHAT_HISTORY_CACHE,
            MESSAGE_RECENT_CHAT_USERS_CACHE,
            MESSAGE_LATEST_CACHE,
            STATISTICS_SENT_COUNT_CACHE,
            STATISTICS_RECEIVED_COUNT_CACHE
    };

    /**
     * 会话相关缓存失效组
     * 包含会话的详情、列表、统计等缓存
     */
    public static final String[] SESSION_CACHE_GROUP = {
            SESSION_DETAIL_CACHE,
            SESSION_USER_LIST_CACHE,
            SESSION_UNREAD_COUNT_CACHE,
            SESSION_USER_COUNT_CACHE,
            SESSION_ACTIVE_CACHE
    };

    /**
     * 设置相关缓存失效组
     * 包含用户设置、权限、统计等缓存
     */
    public static final String[] SETTING_CACHE_GROUP = {
            SETTING_USER_CACHE,
            SETTING_PERMISSION_CACHE,
            SETTING_STRANGER_MSG_CACHE,
            SETTING_AUTO_READ_RECEIPT_CACHE,
            SETTING_NOTIFICATION_CACHE,
            SETTING_STATISTICS_CACHE
    };

    // =================== 缓存键工具方法 ===================

    /**
     * 构建用户间缓存键
     */
    public static String buildUserPairKey(Long userId1, Long userId2) {
        // 确保键的一致性，小ID在前
        if (userId1.compareTo(userId2) <= 0) {
            return userId1 + ":" + userId2;
        } else {
            return userId2 + ":" + userId1;
        }
    }

    /**
     * 构建分页缓存键
     */
    public static String buildPageKey(String prefix, Integer currentPage, Integer pageSize) {
        return prefix + ":" + currentPage + ":" + pageSize;
    }

    /**
     * 构建时间范围缓存键
     */
    public static String buildTimeRangeKey(String prefix, String startTime, String endTime) {
        return prefix + ":" + startTime + ":" + endTime;
    }

    /**
     * 私有构造器，防止实例化
     */
    private MessageCacheConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}