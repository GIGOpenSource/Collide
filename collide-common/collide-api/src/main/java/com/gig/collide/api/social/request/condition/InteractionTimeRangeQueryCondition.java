package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 基于互动时间范围的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InteractionTimeRangeQueryCondition implements SocialQueryCondition {
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 时间类型（created-创建时间，updated-更新时间）
     */
    private String timeType;
    
    /**
     * 最近天数（快捷查询）
     */
    private Integer recentDays;
    
    /**
     * 最近小时数（快捷查询）
     */
    private Integer recentHours;
    
    /**
     * 构造时间范围查询条件
     */
    public InteractionTimeRangeQueryCondition(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeType = "created";
    }
    
    /**
     * 构造最近天数查询条件
     */
    public static InteractionTimeRangeQueryCondition ofRecentDays(Integer recentDays) {
        return new InteractionTimeRangeQueryCondition(null, null, "created", recentDays, null);
    }
    
    /**
     * 构造最近小时数查询条件
     */
    public static InteractionTimeRangeQueryCondition ofRecentHours(Integer recentHours) {
        return new InteractionTimeRangeQueryCondition(null, null, "created", null, recentHours);
    }
    
    /**
     * 构造创建时间范围查询条件
     */
    public static InteractionTimeRangeQueryCondition ofCreatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return new InteractionTimeRangeQueryCondition(startTime, endTime, "created", null, null);
    }
    
    /**
     * 构造更新时间范围查询条件
     */
    public static InteractionTimeRangeQueryCondition ofUpdatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return new InteractionTimeRangeQueryCondition(startTime, endTime, "updated", null, null);
    }
} 