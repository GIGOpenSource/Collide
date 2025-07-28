package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 点赞统计刷新响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "点赞统计刷新响应")
public class LikeStatisticsRefreshResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 刷新是否成功
     */
    @Schema(description = "刷新是否成功")
    private Boolean success;

    /**
     * 创建成功响应
     */
    public static LikeStatisticsRefreshResponse success() {
        LikeStatisticsRefreshResponse response = new LikeStatisticsRefreshResponse();
        response.setSuccess(true);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("刷新成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeStatisticsRefreshResponse error(String errorCode, String errorMessage) {
        LikeStatisticsRefreshResponse response = new LikeStatisticsRefreshResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 