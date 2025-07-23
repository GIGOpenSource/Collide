package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.LikeStatus;
import com.gig.collide.api.like.constant.LikeType;
import com.gig.collide.base.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 点赞查询请求
 * 
 * @author Collide
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "点赞查询请求")
public class LikeQueryRequest extends PageRequest {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "目标对象ID")
    private Long targetId;
    
    @Schema(description = "点赞类型")
    private LikeType likeType;
    
    @Schema(description = "点赞状态")
    private LikeStatus status;
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;
} 