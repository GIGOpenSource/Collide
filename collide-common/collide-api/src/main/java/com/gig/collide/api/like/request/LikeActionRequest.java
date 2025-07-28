package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.ActionTypeEnum;
import com.gig.collide.api.like.constant.PlatformEnum;
import com.gig.collide.api.like.constant.TargetTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 点赞操作请求
 * 对应 t_like 表结构
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "点赞操作请求")
public class LikeActionRequest extends BaseRequest {

    /**
     * 点赞用户ID
     */
    @Schema(description = "点赞用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 目标对象ID（内容ID/评论ID等）
     */
    @Schema(description = "目标对象ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标对象ID不能为空")
    private Long targetId;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标类型不能为空")
    private TargetTypeEnum targetType;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型：1=点赞 0=取消 -1=点踩", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "操作类型不能为空")
    private ActionTypeEnum actionType;

    /**
     * 用户昵称（冗余字段）
     */
    @Schema(description = "用户昵称")
    private String userNickname;

    /**
     * 用户头像URL（冗余字段）
     */
    @Schema(description = "用户头像URL")
    private String userAvatar;

    /**
     * 目标对象标题（冗余字段）
     */
    @Schema(description = "目标对象标题")
    private String targetTitle;

    /**
     * 目标对象作者ID（冗余字段）
     */
    @Schema(description = "目标对象作者ID")
    private Long targetAuthorId;

    /**
     * 操作IP地址
     */
    @Schema(description = "操作IP地址")
    private String ipAddress;

    /**
     * 设备信息JSON
     */
    @Schema(description = "设备信息JSON")
    private String deviceInfo;

    /**
     * 平台
     */
    @Schema(description = "平台")
    private PlatformEnum platform;

    // ===================== 便捷构造器 =====================

    /**
     * 点赞操作
     */
    public static LikeActionRequest like(Long userId, Long targetId, TargetTypeEnum targetType) {
        LikeActionRequest request = new LikeActionRequest();
        request.setUserId(userId);
        request.setTargetId(targetId);
        request.setTargetType(targetType);
        request.setActionType(ActionTypeEnum.LIKE);
        return request;
    }

    /**
     * 点踩操作
     */
    public static LikeActionRequest dislike(Long userId, Long targetId, TargetTypeEnum targetType) {
        LikeActionRequest request = new LikeActionRequest();
        request.setUserId(userId);
        request.setTargetId(targetId);
        request.setTargetType(targetType);
        request.setActionType(ActionTypeEnum.DISLIKE);
        return request;
    }

    /**
     * 取消操作
     */
    public static LikeActionRequest cancel(Long userId, Long targetId, TargetTypeEnum targetType) {
        LikeActionRequest request = new LikeActionRequest();
        request.setUserId(userId);
        request.setTargetId(targetId);
        request.setTargetType(targetType);
        request.setActionType(ActionTypeEnum.CANCEL);
        return request;
    }
} 