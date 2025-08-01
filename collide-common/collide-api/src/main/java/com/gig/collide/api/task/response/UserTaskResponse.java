package com.gig.collide.api.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户任务记录响应DTO
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户任务记录响应")
public class UserTaskResponse {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "任务日期")
    private LocalDate taskDate;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务描述")
    private String taskDesc;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "任务分类")
    private String taskCategory;

    @Schema(description = "目标完成次数")
    private Integer targetCount;

    @Schema(description = "当前完成次数")
    private Integer currentCount;

    @Schema(description = "是否已完成")
    private Boolean isCompleted;

    @Schema(description = "是否已领取奖励")
    private Boolean isRewarded;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;

    @Schema(description = "奖励领取时间")
    private LocalDateTime rewardTime;

    @Schema(description = "任务进度百分比")
    private Double progressPercentage;

    @Schema(description = "剩余需完成次数")
    private Integer remainingCount;

    @Schema(description = "是否可以领取奖励")
    private Boolean canClaimReward;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}