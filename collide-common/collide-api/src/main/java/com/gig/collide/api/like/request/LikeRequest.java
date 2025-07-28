package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.LikeAction;
import com.gig.collide.api.like.constant.LikeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 点赞请求（兼容旧接口）
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
@Schema(description = "点赞请求")
public class LikeRequest {
    
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @Schema(description = "目标对象ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标对象ID不能为空")
    private Long targetId;
    
    @Schema(description = "点赞类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "点赞类型不能为空")
    private LikeType likeType;
    
    @Schema(description = "点赞动作", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "点赞动作不能为空")
    private LikeAction action;
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "设备信息")
    private String deviceInfo;
} 