package com.gig.collide.api.user.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 用户邀请关系创建请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserInviteRelationCreateRequest extends BaseRequest {

    /**
     * 被邀请用户ID（必填）
     */
    @NotNull(message = "被邀请用户ID不能为空")
    private Long userId;

    /**
     * 邀请人ID（必填）
     */
    @NotNull(message = "邀请人ID不能为空")
    private Long inviterId;

    /**
     * 使用的邀请码（必填）
     */
    @NotBlank(message = "邀请码不能为空")
    @Size(max = 20, message = "邀请码长度不能超过20个字符")
    private String inviteCode;

    /**
     * 邀请层级（必填）
     */
    @NotNull(message = "邀请层级不能为空")
    @Positive(message = "邀请层级必须大于0")
    private Integer inviteLevel;

    // ===================== 便捷构造器 =====================

    /**
     * 创建直接邀请关系
     */
    public static UserInviteRelationCreateRequest createDirectInvite(Long userId, Long inviterId, String inviteCode) {
        return new UserInviteRelationCreateRequest(userId, inviterId, inviteCode, 1);
    }

    /**
     * 创建多级邀请关系
     */
    public static UserInviteRelationCreateRequest createMultiLevelInvite(Long userId, Long inviterId, String inviteCode, Integer inviteLevel) {
        return new UserInviteRelationCreateRequest(userId, inviterId, inviteCode, inviteLevel);
    }
} 