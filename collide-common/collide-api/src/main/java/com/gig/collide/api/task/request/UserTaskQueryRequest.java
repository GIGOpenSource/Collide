package com.gig.collide.api.task.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 用户任务查询请求DTO
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户任务查询请求")
public class UserTaskQueryRequest {

    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "任务类型", allowableValues = {"daily", "weekly", "achievement"})
    private String taskType;

    @Schema(description = "任务分类", allowableValues = {"login", "content", "social", "consume"})
    private String taskCategory;

    @Schema(description = "是否已完成")
    private Boolean isCompleted;

    @Schema(description = "是否已领取奖励")
    private Boolean isRewarded;

    @Schema(description = "开始日期（查询范围）")
    private LocalDate startDate;

    @Schema(description = "结束日期（查询范围）")
    private LocalDate endDate;

    @Schema(description = "排序字段", allowableValues = {"id", "taskDate", "createTime", "completeTime"})
    private String orderBy = "taskDate";

    @Schema(description = "排序方向", allowableValues = {"ASC", "DESC"})
    private String orderDirection = "DESC";

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer currentPage = 1;

    @Min(value = 1, message = "页面大小必须大于0")
    @Max(value = 100, message = "页面大小不能超过100")
    @Schema(description = "页面大小", defaultValue = "20")
    private Integer pageSize = 20;
}