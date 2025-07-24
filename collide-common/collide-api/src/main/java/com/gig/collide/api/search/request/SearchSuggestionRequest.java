package com.gig.collide.api.search.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// import javax.validation.constraints.Max;
// import javax.validation.constraints.Min;
// import javax.validation.constraints.NotBlank;
// import javax.validation.constraints.Size;

/**
 * 搜索建议请求DTO
 *
 * @author GIG Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索建议请求")
public class SearchSuggestionRequest {

    @Schema(description = "搜索关键词", example = "Java", required = true)
    private String keyword;

    @Schema(description = "建议类型：KEYWORD-关键词建议, USER-用户建议, TAG-标签建议", 
            example = "KEYWORD", 
            allowableValues = {"KEYWORD", "USER", "TAG"})
    private String suggestionType = "KEYWORD";

    @Schema(description = "建议数量", example = "10")
    private Integer limit = 10;
} 