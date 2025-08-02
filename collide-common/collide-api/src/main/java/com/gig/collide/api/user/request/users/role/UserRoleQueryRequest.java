package com.gig.collide.api.user.request.users.role;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户角色查询请求
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色：user、blogger、admin、vip
     */
    private String role;

    /**
     * 是否包含过期角色
     */
    private Boolean includeExpired = false;

    /**
     * 过期时间开�?
     */
    private LocalDateTime expireTimeStart;

    /**
     * 过期时间结束
     */
    private LocalDateTime expireTimeEnd;

    /**
     * 排序字段：id、user_id、role、expire_time
     */
    private String sortField = "id";

    /**
     * 排序方向：asc、desc
     */
    private String sortDirection = "desc";

    /**
     * 当前页码
     */
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    private Integer pageSize = 20;
}
