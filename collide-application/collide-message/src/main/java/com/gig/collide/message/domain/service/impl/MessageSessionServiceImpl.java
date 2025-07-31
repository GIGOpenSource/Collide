package com.gig.collide.message.domain.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.message.domain.entity.MessageSession;
import com.gig.collide.message.domain.service.MessageSessionService;
import com.gig.collide.message.infrastructure.mapper.MessageSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息会话业务服务实现 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSessionServiceImpl implements MessageSessionService {

    private final MessageSessionMapper messageSessionMapper;

    // =================== 基础操作 ===================

    @Override
    @Transactional
    public MessageSession createOrUpdateSession(Long userId, Long otherUserId, Long messageId, LocalDateTime messageTime) {
        log.debug("创建或更新会话: 用户ID={}, 对方ID={}, 消息ID={}", userId, otherUserId, messageId);
        
        // 尝试更新现有会话
        int updated = messageSessionMapper.insertOrUpdate(userId, otherUserId, messageId, messageTime);
        
        if (updated > 0) {
            log.info("会话创建或更新成功: 用户ID={}, 对方ID={}", userId, otherUserId);
            return messageSessionMapper.findByUserIds(userId, otherUserId);
        } else {
            log.warn("会话创建或更新失败: 用户ID={}, 对方ID={}", userId, otherUserId);
            return null;
        }
    }

    @Override
    public MessageSession getSessionByUserIds(Long userId, Long otherUserId) {
        log.debug("查询用户会话: 用户ID={}, 对方ID={}", userId, otherUserId);
        return messageSessionMapper.findByUserIds(userId, otherUserId);
    }

    @Override
    @Transactional
    public boolean deleteSession(Long sessionId) {
        log.debug("删除会话: ID={}", sessionId);
        
        int result = messageSessionMapper.deleteById(sessionId);
        boolean success = result > 0;
        log.info("删除会话{}: ID={}", success ? "成功" : "失败", sessionId);
        return success;
    }

    // =================== 查询操作 ===================

    @Override
    public Page<MessageSession> getUserSessions(Long userId, Boolean isArchived, Boolean hasUnread,
                                              String orderBy, String orderDirection,
                                              Integer currentPage, Integer pageSize) {
        log.debug("查询用户会话列表: 用户ID={}, 页码={}, 页大小={}", userId, currentPage, pageSize);
        
        Page<MessageSession> page = new Page<>(currentPage, pageSize);
        return messageSessionMapper.findUserSessions(page, userId, isArchived, hasUnread, orderBy, orderDirection);
    }

    @Override
    public Page<MessageSession> getActiveSessions(Long userId, LocalDateTime sinceTime,
                                                Integer currentPage, Integer pageSize) {
        log.debug("查询用户活跃会话: 用户ID={}, 自时间={}, 页码={}", userId, sinceTime, currentPage);
        
        Page<MessageSession> page = new Page<>(currentPage, pageSize);
        return messageSessionMapper.findActiveSessions(page, userId, sinceTime);
    }

    // =================== 统计操作 ===================

    @Override
    public Long countUnreadSessions(Long userId) {
        log.debug("统计未读会话数: 用户ID={}", userId);
        return messageSessionMapper.countUnreadSessions(userId);
    }

    @Override
    public Long countUserSessions(Long userId, Boolean isArchived) {
        log.debug("统计用户会话数: 用户ID={}, 归档={}", userId, isArchived);
        return messageSessionMapper.countUserSessions(userId, isArchived);
    }

    // =================== 状态更新 ===================

    @Override
    @Transactional
    public boolean updateLastMessage(Long userId, Long otherUserId, Long messageId, LocalDateTime messageTime) {
        log.debug("更新会话最后消息: 用户ID={}, 对方ID={}, 消息ID={}", userId, otherUserId, messageId);
        
        int result = messageSessionMapper.updateLastMessage(userId, otherUserId, messageId, messageTime);
        boolean success = result > 0;
        log.info("更新会话最后消息{}: 用户ID={}, 对方ID={}", success ? "成功" : "失败", userId, otherUserId);
        return success;
    }

    @Override
    @Transactional
    public boolean incrementUnreadCount(Long userId, Long otherUserId) {
        log.debug("增加未读计数: 用户ID={}, 对方ID={}", userId, otherUserId);
        
        int result = messageSessionMapper.incrementUnreadCount(userId, otherUserId);
        boolean success = result > 0;
        log.info("增加未读计数{}: 用户ID={}, 对方ID={}", success ? "成功" : "失败", userId, otherUserId);
        return success;
    }

    @Override
    @Transactional
    public boolean clearUnreadCount(Long userId, Long otherUserId) {
        log.debug("清零未读计数: 用户ID={}, 对方ID={}", userId, otherUserId);
        
        int result = messageSessionMapper.clearUnreadCount(userId, otherUserId);
        boolean success = result > 0;
        log.info("清零未读计数{}: 用户ID={}, 对方ID={}", success ? "成功" : "失败", userId, otherUserId);
        return success;
    }

    @Override
    @Transactional
    public boolean updateArchiveStatus(Long sessionId, Boolean isArchived) {
        log.debug("更新会话归档状态: ID={}, 归档={}", sessionId, isArchived);
        
        int result = messageSessionMapper.updateArchiveStatus(sessionId, isArchived);
        boolean success = result > 0;
        log.info("更新会话归档状态{}: ID={}, 归档={}", success ? "成功" : "失败", sessionId, isArchived);
        return success;
    }

    // =================== 消息事件处理 ===================

    @Override
    @Transactional
    public void handleNewMessage(Long senderId, Long receiverId, Long messageId, LocalDateTime messageTime) {
        log.debug("处理新消息事件: 发送者={}, 接收者={}, 消息ID={}", senderId, receiverId, messageId);
        
        // 更新发送者的会话
        createOrUpdateSession(senderId, receiverId, messageId, messageTime);
        
        // 更新接收者的会话并增加未读计数
        createOrUpdateSession(receiverId, senderId, messageId, messageTime);
        incrementUnreadCount(receiverId, senderId);
        
        log.info("新消息事件处理完成: 发送者={}, 接收者={}", senderId, receiverId);
    }

    @Override
    @Transactional
    public void handleMessageRead(Long senderId, Long receiverId) {
        log.debug("处理消息已读事件: 发送者={}, 接收者={}", senderId, receiverId);
        
        // 清零接收者的未读计数
        clearUnreadCount(receiverId, senderId);
        
        log.info("消息已读事件处理完成: 发送者={}, 接收者={}", senderId, receiverId);
    }

    // =================== 清理操作 ===================

    @Override
    @Transactional
    public int cleanEmptySessions() {
        log.debug("清理空会话");
        
        int result = messageSessionMapper.deleteEmptySessions();
        log.info("清理空会话完成: 删除数量={}", result);
        return result;
    }

    @Override
    @Transactional
    public int cleanArchivedSessions(Integer days) {
        log.debug("清理归档会话: 保留天数={}", days);
        
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
        int result = messageSessionMapper.deleteArchivedSessions(beforeTime);
        log.info("清理归档会话完成: 删除数量={}", result);
        return result;
    }
}