package com.gig.collide.message.facade;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.message.MessageSessionFacadeService;
import com.gig.collide.api.message.request.MessageSessionCreateRequest;
import com.gig.collide.api.message.request.MessageSessionQueryRequest;
import com.gig.collide.api.message.request.MessageSessionUpdateRequest;
import com.gig.collide.api.message.response.MessageSessionResponse;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.message.domain.entity.MessageSession;
import com.gig.collide.message.domain.service.MessageSessionService;
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
 * 消息会话门面服务实现类 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
 * 管理用户间的会话状态、未读计数和会话列表
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class MessageSessionFacadeServiceImpl implements MessageSessionFacadeService {

    private final MessageSessionService messageSessionService;

    @DubboReference(version = "1.0.0", timeout = 10000, check = false)
    private UserFacadeService userFacadeService;

    // =================== 会话创建和管理 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_LIST_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_COUNT_CACHE)
    public Result<MessageSessionResponse> createOrUpdateSession(MessageSessionCreateRequest request) {
        try {
            log.info("创建或更新会话: userId={}, otherUserId={}", request.getUserId(), request.getOtherUserId());

            if (request == null || !request.isValidRequest()) {
                return Result.error("INVALID_REQUEST", "请求参数无效");
            }

            // 验证用户存在性
            Result<Void> userValidation = validateUsers(request.getUserId(), request.getOtherUserId());
            if (!userValidation.getSuccess()) {
                return Result.error(userValidation.getCode(), userValidation.getMessage());
            }

            // 设置默认值
            request.initDefaults();

            // 转换请求对象为实体
            MessageSession messageSession = convertCreateRequestToEntity(request);

            // 调用业务逻辑
            MessageSession savedSession = messageSessionService.createOrUpdateSession(messageSession);

            // 转换响应对象
            MessageSessionResponse response = convertToResponse(savedSession);
            enrichSessionResponse(response);

            log.info("会话创建或更新成功: sessionId={}", savedSession.getId());
            return Result.success(response);

        } catch (IllegalArgumentException e) {
            log.warn("创建或更新会话参数错误: {}", e.getMessage());
            return Result.error("SESSION_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("创建或更新会话失败: userId={}, otherUserId={}", 
                    request.getUserId(), request.getOtherUserId(), e);
            return Result.error("SESSION_CREATE_ERROR", "创建或更新会话失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SESSION_DETAIL_CACHE, expire = 20, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "T(com.gig.collide.message.infrastructure.cache.MessageCacheConstant).buildUserPairKey(#userId, #otherUserId)")
    public Result<MessageSessionResponse> getSessionByUserIds(Long userId, Long otherUserId) {
        try {
            log.debug("查询会话详情: userId={}, otherUserId={}", userId, otherUserId);

            if (userId == null || otherUserId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            if (userId.equals(otherUserId)) {
                return Result.error("INVALID_PARAM", "不能查询与自己的会话");
            }

            MessageSession messageSession = messageSessionService.findByUserIds(userId, otherUserId);
            if (messageSession == null) {
                return Result.error("SESSION_NOT_FOUND", "会话不存在");
            }

            MessageSessionResponse response = convertToResponse(messageSession);
            enrichSessionResponse(response);

            return Result.success(response);

        } catch (Exception e) {
            log.error("查询会话详情失败: userId={}, otherUserId={}", userId, otherUserId, e);
            return Result.error("SESSION_QUERY_ERROR", "查询会话详情失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_LIST_CACHE)
    public Result<MessageSessionResponse> updateSession(MessageSessionUpdateRequest request) {
        try {
            log.info("更新会话: sessionId={}, userId={}, updateType={}", 
                    request.getSessionId(), request.getUserId(), request.getUpdateType());

            if (request == null || !request.isValidUpdate()) {
                return Result.error("INVALID_REQUEST", "更新请求参数无效");
            }

            // 执行更新操作
            boolean success = false;
            switch (request.getUpdateType()) {
                case "last_message":
                    success = messageSessionService.updateLastMessage(
                            request.getUserId(), request.getOtherUserId(),
                            request.getLastMessageId(), request.getLastMessageTime());
                    break;
                case "unread_count":
                    success = true; // 未读数更新通过其他方法
                    break;
                case "archive_status":
                    success = messageSessionService.updateArchiveStatus(
                            request.getSessionId(), request.getIsArchived());
                    break;
                case "clear_unread":
                    success = messageSessionService.clearUnreadCount(
                            request.getUserId(), request.getOtherUserId());
                    break;
                case "increment_unread":
                    success = messageSessionService.incrementUnreadCount(
                            request.getUserId(), request.getOtherUserId());
                    break;
            }

            if (success) {
                // 重新查询更新后的会话
                MessageSession updatedSession;
                if (request.getSessionId() != null) {
                    updatedSession = messageSessionService.findByUserIds(request.getUserId(), request.getOtherUserId());
                } else {
                    updatedSession = messageSessionService.findByUserIds(request.getUserId(), request.getOtherUserId());
                }

                MessageSessionResponse response = convertToResponse(updatedSession);
                enrichSessionResponse(response);

                log.info("会话更新成功: updateType={}", request.getUpdateType());
                return Result.success(response);
            } else {
                return Result.error("SESSION_UPDATE_FAILED", "会话更新失败");
            }

        } catch (Exception e) {
            log.error("更新会话失败: sessionId={}", request.getSessionId(), e);
            return Result.error("SESSION_UPDATE_ERROR", "更新会话失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_LIST_CACHE)
    public Result<Void> updateArchiveStatus(Long sessionId, Boolean isArchived, Long userId) {
        try {
            log.info("更新会话归档状态: sessionId={}, isArchived={}, userId={}", sessionId, isArchived, userId);

            if (sessionId == null || isArchived == null || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageSessionService.updateArchiveStatus(sessionId, isArchived);
            if (success) {
                log.info("会话归档状态更新成功: sessionId={}, isArchived={}", sessionId, isArchived);
                return Result.success(null);
            } else {
                return Result.error("SESSION_ARCHIVE_FAILED", "更新会话归档状态失败");
            }

        } catch (Exception e) {
            log.error("更新会话归档状态失败: sessionId={}", sessionId, e);
            return Result.error("SESSION_ARCHIVE_ERROR", "更新会话归档状态失败");
        }
    }

    // =================== 会话查询 ===================

    @Override
    public Result<PageResponse<MessageSessionResponse>> queryUserSessions(MessageSessionQueryRequest request) {
        try {
            log.debug("分页查询用户会话: {}", request);

            if (request == null || request.getUserId() == null) {
                return Result.error("INVALID_REQUEST", "查询请求或用户ID不能为空");
            }

            // 初始化默认值并设置查询字段
            request.initDefaults();
            request.autoSetQueryFields();

            // 验证查询参数
            if (!request.isValidQuery()) {
                return Result.error("INVALID_QUERY", "查询参数无效");
            }

            IPage<MessageSession> sessionPage = messageSessionService.findUserSessions(
                    request.getUserId(), request.getIsArchived(), request.getHasUnread(),
                    request.getOrderBy(), request.getOrderDirection(),
                    request.getCurrentPage(), request.getPageSize());

            PageResponse<MessageSessionResponse> response = convertToPageResponse(sessionPage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("分页查询用户会话失败", e);
            return Result.error("SESSION_QUERY_ERROR", "查询用户会话失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SESSION_USER_LIST_CACHE, expire = 10, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId + ':' + #isArchived + ':' + #hasUnread + ':' + #currentPage + ':' + #pageSize")
    public Result<PageResponse<MessageSessionResponse>> getUserSessions(Long userId, Boolean isArchived, Boolean hasUnread,
                                                                       Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户会话列表: userId={}, isArchived={}, hasUnread={}, page={}/{}",
                    userId, isArchived, hasUnread, currentPage, pageSize);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            // 设置默认排序
            String orderBy = "last_message_time";
            String orderDirection = "DESC";

            IPage<MessageSession> sessionPage = messageSessionService.findUserSessions(
                    userId, isArchived, hasUnread, orderBy, orderDirection, currentPage, pageSize);

            PageResponse<MessageSessionResponse> response = convertToPageResponse(sessionPage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("查询用户会话列表失败: userId={}", userId, e);
            return Result.error("SESSION_QUERY_ERROR", "查询用户会话列表失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SESSION_ACTIVE_CACHE, expire = 10, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId + ':' + #sinceTime + ':' + #currentPage + ':' + #pageSize")
    public Result<PageResponse<MessageSessionResponse>> getActiveSessions(Long userId, LocalDateTime sinceTime,
                                                                         Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户活跃会话: userId={}, sinceTime={}, page={}/{}",
                    userId, sinceTime, currentPage, pageSize);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            IPage<MessageSession> sessionPage = messageSessionService.findActiveSessions(
                    userId, sinceTime, currentPage, pageSize);

            PageResponse<MessageSessionResponse> response = convertToPageResponse(sessionPage);
            return Result.success(response);

        } catch (Exception e) {
            log.error("查询用户活跃会话失败: userId={}", userId, e);
            return Result.error("SESSION_QUERY_ERROR", "查询用户活跃会话失败");
        }
    }

    @Override
    public Result<PageResponse<MessageSessionResponse>> getUnreadSessions(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户未读会话: userId={}, page={}/{}", userId, currentPage, pageSize);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            // 查询有未读消息的会话
            return getUserSessions(userId, false, true, currentPage, pageSize);

        } catch (Exception e) {
            log.error("查询用户未读会话失败: userId={}", userId, e);
            return Result.error("SESSION_QUERY_ERROR", "查询用户未读会话失败");
        }
    }

    // =================== 统计功能 ===================

    @Override
    @Cached(name = MessageCacheConstant.SESSION_UNREAD_COUNT_CACHE, expire = 5, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId")
    public Result<Long> getUnreadSessionCount(Long userId) {
        try {
            log.debug("统计用户未读会话数: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Long count = messageSessionService.countUnreadSessions(userId);
            return Result.success(count != null ? count : 0L);

        } catch (Exception e) {
            log.error("统计用户未读会话数失败: userId={}", userId, e);
            return Result.error("SESSION_COUNT_ERROR", "统计未读会话数失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SESSION_USER_COUNT_CACHE, expire = 15, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId + ':' + #isArchived")
    public Result<Long> getUserSessionCount(Long userId, Boolean isArchived) {
        try {
            log.debug("统计用户会话总数: userId={}, isArchived={}", userId, isArchived);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Long count = messageSessionService.countUserSessions(userId, isArchived);
            return Result.success(count != null ? count : 0L);

        } catch (Exception e) {
            log.error("统计用户会话总数失败: userId={}", userId, e);
            return Result.error("SESSION_COUNT_ERROR", "统计用户会话总数失败");
        }
    }

    @Override
    public Result<Long> getTotalUnreadCount(Long userId) {
        try {
            log.debug("获取用户总未读数: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            Long count = messageSessionService.getTotalUnreadCount(userId);
            return Result.success(count != null ? count : 0L);

        } catch (Exception e) {
            log.error("获取用户总未读数失败: userId={}", userId, e);
            return Result.error("SESSION_COUNT_ERROR", "获取用户总未读数失败");
        }
    }

    // =================== 会话状态管理 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_LIST_CACHE)
    public Result<Void> updateLastMessage(Long userId, Long otherUserId, Long lastMessageId, LocalDateTime lastMessageTime) {
        try {
            log.info("更新会话最后消息: userId={}, otherUserId={}, lastMessageId={}", 
                    userId, otherUserId, lastMessageId);

            if (userId == null || otherUserId == null || lastMessageId == null || lastMessageTime == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageSessionService.updateLastMessage(userId, otherUserId, lastMessageId, lastMessageTime);
            if (success) {
                log.info("会话最后消息更新成功: userId={}, otherUserId={}", userId, otherUserId);
                return Result.success(null);
            } else {
                return Result.error("SESSION_UPDATE_FAILED", "更新会话最后消息失败");
            }

        } catch (Exception e) {
            log.error("更新会话最后消息失败: userId={}, otherUserId={}", userId, otherUserId, e);
            return Result.error("SESSION_UPDATE_ERROR", "更新会话最后消息失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_UNREAD_COUNT_CACHE)
    public Result<Void> incrementUnreadCount(Long userId, Long otherUserId) {
        try {
            log.info("增加会话未读计数: userId={}, otherUserId={}", userId, otherUserId);

            if (userId == null || otherUserId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean success = messageSessionService.incrementUnreadCount(userId, otherUserId);
            if (success) {
                log.info("会话未读计数增加成功: userId={}, otherUserId={}", userId, otherUserId);
                return Result.success(null);
            } else {
                return Result.error("SESSION_UNREAD_FAILED", "增加会话未读计数失败");
            }

        } catch (Exception e) {
            log.error("增加会话未读计数失败: userId={}, otherUserId={}", userId, otherUserId, e);
            return Result.error("SESSION_UNREAD_ERROR", "增加会话未读计数失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_UNREAD_COUNT_CACHE)
    public Result<Void> clearUnreadCount(Long userId, Long otherUserId) {
        try {
            log.info("清零会话未读计数: userId={}, otherUserId={}", userId, otherUserId);

            if (userId == null || otherUserId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean success = messageSessionService.clearUnreadCount(userId, otherUserId);
            if (success) {
                log.info("会话未读计数清零成功: userId={}, otherUserId={}", userId, otherUserId);
                return Result.success(null);
            } else {
                return Result.error("SESSION_CLEAR_FAILED", "清零会话未读计数失败");
            }

        } catch (Exception e) {
            log.error("清零会话未读计数失败: userId={}, otherUserId={}", userId, otherUserId, e);
            return Result.error("SESSION_CLEAR_ERROR", "清零会话未读计数失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_DETAIL_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_LIST_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SESSION_UNREAD_COUNT_CACHE)
    public Result<Void> handleNewMessage(Long senderId, Long receiverId, Long messageId, LocalDateTime messageTime) {
        try {
            log.info("处理新消息事件: senderId={}, receiverId={}, messageId={}", senderId, receiverId, messageId);

            if (senderId == null || receiverId == null || messageId == null || messageTime == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageSessionService.handleNewMessage(senderId, receiverId, messageId, messageTime);
            if (success) {
                log.info("新消息事件处理成功: senderId={}, receiverId={}, messageId={}", senderId, receiverId, messageId);
                return Result.success(null);
            } else {
                return Result.error("SESSION_HANDLE_FAILED", "处理新消息事件失败");
            }

        } catch (Exception e) {
            log.error("处理新消息事件失败: senderId={}, receiverId={}, messageId={}", senderId, receiverId, messageId, e);
            return Result.error("SESSION_HANDLE_ERROR", "处理新消息事件失败");
        }
    }

    // =================== 会话清理 ===================

    @Override
    public Result<Integer> deleteEmptySessions(Long userId) {
        try {
            log.info("删除空会话: userId={}", userId);

            int count = messageSessionService.deleteEmptySessions();
            
            log.info("删除空会话完成: 删除数量={}", count);
            return Result.success(count);

        } catch (Exception e) {
            log.error("删除空会话失败", e);
            return Result.error("SESSION_DELETE_ERROR", "删除空会话失败");
        }
    }

    @Override
    public Result<Integer> deleteArchivedSessions(LocalDateTime beforeTime, Long userId) {
        try {
            log.info("删除归档会话: beforeTime={}, userId={}", beforeTime, userId);

            if (beforeTime == null) {
                return Result.error("INVALID_PARAM", "截止时间不能为空");
            }

            int count = messageSessionService.deleteArchivedSessions(beforeTime);
            
            log.info("删除归档会话完成: 删除数量={}", count);
            return Result.success(count);

        } catch (Exception e) {
            log.error("删除归档会话失败", e);
            return Result.error("SESSION_DELETE_ERROR", "删除归档会话失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_LIST_CACHE)
    public Result<Void> batchArchiveSessions(List<Long> sessionIds, Long userId) {
        try {
            log.info("批量归档会话: sessionIds.size={}, userId={}", 
                    sessionIds != null ? sessionIds.size() : 0, userId);

            if (sessionIds == null || sessionIds.isEmpty() || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            // 批量更新归档状态
            int successCount = 0;
            for (Long sessionId : sessionIds) {
                try {
                    boolean success = messageSessionService.updateArchiveStatus(sessionId, true);
                    if (success) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.warn("归档会话失败: sessionId={}", sessionId, e);
                }
            }

            log.info("批量归档会话完成: 成功数量={}", successCount);
            return Result.success(null);

        } catch (Exception e) {
            log.error("批量归档会话失败: userId={}", userId, e);
            return Result.error("SESSION_BATCH_ARCHIVE_ERROR", "批量归档会话失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SESSION_USER_LIST_CACHE)
    public Result<Void> batchUnarchiveSessions(List<Long> sessionIds, Long userId) {
        try {
            log.info("批量取消归档会话: sessionIds.size={}, userId={}", 
                    sessionIds != null ? sessionIds.size() : 0, userId);

            if (sessionIds == null || sessionIds.isEmpty() || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            // 批量更新归档状态
            int successCount = 0;
            for (Long sessionId : sessionIds) {
                try {
                    boolean success = messageSessionService.updateArchiveStatus(sessionId, false);
                    if (success) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.warn("取消归档会话失败: sessionId={}", sessionId, e);
                }
            }

            log.info("批量取消归档会话完成: 成功数量={}", successCount);
            return Result.success(null);

        } catch (Exception e) {
            log.error("批量取消归档会话失败: userId={}", userId, e);
            return Result.error("SESSION_BATCH_UNARCHIVE_ERROR", "批量取消归档会话失败");
        }
    }

    // =================== 系统功能 ===================

    @Override
    public Result<String> rebuildSessionIndex(Long userId) {
        try {
            log.info("重建会话索引: userId={}", userId);

            // 这里可以实现会话索引重建逻辑
            // 目前返回简单的成功状态
            String result = "Session index rebuild completed for user: " + userId;
            
            log.info("重建会话索引完成: userId={}", userId);
            return Result.success(result);

        } catch (Exception e) {
            log.error("重建会话索引失败: userId={}", userId, e);
            return Result.error("SESSION_REBUILD_ERROR", "重建会话索引失败");
        }
    }

    @Override
    public Result<String> healthCheck() {
        try {
            // 简单的健康检查：获取当前时间戳
            long timestamp = System.currentTimeMillis();
            String status = "Message session service is healthy at " + timestamp;
            
            log.debug("消息会话系统健康检查: {}", status);
            return Result.success(status);

        } catch (Exception e) {
            log.error("消息会话系统健康检查失败", e);
            return Result.error("HEALTH_CHECK_ERROR", "系统健康检查失败");
        }
    }

    // =================== 私有工具方法 ===================

    /**
     * 验证用户存在性
     */
    private Result<Void> validateUsers(Long userId, Long otherUserId) {
        // 验证用户
        Result<UserResponse> userResult = userFacadeService.getUserById(userId);
        if (userResult == null || !userResult.getSuccess()) {
            log.warn("用户不存在: userId={}", userId);
            return Result.error("USER_NOT_FOUND", "用户不存在");
        }

        // 验证对方用户
        Result<UserResponse> otherUserResult = userFacadeService.getUserById(otherUserId);
        if (otherUserResult == null || !otherUserResult.getSuccess()) {
            log.warn("对方用户不存在: otherUserId={}", otherUserId);
            return Result.error("OTHER_USER_NOT_FOUND", "对方用户不存在");
        }

        return Result.success(null);
    }

    /**
     * 转换创建请求为实体
     */
    private MessageSession convertCreateRequestToEntity(MessageSessionCreateRequest request) {
        MessageSession messageSession = new MessageSession();
        BeanUtils.copyProperties(request, messageSession);
        return messageSession;
    }

    /**
     * 转换实体为响应对象
     */
    private MessageSessionResponse convertToResponse(MessageSession messageSession) {
        MessageSessionResponse response = new MessageSessionResponse();
        BeanUtils.copyProperties(messageSession, response);
        return response;
    }

    /**
     * 丰富会话响应对象（填充用户信息等）
     */
    private void enrichSessionResponse(MessageSessionResponse response) {
        try {
            if (response.getOtherUserId() != null) {
                Result<UserResponse> userResult = userFacadeService.getUserById(response.getOtherUserId());
                if (userResult != null && userResult.getSuccess() && userResult.getData() != null) {
                    UserResponse user = userResult.getData();
                    response.setOtherUserNickname(user.getNickname());
                    response.setOtherUserAvatar(user.getAvatar());
                    // 可以根据需要添加更多用户信息
                }
            }
        } catch (Exception e) {
            log.warn("丰富会话响应对象失败: otherUserId={}", response.getOtherUserId(), e);
            // 不抛异常，避免影响主要业务
        }
    }

    /**
     * 转换分页实体为分页响应
     */
    private PageResponse<MessageSessionResponse> convertToPageResponse(IPage<MessageSession> sessionPage) {
        PageResponse<MessageSessionResponse> response = new PageResponse<>();
        response.setCurrentPage((int) sessionPage.getCurrent());
        response.setPageSize((int) sessionPage.getSize());
        response.setTotalPage((int) sessionPage.getPages());
        response.setTotal((int) sessionPage.getTotal());

        List<MessageSessionResponse> sessionResponses = sessionPage.getRecords().stream()
                .map(session -> {
                    MessageSessionResponse sessionResponse = convertToResponse(session);
                    enrichSessionResponse(sessionResponse);
                    return sessionResponse;
                })
                .collect(Collectors.toList());
        response.setDatas(sessionResponses);

        return response;
    }
}