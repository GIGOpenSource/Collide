package com.gig.collide.message.facade;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.message.MessageFacadeService;
import com.gig.collide.api.message.request.MessageCreateRequest;
import com.gig.collide.api.message.request.MessageQueryRequest;
import com.gig.collide.api.message.request.MessageUpdateRequest;
import com.gig.collide.api.message.response.MessageResponse;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.message.domain.entity.Message;
import com.gig.collide.message.domain.service.MessageService;
import com.gig.collide.message.domain.service.MessageSessionService;
import com.gig.collide.message.domain.service.MessageSettingService;
import com.gig.collide.message.infrastructure.cache.MessageCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消息门面服务实现类 - 简洁版
 * 基于message-simple.sql的无连表设计，实现核心消息功能
 * 集成JetCache分布式缓存和Dubbo跨服务调用
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class MessageFacadeServiceImpl implements MessageFacadeService {

    private final MessageService messageService;
    private final MessageSessionService messageSessionService;
    private final MessageSettingService messageSettingService;

    @DubboReference(version = "1.0.0", timeout = 10000, check = false)
    private UserFacadeService userFacadeService;

    // =================== 消息发送 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_UNREAD_COUNT_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_CHAT_HISTORY_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_DETAIL_CACHE)
    public Result<MessageResponse> sendMessage(MessageCreateRequest request) {
        try {
            log.info("发送消息请求: 发送者={}, 接收者={}, 类型={}", 
                    request.getSenderId(), request.getReceiverId(), request.getMessageType());
            long startTime = System.currentTimeMillis();

            // 参数验证
            if (request == null) {
                return Result.error("INVALID_REQUEST", "请求参数不能为空");
            }

            // 验证用户存在性
            Result<Void> userValidation = validateUsers(request.getSenderId(), request.getReceiverId());
            if (!userValidation.getSuccess()) {
                return Result.error(userValidation.getCode(), userValidation.getMessage());
            }

            // 验证发送权限
            boolean canSend = messageSettingService.canSendMessage(request.getSenderId(), request.getReceiverId());
            if (!canSend) {
                log.warn("用户发送消息权限不足: 发送者={}, 接收者={}", request.getSenderId(), request.getReceiverId());
                return Result.error("MESSAGE_PERMISSION_DENIED", "无权限向该用户发送消息");
            }

            // 转换请求对象为实体
            Message message = convertCreateRequestToEntity(request);

            // 调用业务逻辑
            Message savedMessage = messageService.sendMessage(message);

            // 处理会话状态
            messageSessionService.handleNewMessage(
                    savedMessage.getSenderId(), 
                    savedMessage.getReceiverId(),
                    savedMessage.getId(), 
                    savedMessage.getCreateTime()
            );

            // 转换响应对象
            MessageResponse response = convertToResponse(savedMessage);

            long duration = System.currentTimeMillis() - startTime;
            log.info("消息发送成功: ID={}, 发送者={}, 接收者={}, 耗时={}ms", 
                    savedMessage.getId(), request.getSenderId(), request.getReceiverId(), duration);

            return Result.success(response);

        } catch (IllegalArgumentException e) {
            log.warn("发送消息参数错误: 发送者={}, 接收者={}, 错误={}", 
                    request.getSenderId(), request.getReceiverId(), e.getMessage());
            return Result.error("MESSAGE_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("发送消息失败: 发送者={}, 接收者={}", request.getSenderId(), request.getReceiverId(), e);
            return Result.error("MESSAGE_SEND_ERROR", "发送消息失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_CHAT_HISTORY_CACHE)
    public Result<MessageResponse> replyMessage(MessageCreateRequest request) {
        try {
            log.info("回复消息请求: 发送者={}, 接收者={}, 回复消息ID={}", 
                    request.getSenderId(), request.getReceiverId(), request.getReplyToId());

            // 参数验证
            if (request == null || request.getReplyToId() == null) {
                return Result.error("INVALID_REQUEST", "回复消息ID不能为空");
            }

            // 验证用户存在性
            Result<Void> userValidation = validateUsers(request.getSenderId(), request.getReceiverId());
            if (!userValidation.getSuccess()) {
                return Result.error(userValidation.getCode(), userValidation.getMessage());
            }

            // 转换请求对象为实体
            Message message = convertCreateRequestToEntity(request);

            // 调用业务逻辑
            Message savedMessage = messageService.replyMessage(message);

            // 处理会话状态
            messageSessionService.handleNewMessage(
                    savedMessage.getSenderId(), 
                    savedMessage.getReceiverId(),
                    savedMessage.getId(), 
                    savedMessage.getCreateTime()
            );

            // 转换响应对象
            MessageResponse response = convertToResponse(savedMessage);

            log.info("回复消息成功: ID={}, 回复消息ID={}", savedMessage.getId(), request.getReplyToId());
            return Result.success(response);

        } catch (IllegalArgumentException e) {
            log.warn("回复消息参数错误: {}", e.getMessage());
            return Result.error("MESSAGE_REPLY_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("回复消息失败: 回复消息ID={}", request.getReplyToId(), e);
            return Result.error("MESSAGE_REPLY_ERROR", "回复消息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<MessageResponse> sendWallMessage(MessageCreateRequest request) {
        // 留言板消息本质上也是发送消息，只是消息类型不同
        request.setMessageType("wall");
        return sendMessage(request);
    }

    // =================== 消息查询 ===================

    @Override
    @Cached(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE, expire = 30, timeUnit = TimeUnit.MINUTES, 
            cacheType = CacheType.BOTH, key = "#messageId")
    public Result<MessageResponse> getMessageById(Long messageId, Long userId) {
        try {
            log.debug("查询消息详情: messageId={}, userId={}", messageId, userId);

            if (messageId == null || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            Message message = messageService.getMessageById(messageId);
            if (message == null) {
                return Result.error("MESSAGE_NOT_FOUND", "消息不存在");
            }

            // 验证访问权限（只有发送者和接收者可以查看）
            if (!userId.equals(message.getSenderId()) && !userId.equals(message.getReceiverId())) {
                return Result.error("MESSAGE_ACCESS_DENIED", "无权限查看该消息");
            }

            MessageResponse response = convertToResponse(message);
            return Result.success(response);

        } catch (Exception e) {
            log.error("查询消息详情失败: messageId={}", messageId, e);
            return Result.error("MESSAGE_QUERY_ERROR", "查询消息失败");
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> queryMessages(MessageQueryRequest request) {
        try {
            log.debug("条件查询消息: {}", request);

            if (request == null) {
                return Result.error("INVALID_REQUEST", "查询请求不能为空");
            }

            // 初始化默认值
            request.initDefaults();

            // 验证查询条件
            if (!request.isValidQuery()) {
                return Result.error("INVALID_QUERY", "查询条件不符合要求");
            }

            IPage<Message> messagePage = messageService.findWithConditions(
                    request.getSenderId(), request.getReceiverId(), request.getMessageType(),
                    request.getStatus(), request.getIsPinned(), request.getReplyToId(),
                    request.getKeyword(), request.getStartTime(), request.getEndTime(),
                    request.getOrderBy(), request.getOrderDirection(),
                    request.getCurrentPage(), request.getPageSize());

            PageResponse<MessageResponse> response = convertToPageResponse(messagePage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("条件查询消息失败", e);
            return Result.error("MESSAGE_QUERY_ERROR", "查询消息失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.MESSAGE_CHAT_HISTORY_CACHE, expire = 15, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "T(com.gig.collide.message.infrastructure.cache.MessageCacheConstant).buildUserPairKey(#userId1, #userId2) + ':' + #currentPage + ':' + #pageSize")
    public Result<PageResponse<MessageResponse>> getChatHistory(Long userId1, Long userId2, String status,
                                                               Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询聊天记录: userId1={}, userId2={}, page={}/{}", userId1, userId2, currentPage, pageSize);

            if (userId1 == null || userId2 == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            IPage<Message> messagePage = messageService.findChatHistory(userId1, userId2, status, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(messagePage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("查询聊天记录失败: userId1={}, userId2={}", userId1, userId2, e);
            return Result.error("MESSAGE_QUERY_ERROR", "查询聊天记录失败");
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> getWallMessages(Long receiverId, String status,
                                                                Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询留言板消息: receiverId={}, page={}/{}", receiverId, currentPage, pageSize);

            if (receiverId == null) {
                return Result.error("INVALID_PARAM", "接收者ID不能为空");
            }

            IPage<Message> messagePage = messageService.findWallMessages(receiverId, status, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(messagePage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("查询留言板消息失败: receiverId={}", receiverId, e);
            return Result.error("MESSAGE_QUERY_ERROR", "查询留言板消息失败");
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> getMessageReplies(Long replyToId, String status,
                                                                  Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询消息回复: replyToId={}, page={}/{}", replyToId, currentPage, pageSize);

            if (replyToId == null) {
                return Result.error("INVALID_PARAM", "原消息ID不能为空");
            }

            IPage<Message> messagePage = messageService.findReplies(replyToId, status, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(messagePage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("查询消息回复失败: replyToId={}", replyToId, e);
            return Result.error("MESSAGE_QUERY_ERROR", "查询消息回复失败");
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> searchMessages(Long userId, String keyword, String status,
                                                               Integer currentPage, Integer pageSize) {
        try {
            log.debug("搜索用户消息: userId={}, keyword={}, page={}/{}", userId, keyword, currentPage, pageSize);

            if (userId == null || keyword == null || keyword.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "用户ID和搜索关键词不能为空");
            }

            IPage<Message> messagePage = messageService.searchMessages(userId, keyword, status, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(messagePage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("搜索用户消息失败: userId={}, keyword={}", userId, keyword, e);
            return Result.error("MESSAGE_SEARCH_ERROR", "搜索消息失败");
        }
    }

    // =================== 消息管理 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_CHAT_HISTORY_CACHE)
    public Result<MessageResponse> updateMessage(MessageUpdateRequest request) {
        try {
            log.info("更新消息: messageId={}, operatorId={}, updateType={}", 
                    request.getMessageId(), request.getOperatorId(), request.getUpdateType());

            if (request == null || !request.isValidUpdate()) {
                return Result.error("INVALID_REQUEST", "更新请求参数无效");
            }

            // 获取原消息
            Message originalMessage = messageService.getMessageById(request.getMessageId());
            if (originalMessage == null) {
                return Result.error("MESSAGE_NOT_FOUND", "消息不存在");
            }

            // 权限验证（只有发送者可以更新内容）
            if (request.isContentUpdate() && !request.getOperatorId().equals(originalMessage.getSenderId())) {
                return Result.error("MESSAGE_UPDATE_DENIED", "只有发送者可以更新消息内容");
            }

            // 根据更新类型处理
            boolean success = false;
            if (request.isContentUpdate()) {
                // 更新内容逻辑可以在这里实现
                success = true; // 简化处理
            } else if (request.isStatusUpdate()) {
                success = messageService.updateMessageStatus(request.getMessageId(), request.getStatus(), null);
            } else if (request.isPinUpdate()) {
                success = messageService.updatePinnedStatus(request.getMessageId(), request.getIsPinned(), request.getOperatorId());
            }

            if (success) {
                Message updatedMessage = messageService.getMessageById(request.getMessageId());
                MessageResponse response = convertToResponse(updatedMessage);
                log.info("消息更新成功: messageId={}", request.getMessageId());
                return Result.success(response);
            } else {
                return Result.error("MESSAGE_UPDATE_FAILED", "消息更新失败");
            }

        } catch (Exception e) {
            log.error("更新消息失败: messageId={}", request.getMessageId(), e);
            return Result.error("MESSAGE_UPDATE_ERROR", "更新消息失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_CHAT_HISTORY_CACHE)
    public Result<Void> deleteMessage(Long messageId, Long userId) {
        try {
            log.info("删除消息: messageId={}, userId={}", messageId, userId);

            if (messageId == null || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageService.deleteMessage(messageId, userId);
            if (success) {
                log.info("消息删除成功: messageId={}", messageId);
                return Result.success(null);
            } else {
                return Result.error("MESSAGE_DELETE_FAILED", "消息删除失败");
            }

        } catch (Exception e) {
            log.error("删除消息失败: messageId={}", messageId, e);
            return Result.error("MESSAGE_DELETE_ERROR", "删除消息失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_UNREAD_COUNT_CACHE)
    public Result<Void> markAsRead(Long messageId, Long userId) {
        try {
            log.info("标记消息已读: messageId={}, userId={}", messageId, userId);

            if (messageId == null || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageService.updateMessageStatus(messageId, "read", LocalDateTime.now());
            if (success) {
                log.info("消息标记已读成功: messageId={}", messageId);
                return Result.success(null);
            } else {
                return Result.error("MESSAGE_READ_FAILED", "标记消息已读失败");
            }

        } catch (Exception e) {
            log.error("标记消息已读失败: messageId={}", messageId, e);
            return Result.error("MESSAGE_READ_ERROR", "标记消息已读失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE)
    public Result<Void> updatePinnedStatus(Long messageId, Boolean isPinned, Long userId) {
        try {
            log.info("更新消息置顶状态: messageId={}, isPinned={}, userId={}", messageId, isPinned, userId);

            if (messageId == null || isPinned == null || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageService.updatePinnedStatus(messageId, isPinned, userId);
            if (success) {
                log.info("消息置顶状态更新成功: messageId={}, isPinned={}", messageId, isPinned);
                return Result.success(null);
            } else {
                return Result.error("MESSAGE_PIN_FAILED", "更新消息置顶状态失败");
            }

        } catch (Exception e) {
            log.error("更新消息置顶状态失败: messageId={}", messageId, e);
            return Result.error("MESSAGE_PIN_ERROR", "更新消息置顶状态失败");
        }
    }

    // =================== 批量操作 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_UNREAD_COUNT_CACHE)
    public Result<Void> batchMarkAsRead(List<Long> messageIds, Long userId) {
        try {
            log.info("批量标记消息已读: messageIds.size={}, userId={}", 
                    messageIds != null ? messageIds.size() : 0, userId);

            if (messageIds == null || messageIds.isEmpty() || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            int successCount = messageService.batchMarkAsRead(messageIds, userId);
            
            log.info("批量标记消息已读完成: 成功数量={}", successCount);
            return Result.success(null);

        } catch (Exception e) {
            log.error("批量标记消息已读失败: userId={}", userId, e);
            return Result.error("MESSAGE_BATCH_READ_ERROR", "批量标记消息已读失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_CHAT_HISTORY_CACHE)
    public Result<Void> batchDeleteMessages(List<Long> messageIds, Long userId) {
        try {
            log.info("批量删除消息: messageIds.size={}, userId={}", 
                    messageIds != null ? messageIds.size() : 0, userId);

            if (messageIds == null || messageIds.isEmpty() || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            int successCount = messageService.batchDeleteMessages(messageIds, userId);
            
            log.info("批量删除消息完成: 成功数量={}", successCount);
            return Result.success(null);

        } catch (Exception e) {
            log.error("批量删除消息失败: userId={}", userId, e);
            return Result.error("MESSAGE_BATCH_DELETE_ERROR", "批量删除消息失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.MESSAGE_UNREAD_COUNT_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_UNREAD_COUNT_CACHE)
    public Result<Void> markSessionAsRead(Long receiverId, Long senderId) {
        try {
            log.info("标记会话消息已读: receiverId={}, senderId={}", receiverId, senderId);

            if (receiverId == null || senderId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            int successCount = messageService.markSessionMessagesAsRead(receiverId, senderId);
            
            // 清零会话未读数
            messageSessionService.clearUnreadCount(receiverId, senderId);
            
            log.info("标记会话消息已读完成: 成功数量={}", successCount);
            return Result.success(null);

        } catch (Exception e) {
            log.error("标记会话消息已读失败: receiverId={}, senderId={}", receiverId, senderId, e);
            return Result.error("MESSAGE_SESSION_READ_ERROR", "标记会话消息已读失败");
        }
    }

    // =================== 统计功能 ===================

    @Override
    @Cached(name = MessageCacheConstant.MESSAGE_UNREAD_COUNT_CACHE, expire = 5, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId")
    public Result<Long> getUnreadMessageCount(Long userId) {
        try {
            log.debug("统计用户未读消息数: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Long count = messageService.countUnreadMessages(userId);
            return Result.success(count != null ? count : 0L);

        } catch (Exception e) {
            log.error("统计用户未读消息数失败: userId={}", userId, e);
            return Result.error("MESSAGE_COUNT_ERROR", "统计未读消息数失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.MESSAGE_UNREAD_WITH_USER_CACHE, expire = 10, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#receiverId + ':' + #senderId")
    public Result<Long> getUnreadCountWithUser(Long receiverId, Long senderId) {
        try {
            log.debug("统计与用户的未读消息数: receiverId={}, senderId={}", receiverId, senderId);

            if (receiverId == null || senderId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Long count = messageService.countUnreadWithUser(receiverId, senderId);
            return Result.success(count != null ? count : 0L);

        } catch (Exception e) {
            log.error("统计与用户的未读消息数失败: receiverId={}, senderId={}", receiverId, senderId, e);
            return Result.error("MESSAGE_COUNT_ERROR", "统计未读消息数失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.STATISTICS_SENT_COUNT_CACHE, expire = 60, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId + ':' + #startTime + ':' + #endTime")
    public Result<Long> getSentMessageCount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            log.debug("统计用户发送消息数: userId={}, startTime={}, endTime={}", userId, startTime, endTime);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Long count = messageService.countSentMessages(userId, startTime, endTime);
            return Result.success(count != null ? count : 0L);

        } catch (Exception e) {
            log.error("统计用户发送消息数失败: userId={}", userId, e);
            return Result.error("MESSAGE_COUNT_ERROR", "统计发送消息数失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.STATISTICS_RECEIVED_COUNT_CACHE, expire = 60, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId + ':' + #startTime + ':' + #endTime")
    public Result<Long> getReceivedMessageCount(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            log.debug("统计用户接收消息数: userId={}, startTime={}, endTime={}", userId, startTime, endTime);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Long count = messageService.countReceivedMessages(userId, startTime, endTime);
            return Result.success(count != null ? count : 0L);

        } catch (Exception e) {
            log.error("统计用户接收消息数失败: userId={}", userId, e);
            return Result.error("MESSAGE_COUNT_ERROR", "统计接收消息数失败");
        }
    }

    // =================== 会话管理 ===================

    @Override
    @Cached(name = MessageCacheConstant.MESSAGE_RECENT_CHAT_USERS_CACHE, expire = 10, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId + ':' + #limit")
    public Result<List<Long>> getRecentChatUsers(Long userId, Integer limit) {
        try {
            log.debug("获取用户最近聊天用户: userId={}, limit={}", userId, limit);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            List<Long> userIds = messageService.getRecentChatUsers(userId, limit);
            return Result.success(userIds != null ? userIds : List.of());

        } catch (Exception e) {
            log.error("获取用户最近聊天用户失败: userId={}", userId, e);
            return Result.error("MESSAGE_RECENT_USERS_ERROR", "获取最近聊天用户失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.MESSAGE_LATEST_CACHE, expire = 30, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "T(com.gig.collide.message.infrastructure.cache.MessageCacheConstant).buildUserPairKey(#userId1, #userId2)")
    public Result<MessageResponse> getLatestMessage(Long userId1, Long userId2) {
        try {
            log.debug("获取两用户间最新消息: userId1={}, userId2={}", userId1, userId2);

            if (userId1 == null || userId2 == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Message message = messageService.getLatestMessageBetweenUsers(userId1, userId2);
            if (message == null) {
                return Result.error("MESSAGE_NOT_FOUND", "没有找到最新消息");
            }

            MessageResponse response = convertToResponse(message);
            return Result.success(response);

        } catch (Exception e) {
            log.error("获取两用户间最新消息失败: userId1={}, userId2={}", userId1, userId2, e);
            return Result.error("MESSAGE_LATEST_ERROR", "获取最新消息失败");
        }
    }

    // =================== 系统管理 ===================

    @Override
    public Result<Integer> cleanupExpiredMessages(LocalDateTime beforeTime) {
        try {
            log.info("清理过期删除消息: beforeTime={}", beforeTime);

            if (beforeTime == null) {
                return Result.error("INVALID_PARAM", "截止时间不能为空");
            }

            int count = messageService.cleanupExpiredMessages(beforeTime);
            
            log.info("清理过期删除消息完成: 清理数量={}", count);
            return Result.success(count);

        } catch (Exception e) {
            log.error("清理过期删除消息失败", e);
            return Result.error("MESSAGE_CLEANUP_ERROR", "清理过期消息失败");
        }
    }

    @Override
    public Result<String> healthCheck() {
        try {
            // 简单的健康检查：获取当前时间戳
            long timestamp = System.currentTimeMillis();
            String status = "Message service is healthy at " + timestamp;
            
            log.debug("消息系统健康检查: {}", status);
            return Result.success(status);

        } catch (Exception e) {
            log.error("消息系统健康检查失败", e);
            return Result.error("HEALTH_CHECK_ERROR", "系统健康检查失败");
        }
    }

    // =================== 私有工具方法 ===================

    /**
     * 验证用户存在性
     */
    private Result<Void> validateUsers(Long senderId, Long receiverId) {
        // 验证发送者
        Result<UserResponse> senderResult = userFacadeService.getUserById(senderId);
        if (senderResult == null || !senderResult.getSuccess()) {
            log.warn("发送者不存在: senderId={}", senderId);
            return Result.error("SENDER_NOT_FOUND", "发送者不存在");
        }

        // 验证接收者
        Result<UserResponse> receiverResult = userFacadeService.getUserById(receiverId);
        if (receiverResult == null || !receiverResult.getSuccess()) {
            log.warn("接收者不存在: receiverId={}", receiverId);
            return Result.error("RECEIVER_NOT_FOUND", "接收者不存在");
        }

        return Result.success(null);
    }

    /**
     * 转换创建请求为实体
     */
    private Message convertCreateRequestToEntity(MessageCreateRequest request) {
        Message message = new Message();
        BeanUtils.copyProperties(request, message);
        return message;
    }

    /**
     * 转换实体为响应对象
     */
    private MessageResponse convertToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        BeanUtils.copyProperties(message, response);
        return response;
    }

    /**
     * 转换分页实体为分页响应
     */
    private PageResponse<MessageResponse> convertToPageResponse(IPage<Message> messagePage) {
        PageResponse<MessageResponse> response = new PageResponse<>();
        response.setCurrentPage((int) messagePage.getCurrent());
        response.setPageSize((int) messagePage.getSize());
        response.setTotalPage((int) messagePage.getPages());
        response.setTotal((int) messagePage.getTotal());

        List<MessageResponse> messageResponses = messagePage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        response.setDatas(messageResponses);

        return response;
    }
}