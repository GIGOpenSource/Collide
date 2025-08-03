package com.gig.collide.api.message;

import com.gig.collide.api.message.request.MessageCreateRequest;
import com.gig.collide.api.message.request.MessageQueryRequest;
import com.gig.collide.api.message.request.MessageSessionQueryRequest;
import com.gig.collide.api.message.response.MessageResponse;
import com.gig.collide.api.message.response.MessageSessionResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

/**
 * 私信消息门面服务接口 - 简洁版
 * 基于message-simple.sql的单表设计，实现核心私信功能
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageFacadeService {
    
    /**
     * 发送消息
     * 支持文本、图片、文件等多种消息类型
     * 
     * @param request 发送消息请求
     * @return 消息结果
     */
    Result<MessageResponse> sendMessage(MessageCreateRequest request);
    
    /**
     * 获取消息详情
     * 根据消息ID获取详细信息
     * 
     * @param messageId 消息ID
     * @param userId 查看者用户ID（权限验证）
     * @return 消息详情
     */
    Result<MessageResponse> getMessageById(Long messageId, Long userId);
    
    /**
     * 标记消息为已读
     * 更新消息状态为已读，记录已读时间
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> markMessageAsRead(Long messageId, Long userId);
    
    /**
     * 批量标记消息为已读
     * 批量更新多条消息的状态
     * 
     * @param messageIds 消息ID列表
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> batchMarkAsRead(java.util.List<Long> messageIds, Long userId);
    
    /**
     * 删除消息
     * 逻辑删除，更新状态为deleted
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> deleteMessage(Long messageId, Long userId);
    
    /**
     * 分页查询消息
     * 支持按发送者、接收者、状态等条件查询
     * 
     * @param request 查询请求
     * @return 消息列表
     */
    Result<PageResponse<MessageResponse>> queryMessages(MessageQueryRequest request);
    
    /**
     * 获取两用户间的聊天记录
     * 查询两个用户之间的所有消息
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 聊天记录
     */
    Result<PageResponse<MessageResponse>> getChatHistory(Long userId1, Long userId2, Integer currentPage, Integer pageSize);
    
    /**
     * 获取用户会话列表
     * 获取用户的所有聊天会话（最近聊天的人）
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 会话列表
     */
    Result<PageResponse<MessageSessionResponse>> getUserSessions(Long userId, Integer currentPage, Integer pageSize);
    
    /**
     * 获取用户未读消息数
     * 统计用户的未读消息总数
     * 
     * @param userId 用户ID
     * @return 未读消息数
     */
    Result<Long> getUnreadCount(Long userId);
    
    /**
     * 获取与某用户的未读消息数
     * 统计与特定用户的未读消息数
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @return 未读消息数
     */
    Result<Long> getUnreadCountWithUser(Long userId, Long otherUserId);
    
    /**
     * 获取用户留言板消息
     * 查询用户个人页面的留言（包含置顶消息）
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 留言板消息
     */
    Result<PageResponse<MessageResponse>> getUserWallMessages(Long userId, Integer currentPage, Integer pageSize);
    
    /**
     * 置顶/取消置顶消息
     * 用于留言板功能
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @param isPinned 是否置顶
     * @return 操作结果
     */
    Result<Void> pinMessage(Long messageId, Long userId, Boolean isPinned);
    
    /**
     * 回复消息
     * 发送回复消息，设置reply_to_id
     * 
     * @param request 回复消息请求
     * @return 回复结果
     */
    Result<MessageResponse> replyMessage(MessageCreateRequest request);
    
    /**
     * 获取消息的回复列表
     * 查询某条消息的所有回复
     * 
     * @param messageId 原消息ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 回复列表
     */
    Result<PageResponse<MessageResponse>> getMessageReplies(Long messageId, Integer currentPage, Integer pageSize);
    
    /**
     * 搜索消息
     * 在用户消息中搜索关键词
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    Result<PageResponse<MessageResponse>> searchMessages(Long userId, String keyword, Integer currentPage, Integer pageSize);
    
    /**
     * 获取消息统计信息
     * 包含发送数、接收数、未读数等
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Result<java.util.Map<String, Object>> getMessageStatistics(Long userId);
}