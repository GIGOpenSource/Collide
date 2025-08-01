package com.gig.collide.api.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.util.Map;

/**
 * 任务进度更新请求DTO
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务进度更新请求")
public class TaskProgressUpdateRequest {

    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotNull(message = "任务ID不能为空")
    @Positive(message = "任务ID必须为正数")
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long taskId;

    @NotBlank(message = "任务动作不能为空")
    @Schema(description = "任务动作", requiredMode = Schema.RequiredMode.REQUIRED)
    private String taskAction;

    @NotNull(message = "增加次数不能为空")
    @Min(value = 1, message = "增加次数必须大于0")
    @Schema(description = "增加的完成次数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer incrementCount = 1;

    @Schema(description = "扩展数据")
    private Map<String, Object> extraData;
}