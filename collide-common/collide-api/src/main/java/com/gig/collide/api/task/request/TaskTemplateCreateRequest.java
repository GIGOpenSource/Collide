package com.gig.collide.api.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 任务模板创建请求DTO
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务模板创建请求")
public class TaskTemplateCreateRequest {

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称长度不能超过100字符")
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskName;

    @NotBlank(message = "任务描述不能为空")
    @Size(max = 500, message = "任务描述长度不能超过500字符")
    @Schema(description = "任务描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskDesc;

    @NotBlank(message = "任务类型不能为空")
    @Pattern(regexp = "^(daily|weekly|achievement)$", message = "任务类型只能是: daily、weekly、achievement")
    @Schema(description = "任务类型", allowableValues = {"daily", "weekly", "achievement"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskType;

    @NotBlank(message = "任务分类不能为空")
    @Pattern(regexp = "^(login|content|social|consume)$", message = "任务分类只能是: login、content、social、consume")
    @Schema(description = "任务分类", allowableValues = {"login", "content", "social", "consume"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskCategory;

    @NotBlank(message = "任务动作不能为空")
    @Schema(description = "任务动作", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskAction;

    @NotNull(message = "目标完成次数不能为空")
    @Min(value = 1, message = "目标完成次数必须大于0")
    @Schema(description = "目标完成次数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer targetCount;

    @Min(value = 0, message = "排序值不能为负数")
    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "是否启用", defaultValue = "true")
    private Boolean isActive = true;

    @Schema(description = "任务开始日期")
    private LocalDate startDate;

    @Schema(description = "任务结束日期")
    private LocalDate endDate;
}