package com.gig.collide.api.search.response.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索建议项数据模型
 *
 * @author GIG Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索建议项")
public class SuggestionItem {

    @Schema(description = "建议文本")
    private String text;

    @Schema(description = "建议类型：KEYWORD-关键词, USER-用户, TAG-标签")
    private String type;

    @Schema(description = "搜索次数/热度")
    private Long searchCount;

    @Schema(description = "相关度得分")
    private Double relevanceScore;

    @Schema(description = "高亮显示的文本（HTML格式）")
    private String highlightText;

    @Schema(description = "扩展信息，如用户头像、标签描述等")
    private Object extraInfo;
} 