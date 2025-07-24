package com.gig.collide.api.search.response;

import com.gig.collide.api.search.response.data.SearchResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 搜索响应DTO
 *
 * @author GIG Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索响应")
public class SearchResponse {

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "搜索类型")
    private String searchType;

    @Schema(description = "总结果数")
    private Long totalCount;

    @Schema(description = "搜索耗时（毫秒）")
    private Long searchTime;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页大小")
    private Integer pageSize;

    @Schema(description = "总页数")
    private Integer totalPages;

    @Schema(description = "是否有下一页")
    private Boolean hasNext;

    @Schema(description = "搜索结果列表")
    private List<SearchResult> results;

    @Schema(description = "搜索统计信息")
    private Map<String, Long> statistics;

    @Schema(description = "搜索建议词")
    private List<String> suggestions;

    @Schema(description = "相关搜索")
    private List<String> relatedSearches;
} 