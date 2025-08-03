package com.gig.collide.api.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;

/**
 * 任务模板查询请求DTO
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务模板查询请求")
public class TaskTemplateQueryRequest {

    @Schema(description = "任务名称（模糊搜索）")
    private String taskName;

    @Schema(description = "任务类型", allowableValues = {"daily", "weekly", "achievement"})
    private String taskType;

    @Schema(description = "任务分类", allowableValues = {"login", "content", "social", "consume"})
    private String taskCategory;

    @Schema(description = "任务动作")
    private String taskAction;

    @Schema(description = "是否启用")
    private Boolean isActive;

    @Schema(description = "开始日期（查询范围）")
    private LocalDate startDate;

    @Schema(description = "结束日期（查询范围）")
    private LocalDate endDate;

    @Schema(description = "排序字段", allowableValues = {"id", "taskName", "taskType", "sortOrder", "createTime"})
    private String orderBy = "sortOrder";

    @Schema(description = "排序方向", allowableValues = {"ASC", "DESC"})
    private String orderDirection = "ASC";

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer currentPage = 1;

    @Min(value = 1, message = "页面大小必须大于0")
    @Max(value = 100, message = "页面大小不能超过100")
    @Schema(description = "页面大小", defaultValue = "20")
    private Integer pageSize = 20;
}