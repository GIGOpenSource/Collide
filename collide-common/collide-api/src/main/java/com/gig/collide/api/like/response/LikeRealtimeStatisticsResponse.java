package com.gig.collide.api.like.response;

import com.gig.collide.api.like.response.data.LikeStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * 实时点赞统计响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "实时点赞统计响应")
public class LikeRealtimeStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计数据映射 (targetId -> 统计信息)
     */
    @Schema(description = "统计数据映射")
    private Map<Long, LikeStatisticsInfo> statisticsMap;

    /**
     * 详细统计信息列表
     */
    @Schema(description = "详细统计信息列表")
    private List<LikeStatisticsInfo> detailedStatistics;

    /**
     * 创建成功响应
     */
    public static LikeRealtimeStatisticsResponse success(Map<Long, LikeStatisticsInfo> statisticsMap, List<LikeStatisticsInfo> detailedStatistics) {
        LikeRealtimeStatisticsResponse response = new LikeRealtimeStatisticsResponse();
        response.setStatisticsMap(statisticsMap);
        response.setDetailedStatistics(detailedStatistics);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeRealtimeStatisticsResponse error(String errorCode, String errorMessage) {
        LikeRealtimeStatisticsResponse response = new LikeRealtimeStatisticsResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 