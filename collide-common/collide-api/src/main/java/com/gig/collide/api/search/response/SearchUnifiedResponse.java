package com.gig.collide.api.search.response;

import com.gig.collide.api.search.response.data.SearchResultInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 统一搜索响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchUnifiedResponse extends BaseResponse {

    /**
     * 搜索结果列表
     */
    private List<SearchResultInfo> results;

    /**
     * 总结果数量
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
     * 搜索耗时（毫秒）
     */
    private Long searchTime;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 搜索建议
     */
    private List<String> suggestions;

    /**
     * 相关搜索
     */
    private List<String> relatedSearches;

    /**
     * 搜索结果分组统计
     */
    private Map<String, Long> typeStatistics;

    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;

    // ===================== 便捷方法 =====================

    /**
     * 检查是否有搜索结果
     */
    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }

    /**
     * 获取结果数量
     */
    public int getResultCount() {
        return results != null ? results.size() : 0;
    }

    /**
     * 获取格式化的搜索耗时
     */
    public String getFormattedSearchTime() {
        if (searchTime == null) {
            return "未知";
        }
        if (searchTime < 1000) {
            return searchTime + "ms";
        } else {
            return String.format("%.2fs", searchTime / 1000.0);
        }
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