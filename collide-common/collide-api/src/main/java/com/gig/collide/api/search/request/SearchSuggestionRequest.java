package com.gig.collide.api.search.request;

import com.gig.collide.api.search.constant.SuggestionTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

/**
 * 搜索建议请求
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
public class SearchSuggestionRequest extends BaseRequest {

    /**
     * 搜索关键词（必填）
     */
    @NotBlank(message = "搜索关键词不能为空")
    @Size(min = 1, max = 255, message = "搜索关键词长度必须在1-255个字符之间")
    private String keyword;

    /**
     * 建议类型
     */
    private SuggestionTypeEnum suggestionType = SuggestionTypeEnum.KEYWORD;

    /**
     * 用户ID（用于个性化建议）
     */
    private Long userId;

    /**
     * 返回数量限制
     */
    @Min(value = 1, message = "返回数量必须大于0")
    @Max(value = 50, message = "返回数量不能超过50")
    private Integer limit = 10;

    /**
     * 是否高亮显示
     */
    private Boolean highlight = true;

    /**
     * 高亮标签
     */
    private String highlightTag = "em";

    /**
     * 是否包含历史搜索
     */
    private Boolean includeHistory = true;

    /**
     * 是否包含热门搜索
     */
    private Boolean includeHot = true;

    /**
     * 是否个性化推荐
     */
    private Boolean personalized = false;

    /**
     * 最小匹配长度
     */
    private Integer minMatchLength = 1;

    /**
     * 是否完全匹配
     */
    private Boolean exactMatch = false;

    /**
     * 权重阈值
     */
    private Double minWeight = 0.0;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * IP地址
     */
    private String ipAddress;

    // ===================== 便捷构造器 =====================

    /**
     * 根据关键词创建建议请求
     */
    public SearchSuggestionRequest(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 根据关键词和建议类型创建建议请求
     */
    public SearchSuggestionRequest(String keyword, SuggestionTypeEnum suggestionType) {
        this.keyword = keyword;
        this.suggestionType = suggestionType;
    }

    /**
     * 根据关键词、建议类型和用户ID创建建议请求
     */
    public SearchSuggestionRequest(String keyword, SuggestionTypeEnum suggestionType, Long userId) {
        this.keyword = keyword;
        this.suggestionType = suggestionType;
        this.userId = userId;
    }

    // ===================== 便捷方法 =====================

    /**
     * 设置用户信息
     */
    public SearchSuggestionRequest withUser(Long userId, String deviceInfo, String ipAddress) {
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        return this;
    }

    /**
     * 设置高亮配置
     */
    public SearchSuggestionRequest withHighlight(Boolean highlight, String highlightTag) {
        this.highlight = highlight;
        this.highlightTag = highlightTag;
        return this;
    }

    /**
     * 设置个性化配置
     */
    public SearchSuggestionRequest withPersonalization(Boolean personalized, Boolean includeHistory, Boolean includeHot) {
        this.personalized = personalized;
        this.includeHistory = includeHistory;
        this.includeHot = includeHot;
        return this;
    }

    /**
     * 检查是否需要高亮
     */
    public boolean needHighlight() {
        return highlight != null && highlight;
    }

    /**
     * 检查是否需要个性化
     */
    public boolean needPersonalization() {
        return personalized != null && personalized && userId != null;
    }
} 