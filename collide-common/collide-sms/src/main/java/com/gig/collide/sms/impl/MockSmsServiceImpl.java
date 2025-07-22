package com.gig.collide.sms.impl;

import com.gig.collide.sms.SmsService;
import com.gig.collide.sms.response.SmsSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Mock短信服务实现
 * 用于开发和测试环境
 * 参考 nft-turbo-sms 设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "collide.sms.provider", havingValue = "mock", matchIfMissing = true)
public class MockSmsServiceImpl implements SmsService {

    @Override
    public SmsSendResponse sendMsg(String phoneNumber, String code) {
        log.info("🚀 [Mock短信] 发送验证码到手机号: {}, 验证码: {}", phoneNumber, code);
        
        try {
            // 模拟网络延迟
            TimeUnit.MILLISECONDS.sleep(100);
            
            // 模拟发送成功
            String messageId = UUID.randomUUID().toString();
            log.info("✅ [Mock短信] 验证码发送成功, MessageId: {}", messageId);
            
            return SmsSendResponse.success(phoneNumber, messageId);
            
        } catch (Exception e) {
            log.error("❌ [Mock短信] 发送验证码失败", e);
            return SmsSendResponse.failure(phoneNumber, "MOCK_ERROR", "Mock短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public SmsSendResponse sendNotification(String phoneNumber, String message) {
        log.info("🚀 [Mock短信] 发送通知到手机号: {}, 内容: {}", phoneNumber, message);
        
        try {
            // 模拟网络延迟
            TimeUnit.MILLISECONDS.sleep(100);
            
            // 模拟发送成功
            String messageId = UUID.randomUUID().toString();
            log.info("✅ [Mock短信] 通知发送成功, MessageId: {}", messageId);
            
            return SmsSendResponse.success(phoneNumber, messageId);
            
        } catch (Exception e) {
            log.error("❌ [Mock短信] 发送通知失败", e);
            return SmsSendResponse.failure(phoneNumber, "MOCK_ERROR", "Mock短信发送失败: " + e.getMessage());
        }
    }
} 