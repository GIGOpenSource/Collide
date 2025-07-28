package com.gig.collide.api.follow.request;

import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * 关注批量操作请求
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
@Schema(description = "关注批量操作请求")
public class FollowBatchOperationRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    @NotNull(message = "操作类型不能为空")
    @Schema(description = "操作类型", required = true)
    private OperationType operationType;

    /**
     * 关注ID列表（针对已存在的关注记录进行操作）
     */
    @Schema(description = "关注ID列表")
    private List<Long> followIds;

    /**
     * 用户关注关系列表（针对新建关注）
     */
    @Schema(description = "用户关注关系列表")
    private List<UserFollowRelation> userRelations;

    /**
     * 目标关注类型（用于批量更新类型）
     */
    @Schema(description = "目标关注类型")
    private FollowType targetFollowType;

    /**
     * 目标关注状态（用于批量更新状态）
     */
    @Schema(description = "目标关注状态")
    private FollowStatus targetStatus;

    /**
     * 操作原因（可选）
     */
    @Schema(description = "操作原因")
    private String reason;

    /**
     * 是否物理删除（仅用于删除操作）
     */
    @Schema(description = "是否物理删除")
    private Boolean physicalDelete = false;

    // ===================== 内部类 =====================

    /**
     * 用户关注关系
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户关注关系")
    public static class UserFollowRelation {
        /**
         * 关注者用户ID
         */
        @NotNull(message = "关注者用户ID不能为空")
        @Schema(description = "关注者用户ID", required = true)
        private Long followerUserId;

        /**
         * 被关注者用户ID
         */
        @NotNull(message = "被关注者用户ID不能为空")
        @Schema(description = "被关注者用户ID", required = true)
        private Long followedUserId;

        /**
         * 关注类型
         */
        @Schema(description = "关注类型")
        private FollowType followType = FollowType.NORMAL;
    }

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        /**
         * 批量创建关注
         */
        BATCH_CREATE,

        /**
         * 批量删除关注
         */
        BATCH_DELETE,

        /**
         * 批量更新关注类型
         */
        BATCH_UPDATE_TYPE,

        /**
         * 批量更新关注状态
         */
        BATCH_UPDATE_STATUS,

        /**
         * 批量设置为特别关注
         */
        BATCH_SET_SPECIAL,

        /**
         * 批量设置为普通关注
         */
        BATCH_SET_NORMAL,

        /**
         * 批量屏蔽关注
         */
        BATCH_BLOCK,

        /**
         * 批量恢复关注
         */
        BATCH_UNBLOCK
    }

    // ===================== 静态工厂方法 =====================

    /**
     * 批量创建关注
     *
     * @param userRelations 用户关注关系列表
     * @return 批量操作请求
     */
    public static FollowBatchOperationRequest batchCreate(List<UserFollowRelation> userRelations) {
        FollowBatchOperationRequest request = new FollowBatchOperationRequest();
        request.setOperationType(OperationType.BATCH_CREATE);
        request.setUserRelations(userRelations);
        return request;
    }

    /**
     * 批量删除关注
     *
     * @param followIds 关注ID列表
     * @return 批量操作请求
     */
    public static FollowBatchOperationRequest batchDelete(List<Long> followIds) {
        FollowBatchOperationRequest request = new FollowBatchOperationRequest();
        request.setOperationType(OperationType.BATCH_DELETE);
        request.setFollowIds(followIds);
        return request;
    }

    /**
     * 批量设置为特别关注
     *
     * @param followIds 关注ID列表
     * @return 批量操作请求
     */
    public static FollowBatchOperationRequest batchSetSpecial(List<Long> followIds) {
        FollowBatchOperationRequest request = new FollowBatchOperationRequest();
        request.setOperationType(OperationType.BATCH_SET_SPECIAL);
        request.setFollowIds(followIds);
        request.setTargetFollowType(FollowType.SPECIAL);
        return request;
    }

    /**
     * 批量设置为普通关注
     *
     * @param followIds 关注ID列表
     * @return 批量操作请求
     */
    public static FollowBatchOperationRequest batchSetNormal(List<Long> followIds) {
        FollowBatchOperationRequest request = new FollowBatchOperationRequest();
        request.setOperationType(OperationType.BATCH_SET_NORMAL);
        request.setFollowIds(followIds);
        request.setTargetFollowType(FollowType.NORMAL);
        return request;
    }

    /**
     * 批量屏蔽关注
     *
     * @param followIds 关注ID列表
     * @return 批量操作请求
     */
    public static FollowBatchOperationRequest batchBlock(List<Long> followIds) {
        FollowBatchOperationRequest request = new FollowBatchOperationRequest();
        request.setOperationType(OperationType.BATCH_BLOCK);
        request.setFollowIds(followIds);
        request.setTargetStatus(FollowStatus.BLOCKED);
        return request;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否为创建操作
     *
     * @return true-创建操作，false-非创建操作
     */
    public boolean isCreateOperation() {
        return operationType == OperationType.BATCH_CREATE;
    }

    /**
     * 是否为删除操作
     *
     * @return true-删除操作，false-非删除操作
     */
    public boolean isDeleteOperation() {
        return operationType == OperationType.BATCH_DELETE;
    }

    /**
     * 是否为更新操作
     *
     * @return true-更新操作，false-非更新操作
     */
    public boolean isUpdateOperation() {
        return operationType == OperationType.BATCH_UPDATE_TYPE 
            || operationType == OperationType.BATCH_UPDATE_STATUS
            || operationType == OperationType.BATCH_SET_SPECIAL
            || operationType == OperationType.BATCH_SET_NORMAL
            || operationType == OperationType.BATCH_BLOCK
            || operationType == OperationType.BATCH_UNBLOCK;
    }

    /**
     * 验证请求有效性
     *
     * @return true-有效，false-无效
     */
    public boolean isValid() {
        if (operationType == null) {
            return false;
        }
        
        if (isCreateOperation()) {
            return userRelations != null && !userRelations.isEmpty();
        } else {
            return followIds != null && !followIds.isEmpty();
        }
    }
} 