package com.gig.collide.api.goods.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 商品批量操作响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "商品批量操作响应")
public class GoodsBatchOperationResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 总处理数量
     */
    @Schema(description = "总处理数量")
    private Integer totalCount;

    /**
     * 成功处理数量
     */
    @Schema(description = "成功处理数量")
    private Integer successCount;

    /**
     * 失败处理数量
     */
    @Schema(description = "失败处理数量")
    private Integer failureCount;

    /**
     * 成功处理的商品ID列表
     */
    @Schema(description = "成功处理的商品ID列表")
    private List<Long> successGoodsIds;

    /**
     * 失败处理的商品ID列表
     */
    @Schema(description = "失败处理的商品ID列表")
    private List<Long> failureGoodsIds;

    /**
     * 失败原因列表
     */
    @Schema(description = "失败原因列表")
    private List<String> failureReasons;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private String operationType;

    /**
     * 操作时间戳
     */
    @Schema(description = "操作时间戳")
    private Long operationTimestamp;

    /**
     * 批量操作成功响应
     */
    public static GoodsBatchOperationResponse success(
            String operationType,
            Integer totalCount,
            Integer successCount,
            Integer failureCount,
            List<Long> successGoodsIds,
            List<Long> failureGoodsIds,
            List<String> failureReasons) {
        
        GoodsBatchOperationResponse response = new GoodsBatchOperationResponse();
        response.setOperationType(operationType);
        response.setTotalCount(totalCount);
        response.setSuccessCount(successCount);
        response.setFailureCount(failureCount);
        response.setSuccessGoodsIds(successGoodsIds);
        response.setFailureGoodsIds(failureGoodsIds);
        response.setFailureReasons(failureReasons);
        response.setOperationTimestamp(System.currentTimeMillis());
        
        if (failureCount > 0) {
            response.setResponseCode("PARTIAL_SUCCESS");
            response.setResponseMessage(String.format("批量操作部分成功：成功%d个，失败%d个", successCount, failureCount));
        } else {
            response.setResponseCode("SUCCESS");
            response.setResponseMessage(String.format("批量操作全部成功：处理%d个商品", successCount));
        }
        
        return response;
    }

    /**
     * 批量操作全部成功响应
     */
    public static GoodsBatchOperationResponse allSuccess(
            String operationType,
            Integer successCount,
            List<Long> successGoodsIds) {
        
        return success(operationType, successCount, successCount, 0, 
                      successGoodsIds, List.of(), List.of());
    }

    /**
     * 批量操作失败响应
     */
    public static GoodsBatchOperationResponse error(String errorCode, String errorMessage) {
        GoodsBatchOperationResponse response = new GoodsBatchOperationResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }

    /**
     * 无权限执行批量操作响应
     */
    public static GoodsBatchOperationResponse noPermission(String operationType) {
        GoodsBatchOperationResponse response = new GoodsBatchOperationResponse();
        response.setOperationType(operationType);
        response.setResponseCode("NO_PERMISSION");
        response.setResponseMessage("无权限执行此批量操作");
        return response;
    }
} 