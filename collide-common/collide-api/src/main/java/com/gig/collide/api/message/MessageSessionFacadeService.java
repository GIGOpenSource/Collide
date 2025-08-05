package com.gig.collide.api.message;

import com.gig.collide.api.message.request.MessageSessionCreateRequest;
import com.gig.collide.api.message.request.MessageSessionQueryRequest;
import com.gig.collide.api.message.request.MessageSessionUpdateRequest;
import com.gig.collide.api.message.response.MessageSessionResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDateTime;

/**
 * 消息会话门面服务接口 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
 * 管理用户间的会话状态、未读计数和会话列表
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageSessionFacadeService {

    // =================== 会话创建和管理 ===================

    /**
     * 创建或更新会话
     * 如果会话不存在则创建，存在则更新
     * 
     * @param request 会话创建请求
     * @return 会话信息
     */
    Result<MessageSessionResponse> createOrUpdateSession(MessageSessionCreateRequest request);

    /**
     * 根据用户ID获取会话详情
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @return 会话详情
     */
    Result<MessageSessionResponse> getSessionByUserIds(Long userId, Long otherUserId);

    /**
     * 更新会话信息
     * 
     * @param request 更新请求
     * @return 更新后的会话信息
     */
    Result<MessageSessionResponse> updateSession(MessageSessionUpdateRequest request);

    /**
     * 更新会话归档状态
     * 
     * @param sessionId 会话ID
     * @param isArchived 是否归档
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<Void> updateArchiveStatus(Long sessionId, Boolean isArchived, Long userId);

    // =================== 会话查询 ===================

    /**
     * 分页查询用户会话列表
     * 支持按归档状态、未读状态筛选
     * 
     * @param request 查询请求
     * @return 会话列表分页
     */
    Result<PageResponse<MessageSessionResponse>> queryUserSessions(MessageSessionQueryRequest request);

    /**
     * 获取用户的所有会话
     * 
     * @param userId 用户ID
     * @param isArchived 是否归档（可选）
     * @param hasUnread 是否有未读（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 会话列表分页
     */
    Result<PageResponse<MessageSessionResponse>> getUserSessions(Long userId, Boolean isArchived, Boolean hasUnread,
                                                                Integer currentPage, Integer pageSize);

    /**
     * 获取用户的活跃会话
     * 查询指定时间后有消息交互的会话
     * 
     * @param userId 用户ID
     * @param sinceTime 起始时间
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 活跃会话分页
     */
    Result<PageResponse<MessageSessionResponse>> getActiveSessions(Long userId, LocalDateTime sinceTime,
                                                                  Integer currentPage, Integer pageSize);

    /**
     * 获取用户的未读会话列表
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 未读会话分页
     */
    Result<PageResponse<MessageSessionResponse>> getUnreadSessions(Long userId, Integer currentPage, Integer pageSize);

    // =================== 统计功能 ===================

    /**
     * 统计用户的未读会话数
     * 
     * @param userId 用户ID
     * @return 未读会话数
     */
    Result<Long> getUnreadSessionCount(Long userId);

    /**
     * 统计用户的总会话数
     * 
     * @param userId 用户ID
     * @param isArchived 是否归档（可选）
     * @return 总会话数
     */
    Result<Long> getUserSessionCount(Long userId, Boolean isArchived);

    /**
     * 获取用户所有会话的未读总数
     * 
     * @param userId 用户ID
     * @return 未读总数
     */
    Result<Long> getTotalUnreadCount(Long userId);

    // =================== 会话状态管理 ===================

    /**
     * 更新会话的最后消息信息
     * 在新消息到达时自动调用
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @param lastMessageId 最后消息ID
     * @param lastMessageTime 最后消息时间
     * @return 操作结果
     */
    Result<Void> updateLastMessage(Long userId, Long otherUserId, Long lastMessageId, LocalDateTime lastMessageTime);

    /**
     * 增加会话的未读计数
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @return 操作结果
     */
    Result<Void> incrementUnreadCount(Long userId, Long otherUserId);

    /**
     * 清零会话的未读计数
     * 用户查看消息时调用
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @return 操作结果
     */
    Result<Void> clearUnreadCount(Long userId, Long otherUserId);

    /**
     * 处理新消息事件
     * 自动创建或更新相关用户的会话状态
     * 
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param messageId 消息ID
     * @param messageTime 消息时间
     * @return 处理结果
     */
    Result<Void> handleNewMessage(Long senderId, Long receiverId, Long messageId, LocalDateTime messageTime);

    // =================== 会话清理 ===================

    /**
     * 删除空会话
     * 删除没有消息记录的会话
     * 
     * @param userId 用户ID（可选，为空则清理所有用户）
     * @return 删除的会话数量
     */
    Result<Integer> deleteEmptySessions(Long userId);

    /**
     * 删除指定时间前的归档会话
     * 
     * @param beforeTime 截止时间
     * @param userId 用户ID（可选）
     * @return 删除的会话数量
     */
    Result<Integer> deleteArchivedSessions(LocalDateTime beforeTime, Long userId);

    /**
     * 批量归档会话
     * 
     * @param sessionIds 会话ID列表
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<Void> batchArchiveSessions(java.util.List<Long> sessionIds, Long userId);

    /**
     * 批量取消归档会话
     * 
     * @param sessionIds 会话ID列表
     * @param userId 操作用户ID
     * @return 操作结果
     */
    Result<Void> batchUnarchiveSessions(java.util.List<Long> sessionIds, Long userId);

    // =================== 系统功能 ===================

    /**
     * 重建会话索引
     * 系统维护功能，重新计算会话的统计信息
     * 
     * @param userId 用户ID（可选）
     * @return 处理结果
     */
    Result<String> rebuildSessionIndex(Long userId);

    /**
     * 会话系统健康检查
     * 
     * @return 系统状态
     */
    Result<String> healthCheck();
}