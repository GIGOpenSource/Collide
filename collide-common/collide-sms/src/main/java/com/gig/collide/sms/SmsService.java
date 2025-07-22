package com.gig.collide.sms;

import com.gig.collide.sms.response.SmsSendResponse;

/**
 * 短信服务接口
 * 参考 nft-turbo-sms 设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface SmsService {
    
    /**
     * 发送短信验证码
     *
     * @param phoneNumber 手机号码
     * @param code        验证码
     * @return 发送结果
     */
    SmsSendResponse sendMsg(String phoneNumber, String code);
    
    /**
     * 发送短信通知
     *
     * @param phoneNumber 手机号码
     * @param message     短信内容
     * @return 发送结果
     */
    SmsSendResponse sendNotification(String phoneNumber, String message);
} 