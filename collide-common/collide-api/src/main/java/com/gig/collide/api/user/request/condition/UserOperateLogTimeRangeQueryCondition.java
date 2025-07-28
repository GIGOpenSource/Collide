package com.gig.collide.api.user.request.condition;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户操作日志-时间范围查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserOperateLogTimeRangeQueryCondition implements UserQueryCondition {

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
     * 最近N天（相对时间查询）
     */
    private Integer recentDays;

    /**
     * 今天（快捷查询）
     */
    private Boolean today;

    /**
     * 本周（快捷查询）
     */
    private Boolean thisWeek;

    /**
     * 本月（快捷查询）
     */
    private Boolean thisMonth;
} 