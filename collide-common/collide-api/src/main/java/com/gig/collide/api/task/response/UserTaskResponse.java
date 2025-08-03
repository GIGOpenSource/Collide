package com.gig.collide.api.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户任务响应DTO - 签到记录专用版
 * 对应 t_user_checkin_record 表结构
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户任务响应")
public class UserTaskResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID", example = "1001")
    private Long id;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "任务模板ID", example = "1")
    private Long taskTemplateId;

    @Schema(description = "任务名称", example = "每日签到")
    private String taskName;

    @Schema(description = "签到日期", example = "2024-01-16")
    private LocalDate checkinDate;

    @Schema(description = "获得金币数量", example = "10")
    private Integer rewardCoins;

    @Schema(description = "连续签到天数", example = "5")
    private Integer continuousDays;

    @Schema(description = "是否获得连续奖励", example = "false")
    private Boolean isBonus;

    @Schema(description = "签到IP地址", example = "192.168.1.100")
    private String checkinIp;

    @Schema(description = "签到时间")
    private LocalDateTime checkinTime;

    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;

    /**
     * 获取奖励描述
     */
    public String getRewardDesc() {
        if (isBonus != null && isBonus) {
            return String.format("获得%d金币（连续%d天奖励）", rewardCoins, continuousDays);
        } else {
            return String.format("获得%d金币", rewardCoins);
        }
    }
}