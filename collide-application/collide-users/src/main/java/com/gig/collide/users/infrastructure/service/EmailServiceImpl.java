package com.gig.collide.users.infrastructure.service;

import com.gig.collide.users.domain.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现类
 * 实现邮件发送功能，支持激活邮件、密码重置、欢迎邮件等
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${collide.email.from:noreply@collide.com}")
    private String fromEmail;

    @Value("${collide.email.platform-name:Collide社交平台}")
    private String platformName;

    @Value("${collide.email.website-url:https://www.collide.com}")
    private String websiteUrl;

    @Override
    public boolean sendActivationEmail(String email, String activationCode) {
        try {
            log.info("发送激活邮件到：{}，激活码：{}", email, activationCode);
            
            String subject = String.format("【%s】账号激活验证", platformName);
            String content = buildActivationEmailContent(activationCode);
            
            // TODO: 这里应该集成真实的邮件服务提供商（如：腾讯云邮件、阿里云邮件、SendGrid等）
            // 目前使用模拟实现
            return sendEmailInternal(email, subject, content);
            
        } catch (Exception e) {
            log.error("发送激活邮件失败，邮箱：{}，激活码：{}", email, activationCode, e);
            return false;
        }
    }

    @Override
    public boolean sendPasswordResetEmail(String email, String resetCode) {
        try {
            log.info("发送密码重置邮件到：{}，重置码：{}", email, resetCode);
            
            String subject = String.format("【%s】密码重置验证", platformName);
            String content = buildPasswordResetEmailContent(resetCode);
            
            return sendEmailInternal(email, subject, content);
            
        } catch (Exception e) {
            log.error("发送密码重置邮件失败，邮箱：{}，重置码：{}", email, resetCode, e);
            return false;
        }
    }

    @Override
    public boolean sendWelcomeEmail(String email, String username) {
        try {
            log.info("发送欢迎邮件到：{}，用户名：{}", email, username);
            
            String subject = String.format("欢迎加入%s！", platformName);
            String content = buildWelcomeEmailContent(username);
            
            return sendEmailInternal(email, subject, content);
            
        } catch (Exception e) {
            log.error("发送欢迎邮件失败，邮箱：{}，用户名：{}", email, username, e);
            return false;
        }
    }

    @Override
    public boolean sendBloggerStatusEmail(String email, String username, boolean approved, String reason) {
        try {
            log.info("发送博主认证结果邮件到：{}，用户名：{}，通过：{}，原因：{}", 
                email, username, approved, reason);
            
            String subject = String.format("【%s】博主认证结果通知", platformName);
            String content = buildBloggerStatusEmailContent(username, approved, reason);
            
            return sendEmailInternal(email, subject, content);
            
        } catch (Exception e) {
            log.error("发送博主认证结果邮件失败，邮箱：{}，用户名：{}", email, username, e);
            return false;
        }
    }

    /**
     * 内部邮件发送方法
     * 这里应该集成真实的邮件服务提供商
     */
    private boolean sendEmailInternal(String toEmail, String subject, String content) {
        // TODO: 集成真实的邮件服务提供商
        // 可以选择：Spring Boot Mail、腾讯云邮件、阿里云邮件、SendGrid、Mailgun等
        
        log.info("模拟发送邮件");
        log.info("收件人：{}", toEmail);
        log.info("主题：{}", subject);
        log.info("内容：\n{}", content);
        
        // 模拟发送成功
        return true;
    }

    /**
     * 构建激活邮件内容
     */
    private String buildActivationEmailContent(String activationCode) {
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>账号激活</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
            "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
            "        <h2 style=\"color: #4A90E2;\">欢迎注册%s！</h2>\n" +
            "        <p>您好！</p>\n" +
            "        <p>感谢您注册%s账号。为了确保账号安全，请使用以下激活码完成账号激活：</p>\n" +
            "        <div style=\"background-color: #f4f4f4; padding: 15px; margin: 20px 0; text-align: center;\">\n" +
            "            <span style=\"font-size: 24px; font-weight: bold; color: #4A90E2;\">%s</span>\n" +
            "        </div>\n" +
            "        <p><strong>注意事项：</strong></p>\n" +
            "        <ul>\n" +
            "            <li>激活码有效期为10分钟</li>\n" +
            "            <li>激活码仅可使用一次</li>\n" +
            "            <li>如果您没有注册此账号，请忽略此邮件</li>\n" +
            "        </ul>\n" +
            "        <p>如有任何问题，请联系我们的客服团队。</p>\n" +
            "        <hr style=\"margin: 30px 0;\">\n" +
            "        <p style=\"color: #666; font-size: 12px;\">\n" +
            "            此邮件由系统自动发送，请勿回复。<br>\n" +
            "            %s团队<br>\n" +
            "            <a href=\"%s\">%s</a>\n" +
            "        </p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            platformName, platformName, activationCode, platformName, websiteUrl, websiteUrl
        );
    }

    /**
     * 构建密码重置邮件内容
     */
    private String buildPasswordResetEmailContent(String resetCode) {
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>密码重置</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
            "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
            "        <h2 style=\"color: #E74C3C;\">密码重置验证</h2>\n" +
            "        <p>您好！</p>\n" +
            "        <p>我们收到了您的密码重置请求。请使用以下验证码完成密码重置：</p>\n" +
            "        <div style=\"background-color: #f4f4f4; padding: 15px; margin: 20px 0; text-align: center;\">\n" +
            "            <span style=\"font-size: 24px; font-weight: bold; color: #E74C3C;\">%s</span>\n" +
            "        </div>\n" +
            "        <p><strong>安全提示：</strong></p>\n" +
            "        <ul>\n" +
            "            <li>验证码有效期为15分钟</li>\n" +
            "            <li>验证码仅可使用一次</li>\n" +
            "            <li>如果您没有请求密码重置，请立即联系客服</li>\n" +
            "        </ul>\n" +
            "        <hr style=\"margin: 30px 0;\">\n" +
            "        <p style=\"color: #666; font-size: 12px;\">\n" +
            "            此邮件由系统自动发送，请勿回复。<br>\n" +
            "            %s团队<br>\n" +
            "            <a href=\"%s\">%s</a>\n" +
            "        </p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            resetCode, platformName, websiteUrl, websiteUrl
        );
    }

    /**
     * 构建欢迎邮件内容
     */
    private String buildWelcomeEmailContent(String username) {
        return String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>欢迎加入</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
            "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
            "        <h2 style=\"color: #27AE60;\">欢迎加入%s大家庭！</h2>\n" +
            "        <p>亲爱的 <strong>%s</strong>，</p>\n" +
            "        <p>欢迎加入%s！我们很高兴您成为我们社区的一员。</p>\n" +
            "        <p><strong>您可以开始：</strong></p>\n" +
            "        <ul>\n" +
            "            <li>完善个人资料，让更多人了解您</li>\n" +
            "            <li>关注感兴趣的用户和话题</li>\n" +
            "            <li>发布您的第一条动态</li>\n" +
            "            <li>参与社区讨论</li>\n" +
            "        </ul>\n" +
            "        <p>如果您有任何问题或建议，随时可以联系我们。</p>\n" +
            "        <div style=\"text-align: center; margin: 30px 0;\">\n" +
            "            <a href=\"%s\" style=\"background-color: #4A90E2; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px;\">立即开始探索</a>\n" +
            "        </div>\n" +
            "        <hr style=\"margin: 30px 0;\">\n" +
            "        <p style=\"color: #666; font-size: 12px;\">\n" +
            "            %s团队<br>\n" +
            "            <a href=\"%s\">%s</a>\n" +
            "        </p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            platformName, username, platformName, websiteUrl, platformName, websiteUrl, websiteUrl
        );
    }

    /**
     * 构建博主认证结果邮件内容
     */
    private String buildBloggerStatusEmailContent(String username, boolean approved, String reason) {
        String statusColor = approved ? "#27AE60" : "#E74C3C";
        String statusText = approved ? "通过" : "未通过";
        String statusMessage = approved ? 
            "恭喜您！您的博主认证申请已通过审核。现在您可以享受博主的各项特权了。" :
            "很抱歉，您的博主认证申请未能通过审核。";
        
        StringBuilder content = new StringBuilder();
        content.append(String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>博主认证结果</title>\n" +
            "</head>\n" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">\n" +
            "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">\n" +
            "        <h2 style=\"color: %s;\">博主认证结果通知</h2>\n" +
            "        <p>亲爱的 <strong>%s</strong>，</p>\n" +
            "        <p>您的博主认证申请审核结果如下：</p>\n" +
            "        <div style=\"background-color: #f4f4f4; padding: 15px; margin: 20px 0; border-left: 4px solid %s;\">\n" +
            "            <strong style=\"color: %s;\">审核结果：%s</strong>\n" +
            "        </div>\n" +
            "        <p>%s</p>",
            statusColor, username, statusColor, statusColor, statusText, statusMessage
        ));

        if (!approved && reason != null && !reason.trim().isEmpty()) {
            content.append(String.format(
                "        <p><strong>未通过原因：</strong></p>\n" +
                "        <div style=\"background-color: #fff3cd; padding: 10px; border-radius: 5px;\">\n" +
                "            %s\n" +
                "        </div>\n" +
                "        <p>您可以根据以上反馈完善申请材料后重新申请。</p>",
                reason
            ));
        }

        content.append(String.format(
            "        <hr style=\"margin: 30px 0;\">\n" +
            "        <p style=\"color: #666; font-size: 12px;\">\n" +
            "            如有疑问，请联系客服团队。<br>\n" +
            "            %s团队<br>\n" +
            "            <a href=\"%s\">%s</a>\n" +
            "        </p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            platformName, websiteUrl, websiteUrl
        ));

        return content.toString();
    }
} 