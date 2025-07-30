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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 点赞REST控制器 - 缓存增强版
 * 对齐order模块设计风格，通过门面服务提供HTTP接口
 * 包含缓存功能、统一响应格式、错误处理
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
@Tag(name = "点赞管理", description = "点赞相关的API接口 - 缓存增强版")
public class LikeController {

    @Autowired
    private LikeFacadeService likeFacadeService;

    // =================== 点赞核心功能 ===================

    @PostMapping("/add")
    @Operation(summary = "添加点赞 💡 缓存优化", description = "用户对内容、评论或动态进行点赞")
    public Result<LikeResponse> addLike(@RequestBody LikeRequest request) {
        try {
            log.info("添加点赞请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 调用门面服务
            Result<LikeResponse> result = likeFacadeService.addLike(request);
            
            if (result.getSuccess()) {
                log.info("点赞添加成功: 用户={}, 目标={}", request.getUserId(), request.getTargetId());
            }
            return result;
        } catch (Exception e) {
            log.error("添加点赞失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_ADD_ERROR", "添加点赞失败: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消点赞 💡 缓存优化", description = "取消用户的点赞")
    public Result<Void> cancelLike(@RequestBody LikeCancelRequest request) {
        try {
            log.info("取消点赞请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 调用门面服务
            Result<Void> result = likeFacadeService.cancelLike(request);
            
            if (result.getSuccess()) {
                log.info("点赞取消成功: 用户={}, 目标={}", request.getUserId(), request.getTargetId());
            }
            return result;
        } catch (Exception e) {
            log.error("取消点赞失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_CANCEL_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @PostMapping("/toggle")
    @Operation(summary = "切换点赞状态 💡 缓存优化", description = "如果已点赞则取消，如果未点赞则添加")
    public Result<LikeResponse> toggleLike(@RequestBody LikeToggleRequest request) {
        try {
            log.info("切换点赞状态请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 调用门面服务
            Result<LikeResponse> result = likeFacadeService.toggleLike(request);
            
            if (result.getSuccess()) {
                log.info("点赞状态切换成功: 用户={}, 目标={}", request.getUserId(), request.getTargetId());
            }
            return result;
        } catch (Exception e) {
            log.error("切换点赞状态失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_TOGGLE_ERROR", "切换点赞状态失败: " + e.getMessage());
        }
    }

    // =================== 点赞查询功能 ===================

    @PostMapping("/check")
    @Operation(summary = "检查点赞状态 💡 缓存优化", description = "检查用户是否已对目标对象点赞")
    public Result<Boolean> checkLikeStatus(@RequestBody LikeStatusCheckRequest request) {
        try {
            log.debug("检查点赞状态: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());
            
            // 调用门面服务
            Result<Boolean> result = likeFacadeService.checkLikeStatus(
                    request.getUserId(), request.getLikeType(), request.getTargetId());
            
            return result;
        } catch (Exception e) {
            log.error("检查点赞状态失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_CHECK_ERROR", "检查点赞状态失败: " + e.getMessage());
        }
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询点赞记录 💡 缓存优化", description = "根据条件分页查询点赞记录")
    public Result<PageResponse<LikeResponse>> queryLikes(@RequestBody LikeQueryRequest request) {
        try {
            log.info("分页查询点赞记录: 页码={}, 页大小={}, 用户={}, 类型={}", 
                    request.getPageNum(), request.getPageSize(), request.getUserId(), request.getLikeType());

            // 调用门面服务
            Result<PageResponse<LikeResponse>> result = likeFacadeService.queryLikes(request);
            
            if (result.getSuccess()) {
                log.info("点赞记录查询成功: 总数={}, 当前页={}", 
                        result.getData().getTotal(), result.getData().getCurrentPage());
            }
            return result;
        } catch (Exception e) {
            log.error("分页查询点赞记录失败: 页码={}, 页大小={}", request.getPageNum(), request.getPageSize(), e);
            return Result.error("LIKE_QUERY_ERROR", "查询点赞记录失败: " + e.getMessage());
        }
    }

    // =================== 点赞统计功能 ===================

    @PostMapping("/count")
    @Operation(summary = "获取点赞数量 💡 缓存优化", description = "获取目标对象的点赞数量")
    public Result<Long> getLikeCount(@RequestBody LikeCountRequest request) {
        try {
            log.debug("获取点赞数量: 类型={}, 目标={}", request.getLikeType(), request.getTargetId());
            
            // 调用门面服务
            Result<Long> result = likeFacadeService.getLikeCount(request.getLikeType(), request.getTargetId());
            
            return result;
        } catch (Exception e) {
            log.error("获取点赞数量失败: 类型={}, 目标={}", request.getLikeType(), request.getTargetId(), e);
            return Result.error("LIKE_COUNT_ERROR", "获取点赞数量失败: " + e.getMessage());
        }
    }

    @PostMapping("/user/count")
    @Operation(summary = "获取用户点赞数量 💡 缓存优化", description = "获取用户的点赞数量")
    public Result<Long> getUserLikeCount(@RequestBody UserLikeCountRequest request) {
        try {
            log.debug("获取用户点赞数量: 用户={}, 类型={}", request.getUserId(), request.getLikeType());
            
            // 调用门面服务
            Result<Long> result = likeFacadeService.getUserLikeCount(request.getUserId(), request.getLikeType());
            
            return result;
        } catch (Exception e) {
            log.error("获取用户点赞数量失败: 用户={}, 类型={}", request.getUserId(), request.getLikeType(), e);
            return Result.error("USER_LIKE_COUNT_ERROR", "获取用户点赞数量失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/check")
    @Operation(summary = "批量检查点赞状态 💡 缓存优化", description = "批量检查用户对多个目标对象的点赞状态")
    public Result<Map<Long, Boolean>> batchCheckLikeStatus(@RequestBody LikeBatchCheckRequest request) {
        try {
            log.info("批量检查点赞状态: 用户={}, 类型={}, 目标数量={}", 
                    request.getUserId(), request.getLikeType(), 
                    request.getTargetIds() != null ? request.getTargetIds().size() : 0);
            
            // 调用门面服务
            Result<Map<Long, Boolean>> result = likeFacadeService.batchCheckLikeStatus(
                    request.getUserId(), request.getLikeType(), request.getTargetIds()
            );
            
            if (result.getSuccess()) {
                log.info("批量点赞状态检查成功: 用户={}, 检查数量={}", 
                        request.getUserId(), request.getTargetIds() != null ? request.getTargetIds().size() : 0);
            }
            return result;
        } catch (Exception e) {
            log.error("批量检查点赞状态失败: 用户={}, 类型={}", request.getUserId(), request.getLikeType(), e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查点赞状态失败: " + e.getMessage());
        }
    }

}