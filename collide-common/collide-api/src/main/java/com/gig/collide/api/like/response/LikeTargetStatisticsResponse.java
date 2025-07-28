package com.gig.collide.api.like.response;

import com.gig.collide.api.like.response.data.LikeStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 目标对象点赞统计响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "目标对象点赞统计响应")
public class LikeTargetStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计信息
     */
    @Schema(description = "统计信息")
    private LikeStatisticsInfo statisticsInfo;

    /**
     * 创建成功响应
     */
    public static LikeTargetStatisticsResponse success(LikeStatisticsInfo statisticsInfo) {
        LikeTargetStatisticsResponse response = new LikeTargetStatisticsResponse();
        response.setStatisticsInfo(statisticsInfo);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeTargetStatisticsResponse error(String errorCode, String errorMessage) {
        LikeTargetStatisticsResponse response = new LikeTargetStatisticsResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 