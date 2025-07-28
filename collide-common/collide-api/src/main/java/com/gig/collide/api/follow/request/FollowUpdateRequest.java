package com.gig.collide.api.follow.request;

import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 关注更新请求
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
@Schema(description = "关注更新请求")
public class FollowUpdateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
     */
    @NotNull(message = "关注ID不能为空")
    @Schema(description = "关注ID", required = true)
    private Long followId;

    /**
     * 关注类型（可选更新）
     */
    @Schema(description = "关注类型")
    private FollowType followType;

    /**
     * 关注状态（可选更新）
     */
    @Schema(description = "关注状态")
    private FollowStatus status;

    // ===================== 静态工厂方法 =====================

    /**
     * 更新关注类型
     *
     * @param followId 关注ID
     * @param followType 新的关注类型
     * @return 更新请求
     */
    public static FollowUpdateRequest updateType(Long followId, FollowType followType) {
        FollowUpdateRequest request = new FollowUpdateRequest();
        request.setFollowId(followId);
        request.setFollowType(followType);
        return request;
    }

    /**
     * 更新关注状态
     *
     * @param followId 关注ID
     * @param status 新的关注状态
     * @return 更新请求
     */
    public static FollowUpdateRequest updateStatus(Long followId, FollowStatus status) {
        FollowUpdateRequest request = new FollowUpdateRequest();
        request.setFollowId(followId);
        request.setStatus(status);
        return request;
    }

    /**
     * 设置为特别关注
     *
     * @param followId 关注ID
     * @return 更新请求
     */
    public static FollowUpdateRequest setSpecial(Long followId) {
        return updateType(followId, FollowType.SPECIAL);
    }

    /**
     * 设置为普通关注
     *
     * @param followId 关注ID
     * @return 更新请求
     */
    public static FollowUpdateRequest setNormal(Long followId) {
        return updateType(followId, FollowType.NORMAL);
    }

    /**
     * 屏蔽关注
     *
     * @param followId 关注ID
     * @return 更新请求
     */
    public static FollowUpdateRequest block(Long followId) {
        return updateStatus(followId, FollowStatus.BLOCKED);
    }

    /**
     * 恢复正常关注
     *
     * @param followId 关注ID
     * @return 更新请求
     */
    public static FollowUpdateRequest unblock(Long followId) {
        return updateStatus(followId, FollowStatus.NORMAL);
    }

    // ===================== 业务方法 =====================

    /**
     * 是否有更新内容
     *
     * @return true-有更新，false-无更新
     */
    public boolean hasUpdates() {
        return followType != null || status != null;
    }
} 