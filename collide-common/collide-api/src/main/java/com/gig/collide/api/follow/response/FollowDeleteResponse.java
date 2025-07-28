package com.gig.collide.api.follow.response;

import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 关注删除响应
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
@Schema(description = "关注删除响应")
public class FollowDeleteResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
     */
    @Schema(description = "关注ID")
    private Long followId;

    /**
     * 是否为物理删除
     */
    @Schema(description = "是否为物理删除")
    private Boolean isPhysicalDelete;

    /**
     * 删除类型描述
     */
    @Schema(description = "删除类型描述")
    private String deleteType;

    /**
     * 是否有实际删除（记录是否存在并被删除）
     */
    @Schema(description = "是否有实际删除")
    private Boolean hasActualDelete;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功删除响应（逻辑删除）
     *
     * @param followId 关注ID
     * @return 删除响应
     */
    public static FollowDeleteResponse successLogical(Long followId) {
        FollowDeleteResponse response = new FollowDeleteResponse();
        response.setFollowId(followId);
        response.setIsPhysicalDelete(false);
        response.setDeleteType("逻辑删除");
        response.setHasActualDelete(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功删除响应（物理删除）
     *
     * @param followId 关注ID
     * @return 删除响应
     */
    public static FollowDeleteResponse successPhysical(Long followId) {
        FollowDeleteResponse response = new FollowDeleteResponse();
        response.setFollowId(followId);
        response.setIsPhysicalDelete(true);
        response.setDeleteType("物理删除");
        response.setHasActualDelete(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 记录不存在响应
     *
     * @param followId 关注ID
     * @return 删除响应
     */
    public static FollowDeleteResponse notFound(Long followId) {
        FollowDeleteResponse response = new FollowDeleteResponse();
        response.setFollowId(followId);
        response.setHasActualDelete(false);
        response.setSuccess(true);
        response.setResponseMessage("关注记录不存在或已删除");
        return response;
    }

    /**
     * 已删除响应（记录已经是删除状态）
     *
     * @param followId 关注ID
     * @return 删除响应
     */
    public static FollowDeleteResponse alreadyDeleted(Long followId) {
        FollowDeleteResponse response = new FollowDeleteResponse();
        response.setFollowId(followId);
        response.setHasActualDelete(false);
        response.setSuccess(true);
        response.setResponseMessage("关注记录已经删除");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 删除响应
     */
    public static FollowDeleteResponse error(String message) {
        FollowDeleteResponse response = new FollowDeleteResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 权限不足响应
     *
     * @param followId 关注ID
     * @return 删除响应
     */
    public static FollowDeleteResponse permissionDenied(Long followId) {
        FollowDeleteResponse response = new FollowDeleteResponse();
        response.setFollowId(followId);
        response.setSuccess(false);
        response.setResponseMessage("权限不足，无法删除关注记录");
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否删除成功
     *
     * @return true-成功，false-失败
     */
    public boolean isDeleteSuccess() {
        return getSuccess() && followId != null;
    }

    /**
     * 是否为物理删除
     *
     * @return true-物理删除，false-逻辑删除
     */
    public boolean isPhysicalDeleted() {
        return Boolean.TRUE.equals(isPhysicalDelete);
    }

    /**
     * 是否有实际删除操作
     *
     * @return true-有实际删除，false-记录不存在或已删除
     */
    public boolean hasActualDeletion() {
        return Boolean.TRUE.equals(hasActualDelete);
    }

    /**
     * 获取删除类型（安全获取）
     *
     * @return 删除类型，null时返回"未知"
     */
    public String getDeleteTypeSafe() {
        return deleteType != null ? deleteType : "未知";
    }
} 