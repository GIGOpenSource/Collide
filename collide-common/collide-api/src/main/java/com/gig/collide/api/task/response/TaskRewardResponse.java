package com.gig.collide.api.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务奖励响应DTO - 优化版
 * 使用数字常量替代字符串枚举，提升传输效率
 * 
 * @author GIG Team
 * @version 3.0.0 (优化版)
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

    @Schema(description = "奖励类型: 1-金币, 2-道具, 3-VIP, 4-经验, 5-徽章")
    private Integer rewardType;

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

    @Schema(description = "发放状态: 1-待发放, 2-已发放, 3-发放失败, 4-已过期")
    private Integer status;

    @Schema(description = "发放时间")
    private LocalDateTime grantTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}