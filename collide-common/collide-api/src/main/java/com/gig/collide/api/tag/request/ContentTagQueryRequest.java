package com.gig.collide.api.tag.request;

import com.gig.collide.base.request.PageRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 内容标签查询请求
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContentTagQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID（单个标签查询）
     */
    private Long tagId;

    /**
     * 标签ID列表（多标签查询）
     */
    private List<Long> tagIds;

    /**
     * 内容ID（可选，用于单个内容查询）
     */
    private Long contentId;

    /**
     * 打标签开始日期
     */
    private LocalDate tagStartDate;

    /**
     * 打标签结束日期
     */
    private LocalDate tagEndDate;

    /**
     * 标签状态：1-启用 0-禁用
     */
    private Integer tagStatus;

    /**
     * 排序字段：tag_time, tag_name, tag_weight
     */
    private String sortField = "tag_time";

    /**
     * 排序方向：ASC, DESC
     */
    private String sortDirection = "DESC";

    /**
     * 是否包含标签详情
     */
    private Boolean includeTagDetails = false;
}