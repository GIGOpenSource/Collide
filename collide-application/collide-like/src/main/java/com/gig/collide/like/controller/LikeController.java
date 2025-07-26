package com.gig.collide.like.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeRequest;
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
    @Operation(summary = "点赞/取消点赞/点踩", description = "执行点赞相关操作，支持幂等性")
    public Result<LikeActionResult> likeAction(@Valid @RequestBody LikeRequest likeRequest) {
        try {
            Like like = likeDomainService.performLikeAction(likeRequest);
            LikeActionResult result = LikeActionResult.builder()
                    .userId(like.getUserId())
                    .targetId(like.getTargetId())
                    .targetType(like.getTargetType())
                    .action(like.getActionTypeDescription())
                    .timestamp(like.getUpdatedTime())
                    .build();
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("点赞操作失败", e);
            return Result.fail("LIKE_ACTION_ERROR", "点赞操作失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/batch-action")
    @Operation(summary = "批量点赞操作", description = "批量执行点赞操作，全局事务保证")
    public Result<BatchActionResult> batchLikeAction(@Valid @RequestBody BatchLikeRequest batchRequest) {
        try {
            // 参数验证
            if (batchRequest.getRequests() == null || batchRequest.getRequests().isEmpty()) {
                return Result.fail("PARAM_ERROR", "批量操作请求列表不能为空");
            }
            if (batchRequest.getRequests().size() > 100) {
                return Result.fail("PARAM_ERROR", "单次批量操作不能超过100个");
            }
            
            // 执行批量操作
            var response = likeDomainService.batchLikeAction(batchRequest.getRequests());
            
            BatchActionResult result = BatchActionResult.builder()
                    .batchId(batchRequest.getBatchId())
                    .totalCount(batchRequest.getRequests().size())
                    .message("批量操作完成")
                    .build();
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量点赞操作失败", e);
            return Result.fail("BATCH_LIKE_ERROR", "批量点赞操作失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/query")
    @Operation(summary = "查询点赞记录", description = "分页查询点赞记录，包含冗余信息")
    public PageResponse<LikeRecord> queryLikes(@Valid @RequestBody LikeQueryRequest queryRequest) {
        try {
            IPage<Like> page = likeDomainService.queryLikes(queryRequest);
            List<LikeRecord> records = page.getRecords().stream()
                    .map(this::convertToLikeRecord)
                    .collect(Collectors.toList());
            
            return PageResponse.of(records, (int) page.getTotal(), 
                    queryRequest.getPageSize(), queryRequest.getCurrentPage());
        } catch (Exception e) {
            log.error("查询点赞记录失败", e);
            return PageResponse.fail("QUERY_ERROR", "查询失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/check-status")
    @Operation(summary = "检查用户点赞状态", description = "快速检查用户对指定对象的点赞状态")
    public SingleResponse<UserLikeStatus> checkUserLikeStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "目标对象ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        try {
            Like like = likeDomainService.getUserLikeStatus(userId, targetId, 
                    com.gig.collide.api.like.constant.LikeType.valueOf(targetType));
            
            UserLikeStatus status = UserLikeStatus.builder()
                    .userId(userId)
                    .targetId(targetId)
                    .targetType(targetType)
                    .hasLiked(like != null && like.isLiked())
                    .hasDisliked(like != null && like.isDisliked())
                    .currentAction(like != null ? like.getActionTypeDescription() : null)
                    .actionTime(like != null ? like.getCreatedTime() : null)
                    .build();
            
            return SingleResponse.of(status);
        } catch (Exception e) {
            log.error("检查用户点赞状态失败", e);
            return SingleResponse.fail("CHECK_STATUS_ERROR", "检查状态失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取点赞统计", description = "获取指定对象的点赞统计信息，基于冗余字段")
    public SingleResponse<LikeStatistics> getLikeStatistics(
            @Parameter(description = "目标对象ID") @RequestParam Long targetId,
            @Parameter(description = "目标类型") @RequestParam String targetType) {
        try {
            var statistics = likeDomainService.getLikeStatistics(targetId, 
                    com.gig.collide.api.like.constant.LikeType.valueOf(targetType));
            
            LikeStatistics result = LikeStatistics.builder()
                    .targetId(statistics.getTargetId())
                    .targetType(statistics.getTargetType())
                    .totalLikes(statistics.getTotalLikeCount())
                    .totalDislikes(statistics.getTotalDislikeCount())
                    .build();
            
            return SingleResponse.of(result);
        } catch (Exception e) {
            log.error("获取点赞统计失败", e);
            return SingleResponse.fail("STATISTICS_ERROR", "获取统计失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/user-history")
    @Operation(summary = "获取用户点赞历史", description = "分页获取用户的点赞历史记录，包含冗余目标信息")
    public PageResponse<LikeRecord> getUserLikeHistory(@Valid @RequestBody LikeQueryRequest queryRequest) {
        try {
            IPage<Like> page = likeDomainService.getUserLikeHistory(queryRequest);
            List<LikeRecord> records = page.getRecords().stream()
                    .map(this::convertToLikeRecord)
                    .collect(Collectors.toList());
            
            return PageResponse.of(records, (int) page.getTotal(), 
                    queryRequest.getPageSize(), queryRequest.getCurrentPage());
        } catch (Exception e) {
            log.error("获取用户点赞历史失败", e);
            return PageResponse.fail("USER_HISTORY_ERROR", "获取历史失败：" + e.getMessage());
        }
    }
    
    @PostMapping("/batch-status")
    @Operation(summary = "批量查询点赞状态", description = "批量查询用户对多个对象的点赞状态")
    public SingleResponse<BatchStatusResult> getBatchUserLikeStatus(@Valid @RequestBody BatchStatusRequest request) {
        try {
            Map<String, Object> results = request.getTargets().stream()
                    .collect(Collectors.toMap(
                            target -> target.getTargetId() + ":" + target.getTargetType(),
                            target -> {
                                Like like = likeDomainService.getUserLikeStatus(request.getUserId(), 
                                        target.getTargetId(), 
                                        com.gig.collide.api.like.constant.LikeType.valueOf(target.getTargetType()));
                                return UserLikeStatus.builder()
                                        .userId(request.getUserId())
                                        .targetId(target.getTargetId())
                                        .targetType(target.getTargetType())
                                        .hasLiked(like != null && like.isLiked())
                                        .hasDisliked(like != null && like.isDisliked())
                                        .currentAction(like != null ? like.getActionTypeDescription() : null)
                                        .actionTime(like != null ? like.getCreatedTime() : null)
                                        .build();
                            }
                    ));
            
            BatchStatusResult result = BatchStatusResult.builder()
                    .userId(request.getUserId())
                    .results(results)
                    .build();
            
            return SingleResponse.of(result);
        } catch (Exception e) {
            log.error("批量查询点赞状态失败", e);
            return SingleResponse.fail("BATCH_STATUS_ERROR", "批量查询失败：" + e.getMessage());
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
    
    // ========== 内部数据类 ==========
    
    @lombok.Data
    @lombok.Builder
    public static class LikeActionResult {
        private Long userId;
        private Long targetId;
        private String targetType;
        private String action;
        private java.time.LocalDateTime timestamp;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class BatchActionResult {
        private String batchId;
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