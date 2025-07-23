package com.gig.collide.api.social.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 社交动态操作响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "社交动态操作响应")
public class SocialPostResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 动态ID
     */
    @Schema(description = "动态ID")
    private Long postId;

    /**
     * 操作结果说明
     */
    @Schema(description = "操作结果说明")
    private String resultMessage;

    /**
     * 是否为新创建
     */
    @Schema(description = "是否为新创建")
    private Boolean isNew;

    /**
     * 热度分数
     */
    @Schema(description = "热度分数")
    private Double hotScore;

    public SocialPostResponse(Long postId) {
        super();
        this.postId = postId;
        this.setSuccess(true);
    }

    /**
     * 创建成功响应
     *
     * @param postId 动态ID
     * @param message 响应消息
     * @return 响应对象
     */
    public static SocialPostResponse success(Long postId, String message) {
        SocialPostResponse response = new SocialPostResponse(postId);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建失败响应
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return 响应对象
     */
    public static SocialPostResponse error(String errorCode, String errorMessage) {
        SocialPostResponse response = new SocialPostResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 