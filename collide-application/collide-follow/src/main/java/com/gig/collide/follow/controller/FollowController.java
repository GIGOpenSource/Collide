package com.gig.collide.follow.controller;

import com.gig.collide.api.follow.FollowFacadeService;
import com.gig.collide.api.follow.request.*;
import com.gig.collide.api.follow.response.FollowResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 关注管理控制器
 * 提供关注相关的HTTP REST API接口
 * 
 * 主要功能：
 * - 用户关注/取消关注操作
 * - 关注关系查询与管理
 * - 关注者和被关注者列表获取
 * - 关注统计信息查询
 * 
 * 注意：控制器层主要负责HTTP请求处理和参数验证，
 * 具体的业务逻辑由FollowFacadeService处理
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "关注管理", description = "关注相关的API接口")
public class FollowController {

    @Autowired
    private FollowFacadeService followFacadeService;

    // =================== 关注操作 ===================

    /**
     * 关注用户
     * 
     * @param request 关注请求参数，包含关注者ID和被关注者ID
     * @return 关注操作结果，包含关注关系信息
     */
    @PostMapping("/follow")
    @Operation(summary = "关注用户", description = "用户关注另一个用户，建立关注关系")
    public Result<FollowResponse> followUser(@Validated @RequestBody FollowCreateRequest request) {
        try {
            log.info("HTTP关注用户: followerId={}, followeeId={}", 
                    request.getFollowerId(), request.getFolloweeId());
            
            // 委托给facade服务处理
            return followFacadeService.followUser(request);
        } catch (Exception e) {
            log.error("关注用户失败", e);
            return Result.error("FOLLOW_ERROR", "关注操作失败: " + e.getMessage());
        }
    }

    /**
     * 取消关注
     * 
     * @param request 取消关注请求参数
     * @return 取消关注操作结果
     */
    @PostMapping("/unfollow")
    @Operation(summary = "取消关注", description = "用户取消关注另一个用户，解除关注关系")
    public Result<Void> unfollowUser(@Validated @RequestBody FollowDeleteRequest request) {
        try {
            log.info("HTTP取消关注: followerId={}, followeeId={}", 
                    request.getFollowerId(), request.getFolloweeId());
            
            // 委托给facade服务处理
            return followFacadeService.unfollowUser(request);
        } catch (Exception e) {
            log.error("取消关注失败", e);
            return Result.error("UNFOLLOW_ERROR", "取消关注失败: " + e.getMessage());
        }
    }

    // =================== 关注关系查询 ===================

    /**
     * 检查关注关系
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 是否存在关注关系
     */
    @GetMapping("/check")
    @Operation(summary = "检查关注关系", description = "检查两个用户之间是否存在关注关系")
    public Result<Boolean> checkFollowStatus(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId) {
        try {
            log.debug("HTTP检查关注关系: followerId={}, followeeId={}", followerId, followeeId);
            
            return followFacadeService.checkFollowStatus(followerId, followeeId);
        } catch (Exception e) {
            log.error("检查关注关系失败", e);
            return Result.error("CHECK_FOLLOW_ERROR", "检查关注关系失败: " + e.getMessage());
        }
    }

    /**
     * 获取关注详情
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 关注关系详细信息
     */
    @GetMapping("/detail")
    @Operation(summary = "获取关注详情", description = "获取关注关系的详细信息")
    public Result<FollowResponse> getFollowRelation(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId) {
        try {
            log.debug("HTTP获取关注详情: followerId={}, followeeId={}", followerId, followeeId);
            
            return followFacadeService.getFollowRelation(followerId, followeeId);
        } catch (Exception e) {
            log.error("获取关注详情失败", e);
            return Result.error("GET_FOLLOW_ERROR", "获取关注详情失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询关注记录
     * 
     * @param followerId 关注者ID（可选）
     * @param followeeId 被关注者ID（可选）
     * @param status 关注状态（可选）
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 关注记录分页列表
     */
    @GetMapping("/query")
    @Operation(summary = "分页查询关注记录", description = "支持多种条件的关注记录分页查询")
    public Result<PageResponse<FollowResponse>> queryFollows(
            @Parameter(description = "关注者ID") @RequestParam(required = false) Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam(required = false) Long followeeId,
            @Parameter(description = "关注状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP分页查询关注记录: followerId={}, followeeId={}, page={}, size={}", 
                    followerId, followeeId, page, size);
            
            // 创建查询请求
            FollowQueryRequest request = new FollowQueryRequest();
            request.setFollowerId(followerId);
            request.setFolloweeId(followeeId);
            request.setStatus(status);
            request.setPageNum(page);
            request.setPageNum(size);
            
            return followFacadeService.queryFollows(request);
        } catch (Exception e) {
            log.error("分页查询关注记录失败", e);
            return Result.error("QUERY_FOLLOWS_ERROR", "分页查询关注记录失败: " + e.getMessage());
        }
    }

    // =================== 关注者列表 ===================

    /**
     * 获取关注者列表（谁关注了我）
     * 
     * @param userId 用户ID（被关注者）
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 关注者分页列表
     */
    @GetMapping("/followers")
    @Operation(summary = "获取关注者列表", description = "获取指定用户的关注者分页列表（谁关注了我）")
    public Result<PageResponse<FollowResponse>> getFollowers(
            @Parameter(description = "被关注者ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP获取关注者列表: userId={}, page={}, size={}", userId, page, size);
            
            return followFacadeService.getFollowers(userId, page, size);
        } catch (Exception e) {
            log.error("获取关注者列表失败", e);
            return Result.error("GET_FOLLOWERS_ERROR", "获取关注者列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取关注列表（我关注了谁）
     * 
     * @param userId 用户ID（关注者）
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 关注列表分页数据
     */
    @GetMapping("/following")
    @Operation(summary = "获取关注列表", description = "获取指定用户的关注列表分页数据（我关注了谁）")
    public Result<PageResponse<FollowResponse>> getFollowing(
            @Parameter(description = "关注者ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP获取关注列表: userId={}, page={}, size={}", userId, page, size);
            
            return followFacadeService.getFollowing(userId, page, size);
        } catch (Exception e) {
            log.error("获取关注列表失败", e);
            return Result.error("GET_FOLLOWING_ERROR", "获取关注列表失败: " + e.getMessage());
        }
    }

    // =================== 统计信息 ===================

    /**
     * 获取关注统计
     * 
     * @param userId 用户ID
     * @return 关注统计信息，包含关注数和粉丝数
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取关注统计", description = "获取用户的关注统计信息，包括关注数量和粉丝数量")
    public Result<Map<String, Object>> getFollowStatistics(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            log.debug("HTTP获取关注统计: userId={}", userId);
            
            return followFacadeService.getFollowStatistics(userId);
        } catch (Exception e) {
            log.error("获取关注统计失败", e);
            return Result.error("GET_STATISTICS_ERROR", "获取关注统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取关注数量
     * 
     * @param userId 用户ID（关注者）
     * @return 关注数量
     */
    @GetMapping("/following/count")
    @Operation(summary = "获取关注数量", description = "获取用户关注的人数")
    public Result<Long> getFollowingCount(
            @Parameter(description = "关注者ID") @RequestParam Long userId) {
        try {
            log.debug("HTTP获取关注数量: userId={}", userId);
            
            return followFacadeService.getFollowingCount(userId);
        } catch (Exception e) {
            log.error("获取关注数量失败", e);
            return Result.error("GET_FOLLOWING_COUNT_ERROR", "获取关注数量失败: " + e.getMessage());
        }
    }

    /**
     * 获取粉丝数量
     * 
     * @param userId 用户ID（被关注者）
     * @return 粉丝数量
     */
    @GetMapping("/followers/count")
    @Operation(summary = "获取粉丝数量", description = "获取关注某用户的人数")
    public Result<Long> getFollowersCount(
            @Parameter(description = "被关注者ID") @RequestParam Long userId) {
        try {
            log.debug("HTTP获取粉丝数量: userId={}", userId);
            
            return followFacadeService.getFollowersCount(userId);
        } catch (Exception e) {
            log.error("获取粉丝数量失败", e);
            return Result.error("GET_FOLLOWERS_COUNT_ERROR", "获取粉丝数量失败: " + e.getMessage());
        }
    }

    /**
     * 批量检查关注状态
     * 
     * @param followerId 关注者ID
     * @param followeeIds 被关注者ID列表
     * @return 关注状态映射（被关注者ID -> 是否关注）
     */
    @PostMapping("/check/batch")
    @Operation(summary = "批量检查关注状态", description = "批量检查用户对多个目标用户的关注状态")
    public Result<Map<Long, Boolean>> batchCheckFollowStatus(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @RequestBody List<Long> followeeIds) {
        try {
            log.debug("HTTP批量检查关注状态: followerId={}, followeeIds={}", followerId, followeeIds);
            
            return followFacadeService.batchCheckFollowStatus(followerId, followeeIds);
        } catch (Exception e) {
            log.error("批量检查关注状态失败", e);
            return Result.error("BATCH_CHECK_FOLLOW_ERROR", "批量检查关注状态失败: " + e.getMessage());
        }
    }

    // =================== 互相关注 ===================

    /**
     * 获取互相关注列表
     * 
     * @param userId 用户ID
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 互相关注的用户列表
     */
    @GetMapping("/mutual")
    @Operation(summary = "获取互相关注列表", description = "获取与指定用户互相关注的用户列表")
    public Result<PageResponse<FollowResponse>> getMutualFollows(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP获取互相关注列表: userId={}, page={}, size={}", userId, page, size);
            
            return followFacadeService.getMutualFollows(userId, page, size);
        } catch (Exception e) {
            log.error("获取互相关注列表失败", e);
            return Result.error("GET_MUTUAL_ERROR", "获取互相关注列表失败: " + e.getMessage());
        }
    }

    // =================== 管理功能 ===================

    /**
     * 清理已取消的关注记录
     * 
     * @param days 保留天数
     * @return 清理的记录数量
     */
    @DeleteMapping("/clean")
    @Operation(summary = "清理已取消的关注记录", description = "物理删除cancelled状态的关注记录")
    public Result<Integer> cleanCancelledFollows(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer days) {
        try {
            log.info("HTTP清理已取消的关注记录: days={}", days);
            
            return followFacadeService.cleanCancelledFollows(days);
        } catch (Exception e) {
            log.error("清理已取消的关注记录失败", e);
            return Result.error("CLEAN_FOLLOWS_ERROR", "清理已取消的关注记录失败: " + e.getMessage());
        }
    }
}