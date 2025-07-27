package com.gig.collide.like.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.api.like.constant.LikeType;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.like.domain.service.LikeDomainService;
import com.gig.collide.like.infrastructure.converter.LikeConverter;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 点赞服务控制器 - 标准化设计
 * 
 * @author Collide
 * @since 2.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
@Tag(name = "点赞服务", description = "点赞相关接口")
public class LikeController {
    
    private final LikeDomainService likeDomainService;
    private final LikeConverter likeConverter;
    
    @PostMapping("/action")
    @Operation(summary = "点赞操作", description = "执行点赞、取消点赞或点踩操作")
    public Result<LikeActionResult> likeAction(@RequestBody @Valid LikeRequest likeRequest) {
        try {
            Like like = likeDomainService.performLikeAction(likeRequest);
            LikeActionResult result = LikeActionResult.builder()
                .userId(like.getUserId())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .actionType(like.getActionType().toString())
                .actionTime(like.getUpdatedTime())
                .message("操作成功")
                .build();
            return Result.success(result);
        } catch (Exception e) {
            log.error("点赞操作失败: userId={}, targetId={}, likeType={}, action={}", 
                      likeRequest.getUserId(), likeRequest.getTargetId(), likeRequest.getLikeType(), likeRequest.getAction(), e);
            return Result.fail("LIKE_ACTION_FAILED", e.getMessage());
        }
    }

    @PostMapping("/batch")
    @Operation(summary = "批量点赞", description = "执行批量点赞操作") 
    public Result<BatchActionResult> batchLikeAction(@RequestBody @Valid BatchLikeRequest batchRequest) {
        if (batchRequest.getRequests() == null || batchRequest.getRequests().isEmpty()) {
            return Result.fail("INVALID_BATCH_REQUEST", "批量请求不能为空");
        }
        if (batchRequest.getRequests().size() > 100) {
            return Result.fail("BATCH_SIZE_EXCEEDED", "批量操作数量不能超过100");
        }
        
        try {
            var response = likeDomainService.batchLikeAction(batchRequest.getRequests());
            // 计算成功和失败数量
            int successCount = response.getSuccess() ? batchRequest.getRequests().size() : 0;
            int failureCount = batchRequest.getRequests().size() - successCount;
            
            BatchActionResult result = BatchActionResult.builder()
                .batchId(batchRequest.getBatchId())
                .successCount(successCount)
                .totalCount(batchRequest.getRequests().size())
                .failureCount(failureCount)
                .message(response.getResponseMessage())
                .build();
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量点赞操作失败: batchId={}", batchRequest.getBatchId(), e);
            return Result.fail("BATCH_ACTION_FAILED", e.getMessage());
        }
    }

    @GetMapping("/query")
    @Operation(summary = "查询点赞记录", description = "分页查询点赞记录")
    public PageResponse<Like> queryLikes(@Valid LikeQueryRequest request) {
        try {
            IPage<Like> result = likeDomainService.queryLikes(request);
            return PageResponse.of(result.getRecords(), (int) result.getTotal(), 
                request.getPageSize(), request.getCurrentPage());
        } catch (Exception e) {
            log.error("查询点赞记录失败: request={}", request, e);
            return PageResponse.fail("QUERY_LIKES_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(summary = "检查点赞状态", description = "检查用户对特定目标的点赞状态")
    public Result<UserLikeStatus> checkUserLikeStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        try {
            LikeType likeType = LikeType.fromCode(targetType);
            Like like = likeDomainService.getUserLikeStatus(userId, targetId, likeType);
            UserLikeStatus status = UserLikeStatus.builder()
                .userId(userId)
                .targetId(targetId)
                .targetType(targetType)
                .hasLiked(like != null && like.getActionType() == 1)
                .hasDisliked(like != null && like.getActionType() == -1)
                .currentAction(like != null ? like.getActionType().toString() : "NONE")
                .actionTime(like != null ? like.getCreatedTime() : null)
                .build();
            return Result.success(status);
        } catch (Exception e) {
            log.error("检查点赞状态失败: userId={}, targetId={}, targetType={}", userId, targetId, targetType, e);
            return Result.fail("CHECK_STATUS_FAILED", "检查状态失败");
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取点赞统计", description = "获取目标对象的点赞统计信息")
    public Result<LikeStatistics> getLikeStatistics(
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        try {
            LikeType likeType = LikeType.fromCode(targetType);
            var stats = likeDomainService.getLikeStatistics(targetId, likeType);
            LikeStatistics statistics = LikeStatistics.builder()
                .targetId(targetId)
                .targetType(targetType)
                .totalLikes(stats.getTotalLikeCount())
                .totalDislikes(stats.getTotalDislikeCount())
                .build();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取点赞统计失败: targetId={}, targetType={}", targetId, targetType, e);
            return Result.fail("GET_STATISTICS_FAILED", "获取统计失败");
        }
    }

    @GetMapping("/history")
    @Operation(summary = "获取用户点赞历史", description = "分页获取用户的点赞历史记录")
    public PageResponse<LikeRecord> getUserLikeHistory(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            // 构建查询请求对象
            LikeQueryRequest queryRequest = new LikeQueryRequest();
            queryRequest.setUserId(userId);
            queryRequest.setCurrentPage(page);
            queryRequest.setPageSize(size);
            
            IPage<Like> result = likeDomainService.queryLikes(queryRequest);
            List<LikeRecord> records = result.getRecords().stream()
                .map(like -> LikeRecord.builder()
                    .id(like.getId())
                    .userId(like.getUserId())
                    .targetId(like.getTargetId())
                    .targetType(like.getTargetType())
                    .actionType(like.getActionType().toString())
                    .userInfo(UserInfo.builder()
                        .userId(like.getUserId())
                        .nickname(like.getUserNickname())
                        .avatar(like.getUserAvatar())
                        .build())
                    .targetInfo(TargetInfo.builder()
                        .targetId(like.getTargetId())
                        .title(like.getTargetTitle())
                        .authorId(like.getTargetAuthorId())
                        .build())
                    .platform(like.getPlatform())
                    .ipAddress(like.getIpAddress())
                    .createdTime(like.getCreatedTime())
                    .updatedTime(like.getUpdatedTime())
                    .build())
                .collect(Collectors.toList());
            return PageResponse.of(records, (int) result.getTotal(), size, page);
        } catch (Exception e) {
            log.error("获取用户点赞历史失败: userId={}", userId, e);
            return PageResponse.fail("GET_HISTORY_FAILED", "获取历史记录失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/status")
    @Operation(summary = "批量检查点赞状态", description = "批量检查用户对多个目标的点赞状态")
    public Result<BatchStatusResult> batchCheckUserLikeStatus(@RequestBody @Valid BatchStatusRequest request) {
        try {
            Map<String, Object> statusMap = request.getTargets().stream()
                .collect(Collectors.toMap(
                    target -> target.getTargetId() + ":" + target.getTargetType(),
                    target -> {
                        LikeType likeType = LikeType.fromCode(target.getTargetType());
                        Like like = likeDomainService.getUserLikeStatus(request.getUserId(), target.getTargetId(), likeType);
                        return UserLikeStatus.builder()
                            .userId(request.getUserId())
                            .targetId(target.getTargetId())
                            .targetType(target.getTargetType())
                            .hasLiked(like != null && like.getActionType() == 1)
                            .hasDisliked(like != null && like.getActionType() == -1)
                            .currentAction(like != null ? like.getActionType().toString() : "NONE")
                            .actionTime(like != null ? like.getCreatedTime() : null)
                            .build();
                    }
                ));
            
            BatchStatusResult result = BatchStatusResult.builder()
                .userId(request.getUserId())
                .results(statusMap)
                .build();
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量检查点赞状态失败: userId={}", request.getUserId(), e);
            return Result.fail("BATCH_CHECK_FAILED", "批量检查失败");
        }
    }
    
    // ========== 私有方法 ==========
    
    private LikeRecord convertToLikeRecord(Like like) {
        return LikeRecord.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .actionType(like.getActionTypeDescription())
                .userInfo(UserInfo.builder()
                        .userId(like.getUserId())
                        .nickname(like.getUserNickname())
                        .avatar(like.getUserAvatar())
                        .build())
                .targetInfo(TargetInfo.builder()
                        .targetId(like.getTargetId())
                        .title(like.getTargetTitle())
                        .authorId(like.getTargetAuthorId())
                        .build())
                .platform(like.getPlatform())
                .ipAddress(like.getIpAddress())
                .createdTime(like.getCreatedTime())
                .updatedTime(like.getUpdatedTime())
                .build();
    }
    
    // ========== 内部类定义 ==========
    
    @lombok.Data
    @lombok.Builder
    public static class LikeActionResult {
        private Long userId;
        private Long targetId;
        private String targetType;
        private String actionType;
        private java.time.LocalDateTime actionTime;
        private String message;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class BatchActionResult {
        private String batchId;
        private Integer successCount;
        private Integer failureCount;
        private Integer totalCount;
        private String message;
    }
    
    @lombok.Data
    public static class BatchLikeRequest {
        private String batchId;
        private List<LikeRequest> requests;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class UserLikeStatus {
        private Long userId;
        private Long targetId;
        private String targetType;
        private Boolean hasLiked;
        private Boolean hasDisliked;
        private String currentAction;
        private java.time.LocalDateTime actionTime;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class LikeStatistics {
        private Long targetId;
        private String targetType;
        private Long totalLikes;
        private Long totalDislikes;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class LikeRecord {
        private Long id;
        private Long userId;
        private Long targetId;
        private String targetType;
        private String actionType;
        private UserInfo userInfo;
        private TargetInfo targetInfo;
        private String platform;
        private String ipAddress;
        private java.time.LocalDateTime createdTime;
        private java.time.LocalDateTime updatedTime;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String avatar;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class TargetInfo {
        private Long targetId;
        private String title;
        private Long authorId;
    }
    
    @lombok.Data
    public static class BatchStatusRequest {
        private Long userId;
        private List<TargetRequest> targets;
        
        @lombok.Data
        public static class TargetRequest {
            private Long targetId;
            private String targetType;
        }
    }
    
    @lombok.Data
    @lombok.Builder
    public static class BatchStatusResult {
        private Long userId;
        private Map<String, Object> results;
    }
} 