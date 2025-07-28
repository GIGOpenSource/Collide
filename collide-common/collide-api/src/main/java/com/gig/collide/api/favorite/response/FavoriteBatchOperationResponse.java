package com.gig.collide.api.favorite.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 收藏批量操作响应
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏批量操作响应")
public class FavoriteBatchOperationResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 成功处理的收藏ID列表
     */
    @Schema(description = "成功处理的收藏ID列表")
    private List<Long> successIds;

    /**
     * 失败的收藏ID列表
     */
    @Schema(description = "失败的收藏ID列表")
    private List<Long> failedIds;

    /**
     * 总处理数量
     */
    @Schema(description = "总处理数量")
    private Integer totalCount;

    /**
     * 成功数量
     */
    @Schema(description = "成功数量")
    private Integer successCount;

    /**
     * 失败数量
     */
    @Schema(description = "失败数量")
    private Integer failedCount;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private String operationType;

    /**
     * 详细错误信息
     */
    @Schema(description = "详细错误信息")
    private String errorDetails;

    // ===================== 静态工厂方法 =====================

    /**
     * 全部成功响应
     *
     * @param successIds 成功的ID列表
     * @param operationType 操作类型
     * @return 批量操作响应
     */
    public static FavoriteBatchOperationResponse success(List<Long> successIds, String operationType) {
        FavoriteBatchOperationResponse response = new FavoriteBatchOperationResponse();
        response.setSuccessIds(successIds);
        response.setTotalCount(successIds.size());
        response.setSuccessCount(successIds.size());
        response.setFailedCount(0);
        response.setOperationType(operationType);
        response.setSuccess(true);
        return response;
    }

    /**
     * 部分成功响应
     *
     * @param successIds 成功的ID列表
     * @param failedIds 失败的ID列表
     * @param operationType 操作类型
     * @return 批量操作响应
     */
    public static FavoriteBatchOperationResponse partialSuccess(List<Long> successIds, List<Long> failedIds, String operationType) {
        FavoriteBatchOperationResponse response = new FavoriteBatchOperationResponse();
        response.setSuccessIds(successIds);
        response.setFailedIds(failedIds);
        response.setTotalCount(successIds.size() + failedIds.size());
        response.setSuccessCount(successIds.size());
        response.setFailedCount(failedIds.size());
        response.setOperationType(operationType);
        response.setSuccess(true);
        response.setMessage("部分操作成功");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @param operationType 操作类型
     * @return 批量操作响应
     */
    public static FavoriteBatchOperationResponse failure(String message, String operationType) {
        FavoriteBatchOperationResponse response = new FavoriteBatchOperationResponse();
        response.setOperationType(operationType);
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否全部成功
     *
     * @return true-全部成功，false-有失败
     */
    public boolean isAllSuccess() {
        return isSuccess() && (failedCount == null || failedCount == 0);
    }

    /**
     * 是否部分成功
     *
     * @return true-部分成功，false-全部失败或全部成功
     */
    public boolean isPartialSuccess() {
        return isSuccess() && successCount != null && successCount > 0 && failedCount != null && failedCount > 0;
    }
} 