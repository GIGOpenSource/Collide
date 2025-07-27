package com.gig.collide.users.infrastructure.service;

import com.gig.collide.users.domain.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现类
 * 实现短信发送功能，支持激活短信、密码重置、登录验证等
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${collide.sms.platform-name:Collide}")
    private String platformName;

    @Value("${collide.sms.sign-name:【Collide】}")
    private String signName;

    @Override
    public boolean sendActivationSms(String phone, String activationCode) {
        try {
            log.info("发送激活短信到：{}，激活码：{}", phone, activationCode);
            
            String message = String.format(
                "%s您的激活码是：%s，请在10分钟内使用。如非本人操作，请忽略此短信。",
                signName, activationCode
            );
            
            // TODO: 这里应该集成真实的短信服务提供商（如：腾讯云短信、阿里云短信、华为云短信等）
            return sendSmsInternal(phone, message);
            
        } catch (Exception e) {
            log.error("发送激活短信失败，手机号：{}，激活码：{}", phone, activationCode, e);
            return false;
        }
    }

    @Override
    public boolean sendPasswordResetSms(String phone, String resetCode) {
        try {
            log.info("发送密码重置短信到：{}，重置码：{}", phone, resetCode);
            
            String message = String.format(
                "%s您的密码重置验证码是：%s，请在15分钟内使用。如非本人操作，请立即联系客服。",
                signName, resetCode
            );
            
            return sendSmsInternal(phone, message);
            
        } catch (Exception e) {
            log.error("发送密码重置短信失败，手机号：{}，重置码：{}", phone, resetCode, e);
            return false;
        }
    }

    @Override
    public boolean sendLoginVerifySms(String phone, String verifyCode) {
        try {
            log.info("发送登录验证码短信到：{}，验证码：{}", phone, verifyCode);
            
            String message = String.format(
                "%s您的登录验证码是：%s，请在5分钟内使用。如非本人操作，请忽略此短信。",
                signName, verifyCode
            );
            
            return sendSmsInternal(phone, message);
            
        } catch (Exception e) {
            log.error("发送登录验证码短信失败，手机号：{}，验证码：{}", phone, verifyCode, e);
            return false;
        }
    }

    @Override
    public boolean sendBloggerStatusSms(String phone, String username, boolean approved) {
        try {
            log.info("发送博主认证结果短信到：{}，用户名：{}，通过：{}", phone, username, approved);
            
            String message;
            if (approved) {
                message = String.format(
                    "%s恭喜%s！您的博主认证申请已通过审核，现在可以享受博主特权了。详情请登录查看。",
                    signName, username
                );
            } else {
                message = String.format(
                    "%s%s，很抱歉您的博主认证申请未通过审核。请登录查看详细反馈并可重新申请。",
                    signName, username
                );
            }
            
            return sendSmsInternal(phone, message);
            
        } catch (Exception e) {
            log.error("发送博主认证结果短信失败，手机号：{}，用户名：{}", phone, username, e);
            return false;
        }
    }

    /**
     * 内部短信发送方法
     * 这里应该集成真实的短信服务提供商
     */
    private boolean sendSmsInternal(String phone, String message) {
        // TODO: 集成真实的短信服务提供商
        // 可以选择：腾讯云短信、阿里云短信、华为云短信、网易云信、容联云通讯等
        
        // 短信内容长度检查
        if (message.length() > 500) {
            log.warn("短信内容过长，可能被截断：{} 字符", message.length());
        }
        
        // 手机号格式基础验证
        if (!isValidPhoneNumber(phone)) {
            log.error("无效的手机号格式：{}", phone);
            return false;
        }
        
        log.info("模拟发送短信");
        log.info("手机号：{}", phone);
        log.info("内容：{}", message);
        
        // 模拟短信发送成功
        return true;
    }

    /**
     * 基础手机号格式验证
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // 简单的中国大陆手机号验证（11位，以1开头）
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        return cleanPhone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 获取短信内容长度统计
     * 用于短信计费统计
     */
    public int getSmsLength(String message) {
        if (message == null) {
            return 0;
        }
        
        // 中文字符按2个字节计算，英文字符按1个字节计算
        int length = 0;
        for (char c : message.toCharArray()) {
            if (isChinese(c)) {
                length += 2;
            } else {
                length += 1;
            }
        }
        
        return length;
    }

    /**
     * 判断字符是否为中文
     */
    private boolean isChinese(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
    }

    /**
     * 获取短信条数估算
     * 用于成本控制
     */
    public int estimateSmsCount(String message) {
        int length = getSmsLength(message);
        
        // 一般短信长度限制：
        // 纯英文：160字符为1条
        // 中英文混合：70字符为1条
        // 这里简化按70字符一条计算
        return (length + 69) / 70;
    }
} 