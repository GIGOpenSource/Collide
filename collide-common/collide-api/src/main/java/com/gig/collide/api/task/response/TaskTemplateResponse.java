package com.gig.collide.api.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务模板响应DTO - 签到专用版
 * 对应 t_task_template 表结构
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务模板响应")
public class TaskTemplateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "任务模板ID", example = "1")
    private Long id;

    @Schema(description = "任务名称", example = "每日签到")
    private String taskName;

    @Schema(description = "任务类型", example = "DAILY_CHECKIN")
    private String taskType;

    @Schema(description = "任务描述", example = "每天签到获得金币奖励，连续签到有额外奖励")
    private String taskDesc;

    @Schema(description = "基础奖励金币数量", example = "10")
    private Integer rewardCoins;

    @Schema(description = "奖励规则说明", example = "连续签到7天可获得双倍奖励(20金币)")
    private String bonusRule;

    @Schema(description = "是否可重复", example = "true")
    private Boolean isRepeatable;

    @Schema(description = "排序权重", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "启用")
    private String statusDesc;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (this.status == null) return "未知";
        return this.status == 1 ? "启用" : "禁用";
    }
}