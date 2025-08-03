package com.gig.collide.api.tag.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 标签查询请求 - 简洁版
 * 基于tag-simple.sql的字段，支持多种查询条件
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TagQueryRequest implements Serializable {

    /**
     * 标签名称（模糊搜索）
     */
    private String name;

    /**
     * 标签类型：content、interest、system
     */
    private String tagType;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 状态：active、inactive
     */
    private String status;

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
     * 排序字段：create_time、update_time、usage_count、name
     */
    private String orderBy = "create_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "DESC";
} 