package com.gig.collide.message.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.message.domain.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息数据访问接口 - 简洁版
 * 基于message-simple.sql的无连表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    // =================== 基础查询 ===================

    /**
     * 查询两用户间的聊天记录
     */
    Page<Message> findChatHistory(Page<Message> page,
                                 @Param("userId1") Long userId1,
                                 @Param("userId2") Long userId2,
                                 @Param("status") String status);

    /**
     * 条件查询消息
     */
    Page<Message> findWithConditions(Page<Message> page,
                                   @Param("senderId") Long senderId,
                                   @Param("receiverId") Long receiverId,
                                   @Param("messageType") String messageType,
                                   @Param("status") String status,
                                   @Param("isPinned") Boolean isPinned,
                                   @Param("replyToId") Long replyToId,
                                   @Param("keyword") String keyword,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   @Param("orderBy") String orderBy,
                                   @Param("orderDirection") String orderDirection);

    /**
     * 查询用户留言板消息
     */
    Page<Message> findWallMessages(Page<Message> page,
                                 @Param("receiverId") Long receiverId,
                                 @Param("status") String status);

    /**
     * 查询消息的回复列表
     */
    Page<Message> findReplies(Page<Message> page,
                            @Param("replyToId") Long replyToId,
                            @Param("status") String status);

    /**
     * 搜索用户消息
     */
    Page<Message> searchMessages(Page<Message> page,
                               @Param("userId") Long userId,
                               @Param("keyword") String keyword,
                               @Param("status") String status);

    // =================== 统计查询 ===================

    /**
     * 统计用户未读消息数
     */
    Long countUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * 统计与某用户的未读消息数
     */
    Long countUnreadWithUser(@Param("receiverId") Long receiverId,
                           @Param("senderId") Long senderId);

    /**
     * 统计用户发送的消息数
     */
    Long countSentMessages(@Param("senderId") Long senderId,
                         @Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户接收的消息数
     */
    Long countReceivedMessages(@Param("receiverId") Long receiverId,
                             @Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime);

    // =================== 批量操作 ===================

    /**
     * 批量标记消息为已读
     */
    int batchMarkAsRead(@Param("messageIds") List<Long> messageIds,
                       @Param("receiverId") Long receiverId,
                       @Param("readTime") LocalDateTime readTime);

    /**
     * 批量删除消息（逻辑删除）
     */
    int batchDeleteMessages(@Param("messageIds") List<Long> messageIds,
                          @Param("userId") Long userId);

    /**
     * 标记会话中所有消息为已读
     */
    int markSessionMessagesAsRead(@Param("receiverId") Long receiverId,
                                @Param("senderId") Long senderId,
                                @Param("readTime") LocalDateTime readTime);

    // =================== 状态更新 ===================

    /**
     * 更新消息状态
     */
    int updateMessageStatus(@Param("messageId") Long messageId,
                          @Param("status") String status,
                          @Param("readTime") LocalDateTime readTime);

    /**
     * 更新消息置顶状态
     */
    int updatePinnedStatus(@Param("messageId") Long messageId,
                         @Param("isPinned") Boolean isPinned,
                         @Param("receiverId") Long receiverId);

    // =================== 清理操作 ===================

    /**
     * 物理删除指定时间前的已删除消息
     */
    int physicalDeleteExpiredMessages(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 获取用户会话中的最新消息
     */
    Message getLatestMessageBetweenUsers(@Param("userId1") Long userId1,
                                       @Param("userId2") Long userId2);

    /**
     * 获取用户最近的会话用户列表
     */
    List<Long> getRecentChatUsers(@Param("userId") Long userId,
                                @Param("limit") Integer limit);
}