package com.gig.collide.api.like.request;

import com.gig.collide.api.like.constant.LikeStatusEnum;
import com.gig.collide.api.like.constant.LikeType;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 点赞查询请求
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
@Schema(description = "点赞查询请求")
public class LikeQueryRequest extends BaseRequest {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "目标对象ID")
    private Long targetId;
    
    @Schema(description = "点赞类型")
    private LikeType likeType;
    
    @Schema(description = "点赞状态")
    private LikeStatusEnum status;
    
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "20")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段：created_time/updated_time")
    private String sortField = "created_time";

    /**
     * 排序方向
     */
    @Schema(description = "排序方向：ASC/DESC")
    private String sortDirection = "DESC";
} 