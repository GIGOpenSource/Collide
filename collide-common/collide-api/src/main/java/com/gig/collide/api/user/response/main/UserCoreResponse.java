package com.gig.collide.api.user.response.main;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户核心信息响应 - 对应 t_user 表
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoreResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户状态：1-active, 2-inactive, 3-suspended, 4-banned
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 设置状态并自动计算描述
     */
    public void setStatus(Integer status) {
        this.status = status;
        // 设置状态描述
        switch (status) {
            case 1:
                this.statusDesc = "正常";
                break;
            case 2:
                this.statusDesc = "未激活";
                break;
            case 3:
                this.statusDesc = "已暂停";
                break;
            case 4:
                this.statusDesc = "已封禁";
                break;
            default:
                this.statusDesc = "未知";
        }
    }
}