package com.gig.collide.api.favorite.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 收藏查询请求 - 简洁版
 * 基于favorite-simple.sql的字段，支持常用查询条件
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
public class FavoriteQueryRequest implements Serializable {

    /**
     * 收藏类型：CONTENT、GOODS
     */
    private String favoriteType;

    /**
     * 收藏目标ID
     */
    private Long targetId;

    /**
     * 收藏用户ID
     */
    private Long userId;

    /**
     * 收藏对象标题关键词
     */
    private String targetTitle;

    /**
     * 收藏对象作者ID
     */
    private Long targetAuthorId;

    /**
     * 用户昵称关键词
     */
    private String userNickname;

    /**
     * 状态：active、cancelled
     */
    private String status;

    /**
     * 查询类型：user（用户收藏的）、target（被收藏的）、popular（热门）
     */
    private String queryType;

    // =================== 分页参数 ===================

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

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