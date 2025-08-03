package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.UserTagFacadeService;
import com.gig.collide.api.tag.request.UserTagQueryRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.tag.response.UserTagResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 用户标签控制器
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user-tags")
@RequiredArgsConstructor
@Tag(name = "用户标签管理", description = "用户关注标签功能和协同过滤推荐")
public class UserTagController {

    @DubboReference(version = "1.0.0")
    private UserTagFacadeService userTagFacadeService;

    // =================== 用户关注标签管理 ===================

    /**
     * 用户关注标签
     */
    @PostMapping("/{userId}/follow/{tagId}")
    @SaCheckLogin
    @Operation(summary = "关注标签", description = "用户关注指定标签")
    public Result<Void> followTag(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        
        // 权限检查：只能操作自己的标签关注
        if (!userId.equals(StpUtil.getLoginIdAsLong()) && !StpUtil.hasRole("admin")) {
            return Result.error("PERMISSION_DENIED", "无权限操作其他用户的标签关注");
        }
        
        log.info("用户关注标签: userId={}, tagId={}", userId, tagId);
        return userTagFacadeService.followTag(userId, tagId);
    }

    /**
     * 用户取消关注标签
     */
    @DeleteMapping("/{userId}/follow/{tagId}")
    @SaCheckLogin
    @Operation(summary = "取消关注标签", description = "用户取消关注指定标签")
    public Result<Void> unfollowTag(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        
        // 权限检查：只能操作自己的标签关注
        if (!userId.equals(StpUtil.getLoginIdAsLong()) && !StpUtil.hasRole("admin")) {
            return Result.error("PERMISSION_DENIED", "无权限操作其他用户的标签关注");
        }
        
        log.info("用户取消关注标签: userId={}, tagId={}", userId, tagId);
        return userTagFacadeService.unfollowTag(userId, tagId);
    }

    /**
     * 批量关注标签
     */
    @PostMapping("/{userId}/follow/batch")
    @SaCheckLogin
    @Operation(summary = "批量关注标签", description = "用户批量关注多个标签（最多9个）")
    public Result<List<String>> batchFollowTags(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        
        // 权限检查：只能操作自己的标签关注
        if (!userId.equals(StpUtil.getLoginIdAsLong()) && !StpUtil.hasRole("admin")) {
            return Result.error("PERMISSION_DENIED", "无权限操作其他用户的标签关注");
        }
        
        log.info("用户批量关注标签: userId={}, tagIds={}", userId, tagIds);
        return userTagFacadeService.batchFollowTags(userId, tagIds);
    }

    /**
     * 批量取消关注标签
     */
    @DeleteMapping("/{userId}/follow/batch")
    @SaCheckLogin
    @Operation(summary = "批量取消关注标签", description = "用户批量取消关注多个标签")
    public Result<Void> batchUnfollowTags(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        
        // 权限检查：只能操作自己的标签关注
        if (!userId.equals(StpUtil.getLoginIdAsLong()) && !StpUtil.hasRole("admin")) {
            return Result.error("PERMISSION_DENIED", "无权限操作其他用户的标签关注");
        }
        
        log.info("用户批量取消关注标签: userId={}, tagIds={}", userId, tagIds);
        return userTagFacadeService.batchUnfollowTags(userId, tagIds);
    }

    // =================== 用户关注标签查询 ===================

    /**
     * 检查用户是否关注了指定标签
     */
    @GetMapping("/{userId}/follow/{tagId}/check")
    @Operation(summary = "检查关注状态", description = "检查用户是否关注了指定标签")
    public Result<Boolean> isFollowing(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("检查用户关注状态: userId={}, tagId={}", userId, tagId);
        return userTagFacadeService.isFollowing(userId, tagId);
    }

    /**
     * 获取用户关注的标签列表
     */
    @GetMapping("/{userId}/followed")
    @Operation(summary = "获取关注标签", description = "获取用户关注的所有标签")
    public Result<List<TagResponse>> getUserFollowedTags(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId) {
        log.debug("获取用户关注标签: userId={}", userId);
        return userTagFacadeService.getUserFollowedTags(userId);
    }

    /**
     * 分页查询用户关注的标签
     */
    @PostMapping("/{userId}/followed/query")
    @Operation(summary = "分页查询关注标签", description = "分页查询用户关注的标签详情")
    public Result<PageResponse<UserTagResponse>> queryUserFollowedTags(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Valid @RequestBody UserTagQueryRequest request) {
        log.debug("分页查询用户关注标签: userId={}", userId);
        request.setUserId(userId);
        return userTagFacadeService.queryUserFollowedTags(request);
    }

    /**
     * 获取用户关注标签数量
     */
    @GetMapping("/{userId}/followed/count")
    @Operation(summary = "获取关注标签数量", description = "获取用户关注的标签总数")
    public Result<Integer> getUserFollowedTagCount(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId) {
        log.debug("获取用户关注标签数量: userId={}", userId);
        return userTagFacadeService.getUserFollowedTagCount(userId);
    }

    /**
     * 批量检查用户关注状态
     */
    @PostMapping("/{userId}/follow/batch-check")
    @Operation(summary = "批量检查关注状态", description = "批量检查用户对多个标签的关注状态")
    public Result<Map<Long, Boolean>> batchCheckFollowing(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "标签ID列表") @RequestBody List<Long> tagIds) {
        log.debug("批量检查用户关注状态: userId={}, tagIds={}", userId, tagIds);
        return userTagFacadeService.batchCheckFollowing(userId, tagIds);
    }

    // =================== 协同过滤推荐 ===================

    /**
     * 为用户推荐标签
     */
    @GetMapping("/{userId}/recommend")
    @Operation(summary = "推荐标签", description = "基于协同过滤为用户推荐感兴趣的标签")
    public Result<List<TagResponse>> getRecommendTagsForUser(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "9") @Min(1) Integer limit) {
        log.debug("为用户推荐标签: userId={}, limit={}", userId, limit);
        return userTagFacadeService.getRecommendTagsForUser(userId, limit);
    }

    /**
     * 获取与用户兴趣相似的用户
     */
    @GetMapping("/{userId}/similar-users")
    @Operation(summary = "获取相似用户", description = "获取与指定用户兴趣相似的其他用户")
    public Result<List<Long>> getSimilarUsers(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取相似用户: userId={}, limit={}", userId, limit);
        return userTagFacadeService.getSimilarUsers(userId, limit);
    }

    /**
     * 计算两个用户的兴趣相似度
     */
    @GetMapping("/{userId1}/similarity/{userId2}")
    @Operation(summary = "计算用户相似度", description = "计算两个用户的兴趣相似度分数")
    public Result<Double> calculateUserSimilarity(
            @Parameter(description = "用户1 ID") @PathVariable @NotNull @Min(1) Long userId1,
            @Parameter(description = "用户2 ID") @PathVariable @NotNull @Min(1) Long userId2) {
        log.debug("计算用户相似度: userId1={}, userId2={}", userId1, userId2);
        return userTagFacadeService.calculateUserSimilarity(userId1, userId2);
    }

    // =================== 用户标签统计分析 ===================

    /**
     * 获取用户标签统计信息
     */
    @GetMapping("/{userId}/statistics")
    @Operation(summary = "获取用户标签统计", description = "获取用户标签关注的统计信息")
    public Result<Map<String, Object>> getUserTagStatistics(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId) {
        log.debug("获取用户标签统计: userId={}", userId);
        return userTagFacadeService.getUserTagStatistics(userId);
    }

    /**
     * 获取用户最近关注的标签
     */
    @GetMapping("/{userId}/recent-followed")
    @Operation(summary = "获取最近关注标签", description = "获取用户最近关注的标签列表")
    public Result<List<TagResponse>> getUserRecentFollowedTags(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "天数范围") @RequestParam(defaultValue = "30") @Min(1) Integer days,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取用户最近关注标签: userId={}, days={}, limit={}", userId, days, limit);
        return userTagFacadeService.getUserRecentFollowedTags(userId, days, limit);
    }

    /**
     * 获取用户兴趣权重分布
     */
    @GetMapping("/{userId}/weight-distribution")
    @Operation(summary = "获取兴趣权重分布", description = "获取用户关注标签的权重分布统计")
    public Result<Map<String, Integer>> getUserInterestWeightDistribution(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId) {
        log.debug("获取用户兴趣权重分布: userId={}", userId);
        return userTagFacadeService.getUserInterestWeightDistribution(userId);
    }

    // =================== 标签关注者查询 ===================

    /**
     * 获取标签的关注者数量
     */
    @GetMapping("/tag/{tagId}/followers/count")
    @Operation(summary = "获取标签关注者数量", description = "获取指定标签的关注者总数")
    public Result<Long> getTagFollowerCount(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("获取标签关注者数量: tagId={}", tagId);
        return userTagFacadeService.getTagFollowerCount(tagId);
    }

    /**
     * 获取标签的关注者列表
     */
    @GetMapping("/tag/{tagId}/followers")
    @Operation(summary = "获取标签关注者", description = "获取指定标签的关注者用户列表")
    public Result<List<Long>> getTagFollowers(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") @Min(1) Integer limit) {
        log.debug("获取标签关注者: tagId={}, limit={}", tagId, limit);
        return userTagFacadeService.getTagFollowers(tagId, limit);
    }

    /**
     * 获取标签最近的关注者
     */
    @GetMapping("/tag/{tagId}/recent-followers")
    @Operation(summary = "获取最近关注者", description = "获取标签最近的关注者用户列表")
    public Result<List<Long>> getTagRecentFollowers(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId,
            @Parameter(description = "天数范围") @RequestParam(defaultValue = "7") @Min(1) Integer days,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") @Min(1) Integer limit) {
        log.debug("获取标签最近关注者: tagId={}, days={}, limit={}", tagId, days, limit);
        return userTagFacadeService.getTagRecentFollowers(tagId, days, limit);
    }

    // =================== 个性化推荐 ===================

    /**
     * 为新用户推荐热门标签
     */
    @GetMapping("/{userId}/hot-recommend")
    @Operation(summary = "新用户热门标签推荐", description = "为新用户推荐热门标签")
    public Result<List<TagResponse>> getHotTagsForNewUser(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "9") @Min(1) Integer limit) {
        log.debug("为新用户推荐热门标签: userId={}, limit={}", userId, limit);
        return userTagFacadeService.getHotTagsForNewUser(userId, limit);
    }

    /**
     * 基于用户行为推荐标签
     */
    @GetMapping("/{userId}/behavior-recommend")
    @Operation(summary = "行为推荐标签", description = "基于用户行为推荐相关标签")
    public Result<List<TagResponse>> getTagsByUserBehavior(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "行为类型") @RequestParam String behaviorType,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "5") @Min(1) Integer limit) {
        log.debug("基于用户行为推荐标签: userId={}, behaviorType={}, limit={}", userId, behaviorType, limit);
        return userTagFacadeService.getTagsByUserBehavior(userId, behaviorType, limit);
    }

    // =================== 管理功能 ===================

    /**
     * 清理用户的无效标签关注（管理员功能）
     */
    @PostMapping("/{userId}/cleanup")
    @SaCheckRole("admin")
    @Operation(summary = "清理无效关注", description = "清理用户的无效标签关注记录（管理员权限）")
    public Result<Integer> cleanupInvalidUserTagFollows(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        log.info("清理用户无效标签关注: userId={}", userId);
        return userTagFacadeService.cleanupInvalidUserTagFollows(userId);
    }

    /**
     * 检查用户是否可以关注更多标签
     */
    @GetMapping("/{userId}/can-follow/{tagId}")
    @Operation(summary = "检查关注权限", description = "检查用户是否可以关注指定标签")
    public Result<Boolean> canUserFollowTag(
            @Parameter(description = "用户ID") @PathVariable @NotNull @Min(1) Long userId,
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        log.debug("检查用户关注权限: userId={}, tagId={}", userId, tagId);
        return userTagFacadeService.canUserFollowTag(userId, tagId);
    }

    // =================== 当前登录用户快捷操作 ===================

    /**
     * 获取当前用户关注的标签
     */
    @GetMapping("/my/followed")
    @SaCheckLogin
    @Operation(summary = "我的关注标签", description = "获取当前登录用户关注的标签")
    public Result<List<TagResponse>> getMyFollowedTags() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.debug("获取当前用户关注标签: userId={}", userId);
        return userTagFacadeService.getUserFollowedTags(userId);
    }

    /**
     * 当前用户关注标签
     */
    @PostMapping("/my/follow/{tagId}")
    @SaCheckLogin
    @Operation(summary = "关注标签", description = "当前用户关注指定标签")
    public Result<Void> followTagByCurrentUser(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("当前用户关注标签: userId={}, tagId={}", userId, tagId);
        return userTagFacadeService.followTag(userId, tagId);
    }

    /**
     * 当前用户取消关注标签
     */
    @DeleteMapping("/my/follow/{tagId}")
    @SaCheckLogin
    @Operation(summary = "取消关注标签", description = "当前用户取消关注指定标签")
    public Result<Void> unfollowTagByCurrentUser(
            @Parameter(description = "标签ID") @PathVariable @NotNull @Min(1) Long tagId) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("当前用户取消关注标签: userId={}, tagId={}", userId, tagId);
        return userTagFacadeService.unfollowTag(userId, tagId);
    }

    /**
     * 为当前用户推荐标签
     */
    @GetMapping("/my/recommend")
    @SaCheckLogin
    @Operation(summary = "为我推荐标签", description = "为当前用户推荐感兴趣的标签")
    public Result<List<TagResponse>> getRecommendTagsForCurrentUser(
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "9") @Min(1) Integer limit) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.debug("为当前用户推荐标签: userId={}, limit={}", userId, limit);
        return userTagFacadeService.getRecommendTagsForUser(userId, limit);
    }
}