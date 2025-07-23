package com.gig.collide.api.like.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞响应
 * 
 * @author Collide
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "点赞响应")
public class LikeResponse extends BaseResponse {
    
    @Schema(description = "点赞是否成功")
    private Boolean success;
    
    @Schema(description = "目标对象当前点赞数")
    private Long likeCount;
    
    @Schema(description = "目标对象当前点踩数")
    private Long dislikeCount;
    
    @Schema(description = "用户当前对该对象的点赞状态")
    private String userLikeStatus;
    
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