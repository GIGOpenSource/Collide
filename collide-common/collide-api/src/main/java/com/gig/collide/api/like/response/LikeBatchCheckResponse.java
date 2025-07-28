package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 批量检查用户点赞状态响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "批量检查用户点赞状态响应")
public class LikeBatchCheckResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 用户点赞状态列表
     */
    @Schema(description = "用户点赞状态列表")
    private List<UserLikeStatus> userLikeStatuses;

    /**
     * 统计信息（如果请求包含）
     */
    @Schema(description = "统计信息")
    private Map<Long, LikeStatistics> statisticsMap;

    /**
     * 用户点赞状态信息
     */
    @Getter
    @Setter
    @ToString
    public static class UserLikeStatus {
        @Schema(description = "目标对象ID")
        private Long targetId;
        
        @Schema(description = "是否已点赞")
        private Boolean hasLiked;
        
        @Schema(description = "是否已点踩")
        private Boolean hasDisliked;
        
        @Schema(description = "用户状态")
        private String userLikeStatus;
        
        @Schema(description = "点赞时间")
        private LocalDateTime likedTime;
    }

    /**
     * 点赞统计信息
     */
    @Getter
    @Setter
    @ToString
    public static class LikeStatistics {
        @Schema(description = "总点赞数")
        private Long totalLikeCount;
        
        @Schema(description = "总点踩数")
        private Long totalDislikeCount;
        
        @Schema(description = "点赞率")
        private Double likeRate;
    }

    /**
     * 创建成功响应
     */
    public static LikeBatchCheckResponse success(List<UserLikeStatus> userLikeStatuses, Map<Long, LikeStatistics> statisticsMap) {
        LikeBatchCheckResponse response = new LikeBatchCheckResponse();
        response.setUserLikeStatuses(userLikeStatuses);
        response.setStatisticsMap(statisticsMap);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("批量查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeBatchCheckResponse error(String errorCode, String errorMessage) {
        LikeBatchCheckResponse response = new LikeBatchCheckResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 