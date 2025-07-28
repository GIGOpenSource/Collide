package com.gig.collide.api.like.response;

import com.gig.collide.api.like.response.data.LikeStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 点赞统计查询响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "点赞统计查询响应")
public class LikeStatisticsQueryResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计信息列表
     */
    @Schema(description = "统计信息列表")
    private List<LikeStatisticsInfo> statisticsInfos;

    /**
     * 总数量
     */
    @Schema(description = "总数量")
    private Long total;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Integer currentPage;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer pageSize;

    /**
     * 创建成功响应
     */
    public static LikeStatisticsQueryResponse success(List<LikeStatisticsInfo> statisticsInfos, Long total, Integer currentPage, Integer pageSize) {
        LikeStatisticsQueryResponse response = new LikeStatisticsQueryResponse();
        response.setStatisticsInfos(statisticsInfos);
        response.setTotal(total);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeStatisticsQueryResponse error(String errorCode, String errorMessage) {
        LikeStatisticsQueryResponse response = new LikeStatisticsQueryResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 