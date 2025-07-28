package com.gig.collide.api.search.response;

import com.gig.collide.api.search.response.data.SearchStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 搜索统计查询响应
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
public class SearchStatisticsQueryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计数据列表
     */
    private List<SearchStatisticsInfo> statistics;

    /**
     * 总记录数
     */
    private Long totalCount;

    /**
     * 总搜索次数
     */
    private Long totalSearchCount;

    /**
     * 总用户数
     */
    private Long totalUserCount;

    /**
     * 分页信息
     */
    private PageInfo pageInfo;

    /**
     * 查询时间范围
     */
    private String timeRange;

    /**
     * 查询开始时间
     */
    private String startDate;

    /**
     * 查询结束时间
     */
    private String endDate;

    /**
     * 分页信息
     */
    @Getter
    @Setter
    @ToString
    public static class PageInfo {
        
        /**
         * 当前页码
         */
        private Integer pageNum;
        
        /**
         * 每页大小
         */
        private Integer pageSize;
        
        /**
         * 总页数
         */
        private Integer totalPages;
        
        /**
         * 是否有下一页
         */
        private Boolean hasNext;
        
        /**
         * 是否有上一页
         */
        private Boolean hasPrevious;
    }
} 