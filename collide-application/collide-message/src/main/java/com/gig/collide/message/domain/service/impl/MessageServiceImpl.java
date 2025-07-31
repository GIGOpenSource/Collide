package com.gig.collide.message.domain.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.message.domain.entity.Message;
import com.gig.collide.message.domain.service.MessageService;
import com.gig.collide.message.infrastructure.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息业务服务实现 - 简洁版
 * 基于message-simple.sql的单表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    // =================== 基础操作 ===================

    @Override
    @Transactional
    public Message sendMessage(Message message) {
        log.debug("发送消息: 发送者={}, 接收者={}, 类型={}", 
                 message.getSenderId(), message.getReceiverId(), message.getMessageType());
        
        // 设置默认值
        if (message.getStatus() == null) {
            message.setStatus("sent");
        }
        if (message.getMessageType() == null) {
            message.setMessageType("text");
        }
        if (message.getIsPinned() == null) {
            message.setIsPinned(false);
        }
        
        messageMapper.insert(message);
        log.info("消息发送成功: ID={}", message.getId());
        return message;
    }

    @Override
    public Message getMessageById(Long messageId) {
        log.debug("获取消息详情: ID={}", messageId);
        return messageMapper.selectById(messageId);
    }

    @Override
    @Transactional
    public boolean markMessageAsRead(Long messageId, Long userId) {
        log.debug("标记消息已读: ID={}, 用户ID={}", messageId, userId);
        
        Message message = messageMapper.selectById(messageId);
        if (message == null || !message.canBeViewedBy(userId)) {
            log.warn("消息不存在或无权限访问: ID={}, 用户ID={}", messageId, userId);
            return false;
        }
        
        if (message.isRead()) {
            log.debug("消息已是已读状态: ID={}", messageId);
            return true;
        }
        
        int result = messageMapper.updateMessageStatus(messageId, "read", LocalDateTime.now());
        boolean success = result > 0;
        log.info("标记消息已读{}: ID={}", success ? "成功" : "失败", messageId);
        return success;
    }

    @Override
    @Transactional
    public boolean batchMarkAsRead(List<Long> messageIds, Long userId) {
        log.debug("批量标记消息已读: 数量={}, 用户ID={}", messageIds.size(), userId);
        
        if (messageIds.isEmpty()) {
            return true;
        }
        
        int result = messageMapper.batchMarkAsRead(messageIds, userId, LocalDateTime.now());
        boolean success = result > 0;
        log.info("批量标记消息已读{}: 影响行数={}", success ? "成功" : "失败", result);
        return success;
    }

    @Override
    @Transactional
    public boolean deleteMessage(Long messageId, Long userId) {
        log.debug("删除消息: ID={}, 用户ID={}", messageId, userId);
        
        Message message = messageMapper.selectById(messageId);
        if (message == null || !message.canBeViewedBy(userId)) {
            log.warn("消息不存在或无权限删除: ID={}, 用户ID={}", messageId, userId);
            return false;
        }
        
        int result = messageMapper.updateMessageStatus(messageId, "deleted", null);
        boolean success = result > 0;
        log.info("删除消息{}: ID={}", success ? "成功" : "失败", messageId);
        return success;
    }

    @Override
    @Transactional
    public boolean batchDeleteMessages(List<Long> messageIds, Long userId) {
        log.debug("批量删除消息: 数量={}, 用户ID={}", messageIds.size(), userId);
        
        if (messageIds.isEmpty()) {
            return true;
        }
        
        int result = messageMapper.batchDeleteMessages(messageIds, userId);
        boolean success = result > 0;
        log.info("批量删除消息{}: 影响行数={}", success ? "成功" : "失败", result);
        return success;
    }

    // =================== 查询操作 ===================

    @Override
    public Page<Message> queryMessages(Long senderId, Long receiverId, String messageType,
                                     String status, Boolean isPinned, Long replyToId,
                                     String keyword, LocalDateTime startTime, LocalDateTime endTime,
                                     String orderBy, String orderDirection,
                                     Integer currentPage, Integer pageSize) {
        log.debug("条件查询消息: 页码={}, 页大小={}", currentPage, pageSize);
        
        Page<Message> page = new Page<>(currentPage, pageSize);
        return messageMapper.findWithConditions(page, senderId, receiverId, messageType,
                                               status, isPinned, replyToId, keyword,
                                               startTime, endTime, orderBy, orderDirection);
    }

    @Override
    public Page<Message> getChatHistory(Long userId1, Long userId2, Integer currentPage, Integer pageSize) {
        log.debug("获取聊天记录: 用户1={}, 用户2={}, 页码={}", userId1, userId2, currentPage);
        
        Page<Message> page = new Page<>(currentPage, pageSize);
        return messageMapper.findChatHistory(page, userId1, userId2, "deleted");
    }

    @Override
    public Page<Message> getUserWallMessages(Long userId, Integer currentPage, Integer pageSize) {
        log.debug("获取用户留言板: 用户ID={}, 页码={}", userId, currentPage);
        
        Page<Message> page = new Page<>(currentPage, pageSize);
        return messageMapper.findWallMessages(page, userId, "deleted");
    }

    @Override
    public Page<Message> getMessageReplies(Long messageId, Integer currentPage, Integer pageSize) {
        log.debug("获取消息回复: 消息ID={}, 页码={}", messageId, currentPage);
        
        Page<Message> page = new Page<>(currentPage, pageSize);
        return messageMapper.findReplies(page, messageId, "deleted");
    }

    @Override
    public Page<Message> searchMessages(Long userId, String keyword, Integer currentPage, Integer pageSize) {
        log.debug("搜索用户消息: 用户ID={}, 关键词={}, 页码={}", userId, keyword, currentPage);
        
        Page<Message> page = new Page<>(currentPage, pageSize);
        return messageMapper.searchMessages(page, userId, keyword, "deleted");
    }

    // =================== 统计操作 ===================

    @Override
    public Long getUnreadCount(Long userId) {
        log.debug("获取未读消息数: 用户ID={}", userId);
        return messageMapper.countUnreadMessages(userId);
    }

    @Override
    public Long getUnreadCountWithUser(Long userId, Long otherUserId) {
        log.debug("获取与用户的未读消息数: 用户ID={}, 对方ID={}", userId, otherUserId);
        return messageMapper.countUnreadWithUser(userId, otherUserId);
    }

    @Override
    public Map<String, Object> getMessageStatistics(Long userId) {
        log.debug("获取消息统计: 用户ID={}", userId);
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 当前时间的30天前
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
        // 统计发送数和接收数
        Long sentCount = messageMapper.countSentMessages(userId, thirtyDaysAgo, null);
        Long receivedCount = messageMapper.countReceivedMessages(userId, thirtyDaysAgo, null);
        Long unreadCount = messageMapper.countUnreadMessages(userId);
        
        statistics.put("sent_count", sentCount != null ? sentCount : 0);
        statistics.put("received_count", receivedCount != null ? receivedCount : 0);
        statistics.put("unread_count", unreadCount != null ? unreadCount : 0);
        statistics.put("total_count", (sentCount != null ? sentCount : 0) + (receivedCount != null ? receivedCount : 0));
        
        log.info("消息统计结果: 用户ID={}, 发送={}, 接收={}, 未读={}", 
                userId, sentCount, receivedCount, unreadCount);
        
        return statistics;
    }

    // =================== 状态更新 ===================

    @Override
    @Transactional
    public boolean updateMessageStatus(Long messageId, String status) {
        log.debug("更新消息状态: ID={}, 状态={}", messageId, status);
        
        LocalDateTime readTime = "read".equals(status) ? LocalDateTime.now() : null;
        int result = messageMapper.updateMessageStatus(messageId, status, readTime);
        boolean success = result > 0;
        log.info("更新消息状态{}: ID={}, 状态={}", success ? "成功" : "失败", messageId, status);
        return success;
    }

    @Override
    @Transactional
    public boolean pinMessage(Long messageId, Long userId, Boolean isPinned) {
        log.debug("设置消息置顶: ID={}, 用户ID={}, 置顶={}", messageId, userId, isPinned);
        
        int result = messageMapper.updatePinnedStatus(messageId, isPinned, userId);
        boolean success = result > 0;
        log.info("设置消息置顶{}: ID={}, 置顶={}", success ? "成功" : "失败", messageId, isPinned);
        return success;
    }

    @Override
    @Transactional
    public boolean markSessionMessagesAsRead(Long receiverId, Long senderId) {
        log.debug("标记会话所有消息已读: 接收者={}, 发送者={}", receiverId, senderId);
        
        int result = messageMapper.markSessionMessagesAsRead(receiverId, senderId, LocalDateTime.now());
        boolean success = result > 0;
        log.info("标记会话消息已读{}: 影响行数={}", success ? "成功" : "失败", result);
        return success;
    }

    // =================== 工具方法 ===================

    @Override
    public boolean canUserViewMessage(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        return message != null && message.canBeViewedBy(userId) && !message.isDeleted();
    }

    @Override
    public boolean canUserOperateMessage(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        return message != null && message.canBeViewedBy(userId) && !message.isDeleted();
    }

    @Override
    public Message getLatestMessageBetweenUsers(Long userId1, Long userId2) {
        log.debug("获取会话最新消息: 用户1={}, 用户2={}", userId1, userId2);
        return messageMapper.getLatestMessageBetweenUsers(userId1, userId2);
    }

    @Override
    public List<Long> getRecentChatUsers(Long userId, Integer limit) {
        log.debug("获取最近聊天用户: 用户ID={}, 限制={}", userId, limit);
        return messageMapper.getRecentChatUsers(userId, limit);
    }

    @Override
    @Transactional
    public int cleanExpiredDeletedMessages(Integer days) {
        log.debug("清理过期已删除消息: 保留天数={}", days);
        
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
        int result = messageMapper.physicalDeleteExpiredMessages(beforeTime);
        log.info("清理过期消息完成: 删除数量={}", result);
        return result;
    }
}