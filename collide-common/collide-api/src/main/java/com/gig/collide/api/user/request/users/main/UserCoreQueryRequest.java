package com.gig.collide.api.user.request.users.main;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户核心信息查询请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoreQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 邮箱（模糊查询）
     */
    private String email;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 用户状态：1-active, 2-inactive, 3-suspended, 4-banned
     */
    private Integer status;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 20;


}