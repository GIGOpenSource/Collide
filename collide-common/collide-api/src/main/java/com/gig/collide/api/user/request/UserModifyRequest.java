package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 用户信息修改请求
 * 与users模块的UserUpdateRequest保持一致
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyRequest extends BaseRequest {
    
    @NotNull(message = "userId不能为空")
    private Long userId;
    
    /**
     * 昵称
     */
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20字符之间")
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 个人简介
     */
    @Size(max = 200, message = "个人简介不能超过200字符")
    private String bio;
    
    /**
     * 性别 (M:男, F:女, O:其他)
     */
    @Pattern(regexp = "^[MFO]$", message = "性别参数不正确")
    private String gender;
    
    /**
     * 生日
     */
    private LocalDate birthday;
    
    /**
     * 所在地
     */
    @Size(max = 100, message = "所在地不能超过100字符")
    private String location;
}
