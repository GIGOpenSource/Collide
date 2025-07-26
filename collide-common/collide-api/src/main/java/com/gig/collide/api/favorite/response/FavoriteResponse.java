package com.gig.collide.api.favorite.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 收藏操作响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "收藏操作响应")
public class FavoriteResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID（单个操作）
     */
    @Schema(description = "收藏ID")
    private Long favoriteId;

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
     * 当前收藏状态
     */
    @Schema(description = "当前收藏状态")
    private Boolean isFavorited;

    /**
     * 收藏总数
     */
    @Schema(description = "收藏总数")
    private Long favoriteCount;

    // === 批量操作相关字段 ===

    /**
     * 成功的收藏ID列表（批量操作）
     */
    @Schema(description = "成功的收藏ID列表")
    private List<Long> successIds;

    /**
     * 失败的目标ID及原因（批量操作）
     */
    @Schema(description = "失败的目标ID及原因")
    private Map<Long, String> failureReasons;

    /**
     * 成功数量（批量操作）
     */
    @Schema(description = "成功数量")
    private Integer successCount;

    /**
     * 失败数量（批量操作）
     */
    @Schema(description = "失败数量")
    private Integer failureCount;

    public FavoriteResponse(Long favoriteId) {
        super();
        this.favoriteId = favoriteId;
        this.setSuccess(true);
    }

    /**
     * 创建成功响应
     *
     * @param favoriteId 收藏ID
     * @param message 响应消息
     * @return 响应对象
     */
    public static FavoriteResponse success(Long favoriteId, String message) {
        FavoriteResponse response = new FavoriteResponse(favoriteId);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建批量成功响应
     *
     * @param successIds 成功的收藏ID列表
     * @param message 响应消息
     * @return 响应对象
     */
    public static FavoriteResponse success(List<Long> successIds, String message) {
        FavoriteResponse response = new FavoriteResponse();
        response.setSuccess(true);
        response.setSuccessIds(successIds);
        response.setSuccessCount(successIds != null ? successIds.size() : 0);
        response.setFailureCount(0);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建部分成功响应（批量操作）
     *
     * @param successIds 成功的收藏ID列表
     * @param message 响应消息
     * @param failureReasons 失败原因
     * @return 响应对象
     */
    public static FavoriteResponse partialSuccess(List<Long> successIds, String message, Map<Long, String> failureReasons) {
        FavoriteResponse response = new FavoriteResponse();
        response.setSuccess(true); // 部分成功仍然认为是成功
        response.setSuccessIds(successIds);
        response.setFailureReasons(failureReasons);
        response.setSuccessCount(successIds != null ? successIds.size() : 0);
        response.setFailureCount(failureReasons != null ? failureReasons.size() : 0);
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
    public static FavoriteResponse error(String errorCode, String errorMessage) {
        FavoriteResponse response = new FavoriteResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 