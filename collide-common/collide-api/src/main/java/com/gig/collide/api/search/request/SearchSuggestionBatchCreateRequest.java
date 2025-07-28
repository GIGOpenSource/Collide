package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 搜索建议批量创建请求
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
public class SearchSuggestionBatchCreateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 建议列表（必填）
     */
    @NotEmpty(message = "建议列表不能为空")
    @Valid
    private List<SuggestionItem> suggestions;

    /**
     * 创建人ID（必填）
     */
    @NotNull(message = "创建人ID不能为空")
    private Long creatorId;

    /**
     * 批量创建备注（可选）
     */
    @Size(max = 200, message = "批量创建备注长度不能超过200个字符")
    private String remark;

    /**
     * 是否跳过重复项（可选，默认为true）
     */
    private Boolean skipDuplicates = true;

    /**
     * 是否校验权限（可选，默认为true）
     */
    private Boolean validatePermission = true;

    /**
     * 建议项
     */
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SuggestionItem {

        /**
         * 建议关键词（必填）
         */
        @NotNull(message = "建议关键词不能为空")
        @Size(max = 100, message = "建议关键词长度不能超过100个字符")
        private String keyword;

        /**
         * 建议类型（必填）
         */
        @NotNull(message = "建议类型不能为空")
        private String suggestionType;

        /**
         * 内容类型（可选）
         */
        private String contentType;

        /**
         * 搜索类型（可选）
         */
        private String searchType;

        /**
         * 权重（可选，默认为100）
         */
        private Integer weight = 100;

        /**
         * 显示文本（可选）
         */
        @Size(max = 200, message = "显示文本长度不能超过200个字符")
        private String displayText;

        /**
         * 描述信息（可选）
         */
        @Size(max = 500, message = "描述信息长度不能超过500个字符")
        private String description;

        /**
         * 图标URL（可选）
         */
        @Size(max = 200, message = "图标URL长度不能超过200个字符")
        private String iconUrl;

        /**
         * 跳转链接（可选）
         */
        @Size(max = 500, message = "跳转链接长度不能超过500个字符")
        private String redirectUrl;

        /**
         * 标签列表（可选）
         */
        private List<String> tags;

        /**
         * 是否启用（可选，默认为true）
         */
        private Boolean enabled = true;
    }
} 