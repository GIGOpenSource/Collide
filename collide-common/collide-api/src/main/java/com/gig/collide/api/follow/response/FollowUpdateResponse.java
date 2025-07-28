package com.gig.collide.api.follow.response;

import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 关注更新响应
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
@Schema(description = "关注更新响应")
public class FollowUpdateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 更新后的关注信息
     */
    @Schema(description = "更新后的关注信息")
    private FollowInfo followInfo;

    /**
     * 关注ID
     */
    @Schema(description = "关注ID")
    private Long followId;

    /**
     * 更新的字段数量
     */
    @Schema(description = "更新的字段数量")
    private Integer updatedFieldCount;

    /**
     * 是否有实际更新（数据是否发生变化）
     */
    @Schema(description = "是否有实际更新")
    private Boolean hasActualUpdate;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功更新响应
     *
     * @param followInfo 更新后的关注信息
     * @return 更新响应
     */
    public static FollowUpdateResponse success(FollowInfo followInfo) {
        FollowUpdateResponse response = new FollowUpdateResponse();
        response.setFollowInfo(followInfo);
        response.setFollowId(followInfo.getId());
        response.setHasActualUpdate(true);
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功更新响应（带更新字段数量）
     *
     * @param followInfo 更新后的关注信息
     * @param updatedFieldCount 更新的字段数量
     * @return 更新响应
     */
    public static FollowUpdateResponse success(FollowInfo followInfo, Integer updatedFieldCount) {
        FollowUpdateResponse response = new FollowUpdateResponse();
        response.setFollowInfo(followInfo);
        response.setFollowId(followInfo.getId());
        response.setUpdatedFieldCount(updatedFieldCount);
        response.setHasActualUpdate(updatedFieldCount != null && updatedFieldCount > 0);
        response.setSuccess(true);
        return response;
    }

    /**
     * 无更新响应（数据未发生变化）
     *
     * @param followId 关注ID
     * @return 更新响应
     */
    public static FollowUpdateResponse noChange(Long followId) {
        FollowUpdateResponse response = new FollowUpdateResponse();
        response.setFollowId(followId);
        response.setUpdatedFieldCount(0);
        response.setHasActualUpdate(false);
        response.setSuccess(true);
        response.setMessage("数据未发生变化");
        return response;
    }

    /**
     * 记录不存在响应
     *
     * @param followId 关注ID
     * @return 更新响应
     */
    public static FollowUpdateResponse notFound(Long followId) {
        FollowUpdateResponse response = new FollowUpdateResponse();
        response.setFollowId(followId);
        response.setSuccess(false);
        response.setMessage("关注记录不存在");
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 更新响应
     */
    public static FollowUpdateResponse error(String message) {
        FollowUpdateResponse response = new FollowUpdateResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // ===================== 业务方法 =====================

    /**
     * 是否更新成功
     *
     * @return true-成功，false-失败
     */
    public boolean isUpdateSuccess() {
        return isSuccess() && followId != null;
    }

    /**
     * 是否有实际数据变化
     *
     * @return true-有变化，false-无变化
     */
    public boolean hasDataChanged() {
        return Boolean.TRUE.equals(hasActualUpdate);
    }

    /**
     * 获取更新字段数量（安全获取）
     *
     * @return 更新字段数量，null时返回0
     */
    public int getUpdatedFieldCountSafe() {
        return updatedFieldCount != null ? updatedFieldCount : 0;
    }
} 