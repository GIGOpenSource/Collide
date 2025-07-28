package com.gig.collide.api.search.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 搜索历史查询请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchHistoryQueryRequest extends PageRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索类型：content、goods、user
     */
    private String searchType;

    /**
     * 关键词搜索（模糊搜索）
     */
    private String keyword;

    /**
     * 排序方式：create_time、result_count
     */
    private String sortBy = "create_time";

    /**
     * 排序方向：asc、desc
     */
    private String sortDirection = "desc";
} 