package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 搜索统计查询请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchStatisticsQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词（可选）
     */
    @Size(max = 100, message = "搜索关键词长度不能超过100个字符")
    private String keyword;

    /**
     * 用户ID（可选）
     */
    private Long userId;

    /**
     * 内容类型（可选）
     */
    private String contentType;

    /**
     * 搜索类型（可选）
     */
    private String searchType;

    /**
     * 开始日期（格式：YYYY-MM-DD）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "开始日期格式必须为YYYY-MM-DD")
    private String startDate;

    /**
     * 结束日期（格式：YYYY-MM-DD）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "结束日期格式必须为YYYY-MM-DD")
    private String endDate;

    /**
     * 时间范围类型（today/week/month/year）
     */
    @Pattern(regexp = "^(today|week|month|year)$", message = "时间范围类型只能是today、week、month或year")
    private String timeRange;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于等于1")
    private Integer pageSize = 20;

    /**
     * 排序字段（search_count/user_count/hot_score/last_search_time）
     */
    private String sortField = "search_count";

    /**
     * 排序方向（asc/desc）
     */
    @Pattern(regexp = "^(asc|desc)$", message = "排序方向只能是asc或desc")
    private String sortOrder = "desc";

    /**
     * 最小搜索次数筛选
     */
    @Min(value = 0, message = "最小搜索次数不能小于0")
    private Integer minSearchCount;

    /**
     * 最小用户数筛选
     */
    @Min(value = 0, message = "最小用户数不能小于0")
    private Integer minUserCount;

    /**
     * 是否只显示热门关键词
     */
    private Boolean hotOnly = false;
} 