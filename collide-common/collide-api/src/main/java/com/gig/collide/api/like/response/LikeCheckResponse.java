package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 检查用户点赞状态响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "检查用户点赞状态响应")
public class LikeCheckResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 用户是否已点赞
     */
    @Schema(description = "用户是否已点赞")
    private Boolean hasLiked;

    /**
     * 用户是否已点踩
     */
    @Schema(description = "用户是否已点踩")
    private Boolean hasDisliked;

    /**
     * 用户当前状态
     */
    @Schema(description = "用户当前状态：LIKED/DISLIKED/UNLIKED")
    private String userLikeStatus;

    /**
     * 点赞时间（如果已点赞）
     */
    @Schema(description = "点赞时间（如果已点赞）")
    private LocalDateTime likedTime;

    /**
     * 当前目标的总点赞数
     */
    @Schema(description = "当前目标的总点赞数")
    private Long totalLikeCount;

    /**
     * 当前目标的总点踩数
     */
    @Schema(description = "当前目标的总点踩数")
    private Long totalDislikeCount;

    /**
     * 创建成功响应
     */
    public static LikeCheckResponse success(Boolean hasLiked, Boolean hasDisliked, String userLikeStatus, 
                                          LocalDateTime likedTime, Long totalLikeCount, Long totalDislikeCount) {
        LikeCheckResponse response = new LikeCheckResponse();
        response.setHasLiked(hasLiked);
        response.setHasDisliked(hasDisliked);
        response.setUserLikeStatus(userLikeStatus);
        response.setLikedTime(likedTime);
        response.setTotalLikeCount(totalLikeCount);
        response.setTotalDislikeCount(totalDislikeCount);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeCheckResponse error(String errorCode, String errorMessage) {
        LikeCheckResponse response = new LikeCheckResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 