package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 切换点赞状态响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "切换点赞状态响应")
public class LikeToggleResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 操作是否成功
     */
    @Schema(description = "操作是否成功")
    private Boolean success;

    /**
     * 切换后的状态
     */
    @Schema(description = "切换后的状态：LIKED/UNLIKED")
    private String toggledStatus;

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
     * 操作描述
     */
    @Schema(description = "操作描述")
    private String operationDescription;

    /**
     * 创建成功响应
     */
    public static LikeToggleResponse success(String toggledStatus, Long likeCount, Long dislikeCount, String operationDescription) {
        LikeToggleResponse response = new LikeToggleResponse();
        response.setSuccess(true);
        response.setToggledStatus(toggledStatus);
        response.setLikeCount(likeCount);
        response.setDislikeCount(dislikeCount);
        response.setOperationDescription(operationDescription);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("切换成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static LikeToggleResponse error(String errorCode, String errorMessage) {
        LikeToggleResponse response = new LikeToggleResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 