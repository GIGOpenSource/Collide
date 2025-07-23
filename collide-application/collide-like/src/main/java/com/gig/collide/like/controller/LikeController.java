package com.gig.collide.like.controller;

import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.api.like.response.LikeQueryResponse;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.api.like.service.LikeFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点赞服务控制器
 * 
 * @author Collide
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
@Tag(name = "点赞服务", description = "点赞相关接口")
public class LikeController {
    
    private final LikeFacadeService likeFacadeService;
    
    @PostMapping("/action")
    @Operation(summary = "点赞/取消点赞/点踩", description = "执行点赞相关操作")
    public LikeResponse likeAction(@Valid @RequestBody LikeRequest likeRequest) {
        return likeFacadeService.likeAction(likeRequest);
    }
    
    @PostMapping("/batch-action")
    @Operation(summary = "批量点赞操作", description = "批量执行点赞操作")
    public LikeResponse batchLikeAction(@Valid @RequestBody List<LikeRequest> likeRequests) {
        return likeFacadeService.batchLikeAction(likeRequests);
    }
    
    @PostMapping("/query")
    @Operation(summary = "查询点赞记录", description = "分页查询点赞记录")
    public LikeQueryResponse queryLikes(@Valid @RequestBody LikeQueryRequest queryRequest) {
        return likeFacadeService.queryLikes(queryRequest);
    }
    
    @GetMapping("/check-status")
    @Operation(summary = "检查用户点赞状态", description = "检查用户对指定对象的点赞状态")
    public LikeResponse checkUserLikeStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "目标对象ID") @RequestParam Long targetId,
            @Parameter(description = "点赞类型") @RequestParam String likeType) {
        return likeFacadeService.checkUserLikeStatus(userId, targetId, likeType);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取点赞统计", description = "获取指定对象的点赞统计信息")
    public LikeResponse getLikeStatistics(
            @Parameter(description = "目标对象ID") @RequestParam Long targetId,
            @Parameter(description = "点赞类型") @RequestParam String likeType) {
        return likeFacadeService.getLikeStatistics(targetId, likeType);
    }
    
    @PostMapping("/user-history")
    @Operation(summary = "获取用户点赞历史", description = "分页获取用户的点赞历史记录")
    public LikeQueryResponse getUserLikeHistory(@Valid @RequestBody LikeQueryRequest queryRequest) {
        return likeFacadeService.getUserLikeHistory(queryRequest);
    }
} 