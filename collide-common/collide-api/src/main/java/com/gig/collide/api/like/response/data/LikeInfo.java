package com.gig.collide.api.like.response.data;

import com.gig.collide.api.like.constant.LikeStatus;
import com.gig.collide.api.like.constant.LikeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 点赞信息
 * 
 * @author Collide
 * @since 1.0.0
 */
@Data
@Schema(description = "点赞信息")
public class LikeInfo {
    
    @Schema(description = "点赞ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户昵称")
    private String userNickname;
    
    @Schema(description = "用户头像")
    private String userAvatar;
    
    @Schema(description = "目标对象ID")
    private Long targetId;
    
    @Schema(description = "点赞类型")
    private LikeType likeType;
    
    @Schema(description = "点赞状态")
    private LikeStatus status;
    
    @Schema(description = "点赞时间")
    private LocalDateTime likedTime;
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "设备信息")
    private String deviceInfo;
} 