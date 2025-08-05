package com.gig.collide.api.message;

import com.gig.collide.api.message.request.MessageCreateRequest;
import com.gig.collide.api.message.request.MessageQueryRequest;
import com.gig.collide.api.message.request.MessageUpdateRequest;
import com.gig.collide.api.message.response.MessageResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息门面服务接口 - 简洁版
 * 基于message-simple.sql的无连表设计，实现核心消息功能
 * 支持私信、留言板、消息回复等功能
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageFacadeService {

    // =================== 消息发送 ===================

    /**
     * 发送消息
     * 支持文本、图片、文件等多种消息类型
     * 包含用户验证、权限检查、消息过滤等业务逻辑
     * 
     * @param request 消息创建请求
     * @return 发送的消息
     */
    Result<MessageResponse> sendMessage(MessageCreateRequest request);

    /**
     * 回复消息
     * 回复指定的消息，建立回复关系
     * 
     * @param request 回复消息创建请求
     * @return 回复的消息
     */
    Result<MessageResponse> replyMessage(MessageCreateRequest request);

    /**
     * 发送留言板消息
     * 发送到用户留言板的公开消息
     * 
     * @param request 留言消息创建请求
     * @return 发送的留言
     */
    Result<MessageResponse> sendWallMessage(MessageCreateRequest request);

    // =================== 消息查询 ===================

    /**
     * 根据ID获取消息详情
     * 
     * @param messageId 消息ID
     * @param userId 查看者用户ID（用于权限验证）
     * @return 消息详情
     */
    Result<MessageResponse> getMessageById(Long messageId, Long userId);

    /**
     * 分页查询消息
     * 支持多维度条件查询
     * 
     * @param request 查询请求
     * @return 消息列表分页
     */
    Result<PageResponse<MessageResponse>> queryMessages(MessageQueryRequest request);

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
    Result<PageResponse<MessageResponse>> getChatHistory(Long userId1, Long userId2, String status,
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
    Result<PageResponse<MessageResponse>> getWallMessages(Long receiverId, String status,
                                                         Integer currentPage, Integer pageSize);

    /**
     * 查询消息回复列表
     * 
     * @param replyToId 原消息ID
     * @param status 消息状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 回复列表分页
     */
    Result<PageResponse<MessageResponse>> getMessageReplies(Long replyToId, String status,
                                                           Integer currentPage, Integer pageSize);

    /**
     * 搜索用户消息
     * 支持消息内容关键词搜索
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param status 消息状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 搜索结果分页
     */
    Result<PageResponse<MessageResponse>> searchMessages(Long userId, String keyword, String status,
                                                        Integer currentPage, Integer pageSize);

    // =================== 消息管理 ===================

    /**
     * 更新消息
     * 支持更新消息内容（仅限发送者）
     * 
     * @param request 更新请求
     * @return 更新后的消息
     */
    Result<MessageResponse> updateMessage(MessageUpdateRequest request);

    /**
     * 删除消息
     * 逻辑删除，支持发送者和接收者删除
     * 
     * @param messageId 消息ID
     * @param userId 操作用户ID
     * @return 删除结果
     */
    Result<Void> deleteMessage(Long messageId, Long userId);

    /**
     * 标记消息为已读
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> markAsRead(Long messageId, Long userId);

    /**
     * 更新消息置顶状态
     * 
     * @param messageId 消息ID
     * @param isPinned 是否置顶
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<Void> updatePinnedStatus(Long messageId, Boolean isPinned, Long userId);

    // =================== 批量操作 ===================

    /**
     * 批量标记消息为已读
     * 
     * @param messageIds 消息ID列表
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> batchMarkAsRead(List<Long> messageIds, Long userId);

    /**
     * 批量删除消息
     * 
     * @param messageIds 消息ID列表
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<Void> batchDeleteMessages(List<Long> messageIds, Long userId);

    /**
     * 标记会话中所有消息为已读
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @return 操作结果
     */
    Result<Void> markSessionAsRead(Long receiverId, Long senderId);

    // =================== 统计功能 ===================

    /**
     * 统计用户未读消息数
     * 
     * @param userId 用户ID
     * @return 未读消息数
     */
    Result<Long> getUnreadMessageCount(Long userId);

    /**
     * 统计与某用户的未读消息数
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @return 未读消息数
     */
    Result<Long> getUnreadCountWithUser(Long receiverId, Long senderId);

    /**
     * 统计用户发送的消息数
     * 
     * @param userId 用户ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 发送消息数
     */
    Result<Long> getSentMessageCount(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计用户接收的消息数
     * 
     * @param userId 用户ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 接收消息数
     */
    Result<Long> getReceivedMessageCount(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    // =================== 会话管理 ===================

    /**
     * 获取用户最近的聊天用户列表
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近聊天用户ID列表
     */
    Result<List<Long>> getRecentChatUsers(Long userId, Integer limit);

    /**
     * 获取两用户间的最新消息
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 最新消息
     */
    Result<MessageResponse> getLatestMessage(Long userId1, Long userId2);

    // =================== 系统管理 ===================

    /**
     * 清理过期删除消息
     * 系统定时任务调用，物理删除过期的已删除消息
     * 
     * @param beforeTime 截止时间
     * @return 清理的消息数量
     */
    Result<Integer> cleanupExpiredMessages(LocalDateTime beforeTime);

    /**
     * 消息系统健康检查
     * 
     * @return 系统状态
     */
    Result<String> healthCheck();
}