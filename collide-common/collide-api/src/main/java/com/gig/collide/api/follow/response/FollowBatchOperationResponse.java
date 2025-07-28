package com.gig.collide.api.follow.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 关注批量操作响应
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
@Schema(description = "关注批量操作响应")
public class FollowBatchOperationResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型")
    private String operationType;

    /**
     * 总操作数量
     */
    @Schema(description = "总操作数量")
    private Integer totalCount;

    /**
     * 成功操作数量
     */
    @Schema(description = "成功操作数量")
    private Integer successCount;

    /**
     * 失败操作数量
     */
    @Schema(description = "失败操作数量")
    private Integer failureCount;

    /**
     * 成功的关注ID列表
     */
    @Schema(description = "成功的关注ID列表")
    private List<Long> successFollowIds;

    /**
     * 失败的关注ID列表
     */
    @Schema(description = "失败的关注ID列表")
    private List<Long> failureFollowIds;

    /**
     * 失败原因映射（关注ID -> 失败原因）
     */
    @Schema(description = "失败原因映射")
    private Map<Long, String> failureReasons;

    /**
     * 批量操作详细结果
     */
    @Schema(description = "批量操作详细结果")
    private List<BatchOperationResult> detailResults;

    /**
     * 操作汇总信息
     */
    @Schema(description = "操作汇总信息")
    private OperationSummary summary;

    // ===================== 内部类 =====================

    /**
     * 批量操作结果
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "批量操作结果")
    public static class BatchOperationResult {
        /**
         * 关注ID（对于更新/删除操作）
         */
        @Schema(description = "关注ID")
        private Long followId;

        /**
         * 关注者用户ID（对于创建操作）
         */
        @Schema(description = "关注者用户ID")
        private Long followerUserId;

        /**
         * 被关注者用户ID（对于创建操作）
         */
        @Schema(description = "被关注者用户ID")
        private Long followedUserId;

        /**
         * 操作是否成功
         */
        @Schema(description = "操作是否成功")
        private Boolean success;

        /**
         * 失败原因
         */
        @Schema(description = "失败原因")
        private String reason;

        /**
         * 操作结果描述
         */
        @Schema(description = "操作结果描述")
        private String resultDescription;
    }

    /**
     * 操作汇总信息
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "操作汇总信息")
    public static class OperationSummary {
        /**
         * 操作开始时间
         */
        @Schema(description = "操作开始时间")
        private Long startTime;

        /**
         * 操作结束时间
         */
        @Schema(description = "操作结束时间")
        private Long endTime;

        /**
         * 操作耗时（毫秒）
         */
        @Schema(description = "操作耗时（毫秒）")
        private Long duration;

        /**
         * 成功率（百分比）
         */
        @Schema(description = "成功率（百分比）")
        private Double successRate;

        /**
         * 是否全部成功
         */
        @Schema(description = "是否全部成功")
        private Boolean isAllSuccess;

        /**
         * 是否全部失败
         */
        @Schema(description = "是否全部失败")
        private Boolean isAllFailure;
    }

    // ===================== 静态工厂方法 =====================

    /**
     * 全部成功响应
     *
     * @param operationType 操作类型
     * @param successFollowIds 成功的关注ID列表
     * @return 批量操作响应
     */
    public static FollowBatchOperationResponse allSuccess(String operationType, List<Long> successFollowIds) {
        FollowBatchOperationResponse response = new FollowBatchOperationResponse();
        response.setOperationType(operationType);
        response.setTotalCount(successFollowIds.size());
        response.setSuccessCount(successFollowIds.size());
        response.setFailureCount(0);
        response.setSuccessFollowIds(successFollowIds);
        response.setSuccess(true);
        response.setResponseMessage("批量操作全部成功");
        return response;
    }

    /**
     * 部分成功响应
     *
     * @param operationType 操作类型
     * @param successFollowIds 成功的关注ID列表
     * @param failureFollowIds 失败的关注ID列表
     * @param failureReasons 失败原因映射
     * @return 批量操作响应
     */
    public static FollowBatchOperationResponse partialSuccess(String operationType, 
                                                              List<Long> successFollowIds,
                                                              List<Long> failureFollowIds,
                                                              Map<Long, String> failureReasons) {
        FollowBatchOperationResponse response = new FollowBatchOperationResponse();
        response.setOperationType(operationType);
        response.setTotalCount(successFollowIds.size() + failureFollowIds.size());
        response.setSuccessCount(successFollowIds.size());
        response.setFailureCount(failureFollowIds.size());
        response.setSuccessFollowIds(successFollowIds);
        response.setFailureFollowIds(failureFollowIds);
        response.setFailureReasons(failureReasons);
        response.setSuccess(true);
        response.setResponseMessage("批量操作部分成功");
        return response;
    }

    /**
     * 全部失败响应
     *
     * @param operationType 操作类型
     * @param failureFollowIds 失败的关注ID列表
     * @param failureReasons 失败原因映射
     * @return 批量操作响应
     */
    public static FollowBatchOperationResponse allFailure(String operationType,
                                                          List<Long> failureFollowIds,
                                                          Map<Long, String> failureReasons) {
        FollowBatchOperationResponse response = new FollowBatchOperationResponse();
        response.setOperationType(operationType);
        response.setTotalCount(failureFollowIds.size());
        response.setSuccessCount(0);
        response.setFailureCount(failureFollowIds.size());
        response.setFailureFollowIds(failureFollowIds);
        response.setFailureReasons(failureReasons);
        response.setSuccess(false);
        response.setResponseMessage("批量操作全部失败");
        return response;
    }

    /**
     * 带详细结果的响应
     *
     * @param operationType 操作类型
     * @param detailResults 详细结果列表
     * @return 批量操作响应
     */
    public static FollowBatchOperationResponse withDetails(String operationType, List<BatchOperationResult> detailResults) {
        FollowBatchOperationResponse response = new FollowBatchOperationResponse();
        response.setOperationType(operationType);
        response.setDetailResults(detailResults);
        
        int total = detailResults.size();
        int success = (int) detailResults.stream().mapToLong(r -> Boolean.TRUE.equals(r.getSuccess()) ? 1 : 0).sum();
        int failure = total - success;
        
        response.setTotalCount(total);
        response.setSuccessCount(success);
        response.setFailureCount(failure);
        response.setSuccess(failure == 0);
        
        if (failure == 0) {
            response.setResponseMessage("批量操作全部成功");
        } else if (success == 0) {
            response.setResponseMessage("批量操作全部失败");
        } else {
            response.setResponseMessage("批量操作部分成功");
        }
        
        return response;
    }

    /**
     * 失败响应
     *
     * @param operationType 操作类型
     * @param message 错误消息
     * @return 批量操作响应
     */
    public static FollowBatchOperationResponse error(String operationType, String message) {
        FollowBatchOperationResponse response = new FollowBatchOperationResponse();
        response.setOperationType(operationType);
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否全部成功
     *
     * @return true-全部成功，false-有失败
     */
    public boolean isAllSuccess() {
        return failureCount != null && failureCount == 0 && successCount != null && successCount > 0;
    }

    /**
     * 是否全部失败
     *
     * @return true-全部失败，false-有成功
     */
    public boolean isAllFailure() {
        return successCount != null && successCount == 0 && failureCount != null && failureCount > 0;
    }

    /**
     * 是否部分成功
     *
     * @return true-部分成功，false-全部成功或全部失败
     */
    public boolean isPartialSuccess() {
        return successCount != null && successCount > 0 && failureCount != null && failureCount > 0;
    }

    /**
     * 获取成功率
     *
     * @return 成功率（0-1之间的小数）
     */
    public double getSuccessRate() {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        return successCount != null ? (double) successCount / totalCount : 0.0;
    }

    /**
     * 获取成功率百分比
     *
     * @return 成功率百分比（0-100之间的整数）
     */
    public int getSuccessRatePercentage() {
        return (int) Math.round(getSuccessRate() * 100);
    }

    /**
     * 是否有详细结果
     *
     * @return true-有详细结果，false-无详细结果
     */
    public boolean hasDetailResults() {
        return detailResults != null && !detailResults.isEmpty();
    }

    /**
     * 获取操作总数（安全获取）
     *
     * @return 操作总数，null时返回0
     */
    public int getTotalCountSafe() {
        return totalCount != null ? totalCount : 0;
    }

    /**
     * 获取成功数量（安全获取）
     *
     * @return 成功数量，null时返回0
     */
    public int getSuccessCountSafe() {
        return successCount != null ? successCount : 0;
    }

    /**
     * 获取失败数量（安全获取）
     *
     * @return 失败数量，null时返回0
     */
    public int getFailureCountSafe() {
        return failureCount != null ? failureCount : 0;
    }
} 