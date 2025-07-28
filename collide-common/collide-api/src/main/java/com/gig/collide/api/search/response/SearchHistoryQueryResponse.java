package com.gig.collide.api.search.response;

import com.gig.collide.api.search.response.data.SearchHistoryInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 搜索历史查询响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchHistoryQueryResponse extends BaseResponse {

    /**
     * 搜索历史列表
     */
    private List<SearchHistoryInfo> histories;

    /**
     * 总数量
     */
    private Long totalCount;

    /**
     * 当前页码
     */
    private Integer currentPage;

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

    /**
     * 热门搜索关键词
     */
    private List<String> hotKeywords;

    /**
     * 搜索类型统计
     */
    private Map<String, Long> typeStatistics;

    /**
     * 时间范围统计
     */
    private Map<String, Long> timeStatistics;

    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;

    // ===================== 便捷方法 =====================

    /**
     * 检查是否有历史记录
     */
    public boolean hasHistories() {
        return histories != null && !histories.isEmpty();
    }

    /**
     * 获取历史记录数量
     */
    public int getHistoryCount() {
        return histories != null ? histories.size() : 0;
    }

    /**
     * 计算总页数
     */
    public void calculateTotalPages() {
        if (totalCount != null && pageSize != null && pageSize > 0) {
            this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
        }
    }

    /**
     * 设置分页状态
     */
    public void setPaginationStatus() {
        if (currentPage != null && totalPages != null) {
            this.hasPrevious = currentPage > 1;
            this.hasNext = currentPage < totalPages;
        }
    }
} 