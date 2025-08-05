package com.gig.collide.message.domain.service.impl;

import com.gig.collide.message.domain.entity.MessageSetting;
import com.gig.collide.message.domain.service.MessageSettingService;
import com.gig.collide.message.infrastructure.mapper.MessageSettingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息设置业务服务实现类 - 简洁版
 * 基于message-simple.sql的t_message_setting表设计
 * 管理用户的消息偏好设置
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSettingServiceImpl implements MessageSettingService {

    private final MessageSettingMapper messageSettingMapper;

    // =================== 基础CRUD ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageSetting createOrUpdateSetting(MessageSetting messageSetting) {
        log.info("创建或更新消息设置: userId={}", messageSetting.getUserId());

        // 参数验证
        validateSettingParams(messageSetting);

        // 检查是否已存在设置
        MessageSetting existingSetting = messageSettingMapper.findByUserId(messageSetting.getUserId());

        if (existingSetting != null) {
            // 更新现有设置
            updateExistingSetting(existingSetting, messageSetting);
            int result = messageSettingMapper.updateById(existingSetting);
            if (result <= 0) {
                throw new RuntimeException("更新消息设置失败");
            }
            log.info("消息设置更新成功: userId={}", messageSetting.getUserId());
            return existingSetting;
        } else {
            // 创建新设置
            setSettingDefaults(messageSetting);
            int result = messageSettingMapper.insert(messageSetting);
            if (result <= 0) {
                throw new RuntimeException("创建消息设置失败");
            }
            log.info("消息设置创建成功: userId={}", messageSetting.getUserId());
            return messageSetting;
        }
    }

    @Override
    public MessageSetting findByUserId(Long userId) {
        log.debug("查询用户消息设置: userId={}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        MessageSetting setting = messageSettingMapper.findByUserId(userId);
        
        // 如果不存在设置，返回默认设置
        if (setting == null) {
            log.debug("用户设置不存在，返回默认设置: userId={}", userId);
            return getDefaultSetting();
        }

        return setting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageSetting initDefaultSetting(Long userId) {
        log.info("初始化用户默认设置: userId={}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 检查是否已存在设置
        MessageSetting existingSetting = messageSettingMapper.findByUserId(userId);
        if (existingSetting != null) {
            log.info("用户设置已存在，无需初始化: userId={}", userId);
            return existingSetting;
        }

        // 创建默认设置
        MessageSetting defaultSetting = createDefaultSettingForUser(userId);
        int result = messageSettingMapper.insert(defaultSetting);
        if (result <= 0) {
            throw new RuntimeException("初始化用户默认设置失败");
        }

        log.info("用户默认设置初始化成功: userId={}", userId);
        return defaultSetting;
    }

    // =================== 设置更新 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStrangerMessageSetting(Long userId, Boolean allowStrangerMsg) {
        log.info("更新陌生人消息设置: userId={}, allowStrangerMsg={}", userId, allowStrangerMsg);

        if (userId == null || allowStrangerMsg == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 确保设置存在
        ensureSettingExists(userId);

        int result = messageSettingMapper.updateStrangerMessageSetting(userId, allowStrangerMsg);
        boolean success = result > 0;

        if (success) {
            log.info("陌生人消息设置更新成功: userId={}, allowStrangerMsg={}", userId, allowStrangerMsg);
        } else {
            log.warn("陌生人消息设置更新失败: userId={}, allowStrangerMsg={}", userId, allowStrangerMsg);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReadReceiptSetting(Long userId, Boolean autoReadReceipt) {
        log.info("更新已读回执设置: userId={}, autoReadReceipt={}", userId, autoReadReceipt);

        if (userId == null || autoReadReceipt == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 确保设置存在
        ensureSettingExists(userId);

        int result = messageSettingMapper.updateReadReceiptSetting(userId, autoReadReceipt);
        boolean success = result > 0;

        if (success) {
            log.info("已读回执设置更新成功: userId={}, autoReadReceipt={}", userId, autoReadReceipt);
        } else {
            log.warn("已读回执设置更新失败: userId={}, autoReadReceipt={}", userId, autoReadReceipt);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNotificationSetting(Long userId, Boolean messageNotification) {
        log.info("更新消息通知设置: userId={}, messageNotification={}", userId, messageNotification);

        if (userId == null || messageNotification == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 确保设置存在
        ensureSettingExists(userId);

        int result = messageSettingMapper.updateNotificationSetting(userId, messageNotification);
        boolean success = result > 0;

        if (success) {
            log.info("消息通知设置更新成功: userId={}, messageNotification={}", userId, messageNotification);
        } else {
            log.warn("消息通知设置更新失败: userId={}, messageNotification={}", userId, messageNotification);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserSettings(Long userId, Boolean allowStrangerMsg, 
                                     Boolean autoReadReceipt, Boolean messageNotification) {
        log.info("批量更新用户设置: userId={}, allowStrangerMsg={}, autoReadReceipt={}, messageNotification={}",
                userId, allowStrangerMsg, autoReadReceipt, messageNotification);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 确保设置存在
        ensureSettingExists(userId);

        int result = messageSettingMapper.updateUserSettings(userId, allowStrangerMsg, autoReadReceipt, messageNotification);
        boolean success = result > 0;

        if (success) {
            log.info("用户设置批量更新成功: userId={}", userId);
        } else {
            log.warn("用户设置批量更新失败: userId={}", userId);
        }

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrUpdate(Long userId, Boolean allowStrangerMsg, 
                                 Boolean autoReadReceipt, Boolean messageNotification) {
        log.info("创建或更新用户设置: userId={}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        int result = messageSettingMapper.insertOrUpdate(userId, allowStrangerMsg, autoReadReceipt, messageNotification);
        boolean success = result > 0;

        if (success) {
            log.info("用户设置创建或更新成功: userId={}", userId);
        } else {
            log.warn("用户设置创建或更新失败: userId={}", userId);
        }

        return success;
    }

    // =================== 权限验证 ===================

    @Override
    public boolean canSendMessage(Long senderId, Long receiverId) {
        log.debug("检查是否允许发送消息: senderId={}, receiverId={}", senderId, receiverId);

        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 自己不能给自己发消息
        if (senderId.equals(receiverId)) {
            return false;
        }

        // 获取接收者的消息设置
        MessageSetting receiverSetting = findByUserId(receiverId);
        
        // 如果不允许陌生人消息，需要检查是否为好友关系
        if (!receiverSetting.isAllowStrangerMessage()) {
            // 这里可以集成好友关系检查服务
            // 目前简化处理，假设都是陌生人关系
            log.debug("接收者不允许陌生人消息: receiverId={}", receiverId);
            return false;
        }

        return true;
    }

    @Override
    public boolean isStrangerMessageAllowed(Long userId) {
        log.debug("检查是否允许陌生人消息: userId={}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        MessageSetting setting = findByUserId(userId);
        return setting.isAllowStrangerMessage();
    }

    @Override
    public boolean isAutoReadReceiptEnabled(Long userId) {
        log.debug("检查是否开启自动已读回执: userId={}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        MessageSetting setting = findByUserId(userId);
        return setting.isAutoReadReceipt();
    }

    @Override
    public boolean isMessageNotificationEnabled(Long userId) {
        log.debug("检查是否开启消息通知: userId={}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        MessageSetting setting = findByUserId(userId);
        return setting.isMessageNotificationEnabled();
    }

    // =================== 业务逻辑 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetToDefault(Long userId) {
        log.info("重置用户设置为默认值: userId={}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 获取默认设置值
        MessageSetting defaultSetting = getDefaultSetting();
        
        boolean success = updateUserSettings(userId, 
                defaultSetting.getAllowStrangerMsg(),
                defaultSetting.getAutoReadReceipt(),
                defaultSetting.getMessageNotification());

        if (success) {
            log.info("用户设置重置成功: userId={}", userId);
        } else {
            log.warn("用户设置重置失败: userId={}", userId);
        }

        return success;
    }

    @Override
    public MessageSetting getDefaultSetting() {
        // 返回系统默认设置
        MessageSetting defaultSetting = new MessageSetting();
        defaultSetting.setUserId(null); // 默认设置不绑定具体用户
        defaultSetting.setAllowStrangerMsg(true);  // 默认允许陌生人消息
        defaultSetting.setAutoReadReceipt(true);   // 默认开启自动已读回执
        defaultSetting.setMessageNotification(true); // 默认开启消息通知
        
        return defaultSetting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copySettingFromUser(Long fromUserId, Long toUserId) {
        log.info("复制用户设置: fromUserId={}, toUserId={}", fromUserId, toUserId);

        if (fromUserId == null || toUserId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("不能复制自己的设置");
        }

        // 获取源用户设置
        MessageSetting fromSetting = findByUserId(fromUserId);
        if (fromSetting == null) {
            log.warn("源用户设置不存在，使用默认设置: fromUserId={}", fromUserId);
            fromSetting = getDefaultSetting();
        }

        // 复制设置到目标用户
        boolean success = insertOrUpdate(toUserId,
                fromSetting.getAllowStrangerMsg(),
                fromSetting.getAutoReadReceipt(),
                fromSetting.getMessageNotification());

        if (success) {
            log.info("用户设置复制成功: fromUserId={}, toUserId={}", fromUserId, toUserId);
        } else {
            log.warn("用户设置复制失败: fromUserId={}, toUserId={}", fromUserId, toUserId);
        }

        return success;
    }

    // =================== 私有方法 ===================

    /**
     * 验证设置参数
     */
    private void validateSettingParams(MessageSetting messageSetting) {
        if (messageSetting == null) {
            throw new IllegalArgumentException("消息设置对象不能为空");
        }
        if (messageSetting.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
    }

    /**
     * 更新现有设置
     */
    private void updateExistingSetting(MessageSetting existingSetting, MessageSetting newSetting) {
        // 只更新非空字段
        if (newSetting.getAllowStrangerMsg() != null) {
            existingSetting.setAllowStrangerMsg(newSetting.getAllowStrangerMsg());
        }
        if (newSetting.getAutoReadReceipt() != null) {
            existingSetting.setAutoReadReceipt(newSetting.getAutoReadReceipt());
        }
        if (newSetting.getMessageNotification() != null) {
            existingSetting.setMessageNotification(newSetting.getMessageNotification());
        }
        
        existingSetting.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 设置默认值
     */
    private void setSettingDefaults(MessageSetting messageSetting) {
        if (messageSetting.getAllowStrangerMsg() == null) {
            messageSetting.setAllowStrangerMsg(true);
        }
        if (messageSetting.getAutoReadReceipt() == null) {
            messageSetting.setAutoReadReceipt(true);
        }
        if (messageSetting.getMessageNotification() == null) {
            messageSetting.setMessageNotification(true);
        }
        
        messageSetting.setCreateTime(LocalDateTime.now());
        messageSetting.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 为用户创建默认设置
     */
    private MessageSetting createDefaultSettingForUser(Long userId) {
        MessageSetting setting = new MessageSetting();
        setting.setUserId(userId);
        setting.setAllowStrangerMsg(true);
        setting.setAutoReadReceipt(true);
        setting.setMessageNotification(true);
        setting.setCreateTime(LocalDateTime.now());
        setting.setUpdateTime(LocalDateTime.now());
        
        return setting;
    }

    /**
     * 确保用户设置存在，如果不存在则创建默认设置
     */
    private void ensureSettingExists(Long userId) {
        MessageSetting existingSetting = messageSettingMapper.findByUserId(userId);
        if (existingSetting == null) {
            log.debug("用户设置不存在，创建默认设置: userId={}", userId);
            initDefaultSetting(userId);
        }
    }

    /**
     * 验证设置值的有效性
     */
    private boolean isValidSettingValue(Boolean value) {
        return value != null;
    }

    /**
     * 获取安全的设置值（防止null值）
     */
    private boolean getSafeSettingValue(Boolean value, boolean defaultValue) {
        return value != null ? value : defaultValue;
    }
}