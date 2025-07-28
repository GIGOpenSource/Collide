package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 取消点赞响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "取消点赞响应")
public class LikeCancelResponse extends BaseResponse {

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
     * 用户当前状态
     */
    @Schema(description = "用户当前状态")
    private String userLikeStatus;

    /**
     * 创建成功响应
     */
    public static LikeCancelResponse success(Long likeCount, Long dislikeCount) {
        LikeCancelResponse response = new LikeCancelResponse();
        response.setSuccess(true);
        response.setLikeCount(likeCount);
        response.setDislikeCount(dislikeCount);
        response.setUserLikeStatus("UNLIKED");
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("取消成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeCancelResponse error(String errorCode, String errorMessage) {
        LikeCancelResponse response = new LikeCancelResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 