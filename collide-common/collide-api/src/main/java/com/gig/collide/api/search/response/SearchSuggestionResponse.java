package com.gig.collide.api.search.response;

import com.gig.collide.api.search.response.data.SuggestionItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 搜索建议响应DTO
 *
 * @author GIG Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索建议响应")
public class SearchSuggestionResponse {

    @Schema(description = "输入关键词")
    private String keyword;

    @Schema(description = "建议类型")
    private String suggestionType;

    @Schema(description = "建议列表")
    private List<SuggestionItem> suggestions;

    @Schema(description = "热门搜索关键词")
    private List<String> hotKeywords;

    /**
     * 创建失败响应
     *
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     * @return 失败响应
     */
    public static SearchSuggestionResponse fail(String errorCode, String errorMessage) {
        SearchSuggestionResponse response = new SearchSuggestionResponse();
        response.setKeyword("");
        response.setSuggestionType("");
        response.setSuggestions(java.util.Collections.emptyList());
        response.setHotKeywords(java.util.Collections.emptyList());
        return response;
    }
} 