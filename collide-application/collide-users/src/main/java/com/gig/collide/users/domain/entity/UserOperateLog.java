package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户操作日志实体类
 * 对应数据库表：t_user_operate_log
 * 记录用户的各种操作行为，用于审计和分析
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Data
@Accessors(chain = true)
@TableName("t_user_operate_log")
public class UserOperateLog {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 操作类型
     * register-注册, login-登录, logout-登出, update_profile-更新个人信息, 
     * apply_blogger-申请博主, approve_blogger-批准博主, reject_blogger-拒绝博主,
     * upgrade_vip-升级VIP, invite_user-邀请用户
     */
    @TableField("operate_type")
    private String operateType;

    /**
     * 操作描述
     */
    @TableField("operate_desc")
    private String operateDesc;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户代理（浏览器信息）
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 操作时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ================================ 业务方法 ================================

    /**
     * 创建用户操作日志
     *
     * @param userId 用户ID
     * @param operateType 操作类型
     * @param operateDesc 操作描述
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 操作日志实体
     */
    public static UserOperateLog create(Long userId, String operateType, String operateDesc, 
                                       String ipAddress, String userAgent) {
        return new UserOperateLog()
                .setUserId(userId)
                .setOperateType(operateType)
                .setOperateDesc(operateDesc)
                .setIpAddress(ipAddress)
                .setUserAgent(userAgent);
    }

    /**
     * 创建注册日志
     */
    public static UserOperateLog createRegisterLog(Long userId, String ipAddress, String userAgent) {
        return create(userId, "register", "用户注册", ipAddress, userAgent);
    }

    /**
     * 创建登录日志
     */
    public static UserOperateLog createLoginLog(Long userId, String ipAddress, String userAgent) {
        return create(userId, "login", "用户登录", ipAddress, userAgent);
    }

    /**
     * 创建博主申请日志
     */
    public static UserOperateLog createBloggerApplyLog(Long userId, String ipAddress, String userAgent) {
        return create(userId, "apply_blogger", "申请博主认证", ipAddress, userAgent);
    }
} 