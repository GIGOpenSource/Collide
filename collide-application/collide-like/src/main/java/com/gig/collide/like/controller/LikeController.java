package com.gig.collide.like.controller;

import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.like.request.*;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 点赞REST控制器 - MySQL 8.0 优化版
 * 基于新的LikeFacadeService接口，提供完整的点赞HTTP API
 * 
 * 接口特性：
 * - 与LikeFacadeService接口完全对应
 * - 支持用户、目标对象、作者三个维度的查询
 * - 支持时间范围查询和批量操作
 * - 完整的缓存策略和错误处理
 * - 统一的REST API设计规范
 * 
 * @author GIG Team
 * @version 2.0.0 (MySQL 8.0 优化版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
@Tag(name = "点赞管理", description = "点赞相关的API接口 - MySQL 8.0 优化版")
public class LikeController {

    private final LikeFacadeService likeFacadeService;

    // =================== 点赞核心功能 ===================

    @PostMapping("/add")
    @Operation(summary = "添加点赞 💡 缓存优化", description = "用户对内容、评论或动态进行点赞")
    public Result<LikeResponse> addLike(@RequestBody LikeRequest request) {
        try {
            log.info("REST添加点赞请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 调用门面服务
            Result<LikeResponse> result = likeFacadeService.addLike(request);
            
            if (result.getSuccess()) {
                log.info("REST点赞添加成功: 用户={}, 目标={}", request.getUserId(), request.getTargetId());
            }
            return result;
        } catch (Exception e) {
            log.error("REST添加点赞失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_ADD_ERROR", "添加点赞失败: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消点赞 💡 缓存优化", description = "取消用户的点赞")
    public Result<Void> cancelLike(@RequestBody LikeCancelRequest request) {
        try {
            log.info("REST取消点赞请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 调用门面服务
            Result<Void> result = likeFacadeService.cancelLike(request);
            
            if (result.getSuccess()) {
                log.info("REST点赞取消成功: 用户={}, 目标={}", request.getUserId(), request.getTargetId());
            }
            return result;
        } catch (Exception e) {
            log.error("REST取消点赞失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_CANCEL_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @PostMapping("/toggle")
    @Operation(summary = "切换点赞状态 💡 缓存优化", description = "如果已点赞则取消，如果未点赞则添加")
    public Result<LikeResponse> toggleLike(@RequestBody LikeToggleRequest request) {
        try {
            log.info("REST切换点赞状态请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 调用门面服务
            Result<LikeResponse> result = likeFacadeService.toggleLike(request);
            
            if (result.getSuccess()) {
                log.info("REST点赞状态切换成功: 用户={}, 目标={}", request.getUserId(), request.getTargetId());
            }
            return result;
        } catch (Exception e) {
            log.error("REST切换点赞状态失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_TOGGLE_ERROR", "切换点赞状态失败: " + e.getMessage());
        }
    }

    // =================== 点赞查询功能 ===================

    @PostMapping("/check")
    @Operation(summary = "检查点赞状态 💡 缓存优化", description = "检查用户是否已对目标对象点赞")
    public Result<Boolean> checkLikeStatus(@RequestBody LikeStatusCheckRequest request) {
        try {
            log.debug("REST检查点赞状态: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());
            
            // 调用门面服务
            Result<Boolean> result = likeFacadeService.checkLikeStatus(
                    request.getUserId(), request.getLikeType(), request.getTargetId());
            
            log.debug("REST点赞状态检查完成: 用户={}, 目标={}, 已点赞={}", 
                    request.getUserId(), request.getTargetId(), 
                    result.getSuccess() ? result.getData() : "检查失败");
            return result;
        } catch (Exception e) {
            log.error("REST检查点赞状态失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_CHECK_ERROR", "检查点赞状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/user/likes")
    @Operation(summary = "分页查询用户点赞记录 💡 缓存优化", description = "查询指定用户的点赞记录列表")
    public Result<PageResponse<LikeResponse>> findUserLikes(@RequestParam Long userId,
                                                           @RequestParam(required = false) String likeType,
                                                           @RequestParam(required = false) String status,
                                                           @RequestParam(defaultValue = "1") Integer currentPage,
                                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            log.info("REST分页查询用户点赞记录: userId={}, likeType={}, status={}, 页码={}, 页大小={}", 
                    userId, likeType, status, currentPage, pageSize);

            // 调用门面服务
            Result<PageResponse<LikeResponse>> result = likeFacadeService.findUserLikes(userId, likeType, status, currentPage, pageSize);
            
            if (result.getSuccess()) {
                log.info("用户点赞记录查询成功: userId={}, 总数={}, 当前页={}", 
                        userId, result.getData().getTotal(), result.getData().getCurrentPage());
            }
            return result;
        } catch (Exception e) {
            log.error("分页查询用户点赞记录失败: userId={}, 页码={}, 页大小={}", userId, currentPage, pageSize, e);
            return Result.error("USER_LIKES_QUERY_ERROR", "查询用户点赞记录失败: " + e.getMessage());
        }
    }

    @PostMapping("/target/likes")
    @Operation(summary = "分页查询目标对象点赞记录 💡 缓存优化", description = "查询指定目标对象的点赞记录列表")
    public Result<PageResponse<LikeResponse>> findTargetLikes(@RequestParam Long targetId,
                                                             @RequestParam String likeType,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(defaultValue = "1") Integer currentPage,
                                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            log.info("REST分页查询目标对象点赞记录: targetId={}, likeType={}, status={}, 页码={}, 页大小={}", 
                    targetId, likeType, status, currentPage, pageSize);

            // 调用门面服务
            Result<PageResponse<LikeResponse>> result = likeFacadeService.findTargetLikes(targetId, likeType, status, currentPage, pageSize);
            
            if (result.getSuccess()) {
                log.info("目标对象点赞记录查询成功: targetId={}, 总数={}, 当前页={}", 
                        targetId, result.getData().getTotal(), result.getData().getCurrentPage());
            }
            return result;
        } catch (Exception e) {
            log.error("分页查询目标对象点赞记录失败: targetId={}, 页码={}, 页大小={}", targetId, currentPage, pageSize, e);
            return Result.error("TARGET_LIKES_QUERY_ERROR", "查询目标对象点赞记录失败: " + e.getMessage());
        }
    }

    @PostMapping("/author/likes")
    @Operation(summary = "分页查询作者作品点赞记录 💡 缓存优化", description = "查询指定作者作品的点赞记录列表")
    public Result<PageResponse<LikeResponse>> findAuthorLikes(@RequestParam Long targetAuthorId,
                                                             @RequestParam(required = false) String likeType,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(defaultValue = "1") Integer currentPage,
                                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            log.info("REST分页查询作者作品点赞记录: targetAuthorId={}, likeType={}, status={}, 页码={}, 页大小={}", 
                    targetAuthorId, likeType, status, currentPage, pageSize);

            // 调用门面服务
            Result<PageResponse<LikeResponse>> result = likeFacadeService.findAuthorLikes(targetAuthorId, likeType, status, currentPage, pageSize);
            
            if (result.getSuccess()) {
                log.info("作者作品点赞记录查询成功: targetAuthorId={}, 总数={}, 当前页={}", 
                        targetAuthorId, result.getData().getTotal(), result.getData().getCurrentPage());
            }
            return result;
        } catch (Exception e) {
            log.error("分页查询作者作品点赞记录失败: targetAuthorId={}, 页码={}, 页大小={}", targetAuthorId, currentPage, pageSize, e);
            return Result.error("AUTHOR_LIKES_QUERY_ERROR", "查询作者作品点赞记录失败: " + e.getMessage());
        }
    }

    // =================== 点赞统计功能 ===================

    @GetMapping("/target/{targetId}/count")
    @Operation(summary = "统计目标对象点赞数量 💡 缓存优化", description = "统计指定目标对象的点赞数量")
    public Result<Long> countTargetLikes(@PathVariable Long targetId,
                                        @RequestParam String likeType) {
        try {
            log.debug("REST统计目标对象点赞数量: targetId={}, likeType={}", targetId, likeType);
            
            // 调用门面服务
            Result<Long> result = likeFacadeService.countTargetLikes(targetId, likeType);
            
            log.debug("目标对象点赞数量统计完成: targetId={}, count={}", targetId, 
                    result.getSuccess() ? result.getData() : "失败");
            return result;
        } catch (Exception e) {
            log.error("统计目标对象点赞数量失败: targetId={}, likeType={}", targetId, likeType, e);
            return Result.error("TARGET_LIKE_COUNT_ERROR", "统计目标对象点赞数量失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "统计用户点赞数量 💡 缓存优化", description = "统计指定用户的点赞数量")
    public Result<Long> countUserLikes(@PathVariable Long userId,
                                      @RequestParam(required = false) String likeType) {
        try {
            log.debug("REST统计用户点赞数量: userId={}, likeType={}", userId, likeType);
            
            // 调用门面服务
            Result<Long> result = likeFacadeService.countUserLikes(userId, likeType);
            
            log.debug("用户点赞数量统计完成: userId={}, count={}", userId, 
                    result.getSuccess() ? result.getData() : "失败");
            return result;
        } catch (Exception e) {
            log.error("统计用户点赞数量失败: userId={}, likeType={}", userId, likeType, e);
            return Result.error("USER_LIKE_COUNT_ERROR", "统计用户点赞数量失败: " + e.getMessage());
        }
    }

    @GetMapping("/author/{targetAuthorId}/count")
    @Operation(summary = "统计作者作品被点赞数量 💡 缓存优化", description = "统计指定作者作品的被点赞数量")
    public Result<Long> countAuthorLikes(@PathVariable Long targetAuthorId,
                                        @RequestParam(required = false) String likeType) {
        try {
            log.debug("REST统计作者作品被点赞数量: targetAuthorId={}, likeType={}", targetAuthorId, likeType);
            
            // 调用门面服务
            Result<Long> result = likeFacadeService.countAuthorLikes(targetAuthorId, likeType);
            
            log.debug("作者作品被点赞数量统计完成: targetAuthorId={}, count={}", targetAuthorId, 
                    result.getSuccess() ? result.getData() : "失败");
            return result;
        } catch (Exception e) {
            log.error("统计作者作品被点赞数量失败: targetAuthorId={}, likeType={}", targetAuthorId, likeType, e);
            return Result.error("AUTHOR_LIKE_COUNT_ERROR", "统计作者作品被点赞数量失败: " + e.getMessage());
        }
    }

    // =================== 点赞时间查询功能 ===================

    @GetMapping("/time-range")
    @Operation(summary = "查询时间范围内的点赞记录 💡 缓存优化", description = "查询指定时间范围内的点赞记录")
    public Result<List<LikeResponse>> findByTimeRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                                     @RequestParam(required = false) String likeType,
                                                     @RequestParam(required = false) String status) {
        try {
            log.info("REST查询时间范围内的点赞记录: startTime={}, endTime={}, likeType={}, status={}", 
                    startTime, endTime, likeType, status);
            
            // 调用门面服务
            Result<List<LikeResponse>> result = likeFacadeService.findByTimeRange(startTime, endTime, likeType, status);
            
            if (result.getSuccess()) {
                log.info("时间范围点赞记录查询成功: startTime={}, endTime={}, 数量={}", 
                        startTime, endTime, result.getData().size());
            }
            return result;
        } catch (Exception e) {
            log.error("查询时间范围内的点赞记录失败: startTime={}, endTime={}", startTime, endTime, e);
            return Result.error("TIME_RANGE_QUERY_ERROR", "查询时间范围内的点赞记录失败: " + e.getMessage());
        }
    }

    // =================== 点赞批量功能 ===================

    @PostMapping("/batch/check")
    @Operation(summary = "批量检查点赞状态 💡 缓存优化", description = "批量检查用户对多个目标对象的点赞状态")
    public Result<Map<Long, Boolean>> batchCheckLikeStatus(@RequestBody LikeBatchCheckRequest request) {
        try {
            log.info("REST批量检查点赞状态: 用户={}, 类型={}, 目标数量={}", 
                    request.getUserId(), request.getLikeType(), 
                    request.getTargetIds() != null ? request.getTargetIds().size() : 0);
            
            // 调用门面服务
            Result<Map<Long, Boolean>> result = likeFacadeService.batchCheckLikeStatus(
                    request.getUserId(), request.getLikeType(), request.getTargetIds()
            );
            
            if (result.getSuccess()) {
                log.info("REST批量点赞状态检查成功: 用户={}, 检查数量={}", 
                        request.getUserId(), request.getTargetIds() != null ? request.getTargetIds().size() : 0);
            }
            return result;
        } catch (Exception e) {
            log.error("REST批量检查点赞状态失败: 用户={}, 类型={}", request.getUserId(), request.getLikeType(), e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查点赞状态失败: " + e.getMessage());
        }
    }

}