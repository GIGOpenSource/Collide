package com.gig.collide.api.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户签到响应DTO
 * 包含签到结果和用户签到统计信息
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@Data
@Accessors(chain = true)
@Schema(description = "用户签到响应")
public class UserCheckinResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "签到日期", example = "2024-01-16")
    private LocalDate checkinDate;

    @Schema(description = "获得金币数量", example = "10")
    private Integer rewardCoins;

    @Schema(description = "连续签到天数", example = "5")
    private Integer continuousDays;

    @Schema(description = "是否获得连续奖励", example = "false")
    private Boolean isBonus;

    @Schema(description = "签到时间")
    private LocalDateTime checkinTime;

    // ========== 用户签到统计信息 ==========

    @Schema(description = "总签到次数", example = "15")
    private Integer totalCheckinCount;

    @Schema(description = "本月签到次数", example = "8")
    private Integer monthlyCheckinCount;

    @Schema(description = "历史最长连续签到天数", example = "12")
    private Integer maxContinuousDays;

    @Schema(description = "累计获得金币", example = "150")
    private Long totalRewardCoins;

    @Schema(description = "签到消息", example = "签到成功！获得10金币")
    private String message;

    /**
     * 构建签到成功消息
     */
    public void buildSuccessMessage() {
        if (isBonus != null && isBonus) {
            this.message = String.format("连续签到%d天！获得%d金币（含连续奖励）", continuousDays, rewardCoins);
        } else {
            this.message = String.format("签到成功！获得%d金币", rewardCoins);
        }
    }
}