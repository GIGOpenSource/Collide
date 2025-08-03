package com.gig.collide.message.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.message.domain.entity.MessageSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息设置数据访问接口 - 简洁版
 * 基于message-simple.sql的t_message_setting表设计
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Mapper
public interface MessageSettingMapper extends BaseMapper<MessageSetting> {

    // =================== 基础查询 ===================

    /**
     * 根据用户ID查询消息设置
     */
    MessageSetting findByUserId(@Param("userId") Long userId);

    // =================== 更新操作 ===================

    /**
     * 更新陌生人消息设置
     */
    int updateStrangerMessageSetting(@Param("userId") Long userId,
                                   @Param("allowStrangerMsg") Boolean allowStrangerMsg);

    /**
     * 更新已读回执设置
     */
    int updateReadReceiptSetting(@Param("userId") Long userId,
                               @Param("autoReadReceipt") Boolean autoReadReceipt);

    /**
     * 更新消息通知设置
     */
    int updateNotificationSetting(@Param("userId") Long userId,
                                @Param("messageNotification") Boolean messageNotification);

    /**
     * 批量更新用户设置
     */
    int updateUserSettings(@Param("userId") Long userId,
                         @Param("allowStrangerMsg") Boolean allowStrangerMsg,
                         @Param("autoReadReceipt") Boolean autoReadReceipt,
                         @Param("messageNotification") Boolean messageNotification);

    // =================== 创建或更新 ===================

    /**
     * 创建或更新用户设置
     * 如果设置不存在则创建，存在则更新
     */
    int insertOrUpdate(@Param("userId") Long userId,
                      @Param("allowStrangerMsg") Boolean allowStrangerMsg,
                      @Param("autoReadReceipt") Boolean autoReadReceipt,
                      @Param("messageNotification") Boolean messageNotification);
}