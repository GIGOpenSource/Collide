package com.gig.collide.api.follow.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 关注查询请求 - 简洁版
 * 基于follow-simple.sql的字段，支持常用查询条件
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FollowQueryRequest implements Serializable {

    /**
     * 关注者用户ID
     */
    private Long followerId;

    /**
     * 被关注者用户ID
     */
    private Long followeeId;

    /**
     * 关注者昵称关键词
     */
    private String followerNickname;

    /**
     * 被关注者昵称关键词
     */
    private String followeeNickname;

    /**
     * 状态：active、cancelled
     */
    private String status;

    /**
     * 查询类型：following（我关注的）、followers（我的粉丝）、mutual（互关）
     */
    private String queryType;

    // =================== 分页参数 ===================

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    @Min(value = 1, message = "页面大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 排序字段：create_time、update_time
     */
    private String orderBy = "create_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "DESC";
} 