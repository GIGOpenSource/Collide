package com.gig.collide.message.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.message.domain.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息业务服务接口 - 简洁版
 * 基于message-simple.sql的无连表设计，实现核心消息功能
 * 支持私信、留言板、消息回复等功能
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageService {

    // =================== 基础CRUD ===================

    /**
     * 发送消息
     * 包含参数验证、用户存在性验证、消息类型验证等业务逻辑
     * 
     * @param message 消息实体
     * @return 发送的消息
     */
    Message sendMessage(Message message);

    /**
     * 回复消息
     * 验证原消息存在性，设置回复关系
     * 
     * @param message 回复消息实体
     * @return 回复的消息
     */
    Message replyMessage(Message message);

    /**
     * 根据ID获取消息详情
     * 
     * @param messageId 消息ID
     * @return 消息详情
     */
    Message getMessageById(Long messageId);

    /**
     * 更新消息状态
     * 支持已读、删除等状态更新
     * 
     * @param messageId 消息ID
     * @param status 新状态
     * @param readTime 读取时间（可选）
     * @return 是否成功
     */
    boolean updateMessageStatus(Long messageId, String status, LocalDateTime readTime);

    /**
     * 删除消息（逻辑删除）
     * 
     * @param messageId 消息ID
     * @param userId 操作用户ID
     * @return 是否成功
     */
    boolean deleteMessage(Long messageId, Long userId);

    // =================== 查询功能 ===================

    /**
     * 查询两用户间的聊天记录
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param status 消息状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 聊天记录分页
     */
    IPage<Message> findChatHistory(Long userId1, Long userId2, String status, 
                                  Integer currentPage, Integer pageSize);

    /**
     * 条件查询消息
     * 支持多维度条件查询和排序
     * 
     * @param senderId 发送者ID（可选）
     * @param receiverId 接收者ID（可选）
     * @param messageType 消息类型（可选）
     * @param status 消息状态（可选）
     * @param isPinned 是否置顶（可选）
     * @param replyToId 回复消息ID（可选）
     * @param keyword 关键词搜索（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 查询结果分页
     */
    IPage<Message> findWithConditions(Long senderId, Long receiverId, String messageType, 
                                     String status, Boolean isPinned, Long replyToId, 
                                     String keyword, LocalDateTime startTime, LocalDateTime endTime,
                                     String orderBy, String orderDirection,
                                     Integer currentPage, Integer pageSize);

    /**
     * 查询用户留言板消息
     * 
     * @param receiverId 接收者ID
     * @param status 消息状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 留言板消息分页
     */
    IPage<Message> findWallMessages(Long receiverId, String status, 
                                   Integer currentPage, Integer pageSize);

    /**
     * 查询消息的回复列表
     * 
     * @param replyToId 原消息ID
     * @param status 消息状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 回复列表分页
     */
    IPage<Message> findReplies(Long replyToId, String status, 
                              Integer currentPage, Integer pageSize);

    /**
     * 搜索用户消息
     * 支持内容关键词搜索
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param status 消息状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 搜索结果分页
     */
    IPage<Message> searchMessages(Long userId, String keyword, String status, 
                                 Integer currentPage, Integer pageSize);

    // =================== 统计功能 ===================

    /**
     * 统计用户未读消息数
     * 
     * @param receiverId 接收者ID
     * @return 未读消息数
     */
    Long countUnreadMessages(Long receiverId);

    /**
     * 统计与某用户的未读消息数
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @return 未读消息数
     */
    Long countUnreadWithUser(Long receiverId, Long senderId);

    /**
     * 统计用户发送的消息数
     * 
     * @param senderId 发送者ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 发送消息数
     */
    Long countSentMessages(Long senderId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计用户接收的消息数
     * 
     * @param receiverId 接收者ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 接收消息数
     */
    Long countReceivedMessages(Long receiverId, LocalDateTime startTime, LocalDateTime endTime);

    // =================== 批量操作 ===================

    /**
     * 批量标记消息为已读
     * 
     * @param messageIds 消息ID列表
     * @param receiverId 接收者ID
     * @return 标记成功的数量
     */
    int batchMarkAsRead(List<Long> messageIds, Long receiverId);

    /**
     * 批量删除消息（逻辑删除）
     * 
     * @param messageIds 消息ID列表
     * @param userId 操作用户ID
     * @return 删除成功的数量
     */
    int batchDeleteMessages(List<Long> messageIds, Long userId);

    /**
     * 标记会话中所有消息为已读
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @return 标记成功的数量
     */
    int markSessionMessagesAsRead(Long receiverId, Long senderId);

    // =================== 高级功能 ===================

    /**
     * 更新消息置顶状态
     * 
     * @param messageId 消息ID
     * @param isPinned 是否置顶
     * @param receiverId 接收者ID（验证权限）
     * @return 是否成功
     */
    boolean updatePinnedStatus(Long messageId, Boolean isPinned, Long receiverId);

    /**
     * 获取用户会话中的最新消息
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 最新消息
     */
    Message getLatestMessageBetweenUsers(Long userId1, Long userId2);

    /**
     * 获取用户最近的会话用户列表
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近会话用户ID列表
     */
    List<Long> getRecentChatUsers(Long userId, Integer limit);

    /**
     * 清理过期删除消息
     * 物理删除指定时间前的已删除消息
     * 
     * @param beforeTime 截止时间
     * @return 清理的消息数量
     */
    int cleanupExpiredMessages(LocalDateTime beforeTime);
}