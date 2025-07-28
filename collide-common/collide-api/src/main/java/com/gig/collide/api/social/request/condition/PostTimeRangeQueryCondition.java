package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 基于动态时间范围的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostTimeRangeQueryCondition implements SocialQueryCondition {
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 时间类型（created-创建时间，published-发布时间，updated-更新时间）
     */
    private String timeType;
    
    /**
     * 最近天数（快捷查询）
     */
    private Integer recentDays;
    
    /**
     * 构造时间范围查询条件
     */
    public PostTimeRangeQueryCondition(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeType = "published";
    }
    
    /**
     * 构造最近天数查询条件
     */
    public static PostTimeRangeQueryCondition ofRecentDays(Integer recentDays) {
        return new PostTimeRangeQueryCondition(null, null, "published", recentDays);
    }
    
    /**
     * 构造创建时间范围查询条件
     */
    public static PostTimeRangeQueryCondition ofCreatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return new PostTimeRangeQueryCondition(startTime, endTime, "created", null);
    }
    
    /**
     * 构造发布时间范围查询条件
     */
    public static PostTimeRangeQueryCondition ofPublishedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return new PostTimeRangeQueryCondition(startTime, endTime, "published", null);
    }
    
    /**
     * 构造更新时间范围查询条件
     */
    public static PostTimeRangeQueryCondition ofUpdatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return new PostTimeRangeQueryCondition(startTime, endTime, "updated", null);
    }
} 