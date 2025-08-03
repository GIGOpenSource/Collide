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

/**
 * 用户标签查询请求
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserTagQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID无效")
    private Long userId;

    /**
     * 标签ID（可选，用于单个标签查询）
     */
    private Long tagId;

    /**
     * 关注开始日期
     */
    private LocalDate followStartDate;

    /**
     * 关注结束日期
     */
    private LocalDate followEndDate;

    /**
     * 标签状态：1-启用 0-禁用
     */
    private Integer tagStatus;

    /**
     * 排序字段：follow_time, tag_name, tag_weight
     */
    private String sortField = "follow_time";

    /**
     * 排序方向：ASC, DESC
     */
    private String sortDirection = "DESC";
}