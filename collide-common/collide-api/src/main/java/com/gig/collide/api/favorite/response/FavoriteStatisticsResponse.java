package com.gig.collide.api.favorite.response;

import com.gig.collide.api.favorite.response.data.FavoriteStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 收藏统计响应
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
@Schema(description = "收藏统计响应")
public class FavoriteStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计信息列表
     */
    @Schema(description = "统计信息列表")
    private List<FavoriteStatisticsInfo> statisticsList;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long totalCount;

    /**
     * 统计时间范围描述
     */
    @Schema(description = "统计时间范围描述")
    private String timeRangeDescription;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功响应
     *
     * @param statisticsList 统计信息列表
     * @return 统计响应
     */
    public static FavoriteStatisticsResponse success(List<FavoriteStatisticsInfo> statisticsList) {
        FavoriteStatisticsResponse response = new FavoriteStatisticsResponse();
        response.setStatisticsList(statisticsList);
        response.setTotalCount((long) statisticsList.size());
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功响应（带时间范围描述）
     *
     * @param statisticsList 统计信息列表
     * @param timeRangeDescription 时间范围描述
     * @return 统计响应
     */
    public static FavoriteStatisticsResponse success(List<FavoriteStatisticsInfo> statisticsList, String timeRangeDescription) {
        FavoriteStatisticsResponse response = new FavoriteStatisticsResponse();
        response.setStatisticsList(statisticsList);
        response.setTotalCount((long) statisticsList.size());
        response.setTimeRangeDescription(timeRangeDescription);
        response.setSuccess(true);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 统计响应
     */
    public static FavoriteStatisticsResponse failure(String message) {
        FavoriteStatisticsResponse response = new FavoriteStatisticsResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }
} 