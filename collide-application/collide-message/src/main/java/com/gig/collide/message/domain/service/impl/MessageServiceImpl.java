package com.gig.collide.message.domain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.message.domain.entity.Message;
import com.gig.collide.message.domain.service.MessageService;
import com.gig.collide.message.infrastructure.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息业务服务实现类 - 简洁版
 * 基于message-simple.sql的无连表设计，实现核心消息功能
 * 支持私信、留言板、消息回复等功能
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

    // =================== 基础CRUD ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(Message message) {
        log.info("发送消息: senderId={}, receiverId={}, messageType={}", 
                message.getSenderId(), message.getReceiverId(), message.getMessageType());

        // 参数验证
        validateSendMessageParams(message);

        // 业务验证
        validateSendMessageBusiness(message);

        // 设置默认值
        setMessageDefaults(message);

        // 保存消息
        int result = messageMapper.insert(message);
        if (result <= 0) {
            throw new RuntimeException("消息发送失败");
        }

        log.info("消息发送成功: messageId={}", message.getId());
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message replyMessage(Message message) {
        log.info("回复消息: replyToId={}, senderId={}, receiverId={}", 
                message.getReplyToId(), message.getSenderId(), message.getReceiverId());

        // 参数验证
        validateReplyMessageParams(message);

        // 验证原消息存在性
        Message originalMessage = messageMapper.selectById(message.getReplyToId());
        if (originalMessage == null) {
            throw new IllegalArgumentException("原消息不存在");
        }

        // 验证回复权限
        validateReplyPermission(message, originalMessage);

        // 设置默认值
        setMessageDefaults(message);

        // 保存回复消息
        int result = messageMapper.insert(message);
        if (result <= 0) {
            throw new RuntimeException("回复消息失败");
        }

        log.info("回复消息成功: messageId={}, replyToId={}", message.getId(), message.getReplyToId());
        return message;
    }

    @Override
    public Message getMessageById(Long messageId) {
        log.debug("查询消息详情: messageId={}", messageId);

        if (messageId == null) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            log.warn("消息不存在: messageId={}", messageId);
        }

        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMessageStatus(Long messageId, String status, LocalDateTime readTime) {
        log.info("更新消息状态: messageId={}, status={}", messageId, status);

        // 参数验证
        if (messageId == null) {
            throw new IllegalArgumentException("消息ID不能为空");
        }
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("消息状态不能为空");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("无效的消息状态: " + status);
        }

        // 检查消息是否存在
        Message existingMessage = messageMapper.selectById(messageId);
        if (existingMessage == null) {
            throw new IllegalArgumentException("消息不存在");
        }

        // 更新状态
        int result = messageMapper.updateMessageStatus(messageId, status, readTime);
        boolean success = result > 0;

        if (success) {
            log.info("消息状态更新成功: messageId={}, status={}", messageId, status);
        } else {
            log.warn("消息状态更新失败: messageId={}, status={}", messageId, status);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessage(Long messageId, Long userId) {
        log.info("删除消息: messageId={}, userId={}", messageId, userId);

        // 参数验证
        if (messageId == null || userId == null) {
            throw new IllegalArgumentException("消息ID和用户ID不能为空");
        }

        // 检查消息是否存在
        Message existingMessage = messageMapper.selectById(messageId);
        if (existingMessage == null) {
            throw new IllegalArgumentException("消息不存在");
        }

        // 验证删除权限（发送者或接收者才能删除）
        if (!userId.equals(existingMessage.getSenderId()) && !userId.equals(existingMessage.getReceiverId())) {
            throw new IllegalArgumentException("无权限删除该消息");
        }

        // 逻辑删除
        int result = messageMapper.updateMessageStatus(messageId, "deleted", LocalDateTime.now());
        boolean success = result > 0;

        if (success) {
            log.info("消息删除成功: messageId={}, userId={}", messageId, userId);
        } else {
            log.warn("消息删除失败: messageId={}, userId={}", messageId, userId);
        }

        return success;
    }

    // =================== 查询功能 ===================

    @Override
    public IPage<Message> findChatHistory(Long userId1, Long userId2, String status, 
                                         Integer currentPage, Integer pageSize) {
        log.debug("查询聊天记录: userId1={}, userId2={}, status={}, page={}/{}", 
                userId1, userId2, status, currentPage, pageSize);

        // 参数验证
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("不能查询与自己的聊天记录");
        }

        Page<Message> page = createPage(currentPage, pageSize);
        return messageMapper.findChatHistory(page, userId1, userId2, status);
    }

    @Override
    public IPage<Message> findWithConditions(Long senderId, Long receiverId, String messageType, 
                                            String status, Boolean isPinned, Long replyToId, 
                                            String keyword, LocalDateTime startTime, LocalDateTime endTime,
                                            String orderBy, String orderDirection,
                                            Integer currentPage, Integer pageSize) {
        log.debug("条件查询消息: senderId={}, receiverId={}, messageType={}, page={}/{}", 
                senderId, receiverId, messageType, currentPage, pageSize);

        Page<Message> page = createPage(currentPage, pageSize);
        return messageMapper.findWithConditions(page, senderId, receiverId, messageType, 
                status, isPinned, replyToId, keyword, startTime, endTime, orderBy, orderDirection);
    }

    @Override
    public IPage<Message> findWallMessages(Long receiverId, String status, 
                                          Integer currentPage, Integer pageSize) {
        log.debug("查询留言板消息: receiverId={}, status={}, page={}/{}", 
                receiverId, status, currentPage, pageSize);

        if (receiverId == null) {
            throw new IllegalArgumentException("接收者ID不能为空");
        }

        Page<Message> page = createPage(currentPage, pageSize);
        return messageMapper.findWallMessages(page, receiverId, status);
    }

    @Override
    public IPage<Message> findReplies(Long replyToId, String status, 
                                     Integer currentPage, Integer pageSize) {
        log.debug("查询消息回复: replyToId={}, status={}, page={}/{}", 
                replyToId, status, currentPage, pageSize);

        if (replyToId == null) {
            throw new IllegalArgumentException("原消息ID不能为空");
        }

        Page<Message> page = createPage(currentPage, pageSize);
        return messageMapper.findReplies(page, replyToId, status);
    }

    @Override
    public IPage<Message> searchMessages(Long userId, String keyword, String status, 
                                        Integer currentPage, Integer pageSize) {
        log.debug("搜索用户消息: userId={}, keyword={}, status={}, page={}/{}", 
                userId, keyword, status, currentPage, pageSize);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        Page<Message> page = createPage(currentPage, pageSize);
        return messageMapper.searchMessages(page, userId, keyword, status);
    }

    // =================== 统计功能 ===================

    @Override
    public Long countUnreadMessages(Long receiverId) {
        log.debug("统计未读消息数: receiverId={}", receiverId);

        if (receiverId == null) {
            throw new IllegalArgumentException("接收者ID不能为空");
        }

        return messageMapper.countUnreadMessages(receiverId);
    }

    @Override
    public Long countUnreadWithUser(Long receiverId, Long senderId) {
        log.debug("统计与用户的未读消息数: receiverId={}, senderId={}", receiverId, senderId);

        if (receiverId == null || senderId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        return messageMapper.countUnreadWithUser(receiverId, senderId);
    }

    @Override
    public Long countSentMessages(Long senderId, LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("统计发送消息数: senderId={}, startTime={}, endTime={}", senderId, startTime, endTime);

        if (senderId == null) {
            throw new IllegalArgumentException("发送者ID不能为空");
        }

        return messageMapper.countSentMessages(senderId, startTime, endTime);
    }

    @Override
    public Long countReceivedMessages(Long receiverId, LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("统计接收消息数: receiverId={}, startTime={}, endTime={}", receiverId, startTime, endTime);

        if (receiverId == null) {
            throw new IllegalArgumentException("接收者ID不能为空");
        }

        return messageMapper.countReceivedMessages(receiverId, startTime, endTime);
    }

    // =================== 批量操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchMarkAsRead(List<Long> messageIds, Long receiverId) {
        log.info("批量标记已读: messageIds.size={}, receiverId={}", 
                messageIds != null ? messageIds.size() : 0, receiverId);

        if (messageIds == null || messageIds.isEmpty()) {
            throw new IllegalArgumentException("消息ID列表不能为空");
        }
        if (receiverId == null) {
            throw new IllegalArgumentException("接收者ID不能为空");
        }

        LocalDateTime readTime = LocalDateTime.now();
        int result = messageMapper.batchMarkAsRead(messageIds, receiverId, readTime);

        log.info("批量标记已读完成: 成功数量={}", result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteMessages(List<Long> messageIds, Long userId) {
        log.info("批量删除消息: messageIds.size={}, userId={}", 
                messageIds != null ? messageIds.size() : 0, userId);

        if (messageIds == null || messageIds.isEmpty()) {
            throw new IllegalArgumentException("消息ID列表不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        int result = messageMapper.batchDeleteMessages(messageIds, userId);

        log.info("批量删除消息完成: 成功数量={}", result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markSessionMessagesAsRead(Long receiverId, Long senderId) {
        log.info("标记会话消息已读: receiverId={}, senderId={}", receiverId, senderId);

        if (receiverId == null || senderId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        LocalDateTime readTime = LocalDateTime.now();
        int result = messageMapper.markSessionMessagesAsRead(receiverId, senderId, readTime);

        log.info("标记会话消息已读完成: 成功数量={}", result);
        return result;
    }

    // =================== 高级功能 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePinnedStatus(Long messageId, Boolean isPinned, Long receiverId) {
        log.info("更新消息置顶状态: messageId={}, isPinned={}, receiverId={}", 
                messageId, isPinned, receiverId);

        // 参数验证
        if (messageId == null || isPinned == null || receiverId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 检查消息是否存在
        Message existingMessage = messageMapper.selectById(messageId);
        if (existingMessage == null) {
            throw new IllegalArgumentException("消息不存在");
        }

        // 验证权限（只有接收者可以置顶）
        if (!receiverId.equals(existingMessage.getReceiverId())) {
            throw new IllegalArgumentException("只有接收者可以设置置顶状态");
        }

        int result = messageMapper.updatePinnedStatus(messageId, isPinned, receiverId);
        boolean success = result > 0;

        if (success) {
            log.info("消息置顶状态更新成功: messageId={}, isPinned={}", messageId, isPinned);
        } else {
            log.warn("消息置顶状态更新失败: messageId={}, isPinned={}", messageId, isPinned);
        }

        return success;
    }

    @Override
    public Message getLatestMessageBetweenUsers(Long userId1, Long userId2) {
        log.debug("获取用户间最新消息: userId1={}, userId2={}", userId1, userId2);

        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        return messageMapper.getLatestMessageBetweenUsers(userId1, userId2);
    }

    @Override
    public List<Long> getRecentChatUsers(Long userId, Integer limit) {
        log.debug("获取最近聊天用户: userId={}, limit={}", userId, limit);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        int validLimit = limit != null && limit > 0 ? Math.min(limit, 100) : 20;
        return messageMapper.getRecentChatUsers(userId, validLimit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupExpiredMessages(LocalDateTime beforeTime) {
        log.info("清理过期删除消息: beforeTime={}", beforeTime);

        if (beforeTime == null) {
            throw new IllegalArgumentException("截止时间不能为空");
        }

        int result = messageMapper.physicalDeleteExpiredMessages(beforeTime);

        log.info("清理过期删除消息完成: 清理数量={}", result);
        return result;
    }

    // =================== 私有方法 ===================

    /**
     * 验证发送消息参数
     */
    private void validateSendMessageParams(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("消息对象不能为空");
        }
        if (message.getSenderId() == null) {
            throw new IllegalArgumentException("发送者ID不能为空");
        }
        if (message.getReceiverId() == null) {
            throw new IllegalArgumentException("接收者ID不能为空");
        }
        if (!StringUtils.hasText(message.getContent())) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        if (!StringUtils.hasText(message.getMessageType())) {
            throw new IllegalArgumentException("消息类型不能为空");
        }
    }

    /**
     * 验证发送消息业务规则
     */
    private void validateSendMessageBusiness(Message message) {
        // 不能给自己发消息
        if (message.getSenderId().equals(message.getReceiverId())) {
            throw new IllegalArgumentException("不能给自己发送消息");
        }

        // 验证消息类型
        if (!isValidMessageType(message.getMessageType())) {
            throw new IllegalArgumentException("无效的消息类型: " + message.getMessageType());
        }

        // 验证消息内容长度
        if (message.getContent().length() > getMaxContentLength(message.getMessageType())) {
            throw new IllegalArgumentException("消息内容超出长度限制");
        }
    }

    /**
     * 验证回复消息参数
     */
    private void validateReplyMessageParams(Message message) {
        validateSendMessageParams(message);
        if (message.getReplyToId() == null) {
            throw new IllegalArgumentException("回复消息ID不能为空");
        }
    }

    /**
     * 验证回复权限
     */
    private void validateReplyPermission(Message replyMessage, Message originalMessage) {
        // 只能回复给原消息的参与者
        Long senderId = replyMessage.getSenderId();
        Long receiverId = replyMessage.getReceiverId();
        Long originalSenderId = originalMessage.getSenderId();
        Long originalReceiverId = originalMessage.getReceiverId();

        boolean isValidReply = (senderId.equals(originalSenderId) && receiverId.equals(originalReceiverId)) ||
                              (senderId.equals(originalReceiverId) && receiverId.equals(originalSenderId));

        if (!isValidReply) {
            throw new IllegalArgumentException("回复消息的参与者必须是原消息的参与者");
        }
    }

    /**
     * 设置消息默认值
     */
    private void setMessageDefaults(Message message) {
        if (message.getStatus() == null) {
            message.setStatus("sent");
        }
        if (message.getIsPinned() == null) {
            message.setIsPinned(false);
        }
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 创建分页对象
     */
    private Page<Message> createPage(Integer currentPage, Integer pageSize) {
        int page = currentPage != null && currentPage > 0 ? currentPage : 1;
        int size = pageSize != null && pageSize > 0 ? Math.min(pageSize, 100) : 20;
        return new Page<>(page, size);
    }

    /**
     * 验证消息状态
     */
    private boolean isValidStatus(String status) {
        return "sent".equals(status) || "delivered".equals(status) || 
               "read".equals(status) || "deleted".equals(status);
    }

    /**
     * 验证消息类型
     */
    private boolean isValidMessageType(String messageType) {
        return "text".equals(messageType) || "image".equals(messageType) || 
               "file".equals(messageType) || "system".equals(messageType);
    }

    /**
     * 获取消息内容最大长度
     */
    private int getMaxContentLength(String messageType) {
        switch (messageType) {
            case "text":
                return 1000;
            case "image":
            case "file":
                return 200;
            case "system":
                return 500;
            default:
                return 1000;
        }
    }
}