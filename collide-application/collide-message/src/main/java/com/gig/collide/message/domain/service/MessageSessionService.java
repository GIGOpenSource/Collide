package com.gig.collide.message.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.message.domain.entity.MessageSession;

import java.time.LocalDateTime;

/**
 * 消息会话业务服务接口 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageSessionService {

    // =================== 基础操作 ===================

    /**
     * 创建或更新会话
     */
    MessageSession createOrUpdateSession(Long userId, Long otherUserId, Long messageId, LocalDateTime messageTime);

    /**
     * 获取用户与对方的会话
     */
    MessageSession getSessionByUserIds(Long userId, Long otherUserId);

    /**
     * 删除会话
     */
    boolean deleteSession(Long sessionId);

    // =================== 查询操作 ===================

    /**
     * 分页查询用户会话列表
     */
    Page<MessageSession> getUserSessions(Long userId, Boolean isArchived, Boolean hasUnread,
                                       String orderBy, String orderDirection,
                                       Integer currentPage, Integer pageSize);

    /**
     * 查询用户的活跃会话
     */
    Page<MessageSession> getActiveSessions(Long userId, LocalDateTime sinceTime,
                                         Integer currentPage, Integer pageSize);

    // =================== 统计操作 ===================

    /**
     * 统计用户的未读会话数
     */
    Long countUnreadSessions(Long userId);

    /**
     * 统计用户的总会话数
     */
    Long countUserSessions(Long userId, Boolean isArchived);

    // =================== 状态更新 ===================

    /**
     * 更新会话的最后消息信息
     */
    boolean updateLastMessage(Long userId, Long otherUserId, Long messageId, LocalDateTime messageTime);

    /**
     * 增加会话的未读计数
     */
    boolean incrementUnreadCount(Long userId, Long otherUserId);

    /**
     * 清零会话的未读计数
     */
    boolean clearUnreadCount(Long userId, Long otherUserId);

    /**
     * 更新会话归档状态
     */
    boolean updateArchiveStatus(Long sessionId, Boolean isArchived);

    // =================== 消息事件处理 ===================

    /**
     * 处理新消息事件（自动创建/更新会话）
     */
    void handleNewMessage(Long senderId, Long receiverId, Long messageId, LocalDateTime messageTime);

    /**
     * 处理消息已读事件（更新未读计数）
     */
    void handleMessageRead(Long senderId, Long receiverId);

    // =================== 清理操作 ===================

    /**
     * 清理空会话
     */
    int cleanEmptySessions();

    /**
     * 清理过期的归档会话
     */
    int cleanArchivedSessions(Integer days);
}