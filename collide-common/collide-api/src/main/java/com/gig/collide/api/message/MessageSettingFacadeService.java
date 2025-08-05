package com.gig.collide.api.message;

import com.gig.collide.api.message.request.MessageSettingCreateRequest;
import com.gig.collide.api.message.request.MessageSettingUpdateRequest;
import com.gig.collide.api.message.response.MessageSettingResponse;
import com.gig.collide.web.vo.Result;

/**
 * 消息设置门面服务接口 - 简洁版
 * 基于message-simple.sql的t_message_setting表设计
 * 管理用户的消息偏好设置和权限控制
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageSettingFacadeService {

    // =================== 设置管理 ===================

    /**
     * 创建或更新用户消息设置
     * 如果设置不存在则创建默认设置，存在则更新
     * 
     * @param request 设置创建请求
     * @return 保存的设置
     */
    Result<MessageSettingResponse> createOrUpdateSetting(MessageSettingCreateRequest request);

    /**
     * 根据用户ID获取消息设置
     * 如果不存在则返回默认设置
     * 
     * @param userId 用户ID
     * @return 消息设置
     */
    Result<MessageSettingResponse> getUserSetting(Long userId);

    /**
     * 更新用户消息设置
     * 
     * @param request 更新请求
     * @return 更新后的设置
     */
    Result<MessageSettingResponse> updateUserSetting(MessageSettingUpdateRequest request);

    /**
     * 初始化用户默认设置
     * 为新用户创建默认的消息设置
     * 
     * @param userId 用户ID
     * @return 创建的默认设置
     */
    Result<MessageSettingResponse> initDefaultSetting(Long userId);

    // =================== 单项设置更新 ===================

    /**
     * 更新陌生人消息设置
     * 
     * @param userId 用户ID
     * @param allowStrangerMsg 是否允许陌生人发消息
     * @return 操作结果
     */
    Result<Void> updateStrangerMessageSetting(Long userId, Boolean allowStrangerMsg);

    /**
     * 更新已读回执设置
     * 
     * @param userId 用户ID
     * @param autoReadReceipt 是否自动发送已读回执
     * @return 操作结果
     */
    Result<Void> updateReadReceiptSetting(Long userId, Boolean autoReadReceipt);

    /**
     * 更新消息通知设置
     * 
     * @param userId 用户ID
     * @param messageNotification 是否开启消息通知
     * @return 操作结果
     */
    Result<Void> updateNotificationSetting(Long userId, Boolean messageNotification);

    /**
     * 批量更新用户设置
     * 支持同时更新多个设置项
     * 
     * @param userId 用户ID
     * @param allowStrangerMsg 是否允许陌生人发消息（可选）
     * @param autoReadReceipt 是否自动发送已读回执（可选）
     * @param messageNotification 是否开启消息通知（可选）
     * @return 操作结果
     */
    Result<Void> updateBatchSettings(Long userId, Boolean allowStrangerMsg, 
                                    Boolean autoReadReceipt, Boolean messageNotification);

    // =================== 权限验证 ===================

    /**
     * 检查是否允许发送消息
     * 根据用户设置和关系验证是否可以向目标用户发送消息
     * 
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 是否允许发送
     */
    Result<Boolean> canSendMessage(Long senderId, Long receiverId);

    /**
     * 检查是否允许陌生人消息
     * 
     * @param userId 用户ID
     * @return 是否允许陌生人发消息
     */
    Result<Boolean> isStrangerMessageAllowed(Long userId);

    /**
     * 检查是否开启自动已读回执
     * 
     * @param userId 用户ID
     * @return 是否开启自动已读回执
     */
    Result<Boolean> isAutoReadReceiptEnabled(Long userId);

    /**
     * 检查是否开启消息通知
     * 
     * @param userId 用户ID
     * @return 是否开启消息通知
     */
    Result<Boolean> isMessageNotificationEnabled(Long userId);

    /**
     * 批量检查用户设置状态
     * 
     * @param userId 用户ID
     * @return 设置状态汇总
     */
    Result<MessageSettingResponse> checkAllSettings(Long userId);

    // =================== 设置模板 ===================

    /**
     * 重置用户设置为默认值
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> resetToDefault(Long userId);

    /**
     * 获取默认消息设置
     * 
     * @return 默认设置
     */
    Result<MessageSettingResponse> getDefaultSetting();

    /**
     * 复制设置到新用户
     * 从模板用户复制设置到新用户
     * 
     * @param fromUserId 模板用户ID
     * @param toUserId 目标用户ID
     * @return 操作结果
     */
    Result<Void> copySettingFromUser(Long fromUserId, Long toUserId);

    /**
     * 批量初始化用户设置
     * 为多个用户批量创建默认设置
     * 
     * @param userIds 用户ID列表
     * @return 操作结果
     */
    Result<Integer> batchInitSettings(java.util.List<Long> userIds);

    // =================== 设置分析 ===================

    /**
     * 获取设置统计信息
     * 统计各设置项的启用情况
     * 
     * @return 统计信息
     */
    Result<java.util.Map<String, Object>> getSettingStatistics();

    /**
     * 获取用户设置历史
     * 查看用户设置的变更记录
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 设置历史记录
     */
    Result<java.util.List<MessageSettingResponse>> getSettingHistory(Long userId, Integer limit);

    // =================== 系统功能 ===================

    /**
     * 同步用户设置
     * 同步用户在其他系统中的设置
     * 
     * @param userId 用户ID
     * @return 同步结果
     */
    Result<String> syncUserSetting(Long userId);

    /**
     * 消息设置系统健康检查
     * 
     * @return 系统状态
     */
    Result<String> healthCheck();
}