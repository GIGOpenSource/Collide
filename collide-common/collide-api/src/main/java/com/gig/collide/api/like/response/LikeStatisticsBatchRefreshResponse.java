package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 批量点赞统计刷新响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "批量点赞统计刷新响应")
public class LikeStatisticsBatchRefreshResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 刷新成功数量
     */
    @Schema(description = "刷新成功数量")
    private Integer successCount;

    /**
     * 刷新失败数量
     */
    @Schema(description = "刷新失败数量")
    private Integer failureCount;

    /**
     * 创建成功响应
     */
    public static LikeStatisticsBatchRefreshResponse success(Integer successCount, Integer failureCount) {
        LikeStatisticsBatchRefreshResponse response = new LikeStatisticsBatchRefreshResponse();
        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("批量刷新完成");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeStatisticsBatchRefreshResponse error(String errorCode, String errorMessage) {
        LikeStatisticsBatchRefreshResponse response = new LikeStatisticsBatchRefreshResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 