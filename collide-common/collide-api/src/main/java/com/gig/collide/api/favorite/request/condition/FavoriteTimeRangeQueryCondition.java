package com.gig.collide.api.favorite.request.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 按时间范围查询收藏条件
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "按时间范围查询收藏条件")
public class FavoriteTimeRangeQueryCondition implements FavoriteQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Override
    public String getConditionType() {
        return "TIME_RANGE";
    }

    @Override
    public boolean isValid() {
        if (startTime == null && endTime == null) {
            return false;
        }
        if (startTime != null && endTime != null) {
            return !startTime.isAfter(endTime);
        }
        return true;
    }
} 