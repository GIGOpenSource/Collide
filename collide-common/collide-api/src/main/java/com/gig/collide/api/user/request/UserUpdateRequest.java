package com.gig.collide.api.user.request;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 用户更新请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class UserUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @Size(max = 100, message = "昵称长度不能超过100字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String avatar;

    @Size(max = 500, message = "个人简介长度不能超过500字符")
    private String bio;

    private LocalDate birthday;

    @Pattern(regexp = "^(male|female|unknown)$", message = "性别只能是male、female或unknown")
    private String gender;

    @Size(max = 100, message = "所在地长度不能超过100字符")
    private String location;
} 