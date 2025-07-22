package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户分页查询请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserPageQueryRequest extends BaseRequest {

    /**
     * 页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 用户名关键词搜索
     */
    private String usernameKeyword;

    /**
     * 用户状态过滤
     */
    private String status;

    /**
     * 用户角色过滤
     */
    private String role;
}
