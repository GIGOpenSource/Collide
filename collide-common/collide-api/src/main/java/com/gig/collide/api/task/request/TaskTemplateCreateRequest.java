package com.gig.collide.api.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 任务模板创建请求DTO - 优化版
 * 使用数字常量替代字符串枚举，提升性能和类型安全
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
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

    @NotNull(message = "任务类型不能为空")
    @Min(value = 1, message = "任务类型必须为有效值")
    @Max(value = 4, message = "任务类型必须为有效值")
    @Schema(description = "任务类型: 1-每日任务, 2-周常任务, 3-月度任务, 4-成就任务", 
            allowableValues = {"1", "2", "3", "4"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer taskType;

    @NotNull(message = "任务分类不能为空")
    @Min(value = 1, message = "任务分类必须为有效值") 
    @Max(value = 5, message = "任务分类必须为有效值")
    @Schema(description = "任务分类: 1-登录, 2-内容, 3-社交, 4-消费, 5-邀请", 
            allowableValues = {"1", "2", "3", "4", "5"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer taskCategory;

    @NotNull(message = "任务动作不能为空")
    @Min(value = 1, message = "任务动作必须为有效值")
    @Max(value = 7, message = "任务动作必须为有效值")
    @Schema(description = "任务动作: 1-登录, 2-发布内容, 3-点赞, 4-评论, 5-分享, 6-购买, 7-邀请用户", 
            allowableValues = {"1", "2", "3", "4", "5", "6", "7"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer taskAction;

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