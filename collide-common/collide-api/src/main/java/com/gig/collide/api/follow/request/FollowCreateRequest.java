package com.gig.collide.api.follow.request;

import com.gig.collide.api.follow.constant.FollowType;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 关注创建请求
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
@Schema(description = "关注创建请求")
public class FollowCreateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

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
     * 关注类型，默认为普通关注
     */
    @Schema(description = "关注类型，默认为普通关注")
    private FollowType followType = FollowType.NORMAL;

    // ===================== 静态工厂方法 =====================

    /**
     * 创建普通关注请求
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 关注请求
     */
    public static FollowCreateRequest createNormalFollow(Long followerUserId, Long followedUserId) {
        return new FollowCreateRequest(followerUserId, followedUserId, FollowType.NORMAL);
    }

    /**
     * 创建特别关注请求
     *
     * @param followerUserId 关注者用户ID
     * @param followedUserId 被关注者用户ID
     * @return 特别关注请求
     */
    public static FollowCreateRequest createSpecialFollow(Long followerUserId, Long followedUserId) {
        return new FollowCreateRequest(followerUserId, followedUserId, FollowType.SPECIAL);
    }

    // ===================== 业务方法 =====================

    /**
     * 验证请求有效性
     *
     * @return true-有效，false-无效
     */
    public boolean isValid() {
        return followerUserId != null 
            && followedUserId != null 
            && !followerUserId.equals(followedUserId);
    }

    /**
     * 是否为自己关注自己
     *
     * @return true-自己关注自己，false-正常关注
     */
    public boolean isSelfFollow() {
        return followerUserId != null && followerUserId.equals(followedUserId);
    }
} 