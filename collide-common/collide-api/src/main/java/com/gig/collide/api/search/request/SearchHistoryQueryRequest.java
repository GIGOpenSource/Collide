package com.gig.collide.api.search.request;

import com.gig.collide.api.search.constant.SearchTypeEnum;
import com.gig.collide.api.search.constant.ContentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 搜索历史查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchHistoryQueryRequest extends BaseRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索类型
     */
    private SearchTypeEnum searchType;

    /**
     * 内容类型
     */
    private ContentTypeEnum contentType;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    private String orderBy = "create_time";

    /**
     * 是否降序
     */
    private Boolean descending = true;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 最小结果数量
     */
    private Long minResultCount;

    /**
     * 最大结果数量
     */
    private Long maxResultCount;

    /**
     * 最小搜索耗时
     */
    private Long minSearchTime;

    /**
     * 最大搜索耗时
     */
    private Long maxSearchTime;

    // ===================== 便捷构造器 =====================

    /**
     * 根据用户ID查询
     */
    public SearchHistoryQueryRequest(Long userId) {
        this.userId = userId;
    }

    /**
     * 根据用户ID和关键词查询
     */
    public SearchHistoryQueryRequest(Long userId, String keyword) {
        this.userId = userId;
        this.keyword = keyword;
    }

    // ===================== 便捷方法 =====================

    /**
     * 设置时间范围
     */
    public SearchHistoryQueryRequest withTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        return this;
    }

    /**
     * 设置分页信息
     */
    public SearchHistoryQueryRequest withPagination(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 设置排序
     */
    public SearchHistoryQueryRequest withSort(String orderBy, Boolean descending) {
        this.orderBy = orderBy;
        this.descending = descending;
        return this;
    }

    /**
     * 获取搜索偏移量
     */
    public Integer getOffset() {
        if (pageNum == null || pageSize == null || pageNum <= 0 || pageSize <= 0) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
} 