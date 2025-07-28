package com.gig.collide.api.tag.request;

import com.gig.collide.api.tag.request.condition.TagQueryCondition;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 标签统计查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TagStatisticsQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 查询条件
     */
    private TagQueryCondition queryCondition;

    /**
     * 开始日期
     */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /**
     * 统计维度（daily、weekly、monthly）
     */
    private String statisticsDimension = "daily";

    /**
     * 是否包含详细信息
     */
    private Boolean includeDetails = false;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 20;

    /**
     * 今日统计
     */
    public static TagStatisticsQueryRequest today() {
        TagStatisticsQueryRequest request = new TagStatisticsQueryRequest();
        LocalDate today = LocalDate.now();
        request.setStartDate(today);
        request.setEndDate(today);
        request.setStatisticsDimension("daily");
        return request;
    }

    /**
     * 本周统计
     */
    public static TagStatisticsQueryRequest thisWeek() {
        TagStatisticsQueryRequest request = new TagStatisticsQueryRequest();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        request.setStartDate(startOfWeek);
        request.setEndDate(today);
        request.setStatisticsDimension("weekly");
        return request;
    }

    /**
     * 本月统计
     */
    public static TagStatisticsQueryRequest thisMonth() {
        TagStatisticsQueryRequest request = new TagStatisticsQueryRequest();
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        request.setStartDate(startOfMonth);
        request.setEndDate(today);
        request.setStatisticsDimension("monthly");
        return request;
    }
} 