package com.gig.collide.api.like.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 时间范围查询条件
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LikeTimeRangeQueryCondition implements LikeQueryCondition {

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    public LikeTimeRangeQueryCondition(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean isValid() {
        // 至少需要一个时间边界
        if (startTime == null && endTime == null) {
            return false;
        }
        
        // 如果两个时间都有，开始时间应该小于等于结束时间
        if (startTime != null && endTime != null) {
            return !startTime.isAfter(endTime);
        }
        
        return true;
    }
} 