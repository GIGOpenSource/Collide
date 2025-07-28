package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 通用点赞响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "通用点赞响应")
public class LikeResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 操作是否成功
     */
    @Schema(description = "操作是否成功")
    private Boolean success;

    /**
     * 当前点赞数
     */
    @Schema(description = "当前点赞数")
    private Long likeCount;

    /**
     * 当前点踩数
     */
    @Schema(description = "当前点踩数")
    private Long dislikeCount;

    /**
     * 用户当前对该对象的点赞状态
     */
    @Schema(description = "用户当前对该对象的点赞状态：LIKED/DISLIKED/UNLIKED")
    private String userLikeStatus;

    /**
     * 点赞率
     */
    @Schema(description = "点赞率")
    private Double likeRate;

    /**
     * 操作消息
     */
    @Schema(description = "操作消息")
    private String operationMessage;

    /**
     * 创建成功响应
     */
    public static LikeResponse success(Long likeCount, Long dislikeCount, String userLikeStatus) {
        LikeResponse response = new LikeResponse();
        response.setSuccess(true);
        response.setLikeCount(likeCount);
        response.setDislikeCount(dislikeCount);
        response.setUserLikeStatus(userLikeStatus);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("操作成功");
        
        // 计算点赞率
        long total = likeCount + dislikeCount;
        if (total > 0) {
            response.setLikeRate((double) likeCount / total * 100);
        } else {
            response.setLikeRate(0.0);
        }
        
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeResponse error(String errorCode, String errorMessage) {
        LikeResponse response = new LikeResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 