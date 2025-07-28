package com.gig.collide.api.goods.response;

import com.gig.collide.api.goods.response.data.GoodsStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 商品统计响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "商品统计响应")
public class GoodsStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计信息列表（按类型分组）
     */
    @Schema(description = "统计信息列表")
    private List<GoodsStatisticsInfo> statisticsInfos;

    /**
     * 总体统计信息
     */
    @Schema(description = "总体统计信息")
    private GoodsStatisticsInfo totalStatistics;

    /**
     * 统计时间范围描述
     */
    @Schema(description = "统计时间范围描述")
    private String timeRangeDescription;

    /**
     * 统计成功响应
     */
    public static GoodsStatisticsResponse success(List<GoodsStatisticsInfo> statisticsInfos, GoodsStatisticsInfo totalStatistics) {
        GoodsStatisticsResponse response = new GoodsStatisticsResponse();
        response.setStatisticsInfos(statisticsInfos);
        response.setTotalStatistics(totalStatistics);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("统计查询成功");
        return response;
    }

    /**
     * 统计成功响应（简化版）
     */
    public static GoodsStatisticsResponse success(GoodsStatisticsInfo totalStatistics) {
        GoodsStatisticsResponse response = new GoodsStatisticsResponse();
        response.setTotalStatistics(totalStatistics);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("统计查询成功");
        return response;
    }

    /**
     * 统计失败响应
     */
    public static GoodsStatisticsResponse error(String errorCode, String errorMessage) {
        GoodsStatisticsResponse response = new GoodsStatisticsResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 