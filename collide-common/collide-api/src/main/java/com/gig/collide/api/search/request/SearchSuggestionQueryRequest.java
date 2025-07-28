package com.gig.collide.api.search.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 搜索建议查询请求
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
public class SearchSuggestionQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 建议关键词（可选）
     */
    @Size(max = 100, message = "建议关键词长度不能超过100个字符")
    private String keyword;

    /**
     * 建议类型（可选）
     */
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
     * 启用状态（可选）
     */
    private Boolean enabled;

    /**
     * 最小权重（可选）
     */
    @Min(value = 0, message = "最小权重不能小于0")
    private Integer minWeight;

    /**
     * 最大权重（可选）
     */
    @Min(value = 0, message = "最大权重不能小于0")
    private Integer maxWeight;

    /**
     * 创建人ID（可选）
     */
    private Long creatorId;

    /**
     * 创建开始时间（可选，格式：YYYY-MM-DD）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "创建开始时间格式必须为YYYY-MM-DD")
    private String createStartDate;

    /**
     * 创建结束时间（可选，格式：YYYY-MM-DD）
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "创建结束时间格式必须为YYYY-MM-DD")
    private String createEndDate;

    /**
     * 标签列表（可选）
     */
    private List<String> tags;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于等于1")
    private Integer pageSize = 20;

    /**
     * 排序字段（weight/click_count/create_time/update_time）
     */
    private String sortField = "weight";

    /**
     * 排序方向（asc/desc）
     */
    @Pattern(regexp = "^(asc|desc)$", message = "排序方向只能是asc或desc")
    private String sortOrder = "desc";
} 