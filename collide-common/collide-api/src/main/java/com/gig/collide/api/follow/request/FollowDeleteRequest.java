package com.gig.collide.api.follow.request;

import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 关注删除请求
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
@Schema(description = "关注删除请求")
public class FollowDeleteRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID（通过ID删除）
     */
    @Schema(description = "关注ID")
    private Long followId;

    /**
     * 关注者用户ID（通过关系删除）
     */
    @Schema(description = "关注者用户ID")
    private Long followerUserId;

    /**
     * 被关注者用户ID（通过关系删除）
     */
    @Schema(description = "被关注者用户ID")
    private Long followedUserId;

    /**
     * 是否物理删除，默认为逻辑删除
     */
    @Schema(description = "是否物理删除，默认为逻辑删除")
    private Boolean physicalDelete = false;

    /**
     * 删除原因（可选）
     */
    @Schema(description = "删除原因")
    private String reason;

    // ===================== 静态工厂方法 =====================

    /**
     * 通过关注ID删除（逻辑删除）
     *
     * @param followId 关注ID
     * @return 删除请求
     */
    public static FollowDeleteRequest byId(Long followId) {
        return new FollowDeleteRequest(followId, null, null, false, null);
    }

    /**
     * 通过关注ID删除（物理删除）
     *
     * @param followId 关注ID
     * @return 删除请求
     */
    public static FollowDeleteRequest byIdPhysical(Long followId) {
        return new FollowDeleteRequest(followId, null, null, true, null);
    }

    /**
     * 通过关注关系删除（逻辑删除）
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 删除请求
     */
    public static FollowDeleteRequest byRelation(Long followerUserId, Long followedUserId) {
        return new FollowDeleteRequest(null, followerUserId, followedUserId, false, null);
    }

    /**
     * 通过关注关系删除（物理删除）
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 删除请求
     */
    public static FollowDeleteRequest byRelationPhysical(Long followerUserId, Long followedUserId) {
        return new FollowDeleteRequest(null, followerUserId, followedUserId, true, null);
    }

    /**
     * 带原因的删除
     *
     * @param followId 关注ID
     * @param reason 删除原因
     * @return 删除请求
     */
    public static FollowDeleteRequest byIdWithReason(Long followId, String reason) {
        return new FollowDeleteRequest(followId, null, null, false, reason);
    }

    // ===================== 业务方法 =====================

    /**
     * 验证请求有效性
     *
     * @return true-有效，false-无效
     */
    public boolean isValid() {
        return (followId != null) || (followerUserId != null && followedUserId != null);
    }

    /**
     * 是否通过ID删除
     *
     * @return true-通过ID删除，false-通过关系删除
     */
    public boolean isDeleteById() {
        return followId != null;
    }

    /**
     * 是否通过关系删除
     *
     * @return true-通过关系删除，false-通过ID删除
     */
    public boolean isDeleteByRelation() {
        return followerUserId != null && followedUserId != null;
    }
} 