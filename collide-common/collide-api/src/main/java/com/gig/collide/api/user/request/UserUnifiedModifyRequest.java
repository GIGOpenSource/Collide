package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * 用户统一信息修改请求
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
public class UserUnifiedModifyRequest extends BaseRequest {

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 昵称（可选）
     */
    @Size(min = 1, max = 50, message = "昵称长度必须在1-50个字符之间")
    private String nickname;

    /**
     * 头像URL（可选）
     */
    private String avatar;

    /**
     * 邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号（可选）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 旧密码（修改密码时必填）
     */
    private String oldPassword;

    /**
     * 新密码（可选）
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;

    /**
     * 个人简介（可选）
     */
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;

    /**
     * 生日（可选）
     */
    private LocalDate birthday;

    /**
     * 性别（可选）
     */
    private String gender;

    /**
     * 所在地（可选）
     */
    @Size(max = 100, message = "所在地长度不能超过100个字符")
    private String location;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;
} 