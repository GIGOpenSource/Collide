package com.gig.collide.api.user.request.role;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户角色更新请求 - 对应 t_user_role �?
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色记录ID
     */
    @NotNull(message = "角色记录ID不能为空")
    private Integer id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色：user、blogger、admin、vip
     */
    private String role;

    /**
     * 角色过期时间
     */
    private LocalDateTime expireTime;
}
