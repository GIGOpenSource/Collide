package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 搜索自动完成请求
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
public class SearchAutocompleteRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索关键词前缀（必填）
     */
    @NotBlank(message = "搜索关键词前缀不能为空")
    @Size(min = 1, max = 50, message = "搜索关键词前缀长度必须在1-50个字符之间")
    private String prefix;

    /**
     * 用户ID（可选，用于个性化推荐）
     */
    private Long userId;

    /**
     * 返回数量限制（可选，默认为10）
     */
    @Min(value = 1, message = "返回数量限制必须大于等于1")
    private Integer limit = 10;

    /**
     * 内容类型过滤（可选）
     */
    private List<String> contentTypes;

    /**
     * 搜索类型过滤（可选）
     */
    private List<String> searchTypes;

    /**
     * 是否包含历史搜索（可选，默认为true）
     */
    private Boolean includeHistory = true;

    /**
     * 是否包含热门搜索（可选，默认为true）
     */
    private Boolean includeHot = true;

    /**
     * 是否包含智能建议（可选，默认为true）
     */
    private Boolean includeSmart = true;

    /**
     * 最小匹配长度（可选，默认为1）
     */
    @Min(value = 1, message = "最小匹配长度必须大于等于1")
    private Integer minMatchLength = 1;

    /**
     * 匹配模式（可选）：prefix/fuzzy/semantic
     */
    private String matchMode = "prefix";

    /**
     * 语言代码（可选，默认为zh-CN）
     */
    private String language = "zh-CN";

    /**
     * 是否实时记录（可选，默认为true）
     */
    private Boolean realTimeRecord = true;
} 