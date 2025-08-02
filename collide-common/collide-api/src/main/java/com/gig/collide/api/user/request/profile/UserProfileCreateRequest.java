package com.gig.collide.api.user.request.profile;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户资料创建请求 - 对应 t_user_profile 表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 100, message = "昵称长度必须在1-100个字符之间")
    private String nickname;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatar;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 性别：0-unknown, 1-male, 2-female
     */
    private Integer gender = 0;

    /**
     * 所在地
     */
    @Size(max = 100, message = "所在地长度不能超过100个字符")
    private String location;


}