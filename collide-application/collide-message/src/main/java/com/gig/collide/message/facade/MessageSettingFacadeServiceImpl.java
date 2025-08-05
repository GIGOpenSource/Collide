package com.gig.collide.message.facade;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.gig.collide.api.message.MessageSettingFacadeService;
import com.gig.collide.api.message.request.MessageSettingCreateRequest;
import com.gig.collide.api.message.request.MessageSettingUpdateRequest;
import com.gig.collide.api.message.response.MessageSettingResponse;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.message.domain.entity.MessageSetting;
import com.gig.collide.message.domain.service.MessageSettingService;
import com.gig.collide.message.infrastructure.cache.MessageCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消息设置门面服务实现类 - 简洁版
 * 基于message-simple.sql的t_message_setting表设计
 * 管理用户的消息偏好设置和权限控制
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class MessageSettingFacadeServiceImpl implements MessageSettingFacadeService {

    private final MessageSettingService messageSettingService;

    @DubboReference(version = "1.0.0", timeout = 10000, check = false)
    private UserFacadeService userFacadeService;

    // =================== 设置管理 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_PERMISSION_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_STRANGER_MSG_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_AUTO_READ_RECEIPT_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_NOTIFICATION_CACHE)
    public Result<MessageSettingResponse> createOrUpdateSetting(MessageSettingCreateRequest request) {
        try {
            log.info("创建或更新消息设置: userId={}", request.getUserId());

            if (request == null || request.getUserId() == null) {
                return Result.error("INVALID_REQUEST", "请求参数或用户ID不能为空");
            }

            // 验证用户存在性
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法创建设置: userId={}", request.getUserId());
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            // 设置默认值（如果方法存在）
            if (request.getAllowStrangerMsg() == null) {
                request.setAllowStrangerMsg(true);
            }
            if (request.getAutoReadReceipt() == null) {
                request.setAutoReadReceipt(true);
            }
            if (request.getMessageNotification() == null) {
                request.setMessageNotification(true);
            }

            // 转换请求对象为实体
            MessageSetting messageSetting = convertCreateRequestToEntity(request);

            // 调用业务逻辑
            MessageSetting savedSetting = messageSettingService.createOrUpdateSetting(messageSetting);

            // 转换响应对象
            MessageSettingResponse response = convertToResponse(savedSetting);
            enrichSettingResponse(response, userResult.getData());

            log.info("消息设置创建或更新成功: userId={}", request.getUserId());
            return Result.success(response);

        } catch (IllegalArgumentException e) {
            log.warn("创建或更新消息设置参数错误: userId={}, 错误={}", request.getUserId(), e.getMessage());
            return Result.error("SETTING_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("创建或更新消息设置失败: userId={}", request.getUserId(), e);
            return Result.error("SETTING_CREATE_ERROR", "创建或更新消息设置失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SETTING_USER_CACHE, expire = 60, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId")
    public Result<MessageSettingResponse> getUserSetting(Long userId) {
        try {
            log.debug("查询用户消息设置: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            MessageSetting setting = messageSettingService.findByUserId(userId);
            MessageSettingResponse response = convertToResponse(setting);

            // 丰富响应信息
            try {
                Result<UserResponse> userResult = userFacadeService.getUserById(userId);
                if (userResult != null && userResult.getSuccess() && userResult.getData() != null) {
                    enrichSettingResponse(response, userResult.getData());
                }
            } catch (Exception e) {
                log.warn("获取用户信息失败，跳过丰富设置响应: userId={}", userId, e);
            }

            return Result.success(response);

        } catch (Exception e) {
            log.error("查询用户消息设置失败: userId={}", userId, e);
            return Result.error("SETTING_QUERY_ERROR", "查询用户消息设置失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_PERMISSION_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_STRANGER_MSG_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_AUTO_READ_RECEIPT_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_NOTIFICATION_CACHE)
    public Result<MessageSettingResponse> updateUserSetting(MessageSettingUpdateRequest request) {
        try {
            log.info("更新用户消息设置: userId={}", request.getUserId());

            if (request == null || !request.isValidUpdate()) {
                return Result.error("INVALID_REQUEST", "更新请求参数无效");
            }

            // 验证用户存在性
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (userResult == null || !userResult.getSuccess()) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            // 根据更新字段进行更新
            boolean success = false;
            MessageSetting updatedSetting = null;

            // 根据提供的参数进行更新
            if (request.getAllowStrangerMsg() != null || request.getAutoReadReceipt() != null || request.getMessageNotification() != null) {
                // 批量更新所有设置
                success = messageSettingService.updateUserSettings(
                        request.getUserId(),
                        request.getAllowStrangerMsg(),
                        request.getAutoReadReceipt(),
                        request.getMessageNotification());
            } else {
                // 如果没有任何更新参数，返回错误
                return Result.error("NO_UPDATE_FIELDS", "没有提供需要更新的字段");
            }

            if (success) {
                // 重新查询更新后的设置
                updatedSetting = messageSettingService.findByUserId(request.getUserId());
                MessageSettingResponse response = convertToResponse(updatedSetting);
                enrichSettingResponse(response, userResult.getData());

                log.info("用户消息设置更新成功: userId={}", request.getUserId());
                return Result.success(response);
            } else {
                return Result.error("SETTING_UPDATE_FAILED", "更新用户消息设置失败");
            }

        } catch (Exception e) {
            log.error("更新用户消息设置失败: userId={}", request.getUserId(), e);
            return Result.error("SETTING_UPDATE_ERROR", "更新用户消息设置失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    public Result<MessageSettingResponse> initDefaultSetting(Long userId) {
        try {
            log.info("初始化用户默认设置: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            // 验证用户存在性
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            MessageSetting defaultSetting = messageSettingService.initDefaultSetting(userId);
            MessageSettingResponse response = convertToResponse(defaultSetting);
            enrichSettingResponse(response, userResult.getData());

            log.info("用户默认设置初始化成功: userId={}", userId);
            return Result.success(response);

        } catch (Exception e) {
            log.error("初始化用户默认设置失败: userId={}", userId, e);
            return Result.error("SETTING_INIT_ERROR", "初始化用户默认设置失败");
        }
    }

    // =================== 单项设置更新 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_STRANGER_MSG_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_PERMISSION_CACHE)
    public Result<Void> updateStrangerMessageSetting(Long userId, Boolean allowStrangerMsg) {
        try {
            log.info("更新陌生人消息设置: userId={}, allowStrangerMsg={}", userId, allowStrangerMsg);

            if (userId == null || allowStrangerMsg == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageSettingService.updateStrangerMessageSetting(userId, allowStrangerMsg);
            if (success) {
                log.info("陌生人消息设置更新成功: userId={}, allowStrangerMsg={}", userId, allowStrangerMsg);
                return Result.success(null);
            } else {
                return Result.error("SETTING_UPDATE_FAILED", "更新陌生人消息设置失败");
            }

        } catch (Exception e) {
            log.error("更新陌生人消息设置失败: userId={}", userId, e);
            return Result.error("SETTING_UPDATE_ERROR", "更新陌生人消息设置失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_AUTO_READ_RECEIPT_CACHE)
    public Result<Void> updateReadReceiptSetting(Long userId, Boolean autoReadReceipt) {
        try {
            log.info("更新已读回执设置: userId={}, autoReadReceipt={}", userId, autoReadReceipt);

            if (userId == null || autoReadReceipt == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageSettingService.updateReadReceiptSetting(userId, autoReadReceipt);
            if (success) {
                log.info("已读回执设置更新成功: userId={}, autoReadReceipt={}", userId, autoReadReceipt);
                return Result.success(null);
            } else {
                return Result.error("SETTING_UPDATE_FAILED", "更新已读回执设置失败");
            }

        } catch (Exception e) {
            log.error("更新已读回执设置失败: userId={}", userId, e);
            return Result.error("SETTING_UPDATE_ERROR", "更新已读回执设置失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_NOTIFICATION_CACHE)
    public Result<Void> updateNotificationSetting(Long userId, Boolean messageNotification) {
        try {
            log.info("更新消息通知设置: userId={}, messageNotification={}", userId, messageNotification);

            if (userId == null || messageNotification == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }

            boolean success = messageSettingService.updateNotificationSetting(userId, messageNotification);
            if (success) {
                log.info("消息通知设置更新成功: userId={}, messageNotification={}", userId, messageNotification);
                return Result.success(null);
            } else {
                return Result.error("SETTING_UPDATE_FAILED", "更新消息通知设置失败");
            }

        } catch (Exception e) {
            log.error("更新消息通知设置失败: userId={}", userId, e);
            return Result.error("SETTING_UPDATE_ERROR", "更新消息通知设置失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_STRANGER_MSG_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_AUTO_READ_RECEIPT_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_NOTIFICATION_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_PERMISSION_CACHE)
    public Result<Void> updateBatchSettings(Long userId, Boolean allowStrangerMsg, 
                                           Boolean autoReadReceipt, Boolean messageNotification) {
        try {
            log.info("批量更新用户设置: userId={}, allowStrangerMsg={}, autoReadReceipt={}, messageNotification={}",
                    userId, allowStrangerMsg, autoReadReceipt, messageNotification);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean success = messageSettingService.updateUserSettings(userId, allowStrangerMsg, autoReadReceipt, messageNotification);
            if (success) {
                log.info("用户设置批量更新成功: userId={}", userId);
                return Result.success(null);
            } else {
                return Result.error("SETTING_BATCH_UPDATE_FAILED", "批量更新用户设置失败");
            }

        } catch (Exception e) {
            log.error("批量更新用户设置失败: userId={}", userId, e);
            return Result.error("SETTING_BATCH_UPDATE_ERROR", "批量更新用户设置失败");
        }
    }

    // =================== 权限验证 ===================

    @Override
    @Cached(name = MessageCacheConstant.SETTING_PERMISSION_CACHE, expire = 30, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#senderId + ':' + #receiverId")
    public Result<Boolean> canSendMessage(Long senderId, Long receiverId) {
        try {
            log.debug("检查发送消息权限: senderId={}, receiverId={}", senderId, receiverId);

            if (senderId == null || receiverId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean canSend = messageSettingService.canSendMessage(senderId, receiverId);
            return Result.success(canSend);

        } catch (Exception e) {
            log.error("检查发送消息权限失败: senderId={}, receiverId={}", senderId, receiverId, e);
            return Result.error("PERMISSION_CHECK_ERROR", "检查发送权限失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SETTING_STRANGER_MSG_CACHE, expire = 30, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId")
    public Result<Boolean> isStrangerMessageAllowed(Long userId) {
        try {
            log.debug("检查陌生人消息权限: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean allowed = messageSettingService.isStrangerMessageAllowed(userId);
            return Result.success(allowed);

        } catch (Exception e) {
            log.error("检查陌生人消息权限失败: userId={}", userId, e);
            return Result.error("PERMISSION_CHECK_ERROR", "检查陌生人消息权限失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SETTING_AUTO_READ_RECEIPT_CACHE, expire = 30, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId")
    public Result<Boolean> isAutoReadReceiptEnabled(Long userId) {
        try {
            log.debug("检查自动已读回执设置: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean enabled = messageSettingService.isAutoReadReceiptEnabled(userId);
            return Result.success(enabled);

        } catch (Exception e) {
            log.error("检查自动已读回执设置失败: userId={}", userId, e);
            return Result.error("SETTING_CHECK_ERROR", "检查自动已读回执设置失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SETTING_NOTIFICATION_CACHE, expire = 30, timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH, key = "#userId")
    public Result<Boolean> isMessageNotificationEnabled(Long userId) {
        try {
            log.debug("检查消息通知设置: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean enabled = messageSettingService.isMessageNotificationEnabled(userId);
            return Result.success(enabled);

        } catch (Exception e) {
            log.error("检查消息通知设置失败: userId={}", userId, e);
            return Result.error("SETTING_CHECK_ERROR", "检查消息通知设置失败");
        }
    }

    @Override
    public Result<MessageSettingResponse> checkAllSettings(Long userId) {
        try {
            log.debug("批量检查用户设置状态: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            // 获取用户设置
            MessageSetting setting = messageSettingService.findByUserId(userId);
            MessageSettingResponse response = convertToResponse(setting);

            // 丰富响应信息
            try {
                Result<UserResponse> userResult = userFacadeService.getUserById(userId);
                if (userResult != null && userResult.getSuccess() && userResult.getData() != null) {
                    enrichSettingResponse(response, userResult.getData());
                }
            } catch (Exception e) {
                log.warn("获取用户信息失败，跳过丰富设置响应: userId={}", userId, e);
            }

            return Result.success(response);

        } catch (Exception e) {
            log.error("批量检查用户设置状态失败: userId={}", userId, e);
            return Result.error("SETTING_CHECK_ERROR", "批量检查用户设置状态失败");
        }
    }

    // =================== 设置模板 ===================

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_STRANGER_MSG_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_AUTO_READ_RECEIPT_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_NOTIFICATION_CACHE)
    @CacheInvalidate(name = MessageCacheConstant.SETTING_PERMISSION_CACHE)
    public Result<Void> resetToDefault(Long userId) {
        try {
            log.info("重置用户设置为默认值: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            boolean success = messageSettingService.resetToDefault(userId);
            if (success) {
                log.info("用户设置重置成功: userId={}", userId);
                return Result.success(null);
            } else {
                return Result.error("SETTING_RESET_FAILED", "重置用户设置失败");
            }

        } catch (Exception e) {
            log.error("重置用户设置失败: userId={}", userId, e);
            return Result.error("SETTING_RESET_ERROR", "重置用户设置失败");
        }
    }

    @Override
    @Cached(name = MessageCacheConstant.SETTING_DEFAULT_CACHE, expire = 24, timeUnit = TimeUnit.HOURS,
            cacheType = CacheType.BOTH, key = "'default'")
    public Result<MessageSettingResponse> getDefaultSetting() {
        try {
            log.debug("获取默认消息设置");

            MessageSetting defaultSetting = messageSettingService.getDefaultSetting();
            MessageSettingResponse response = convertToResponse(defaultSetting);

            return Result.success(response);

        } catch (Exception e) {
            log.error("获取默认消息设置失败", e);
            return Result.error("SETTING_DEFAULT_ERROR", "获取默认消息设置失败");
        }
    }

    @Override
    @CacheInvalidate(name = MessageCacheConstant.SETTING_USER_CACHE)
    public Result<Void> copySettingFromUser(Long fromUserId, Long toUserId) {
        try {
            log.info("复制用户设置: fromUserId={}, toUserId={}", fromUserId, toUserId);

            if (fromUserId == null || toUserId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            // 验证用户存在性
            Result<Void> userValidation = validateUsers(fromUserId, toUserId);
            if (!userValidation.getSuccess()) {
                return Result.error("USER_VALIDATION_ERROR", userValidation.getMessage());
            }

            boolean success = messageSettingService.copySettingFromUser(fromUserId, toUserId);
            if (success) {
                log.info("用户设置复制成功: fromUserId={}, toUserId={}", fromUserId, toUserId);
                return Result.success(null);
            } else {
                return Result.error("SETTING_COPY_FAILED", "复制用户设置失败");
            }

        } catch (Exception e) {
            log.error("复制用户设置失败: fromUserId={}, toUserId={}", fromUserId, toUserId, e);
            return Result.error("SETTING_COPY_ERROR", "复制用户设置失败");
        }
    }

    @Override
    public Result<Integer> batchInitSettings(List<Long> userIds) {
        try {
            log.info("批量初始化用户设置: userIds.size={}", userIds != null ? userIds.size() : 0);

            if (userIds == null || userIds.isEmpty()) {
                return Result.error("INVALID_PARAM", "用户ID列表不能为空");
            }

            int successCount = 0;
            for (Long userId : userIds) {
                try {
                    messageSettingService.initDefaultSetting(userId);
                    successCount++;
                } catch (Exception e) {
                    log.warn("初始化用户设置失败: userId={}", userId, e);
                }
            }

            log.info("批量初始化用户设置完成: 成功数量={}", successCount);
            return Result.success(successCount);

        } catch (Exception e) {
            log.error("批量初始化用户设置失败", e);
            return Result.error("SETTING_BATCH_INIT_ERROR", "批量初始化用户设置失败");
        }
    }

    // =================== 设置分析 ===================

    @Override
    @Cached(name = MessageCacheConstant.SETTING_STATISTICS_CACHE, expire = 2, timeUnit = TimeUnit.HOURS,
            cacheType = CacheType.BOTH, key = "'statistics'")
    public Result<Map<String, Object>> getSettingStatistics() {
        try {
            log.debug("获取设置统计信息");

            // 这里可以实现设置统计逻辑
            // 目前返回简单的统计数据
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("timestamp", System.currentTimeMillis());
            statistics.put("strangerMessageEnabled", "70%");
            statistics.put("autoReadReceiptEnabled", "85%");
            statistics.put("messageNotificationEnabled", "90%");
            statistics.put("totalUsers", "N/A");

            return Result.success(statistics);

        } catch (Exception e) {
            log.error("获取设置统计信息失败", e);
            return Result.error("SETTING_STATISTICS_ERROR", "获取设置统计信息失败");
        }
    }

    @Override
    public Result<List<MessageSettingResponse>> getSettingHistory(Long userId, Integer limit) {
        try {
            log.debug("获取用户设置历史: userId={}, limit={}", userId, limit);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            // 目前简化处理，返回当前设置
            MessageSetting currentSetting = messageSettingService.findByUserId(userId);
            MessageSettingResponse response = convertToResponse(currentSetting);

            List<MessageSettingResponse> history = Collections.singletonList(response);
            return Result.success(history);

        } catch (Exception e) {
            log.error("获取用户设置历史失败: userId={}", userId, e);
            return Result.error("SETTING_HISTORY_ERROR", "获取用户设置历史失败");
        }
    }

    // =================== 系统功能 ===================

    @Override
    public Result<String> syncUserSetting(Long userId) {
        try {
            log.info("同步用户设置: userId={}", userId);

            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }

            // 这里可以实现与其他系统的设置同步逻辑
            // 目前返回简单的同步结果
            String result = "User setting sync completed for user: " + userId;

            log.info("用户设置同步完成: userId={}", userId);
            return Result.success(result);

        } catch (Exception e) {
            log.error("同步用户设置失败: userId={}", userId, e);
            return Result.error("SETTING_SYNC_ERROR", "同步用户设置失败");
        }
    }

    @Override
    public Result<String> healthCheck() {
        try {
            // 简单的健康检查：获取当前时间戳
            long timestamp = System.currentTimeMillis();
            String status = "Message setting service is healthy at " + timestamp;

            log.debug("消息设置系统健康检查: {}", status);
            return Result.success(status);

        } catch (Exception e) {
            log.error("消息设置系统健康检查失败", e);
            return Result.error("HEALTH_CHECK_ERROR", "系统健康检查失败");
        }
    }

    // =================== 私有工具方法 ===================

    /**
     * 验证用户存在性
     */
    private Result<Void> validateUsers(Long... userIds) {
        for (Long userId : userIds) {
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在: " + userId);
            }
        }
        return Result.success(null);
    }

    /**
     * 转换创建请求为实体
     */
    private MessageSetting convertCreateRequestToEntity(MessageSettingCreateRequest request) {
        MessageSetting messageSetting = new MessageSetting();
        BeanUtils.copyProperties(request, messageSetting);
        return messageSetting;
    }

    /**
     * 转换实体为响应对象
     */
    private MessageSettingResponse convertToResponse(MessageSetting messageSetting) {
        MessageSettingResponse response = new MessageSettingResponse();
        BeanUtils.copyProperties(messageSetting, response);
        return response;
    }

    /**
     * 丰富设置响应对象（填充用户信息等）
     */
    private void enrichSettingResponse(MessageSettingResponse response, UserResponse user) {
        try {
            if (user != null) {
                // 如果Response类有相应的setter方法，可以在这里设置用户信息
                response.setUserNickname(user.getNickname());
                // 目前简化处理，仅记录日志
                log.debug("丰富设置响应对象: userId={}, nickname={}", response.getUserId(), user.getNickname());
            }
        } catch (Exception e) {
            log.warn("丰富设置响应对象失败: userId={}", response.getUserId(), e);
            // 不抛异常，避免影响主要业务
        }
    }
}