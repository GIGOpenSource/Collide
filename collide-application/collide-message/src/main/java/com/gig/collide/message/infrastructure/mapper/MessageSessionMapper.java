package com.gig.collide.message.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.message.domain.entity.MessageSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 消息会话数据访问接口 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Mapper
public interface MessageSessionMapper extends BaseMapper<MessageSession> {

    // =================== 基础查询 ===================

    /**
     * 根据用户ID和对方用户ID查询会话
     */
    MessageSession findByUserIds(@Param("userId") Long userId,
                               @Param("otherUserId") Long otherUserId);

    /**
     * 查询用户的会话列表
     */
    Page<MessageSession> findUserSessions(Page<MessageSession> page,
                                        @Param("userId") Long userId,
                                        @Param("isArchived") Boolean isArchived,
                                        @Param("hasUnread") Boolean hasUnread,
                                        @Param("orderBy") String orderBy,
                                        @Param("orderDirection") String orderDirection);

    /**
     * 查询用户的活跃会话（有消息交互的会话）
     */
    Page<MessageSession> findActiveSessions(Page<MessageSession> page,
                                          @Param("userId") Long userId,
                                          @Param("sinceTime") LocalDateTime sinceTime);

    // =================== 统计查询 ===================

    /**
     * 统计用户的未读会话数
     */
    Long countUnreadSessions(@Param("userId") Long userId);

    /**
     * 统计用户的总会话数
     */
    Long countUserSessions(@Param("userId") Long userId,
                         @Param("isArchived") Boolean isArchived);

    // =================== 更新操作 ===================

    /**
     * 更新会话的最后消息信息
     */
    int updateLastMessage(@Param("userId") Long userId,
                        @Param("otherUserId") Long otherUserId,
                        @Param("lastMessageId") Long lastMessageId,
                        @Param("lastMessageTime") LocalDateTime lastMessageTime);

    /**
     * 增加会话的未读计数
     */
    int incrementUnreadCount(@Param("userId") Long userId,
                           @Param("otherUserId") Long otherUserId);

    /**
     * 清零会话的未读计数
     */
    int clearUnreadCount(@Param("userId") Long userId,
                       @Param("otherUserId") Long otherUserId);

    /**
     * 更新会话归档状态
     */
    int updateArchiveStatus(@Param("sessionId") Long sessionId,
                          @Param("isArchived") Boolean isArchived);

    // =================== 创建或更新 ===================

    /**
     * 创建或更新会话信息
     * 如果会话不存在则创建，存在则更新
     */
    int insertOrUpdate(@Param("userId") Long userId,
                      @Param("otherUserId") Long otherUserId,
                      @Param("lastMessageId") Long lastMessageId,
                      @Param("lastMessageTime") LocalDateTime lastMessageTime);

    // =================== 清理操作 ===================

    /**
     * 删除空会话（没有消息的会话）
     */
    int deleteEmptySessions();

    /**
     * 删除指定时间前的归档会话
     */
    int deleteArchivedSessions(@Param("beforeTime") LocalDateTime beforeTime);
}