package com.gig.collide.api.like.response;

import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 目标对象点赞详情响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "目标对象点赞详情响应")
public class LikeTargetDetailResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 目标对象ID
     */
    @Schema(description = "目标对象ID")
    private Long targetId;

    /**
     * 总点赞数
     */
    @Schema(description = "总点赞数")
    private Long totalLikeCount;

    /**
     * 总点踩数
     */
    @Schema(description = "总点踩数")
    private Long totalDislikeCount;

    /**
     * 点赞率
     */
    @Schema(description = "点赞率")
    private Double likeRate;

    /**
     * 最后点赞时间
     */
    @Schema(description = "最后点赞时间")
    private LocalDateTime lastLikeTime;

    /**
     * 今日点赞数
     */
    @Schema(description = "今日点赞数")
    private Long todayLikeCount;

    /**
     * 本周点赞数
     */
    @Schema(description = "本周点赞数")
    private Long weekLikeCount;

    /**
     * 本月点赞数
     */
    @Schema(description = "本月点赞数")
    private Long monthLikeCount;

    /**
     * 点赞用户列表（如果请求包含）
     */
    @Schema(description = "点赞用户列表")
    private List<LikeInfo> likeUserList;

    /**
     * 用户列表总数
     */
    @Schema(description = "用户列表总数")
    private Long userListTotal;

    /**
     * 创建成功响应
     */
    public static LikeTargetDetailResponse success(Long targetId, Long totalLikeCount, Long totalDislikeCount, 
                                                 Double likeRate, LocalDateTime lastLikeTime) {
        LikeTargetDetailResponse response = new LikeTargetDetailResponse();
        response.setTargetId(targetId);
        response.setTotalLikeCount(totalLikeCount);
        response.setTotalDislikeCount(totalDislikeCount);
        response.setLikeRate(likeRate);
        response.setLastLikeTime(lastLikeTime);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeTargetDetailResponse error(String errorCode, String errorMessage) {
        LikeTargetDetailResponse response = new LikeTargetDetailResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 