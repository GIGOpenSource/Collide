package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 用户操作日志创建请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserOperateLogCreateRequest extends BaseRequest {

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 操作类型（必填）
     */
    @NotBlank(message = "操作类型不能为空")
    private String operateType;

    /**
     * 操作描述（必填）
     */
    @NotBlank(message = "操作描述不能为空")
    @Size(max = 500, message = "操作描述长度不能超过500个字符")
    private String operateDesc;

    /**
     * IP地址（可选）
     */
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    private String ipAddress;

    /**
     * 用户代理（可选）
     */
    @Size(max = 1000, message = "用户代理长度不能超过1000个字符")
    private String userAgent;

    // ===================== 便捷构造器 =====================

    /**
     * 创建注册日志
     */
    public static UserOperateLogCreateRequest createRegisterLog(Long userId, String ipAddress, String userAgent) {
        return new UserOperateLogCreateRequest(userId, "register", "用户注册", ipAddress, userAgent);
    }

    /**
     * 创建登录日志
     */
    public static UserOperateLogCreateRequest createLoginLog(Long userId, String ipAddress, String userAgent) {
        return new UserOperateLogCreateRequest(userId, "login", "用户登录", ipAddress, userAgent);
    }

    /**
     * 创建博主申请日志
     */
    public static UserOperateLogCreateRequest createBloggerApplyLog(Long userId, String ipAddress, String userAgent) {
        return new UserOperateLogCreateRequest(userId, "apply_blogger", "申请博主认证", ipAddress, userAgent);
    }

    /**
     * 创建通用操作日志
     */
    public static UserOperateLogCreateRequest create(Long userId, String operateType, String operateDesc, String ipAddress, String userAgent) {
        return new UserOperateLogCreateRequest(userId, operateType, operateDesc, ipAddress, userAgent);
    }
} 