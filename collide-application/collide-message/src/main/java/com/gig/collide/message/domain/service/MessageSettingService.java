package com.gig.collide.message.domain.service;

import com.gig.collide.message.domain.entity.MessageSetting;

/**
 * 消息设置业务服务接口 - 简洁版
 * 基于message-simple.sql的t_message_setting表设计
 * 管理用户的消息偏好设置
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
public interface MessageSettingService {

    // =================== 基础CRUD ===================

    /**
     * 创建或更新用户消息设置
     * 如果设置不存在则创建，存在则更新
     * 
     * @param messageSetting 消息设置实体
     * @return 保存的设置
     */
    MessageSetting createOrUpdateSetting(MessageSetting messageSetting);

    /**
     * 根据用户ID查询消息设置
     * 如果不存在则返回默认设置
     * 
     * @param userId 用户ID
     * @return 消息设置
     */
    MessageSetting findByUserId(Long userId);

    /**
     * 初始化用户默认设置
     * 为新用户创建默认的消息设置
     * 
     * @param userId 用户ID
     * @return 创建的默认设置
     */
    MessageSetting initDefaultSetting(Long userId);

    // =================== 设置更新 ===================

    /**
     * 更新陌生人消息设置
     * 
     * @param userId 用户ID
     * @param allowStrangerMsg 是否允许陌生人发消息
     * @return 是否成功
     */
    boolean updateStrangerMessageSetting(Long userId, Boolean allowStrangerMsg);

    /**
     * 更新已读回执设置
     * 
     * @param userId 用户ID
     * @param autoReadReceipt 是否自动发送已读回执
     * @return 是否成功
     */
    boolean updateReadReceiptSetting(Long userId, Boolean autoReadReceipt);

    /**
     * 更新消息通知设置
     * 
     * @param userId 用户ID
     * @param messageNotification 是否开启消息通知
     * @return 是否成功
     */
    boolean updateNotificationSetting(Long userId, Boolean messageNotification);

    /**
     * 批量更新用户设置
     * 
     * @param userId 用户ID
     * @param allowStrangerMsg 是否允许陌生人发消息（可选）
     * @param autoReadReceipt 是否自动发送已读回执（可选）
     * @param messageNotification 是否开启消息通知（可选）
     * @return 是否成功
     */
    boolean updateUserSettings(Long userId, Boolean allowStrangerMsg, 
                              Boolean autoReadReceipt, Boolean messageNotification);

    /**
     * 创建或更新用户设置（便捷方法）
     * 
     * @param userId 用户ID
     * @param allowStrangerMsg 是否允许陌生人发消息
     * @param autoReadReceipt 是否自动发送已读回执
     * @param messageNotification 是否开启消息通知
     * @return 是否成功
     */
    boolean insertOrUpdate(Long userId, Boolean allowStrangerMsg, 
                          Boolean autoReadReceipt, Boolean messageNotification);

    // =================== 权限验证 ===================

    /**
     * 检查是否允许发送消息
     * 根据用户设置验证是否可以向目标用户发送消息
     * 
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 是否允许发送
     */
    boolean canSendMessage(Long senderId, Long receiverId);

    /**
     * 检查是否允许陌生人消息
     * 
     * @param userId 用户ID
     * @return 是否允许陌生人发消息
     */
    boolean isStrangerMessageAllowed(Long userId);

    /**
     * 检查是否开启自动已读回执
     * 
     * @param userId 用户ID
     * @return 是否开启自动已读回执
     */
    boolean isAutoReadReceiptEnabled(Long userId);

    /**
     * 检查是否开启消息通知
     * 
     * @param userId 用户ID
     * @return 是否开启消息通知
     */
    boolean isMessageNotificationEnabled(Long userId);

    // =================== 业务逻辑 ===================

    /**
     * 重置用户设置为默认值
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean resetToDefault(Long userId);

    /**
     * 获取默认消息设置
     * 
     * @return 默认设置对象
     */
    MessageSetting getDefaultSetting();

    /**
     * 复制设置到新用户
     * 从模板用户复制设置到新用户
     * 
     * @param fromUserId 模板用户ID
     * @param toUserId 目标用户ID
     * @return 是否成功
     */
    boolean copySettingFromUser(Long fromUserId, Long toUserId);
}