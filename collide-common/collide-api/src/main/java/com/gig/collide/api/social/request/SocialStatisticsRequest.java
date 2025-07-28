package com.gig.collide.api.social.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交统计请求
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialStatisticsRequest extends BaseRequest {
    
    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    /**
     * 统计维度（daily-按天，weekly-按周，monthly-按月，yearly-按年）
     */
    @NotNull(message = "统计维度不能为空")
    private String statisticsDimension = "daily";
    
    /**
     * 用户ID列表（特定用户统计）
     */
    @Size(max = 100, message = "用户ID列表不能超过100个")
    private List<Long> userIds;
    
    /**
     * 动态ID列表（特定动态统计）
     */
    @Size(max = 100, message = "动态ID列表不能超过100个")
    private List<Long> postIds;
    
    /**
     * 是否包含详细信息
     */
    private Boolean includeDetails = false;
    
    /**
     * 排行榜限制数量
     */
    @Min(value = 1, message = "排行榜限制数量必须大于0")
    @Max(value = 100, message = "排行榜限制数量不能超过100")
    private Integer rankingLimit = 10;
    
    /**
     * 构造今日统计请求
     */
    public static SocialStatisticsRequest today() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        return new SocialStatisticsRequest(startOfDay, now, "daily", null, null, false, 10);
    }
    
    /**
     * 构造本周统计请求
     */
    public static SocialStatisticsRequest thisWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.toLocalDate().atStartOfDay().minusDays(now.getDayOfWeek().getValue() - 1);
        return new SocialStatisticsRequest(startOfWeek, now, "weekly", null, null, false, 10);
    }
    
    /**
     * 构造本月统计请求
     */
    public static SocialStatisticsRequest thisMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        return new SocialStatisticsRequest(startOfMonth, now, "monthly", null, null, false, 10);
    }
    
    /**
     * 构造用户统计请求
     */
    public static SocialStatisticsRequest forUsers(List<Long> userIds, LocalDateTime startTime, LocalDateTime endTime) {
        return new SocialStatisticsRequest(startTime, endTime, "daily", userIds, null, true, 10);
    }
    
    /**
     * 构造动态统计请求
     */
    public static SocialStatisticsRequest forPosts(List<Long> postIds, LocalDateTime startTime, LocalDateTime endTime) {
        return new SocialStatisticsRequest(startTime, endTime, "daily", null, postIds, true, 10);
    }
    
    /**
     * 构造排行榜请求
     */
    public static SocialStatisticsRequest ranking(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        return new SocialStatisticsRequest(startTime, endTime, "daily", null, null, false, limit);
    }
} 