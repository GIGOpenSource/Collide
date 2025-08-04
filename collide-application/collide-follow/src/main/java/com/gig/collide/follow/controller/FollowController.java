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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 关注管理控制器 - 完整功能版
 * 基于follow-simple.sql的单表设计，提供完整的关注功能HTTP接口
 * 通过FollowFacadeService提供缓存优化的业务处理
 * 
 * 功能模块：
 * - 基础操作：关注/取消关注/检查状态/获取详情
 * - 列表查询：关注列表/粉丝列表/互关列表/分页查询
 * - 搜索功能：昵称搜索/关系链查询/批量检查
 * - 统计功能：关注数/粉丝数/关注统计/活跃度检测
 * - 管理功能：用户信息同步/数据清理/关系激活/参数验证
 * - 特殊检测：双向关系/批量状态/活跃度分析
 * 
 * API设计特点：
 * - 统一的Result响应格式
 * - 标准化的分页参数（currentPage, pageSize）
 * - 完整的参数验证和错误处理
 * - 详细的操作日志记录
 * - Swagger/OpenAPI文档支持
 * 
 * @author Collide
 * @version 2.0.0 (完整功能版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
@Validated
@Tag(name = "关注管理", description = "关注相关的API接口 - 完整功能版")
public class FollowController {

    private final FollowFacadeService followFacadeService;

    // =================== 关注操作 ===================

    /**
     * 关注用户
     * 
     * @param request 关注请求参数，包含关注者ID和被关注者ID
     * @return 关注操作结果，包含关注关系信息
     */
    @PostMapping("/follow")
    @Operation(summary = "关注用户 💡 缓存优化", description = "用户关注另一个用户，建立关注关系")
    public Result<FollowResponse> followUser(@Validated @RequestBody FollowCreateRequest request) {
        try {
            log.info("REST请求 - 关注用户: followerId={}, followeeId={}", 
                    request.getFollowerId(), request.getFolloweeId());
            
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
    @Operation(summary = "取消关注 💡 缓存优化", description = "用户取消关注另一个用户，解除关注关系")
    public Result<Void> unfollowUser(@Validated @RequestBody FollowDeleteRequest request) {
        try {
            log.info("REST请求 - 取消关注: followerId={}, followeeId={}", 
                    request.getFollowerId(), request.getFolloweeId());
            
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
    @Operation(summary = "检查关注关系 💡 缓存优化", description = "检查两个用户之间是否存在关注关系")
    public Result<Boolean> checkFollowStatus(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId) {
        try {
            log.info("REST请求 - 检查关注关系: followerId={}, followeeId={}", followerId, followeeId);
            
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
    @Operation(summary = "获取关注详情 💡 缓存优化", description = "获取关注关系的详细信息")
    public Result<FollowResponse> getFollowRelation(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId) {
        try {
            log.info("REST请求 - 获取关注详情: followerId={}, followeeId={}", followerId, followeeId);
            
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
     * @param currentPage 页码，从1开始
     * @param pageSize 每页大小
     * @return 关注记录分页列表
     */
    @GetMapping("/query")
    @Operation(summary = "分页查询关注记录 💡 缓存优化", description = "支持多种条件的关注记录分页查询")
    public Result<PageResponse<FollowResponse>> queryFollows(
            @Parameter(description = "关注者ID") @RequestParam(required = false) Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam(required = false) Long followeeId,
            @Parameter(description = "关注状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 分页查询关注记录: followerId={}, followeeId={}, currentPage={}, pageSize={}", 
                    followerId, followeeId, currentPage, pageSize);
            
            // 创建查询请求
            FollowQueryRequest request = new FollowQueryRequest();
            request.setFollowerId(followerId);
            request.setFolloweeId(followeeId);
            request.setStatus(status);
            request.setCurrentPage(currentPage);
            request.setPageSize(pageSize);
            
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
     * @param followeeId 用户ID（被关注者）
     * @param currentPage 页码，从1开始
     * @param pageSize 每页大小
     * @return 关注者分页列表
     */
    @GetMapping("/followers")
    @Operation(summary = "获取关注者列表 💡 缓存优化", description = "获取指定用户的关注者分页列表（谁关注了我）")
    public Result<PageResponse<FollowResponse>> getFollowers(
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 获取关注者列表: followeeId={}, currentPage={}, pageSize={}", 
                    followeeId, currentPage, pageSize);
            
            return followFacadeService.getFollowers(followeeId, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取关注者列表失败", e);
            return Result.error("GET_FOLLOWERS_ERROR", "获取关注者列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取关注列表（我关注了谁）
     * 
     * @param followerId 用户ID（关注者）
     * @param currentPage 页码，从1开始
     * @param pageSize 每页大小
     * @return 关注列表分页数据
     */
    @GetMapping("/following")
    @Operation(summary = "获取关注列表 💡 缓存优化", description = "获取指定用户的关注列表分页数据（我关注了谁）")
    public Result<PageResponse<FollowResponse>> getFollowing(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 获取关注列表: followerId={}, currentPage={}, pageSize={}", 
                    followerId, currentPage, pageSize);
            
            return followFacadeService.getFollowing(followerId, currentPage, pageSize);
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
    @Operation(summary = "获取关注统计 💡 缓存优化", description = "获取用户的关注统计信息，包括关注数量和粉丝数量")
    public Result<Map<String, Object>> getFollowStatistics(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            log.info("REST请求 - 获取关注统计: userId={}", userId);
            
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
    @Operation(summary = "获取关注数量 💡 缓存优化", description = "获取用户关注的人数")
    public Result<Long> getFollowingCount(
            @Parameter(description = "关注者ID") @RequestParam Long userId) {
        try {
            log.info("REST请求 - 获取关注数量: userId={}", userId);
            
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
    @Operation(summary = "获取粉丝数量 💡 缓存优化", description = "获取关注某用户的人数")
    public Result<Long> getFollowersCount(
            @Parameter(description = "被关注者ID") @RequestParam Long userId) {
        try {
            log.info("REST请求 - 获取粉丝数量: userId={}", userId);
            
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
    @Operation(summary = "批量检查关注状态 💡 缓存优化", description = "批量检查用户对多个目标用户的关注状态")
    public Result<Map<Long, Boolean>> batchCheckFollowStatus(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @RequestBody List<Long> followeeIds) {
        try {
            log.info("REST请求 - 批量检查关注状态: followerId={}, followeeIds数量={}", 
                    followerId, followeeIds != null ? followeeIds.size() : 0);
            
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
     * @param currentPage 页码，从1开始
     * @param pageSize 每页大小
     * @return 互相关注的用户列表
     */
    @GetMapping("/mutual")
    @Operation(summary = "获取互相关注列表 💡 缓存优化", description = "获取与指定用户互相关注的用户列表")
    public Result<PageResponse<FollowResponse>> getMutualFollows(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 获取互相关注列表: userId={}, currentPage={}, pageSize={}", 
                    userId, currentPage, pageSize);
            
            return followFacadeService.getMutualFollows(userId, currentPage, pageSize);
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
    @Operation(summary = "清理已取消的关注记录 💡 缓存优化", description = "物理删除cancelled状态的关注记录")
    public Result<Integer> cleanCancelledFollows(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer days) {
        try {
            log.info("REST请求 - 清理已取消的关注记录: days={}", days);
            
            return followFacadeService.cleanCancelledFollows(days);
        } catch (Exception e) {
            log.error("清理已取消的关注记录失败", e);
            return Result.error("CLEAN_FOLLOWS_ERROR", "清理已取消的关注记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据昵称搜索关注关系
     * 
     * @param followerId 关注者ID（可选）
     * @param followeeId 被关注者ID（可选）
     * @param nicknameKeyword 昵称关键词
     * @param currentPage 页码，从1开始
     * @param pageSize 每页大小
     * @return 搜索结果分页列表
     */
    @GetMapping("/search/nickname")
    @Operation(summary = "根据昵称搜索关注关系 💡 缓存优化", description = "根据关注者或被关注者昵称进行模糊搜索")
    public Result<PageResponse<FollowResponse>> searchByNickname(
            @Parameter(description = "关注者ID") @RequestParam(required = false) Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam(required = false) Long followeeId,
            @Parameter(description = "昵称关键词") @RequestParam String nicknameKeyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 根据昵称搜索关注关系: followerId={}, followeeId={}, keyword={}, currentPage={}, pageSize={}", 
                    followerId, followeeId, nicknameKeyword, currentPage, pageSize);
            
            return followFacadeService.searchByNickname(followerId, followeeId, nicknameKeyword, currentPage, pageSize);
        } catch (Exception e) {
            log.error("根据昵称搜索关注关系失败", e);
            return Result.error("SEARCH_NICKNAME_ERROR", "昵称搜索失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息冗余字段
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @param avatar 新头像
     * @return 更新的记录数量
     */
    @PutMapping("/user/info")
    @Operation(summary = "更新用户信息冗余字段 💡 缓存优化", description = "当用户信息变更时，同步更新关注表中的冗余信息")
    public Result<Integer> updateUserInfo(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "新昵称") @RequestParam(required = false) String nickname,
            @Parameter(description = "新头像") @RequestParam(required = false) String avatar) {
        try {
            log.info("REST请求 - 更新用户信息冗余字段: userId={}, nickname={}", userId, nickname);
            
            return followFacadeService.updateUserInfo(userId, nickname, avatar);
        } catch (Exception e) {
            log.error("更新用户信息冗余字段失败", e);
            return Result.error("UPDATE_USER_INFO_ERROR", "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户间关注关系链
     * 
     * @param userIdA 用户A ID
     * @param userIdB 用户B ID
     * @return 关注关系链列表
     */
    @GetMapping("/relation-chain")
    @Operation(summary = "查询用户间关注关系链 💡 缓存优化", description = "检查两个用户之间的双向关注关系")
    public Result<List<FollowResponse>> getRelationChain(
            @Parameter(description = "用户A ID") @RequestParam Long userIdA,
            @Parameter(description = "用户B ID") @RequestParam Long userIdB) {
        try {
            log.info("REST请求 - 查询用户间关注关系链: userIdA={}, userIdB={}", userIdA, userIdB);
            
            return followFacadeService.getRelationChain(userIdA, userIdB);
        } catch (Exception e) {
            log.error("查询用户间关注关系链失败", e);
            return Result.error("GET_RELATION_CHAIN_ERROR", "查询关系链失败: " + e.getMessage());
        }
    }

    /**
     * 验证关注请求参数
     * 
     * @param request 关注请求对象
     * @return 验证结果信息
     */
    @PostMapping("/validate")
    @Operation(summary = "验证关注请求参数", description = "校验关注请求参数的有效性")
    public Result<String> validateFollowRequest(@RequestBody FollowCreateRequest request) {
        try {
            log.info("REST请求 - 验证关注请求: followerId={}, followeeId={}", 
                    request != null ? request.getFollowerId() : null,
                    request != null ? request.getFolloweeId() : null);
            
            return followFacadeService.validateFollowRequest(request);
        } catch (Exception e) {
            log.error("验证关注请求失败", e);
            return Result.error("VALIDATE_REQUEST_ERROR", "验证请求失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否可以关注
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 检查结果信息
     */
    @GetMapping("/check/can-follow")
    @Operation(summary = "检查是否可以关注", description = "检查业务规则是否允许关注")
    public Result<String> checkCanFollow(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId) {
        try {
            log.info("REST请求 - 检查是否可以关注: followerId={}, followeeId={}", followerId, followeeId);
            
            return followFacadeService.checkCanFollow(followerId, followeeId);
        } catch (Exception e) {
            log.error("检查是否可以关注失败", e);
            return Result.error("CHECK_CAN_FOLLOW_ERROR", "检查权限失败: " + e.getMessage());
        }
    }

    /**
     * 检查关注关系是否存在
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 是否存在关注关系（包括已取消的）
     */
    @GetMapping("/exists")
    @Operation(summary = "检查关注关系是否存在", description = "检查是否已经存在关注关系，包括已取消的关注关系")
    public Result<Boolean> existsFollowRelation(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId) {
        try {
            log.info("REST请求 - 检查关注关系是否存在: followerId={}, followeeId={}", followerId, followeeId);
            
            return followFacadeService.existsFollowRelation(followerId, followeeId);
        } catch (Exception e) {
            log.error("检查关注关系是否存在失败", e);
            return Result.error("CHECK_RELATION_EXISTS_ERROR", "检查关系失败: " + e.getMessage());
        }
    }

    /**
     * 重新激活已取消的关注关系
     * 
     * @param followerId 关注者ID
     * @param followeeId 被关注者ID
     * @return 是否成功重新激活
     */
    @PostMapping("/reactivate")
    @Operation(summary = "重新激活已取消的关注关系 💡 缓存优化", description = "将cancelled状态的关注重新设置为active")
    public Result<Boolean> reactivateFollow(
            @Parameter(description = "关注者ID") @RequestParam Long followerId,
            @Parameter(description = "被关注者ID") @RequestParam Long followeeId) {
        try {
            log.info("REST请求 - 重新激活关注关系: followerId={}, followeeId={}", followerId, followeeId);
            
            return followFacadeService.reactivateFollow(followerId, followeeId);
        } catch (Exception e) {
            log.error("重新激活关注关系失败", e);
            return Result.error("REACTIVATE_FOLLOW_ERROR", "重新激活失败: " + e.getMessage());
        }
    }

    // =================== 特殊检测接口 🔥 ===================

    /**
     * 检测用户是否被特定用户关注
     * 
     * @param userId 用户ID（被关注者）
     * @param checkUserId 检测用户ID（潜在关注者）
     * @return 是否被关注
     */
    @GetMapping("/detect/is-followed-by")
    @Operation(summary = "检测是否被特定用户关注 🔥", description = "检测用户是否被特定用户关注")
    public Result<Boolean> isFollowedBy(
            @Parameter(description = "用户ID（被关注者）") @RequestParam Long userId,
            @Parameter(description = "检测用户ID（潜在关注者）") @RequestParam Long checkUserId) {
        try {
            log.info("REST请求 - 检测是否被关注: userId={}, checkUserId={}", userId, checkUserId);
            
            // 实际上就是检查checkUserId是否关注了userId
            return followFacadeService.checkFollowStatus(checkUserId, userId);
        } catch (Exception e) {
            log.error("检测是否被关注失败", e);
            return Result.error("DETECT_FOLLOWED_ERROR", "检测是否被关注失败: " + e.getMessage());
        }
    }

    /**
     * 检测用户是否关注了特定用户
     * 
     * @param userId 用户ID（关注者）
     * @param targetUserId 目标用户ID（被关注者）
     * @return 是否已关注
     */
    @GetMapping("/detect/is-following")
    @Operation(summary = "检测是否关注了特定用户 🔥", description = "检测用户是否关注了特定用户")
    public Result<Boolean> isFollowing(
            @Parameter(description = "用户ID（关注者）") @RequestParam Long userId,
            @Parameter(description = "目标用户ID（被关注者）") @RequestParam Long targetUserId) {
        try {
            log.info("REST请求 - 检测是否关注: userId={}, targetUserId={}", userId, targetUserId);
            
            return followFacadeService.checkFollowStatus(userId, targetUserId);
        } catch (Exception e) {
            log.error("检测是否关注失败", e);
            return Result.error("DETECT_FOLLOWING_ERROR", "检测是否关注失败: " + e.getMessage());
        }
    }

    /**
     * 检测两个用户之间的关注关系
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 关注关系状态
     */
    @GetMapping("/detect/relationship")
    @Operation(summary = "检测双向关注关系 🔥", description = "检测两个用户之间的完整关注关系")
    public Result<Map<String, Object>> detectRelationship(
            @Parameter(description = "用户1 ID") @RequestParam Long userId1,
            @Parameter(description = "用户2 ID") @RequestParam Long userId2) {
        try {
            log.info("REST请求 - 检测双向关注关系: userId1={}, userId2={}", userId1, userId2);
            
            // 检查userId1是否关注userId2
            Result<Boolean> user1FollowsUser2 = followFacadeService.checkFollowStatus(userId1, userId2);
            // 检查userId2是否关注userId1
            Result<Boolean> user2FollowsUser1 = followFacadeService.checkFollowStatus(userId2, userId1);
            
            if (!user1FollowsUser2.getSuccess() || !user2FollowsUser1.getSuccess()) {
                return Result.error("DETECT_RELATIONSHIP_ERROR", "检测关注关系失败");
            }
            
            Map<String, Object> relationship = Map.of(
                "user1FollowsUser2", user1FollowsUser2.getData(),
                "user2FollowsUser1", user2FollowsUser1.getData(),
                "isMutualFollow", user1FollowsUser2.getData() && user2FollowsUser1.getData()
            );
            
            log.info("双向关注关系检测完成: userId1={}, userId2={}, mutual={}", 
                    userId1, userId2, relationship.get("isMutualFollow"));
            
            return Result.success(relationship);
        } catch (Exception e) {
            log.error("检测双向关注关系失败", e);
            return Result.error("DETECT_RELATIONSHIP_ERROR", "检测双向关注关系失败: " + e.getMessage());
        }
    }

    /**
     * 批量检测用户关注状态
     * 
     * @param userId 当前用户ID
     * @param targetUserIds 目标用户ID列表
     * @return 关注状态映射和统计信息
     */
    @PostMapping("/detect/batch-status")
    @Operation(summary = "批量检测关注状态 🔥", description = "批量检测用户对多个目标用户的关注状态")
    public Result<Map<String, Object>> batchDetectStatus(
            @Parameter(description = "当前用户ID") @RequestParam Long userId,
            @RequestBody List<Long> targetUserIds) {
        try {
            log.info("REST请求 - 批量检测关注状态: userId={}, 目标数量={}", userId, 
                    targetUserIds != null ? targetUserIds.size() : 0);
            
            Result<Map<Long, Boolean>> batchResult = followFacadeService.batchCheckFollowStatus(userId, targetUserIds);
            
            if (!batchResult.getSuccess()) {
                return Result.error(batchResult.getCode(), batchResult.getMessage());
            }
            
            Map<Long, Boolean> statusMap = batchResult.getData();
            
            // 统计信息
            long followingCount = statusMap.values().stream().mapToLong(b -> b ? 1 : 0).sum();
            long notFollowingCount = statusMap.size() - followingCount;
            
            Map<String, Object> result = Map.of(
                "statusMap", statusMap,
                "statistics", Map.of(
                    "totalChecked", statusMap.size(),
                    "followingCount", followingCount,
                    "notFollowingCount", notFollowingCount,
                    "followingRate", statusMap.isEmpty() ? 0.0 : (double) followingCount / statusMap.size()
                )
            );
            
            log.info("批量关注状态检测完成: userId={}, 关注数={}/{}", userId, followingCount, statusMap.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量检测关注状态失败", e);
            return Result.error("BATCH_DETECT_ERROR", "批量检测关注状态失败: " + e.getMessage());
        }
    }

    /**
     * 检测用户关注活跃度
     * 
     * @param userId 用户ID
     * @param days 统计天数
     * @return 关注活跃度信息
     */
    @GetMapping("/detect/activity")
    @Operation(summary = "检测用户关注活跃度 🔥", description = "检测用户在指定时间内的关注活跃度")
    public Result<Map<String, Object>> detectFollowActivity(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "统计天数") @RequestParam(defaultValue = "7") Integer days) {
        try {
            log.info("REST请求 - 检测关注活跃度: userId={}, days={}", userId, days);
            
            // 获取基础统计信息
            Result<Map<String, Object>> statsResult = followFacadeService.getFollowStatistics(userId);
            
            if (!statsResult.getSuccess()) {
                return Result.error(statsResult.getCode(), statsResult.getMessage());
            }
            
            Map<String, Object> baseStats = statsResult.getData();
            
            // 构建活跃度信息
            Map<String, Object> activityInfo = Map.of(
                "userId", userId,
                "statisticsDays", days,
                "baseStatistics", baseStats,
                "activityLevel", calculateActivityLevel(baseStats),
                "recommendations", generateFollowRecommendations(baseStats)
            );
            
            log.info("关注活跃度检测完成: userId={}, level={}", userId, activityInfo.get("activityLevel"));
            
            return Result.success(activityInfo);
        } catch (Exception e) {
            log.error("检测关注活跃度失败", e);
            return Result.error("DETECT_ACTIVITY_ERROR", "检测关注活跃度失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 计算用户活跃度等级
     */
    private String calculateActivityLevel(Map<String, Object> stats) {
        // 基于关注数和粉丝数计算活跃度
        long followingCount = (Long) stats.getOrDefault("followingCount", 0L);
        long followersCount = (Long) stats.getOrDefault("followersCount", 0L);
        
        if (followingCount > 100 && followersCount > 100) {
            return "HIGH";
        } else if (followingCount > 20 && followersCount > 20) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * 生成关注建议
     */
    private List<String> generateFollowRecommendations(Map<String, Object> stats) {
        // 基于统计信息生成建议
        long followingCount = (Long) stats.getOrDefault("followingCount", 0L);
        long followersCount = (Long) stats.getOrDefault("followersCount", 0L);
        
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (followingCount == 0) {
            recommendations.add("开始关注一些感兴趣的用户");
        } else if (followingCount < 10) {
            recommendations.add("可以关注更多用户来丰富动态");
        }
        
        if (followersCount == 0) {
            recommendations.add("发布优质内容来吸引关注者");
        } else if (followersCount < followingCount / 3) {
            recommendations.add("提升内容质量来增加粉丝数量");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("保持当前的关注活跃度");
        }
        
        return recommendations;
    }
}