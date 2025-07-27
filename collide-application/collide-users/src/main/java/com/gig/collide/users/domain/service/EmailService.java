package com.gig.collide.users.domain.service;

/**
 * 邮件服务接口
 * 处理用户相关的邮件发送业务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface EmailService {

    /**
     * 发送激活邮件
     *
     * @param email 邮箱地址
     * @param activationCode 激活码
     * @return 是否发送成功
     */
    boolean sendActivationEmail(String email, String activationCode);

    /**
     * 发送密码重置邮件
     *
     * @param email 邮箱地址
     * @param resetCode 重置码
     * @return 是否发送成功
     */
    boolean sendPasswordResetEmail(String email, String resetCode);

    /**
     * 发送欢迎邮件
     *
     * @param email 邮箱地址
     * @param username 用户名
     * @return 是否发送成功
     */
    boolean sendWelcomeEmail(String email, String username);

    /**
     * 发送博主认证结果邮件
     *
     * @param email 邮箱地址
     * @param username 用户名
     * @param approved 是否通过认证
     * @param reason 原因（如果未通过）
     * @return 是否发送成功
     */
    boolean sendBloggerStatusEmail(String email, String username, boolean approved, String reason);
} 