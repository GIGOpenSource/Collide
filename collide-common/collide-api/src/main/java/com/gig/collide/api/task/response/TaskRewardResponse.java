package com.gig.collide.api.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务奖励响应DTO
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "任务奖励响应")
public class TaskRewardResponse {

    @Schema(description = "奖励ID")
    private Long id;

    @Schema(description = "任务模板ID")
    private Long taskId;

    @Schema(description = "奖励类型")
    private String rewardType;

    @Schema(description = "奖励名称")
    private String rewardName;

    @Schema(description = "奖励描述")
    private String rewardDesc;

    @Schema(description = "奖励数量")
    private Integer rewardAmount;

    @Schema(description = "奖励扩展数据")
    private Map<String, Object> rewardData;

    @Schema(description = "是否主要奖励")
    private Boolean isMainReward;

    @Schema(description = "发放状态")
    private String status;

    @Schema(description = "发放时间")
    private LocalDateTime grantTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}