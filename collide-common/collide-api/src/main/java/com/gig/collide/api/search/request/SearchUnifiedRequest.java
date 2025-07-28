package com.gig.collide.api.search.request;

import com.gig.collide.api.search.constant.ContentTypeEnum;
import com.gig.collide.api.search.constant.SearchTypeEnum;
import com.gig.collide.api.search.constant.SortTypeEnum;
import com.gig.collide.api.search.request.condition.SearchQueryCondition;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 统一搜索请求
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
public class SearchUnifiedRequest extends BaseRequest {

    /**
     * 搜索关键词（必填）
     */
    @NotBlank(message = "搜索关键词不能为空")
    @Size(min = 1, max = 255, message = "搜索关键词长度必须在1-255个字符之间")
    private String keyword;

    /**
     * 搜索类型
     */
    private SearchTypeEnum searchType = SearchTypeEnum.ALL;

    /**
     * 内容类型过滤
     */
    private ContentTypeEnum contentType;

    /**
     * 排序类型
     */
    private SortTypeEnum sortType = SortTypeEnum.RELEVANCE;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 20;

    /**
     * 搜索用户ID（用于记录搜索历史）
     */
    private Long userId;

    /**
     * 是否完全匹配
     */
    private Boolean exactMatch = false;

    /**
     * 是否忽略大小写
     */
    private Boolean ignoreCase = true;

    /**
     * 是否高亮显示
     */
    private Boolean highlight = true;

    /**
     * 高亮标签
     */
    private String highlightTag = "em";

    /**
     * 搜索字段范围
     */
    private List<String> searchFields;

    /**
     * 过滤条件
     */
    private Map<String, Object> filters;

    /**
     * 高级查询条件
     */
    @Valid
    private List<SearchQueryCondition> queryConditions;

    /**
     * 是否记录搜索历史
     */
    private Boolean recordHistory = true;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 应用版本
     */
    private String appVersion;

    // ===================== 便捷构造器 =====================

    /**
     * 根据关键词创建搜索请求
     */
    public SearchUnifiedRequest(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 根据关键词和搜索类型创建搜索请求
     */
    public SearchUnifiedRequest(String keyword, SearchTypeEnum searchType) {
        this.keyword = keyword;
        this.searchType = searchType;
    }

    /**
     * 根据关键词、搜索类型和用户ID创建搜索请求
     */
    public SearchUnifiedRequest(String keyword, SearchTypeEnum searchType, Long userId) {
        this.keyword = keyword;
        this.searchType = searchType;
        this.userId = userId;
    }

    // ===================== 便捷方法 =====================

    /**
     * 设置分页信息
     */
    public SearchUnifiedRequest withPagination(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 设置排序类型
     */
    public SearchUnifiedRequest withSort(SortTypeEnum sortType) {
        this.sortType = sortType;
        return this;
    }

    /**
     * 设置内容类型过滤
     */
    public SearchUnifiedRequest withContentType(ContentTypeEnum contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * 设置用户信息
     */
    public SearchUnifiedRequest withUser(Long userId, String deviceInfo, String ipAddress) {
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        return this;
    }

    /**
     * 检查是否为分页搜索
     */
    public boolean isPaginated() {
        return pageNum != null && pageSize != null && pageNum > 0 && pageSize > 0;
    }

    /**
     * 检查是否需要高亮
     */
    public boolean needHighlight() {
        return highlight != null && highlight;
    }

    /**
     * 获取搜索偏移量
     */
    public Integer getOffset() {
        if (!isPaginated()) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
} 