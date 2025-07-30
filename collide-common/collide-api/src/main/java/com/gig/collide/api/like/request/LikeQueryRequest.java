package com.gig.collide.api.like.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点赞查询请求 - 简洁版
 * 基于like-simple.sql的字段，支持常用查询条件
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
public class LikeQueryRequest implements Serializable {

    /**
     * 用户ID - 查询某用户的点赞记录
     */
    private Long userId;

    /**
     * 点赞类型：CONTENT、COMMENT、DYNAMIC
     */
    private String likeType;

    /**
     * 目标对象ID - 查询某对象的点赞记录
     */
    private Long targetId;

    /**
     * 目标对象作者ID - 查询某作者的作品被点赞记录
     */
    private Long targetAuthorId;

    /**
     * 状态：active、cancelled
     */
    private String status;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;

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