package com.gig.collide.api.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务模板响应DTO - 优化版
 * 使用数字常量替代字符串枚举，提升传输效率
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务模板响应")
public class TaskTemplateResponse {

    @Schema(description = "任务模板ID")
    private Long id;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "任务描述")
    private String taskDesc;

    @Schema(description = "任务类型: 1-每日任务, 2-周常任务, 3-月度任务, 4-成就任务")
    private Integer taskType;

    @Schema(description = "任务分类: 1-登录, 2-内容, 3-社交, 4-消费, 5-邀请")
    private Integer taskCategory;

    @Schema(description = "任务动作: 1-登录, 2-发布内容, 3-点赞, 4-评论, 5-分享, 6-购买, 7-邀请用户")
    private Integer taskAction;

    @Schema(description = "目标完成次数")
    private Integer targetCount;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "是否启用")
    private Boolean isActive;

    @Schema(description = "任务开始日期")
    private LocalDate startDate;

    @Schema(description = "任务结束日期")
    private LocalDate endDate;

    @Schema(description = "是否可用")
    private Boolean isAvailable;

    @Schema(description = "任务奖励列表")
    private List<TaskRewardResponse> rewards;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}