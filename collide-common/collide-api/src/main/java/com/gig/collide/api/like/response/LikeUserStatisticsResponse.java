package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户点赞统计响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "用户点赞统计响应")
public class LikeUserStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 给出的点赞数
     */
    @Schema(description = "给出的点赞数")
    private Long givenLikeCount;

    /**
     * 收到的点赞数
     */
    @Schema(description = "收到的点赞数")
    private Long receivedLikeCount;

    /**
     * 创建成功响应
     */
    public static LikeUserStatisticsResponse success(Long userId, Long givenLikeCount, Long receivedLikeCount) {
        LikeUserStatisticsResponse response = new LikeUserStatisticsResponse();
        response.setUserId(userId);
        response.setGivenLikeCount(givenLikeCount);
        response.setReceivedLikeCount(receivedLikeCount);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeUserStatisticsResponse error(String errorCode, String errorMessage) {
        LikeUserStatisticsResponse response = new LikeUserStatisticsResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 