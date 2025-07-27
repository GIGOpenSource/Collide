package com.gig.collide.users.domain.service;

/**
 * 短信服务接口
 * 处理用户相关的短信发送业务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface SmsService {

    /**
     * 发送激活短信
     *
     * @param phone 手机号
     * @param activationCode 激活码
     * @return 是否发送成功
     */
    boolean sendActivationSms(String phone, String activationCode);

    /**
     * 发送密码重置短信
     *
     * @param phone 手机号
     * @param resetCode 重置码
     * @return 是否发送成功
     */
    boolean sendPasswordResetSms(String phone, String resetCode);

    /**
     * 发送登录验证码短信
     *
     * @param phone 手机号
     * @param verifyCode 验证码
     * @return 是否发送成功
     */
    boolean sendLoginVerifySms(String phone, String verifyCode);

    /**
     * 发送博主认证结果短信
     *
     * @param phone 手机号
     * @param username 用户名
     * @param approved 是否通过认证
     * @return 是否发送成功
     */
    boolean sendBloggerStatusSms(String phone, String username, boolean approved);
} 