package com.gig.collide.api.search.request.condition;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 日期范围查询条件
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class DateRangeQueryCondition extends SearchQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 时间字段名
     */
    private String timeField = "create_time";

    /**
     * 快捷时间范围：TODAY, WEEK, MONTH, YEAR
     */
    private String quickRange;

    public DateRangeQueryCondition(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DateRangeQueryCondition(String quickRange) {
        this.quickRange = quickRange;
        setQuickRangeToDateTime(quickRange);
    }

    @Override
    public String getConditionType() {
        return "DATE_RANGE";
    }

    @Override
    public boolean isValid() {
        return startTime != null || endTime != null || quickRange != null;
    }

    @AssertTrue(message = "结束时间必须大于开始时间")
    public boolean isValidTimeRange() {
        if (startTime != null && endTime != null) {
            return !endTime.isBefore(startTime);
        }
        return true;
    }

    /**
     * 设置快捷时间范围
     */
    private void setQuickRangeToDateTime(String range) {
        if (range == null) return;
        
        LocalDateTime now = LocalDateTime.now();
        switch (range.toUpperCase()) {
            case "TODAY":
                this.startTime = now.toLocalDate().atStartOfDay();
                this.endTime = now;
                break;
            case "WEEK":
                this.startTime = now.minusWeeks(1);
                this.endTime = now;
                break;
            case "MONTH":
                this.startTime = now.minusMonths(1);
                this.endTime = now;
                break;
            case "YEAR":
                this.startTime = now.minusYears(1);
                this.endTime = now;
                break;
        }
    }
} 