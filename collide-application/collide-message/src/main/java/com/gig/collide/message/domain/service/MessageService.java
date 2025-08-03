package com.gig.collide.message.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.message.domain.entity.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息业务服务接口 - 简洁版
 * 基于message-simple.sql的单表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageService {

    // =================== 基础操作 ===================

    /**
     * 发送消息
     */
    Message sendMessage(Message message);

    /**
     * 根据ID获取消息
     */
    Message getMessageById(Long messageId);

    /**
     * 标记消息为已读
     */
    boolean markMessageAsRead(Long messageId, Long userId);

    /**
     * 批量标记消息为已读
     */
    boolean batchMarkAsRead(List<Long> messageIds, Long userId);

    /**
     * 删除消息（逻辑删除）
     */
    boolean deleteMessage(Long messageId, Long userId);

    /**
     * 批量删除消息
     */
    boolean batchDeleteMessages(List<Long> messageIds, Long userId);

    // =================== 查询操作 ===================

    /**
     * 分页查询消息
     */
    Page<Message> queryMessages(Long senderId, Long receiverId, String messageType,
                              String status, Boolean isPinned, Long replyToId,
                              String keyword, LocalDateTime startTime, LocalDateTime endTime,
                              String orderBy, String orderDirection,
                              Integer currentPage, Integer pageSize);

    /**
     * 获取两用户间的聊天记录
     */
    Page<Message> getChatHistory(Long userId1, Long userId2, Integer currentPage, Integer pageSize);

    /**
     * 获取用户留言板消息
     */
    Page<Message> getUserWallMessages(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取消息的回复列表
     */
    Page<Message> getMessageReplies(Long messageId, Integer currentPage, Integer pageSize);

    /**
     * 搜索消息
     */
    Page<Message> searchMessages(Long userId, String keyword, Integer currentPage, Integer pageSize);

    // =================== 统计操作 ===================

    /**
     * 获取用户未读消息数
     */
    Long getUnreadCount(Long userId);

    /**
     * 获取与某用户的未读消息数
     */
    Long getUnreadCountWithUser(Long userId, Long otherUserId);

    /**
     * 获取消息统计信息
     */
    Map<String, Object> getMessageStatistics(Long userId);

    // =================== 状态更新 ===================

    /**
     * 更新消息状态
     */
    boolean updateMessageStatus(Long messageId, String status);

    /**
     * 置顶/取消置顶消息
     */
    boolean pinMessage(Long messageId, Long userId, Boolean isPinned);

    /**
     * 标记会话中所有消息为已读
     */
    boolean markSessionMessagesAsRead(Long receiverId, Long senderId);

    // =================== 工具方法 ===================

    /**
     * 验证用户是否可以查看消息
     */
    boolean canUserViewMessage(Long messageId, Long userId);

    /**
     * 验证用户是否可以操作消息
     */
    boolean canUserOperateMessage(Long messageId, Long userId);

    /**
     * 获取会话中的最新消息
     */
    Message getLatestMessageBetweenUsers(Long userId1, Long userId2);

    /**
     * 获取用户最近的会话用户列表
     */
    List<Long> getRecentChatUsers(Long userId, Integer limit);

    /**
     * 清理过期的已删除消息
     */
    int cleanExpiredDeletedMessages(Integer days);
}