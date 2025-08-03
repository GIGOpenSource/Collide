package com.gig.collide.api.user.request.main;

import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户核心信息更新请求 - 对应 t_user 表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoreUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 用户状态：1-active, 2-inactive, 3-suspended, 4-banned
     */
    private Integer status;


}