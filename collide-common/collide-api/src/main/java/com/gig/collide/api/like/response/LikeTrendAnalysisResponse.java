package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 点赞趋势分析响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "点赞趋势分析响应")
public class LikeTrendAnalysisResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 趋势数据点列表
     */
    @Schema(description = "趋势数据点列表")
    private List<TrendDataPoint> trendData;

    /**
     * 趋势总结
     */
    @Schema(description = "趋势总结")
    private TrendSummary summary;

    /**
     * 趋势数据点
     */
    @Getter
    @Setter
    @ToString
    public static class TrendDataPoint {
        @Schema(description = "时间点")
        private LocalDateTime timePoint;
        
        @Schema(description = "点赞数")
        private Long likeCount;
        
        @Schema(description = "点踩数")
        private Long dislikeCount;
        
        @Schema(description = "增长率")
        private Double growthRate;
    }

    /**
     * 趋势总结
     */
    @Getter
    @Setter
    @ToString
    public static class TrendSummary {
        @Schema(description = "总增长")
        private Long totalGrowth;
        
        @Schema(description = "平均增长率")
        private Double avgGrowthRate;
        
        @Schema(description = "峰值时间")
        private LocalDateTime peakTime;
        
        @Schema(description = "峰值数量")
        private Long peakValue;
    }

    /**
     * 创建成功响应
     */
    public static LikeTrendAnalysisResponse success(List<TrendDataPoint> trendData, TrendSummary summary) {
        LikeTrendAnalysisResponse response = new LikeTrendAnalysisResponse();
        response.setTrendData(trendData);
        response.setSummary(summary);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("分析成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeTrendAnalysisResponse error(String errorCode, String errorMessage) {
        LikeTrendAnalysisResponse response = new LikeTrendAnalysisResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 