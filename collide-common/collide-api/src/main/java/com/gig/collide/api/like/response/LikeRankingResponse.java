package com.gig.collide.api.like.response;

import com.gig.collide.api.like.response.data.LikeStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 点赞排行榜响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "点赞排行榜响应")
public class LikeRankingResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 排行榜列表
     */
    @Schema(description = "排行榜列表")
    private List<LikeStatisticsInfo> rankings;

    /**
     * 排行榜类型
     */
    @Schema(description = "排行榜类型")
    private String rankingType;

    /**
     * 创建成功响应
     */
    public static LikeRankingResponse success(List<LikeStatisticsInfo> rankings, String rankingType) {
        LikeRankingResponse response = new LikeRankingResponse();
        response.setRankings(rankings);
        response.setRankingType(rankingType);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeRankingResponse error(String errorCode, String errorMessage) {
        LikeRankingResponse response = new LikeRankingResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 