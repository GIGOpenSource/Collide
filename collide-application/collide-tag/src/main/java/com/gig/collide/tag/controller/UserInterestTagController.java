package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.UserInterestTagFacadeService;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.UserInterestTagResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户兴趣标签管理控制器
 * 负责用户与标签的兴趣关系管理功能
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user-interest-tags")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户兴趣标签管理", description = "用户与标签的兴趣关系管理功能")
public class UserInterestTagController {

    private final UserInterestTagFacadeService userInterestTagFacadeService;

    // =================== 用户兴趣标签基础操作 ===================

    @PostMapping
    @Operation(summary = "添加用户兴趣标签", description = "为用户添加兴趣标签，包含重复检查和分数验证")
    public Result<UserInterestTagResponse> addUserInterest(
            @Valid @RequestBody UserInterestTagRequest request) {
        log.info("添加用户兴趣标签请求: {}", request);
        return userInterestTagFacadeService.addUserInterest(request);
    }

    @DeleteMapping("/user/{userId}/tag/{tagId}")
    @Operation(summary = "移除用户兴趣标签", description = "移除用户的指定兴趣标签")
    public Result<Void> removeUserInterest(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("移除用户兴趣标签: 用户ID={}, 标签ID={}, 操作人={}", userId, tagId, operatorId);
        return userInterestTagFacadeService.removeUserInterest(userId, tagId, operatorId);
    }

    @PutMapping("/user/{userId}/tag/{tagId}/score")
    @Operation(summary = "更新用户兴趣分数", description = "更新用户对指定标签的兴趣分数")
    public Result<Void> updateUserInterestScore(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "兴趣分数", required = true)
            @RequestParam @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal interestScore,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("更新用户兴趣分数: 用户ID={}, 标签ID={}, 分数={}, 操作人={}", userId, tagId, interestScore, operatorId);
        return userInterestTagFacadeService.updateUserInterestScore(userId, tagId, interestScore, operatorId);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户兴趣标签列表", description = "获取指定用户的所有兴趣标签")
    public Result<List<UserInterestTagResponse>> getUserInterests(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId) {
        log.debug("获取用户兴趣标签: 用户ID={}", userId);
        return userInterestTagFacadeService.getUserInterests(userId);
    }

    @GetMapping("/user/{userId}/top")
    @Operation(summary = "获取用户高分兴趣标签", description = "获取用户兴趣分数最高的标签列表")
    public Result<List<UserInterestTagResponse>> getUserTopInterests(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("获取用户高分兴趣标签: 用户ID={}, 限制数量={}", userId, limit);
        return userInterestTagFacadeService.getUserTopInterests(userId, limit);
    }

    @GetMapping("/tag/{tagId}/followers")
    @Operation(summary = "获取标签的关注用户", description = "获取关注指定标签的用户列表")
    public Result<List<UserInterestTagResponse>> getTagFollowers(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("获取标签关注用户: 标签ID={}", tagId);
        return userInterestTagFacadeService.getTagFollowers(tagId);
    }

    // =================== 批量操作 ===================

    @PostMapping("/user/{userId}/batch/set")
    @Operation(summary = "批量设置用户兴趣标签", description = "批量为用户设置多个兴趣标签")
    public Result<Integer> batchSetUserInterests(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds,
            @Parameter(description = "默认兴趣分数", example = "50.0")
            @RequestParam(defaultValue = "50.0") @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal defaultScore,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("批量设置用户兴趣标签: 用户ID={}, 标签数量={}, 默认分数={}, 操作人={}", 
                userId, tagIds.size(), defaultScore, operatorId);
        return userInterestTagFacadeService.batchSetUserInterests(userId, tagIds, defaultScore, operatorId);
    }

    @PostMapping("/user/{userId}/batch/update-status")
    @Operation(summary = "批量更新用户标签状态", description = "批量更新用户兴趣标签的状态")
    public Result<Integer> batchUpdateUserTagStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds,
            @Parameter(description = "状态", required = true)
            @RequestParam String status,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("批量更新用户标签状态: 用户ID={}, 标签数量={}, 状态={}, 操作人={}", 
                userId, tagIds.size(), status, operatorId);
        return userInterestTagFacadeService.batchUpdateUserTagStatus(userId, tagIds, status, operatorId);
    }

    @PostMapping("/user/{userId}/batch/activate")
    @Operation(summary = "批量激活用户兴趣标签", description = "批量激活用户的多个兴趣标签")
    public Result<Integer> batchActivateUserInterests(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("批量激活用户兴趣标签: 用户ID={}, 标签数量={}, 操作人={}", userId, tagIds.size(), operatorId);
        return userInterestTagFacadeService.batchActivateUserInterests(userId, tagIds, operatorId);
    }

    @PostMapping("/user/{userId}/batch/deactivate")
    @Operation(summary = "批量停用用户兴趣标签", description = "批量停用用户的多个兴趣标签")
    public Result<Integer> batchDeactivateUserInterests(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID列表", required = true)
            @RequestBody List<Long> tagIds,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("批量停用用户兴趣标签: 用户ID={}, 标签数量={}, 操作人={}", userId, tagIds.size(), operatorId);
        return userInterestTagFacadeService.batchDeactivateUserInterests(userId, tagIds, operatorId);
    }

    // =================== 兴趣分数管理 ===================

    @PostMapping("/user/{userId}/tag/{tagId}/toggle")
    @Operation(summary = "切换用户兴趣标签状态", description = "激活或停用用户兴趣标签")
    public Result<Void> toggleUserInterest(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "是否激活", required = true)
            @RequestParam boolean active,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("切换用户兴趣标签状态: 用户ID={}, 标签ID={}, 激活={}, 操作人={}", userId, tagId, active, operatorId);
        return userInterestTagFacadeService.toggleUserInterest(userId, tagId, active, operatorId);
    }

    @PostMapping("/user/{userId}/tag/{tagId}/increase-score")
    @Operation(summary = "增加用户兴趣分数", description = "增加用户对指定标签的兴趣分数")
    public Result<Void> increaseInterestScore(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "增加的分数", required = true)
            @RequestParam @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal increment,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("增加用户兴趣分数: 用户ID={}, 标签ID={}, 增量={}, 操作人={}", userId, tagId, increment, operatorId);
        return userInterestTagFacadeService.increaseInterestScore(userId, tagId, increment, operatorId);
    }

    @PostMapping("/user/{userId}/tag/{tagId}/decrease-score")
    @Operation(summary = "减少用户兴趣分数", description = "减少用户对指定标签的兴趣分数")
    public Result<Void> decreaseInterestScore(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "减少的分数", required = true)
            @RequestParam @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal decrement,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("减少用户兴趣分数: 用户ID={}, 标签ID={}, 减量={}, 操作人={}", userId, tagId, decrement, operatorId);
        return userInterestTagFacadeService.decreaseInterestScore(userId, tagId, decrement, operatorId);
    }

    @PostMapping("/user/{userId}/tag/{tagId}/reset-score")
    @Operation(summary = "重置用户兴趣分数", description = "重置用户对指定标签的兴趣分数")
    public Result<Void> resetInterestScore(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "新的兴趣分数", required = true)
            @RequestParam @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal newScore,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("重置用户兴趣分数: 用户ID={}, 标签ID={}, 新分数={}, 操作人={}", userId, tagId, newScore, operatorId);
        return userInterestTagFacadeService.resetInterestScore(userId, tagId, newScore, operatorId);
    }

    // =================== 查询功能 ===================

    @GetMapping("/user/{userId}/active")
    @Operation(summary = "获取用户活跃兴趣标签", description = "获取用户当前活跃的兴趣标签列表")
    public Result<List<UserInterestTagResponse>> getActiveUserInterests(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId) {
        log.debug("获取用户活跃兴趣标签: 用户ID={}", userId);
        return userInterestTagFacadeService.getActiveUserInterests(userId);
    }

    @GetMapping("/user/{userId}/high-interest")
    @Operation(summary = "获取用户高兴趣标签", description = "获取用户兴趣分数高于指定阈值的标签")
    public Result<List<UserInterestTagResponse>> getHighInterestTags(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "最小分数阈值", example = "60.0")
            @RequestParam(defaultValue = "60.0") @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal minScore) {
        log.debug("获取用户高兴趣标签: 用户ID={}, 最小分数={}", userId, minScore);
        return userInterestTagFacadeService.getHighInterestTags(userId, minScore);
    }

    @GetMapping("/check/user/{userId}/tag/{tagId}")
    @Operation(summary = "检查用户是否关注标签", description = "检查用户是否已经关注指定标签")
    public Result<Boolean> isUserInterestedInTag(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId) {
        log.debug("检查用户是否关注标签: 用户ID={}, 标签ID={}", userId, tagId);
        return userInterestTagFacadeService.isUserInterestedInTag(userId, tagId);
    }

    // =================== 统计分析 ===================

    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "获取用户兴趣统计", description = "获取用户兴趣标签的统计信息")
    public Result<List<Map<String, Object>>> getUserInterestStats(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "最小分数阈值")
            @RequestParam(required = false) BigDecimal minScore) {
        log.debug("获取用户兴趣统计: 用户ID={}, 最小分数={}", userId, minScore);
        return userInterestTagFacadeService.getUserInterestStats(userId, minScore);
    }

    @GetMapping("/tag/{tagId}/hot-users")
    @Operation(summary = "获取标签热门关注用户", description = "获取关注指定标签的热门用户列表")
    public Result<List<Map<String, Object>>> getTagHotUsers(
            @Parameter(description = "标签ID", required = true)
            @PathVariable @NotNull @Positive Long tagId,
            @Parameter(description = "限制数量", example = "20")
            @RequestParam(defaultValue = "20") Integer limit) {
        log.debug("获取标签热门关注用户: 标签ID={}, 限制数量={}", tagId, limit);
        return userInterestTagFacadeService.getTagHotUsers(tagId, limit);
    }

    @GetMapping("/user/{userId}/analysis")
    @Operation(summary = "用户兴趣分析", description = "获取用户兴趣标签的统计分析")
    public Result<Map<String, Object>> getUserInterestAnalysis(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId) {
        log.debug("用户兴趣分析: 用户ID={}", userId);
        return userInterestTagFacadeService.getUserInterestAnalysis(userId);
    }

    @GetMapping("/user/{userId}/tags-with-interest")
    @Operation(summary = "获取用户标签详细信息", description = "获取用户相关的完整标签信息，包含详情和兴趣分数")
    public Result<List<Map<String, Object>>> getUserTagsWithInterest(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId) {
        log.debug("获取用户标签详细信息: 用户ID={}", userId);
        return userInterestTagFacadeService.getUserTagsWithInterest(userId);
    }

    // =================== 推荐系统 ===================

    @GetMapping("/user/{userId}/recommend-tags")
    @Operation(summary = "推荐用户标签", description = "基于用户已有兴趣和热门标签推荐可能感兴趣的标签")
    public Result<List<Map<String, Object>>> recommendTagsForUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("推荐用户标签: 用户ID={}, 限制数量={}", userId, limit);
        return userInterestTagFacadeService.recommendTagsForUser(userId, limit);
    }

    @GetMapping("/user/{userId}/recommend-similar-users")
    @Operation(summary = "推荐相似用户", description = "根据用户兴趣推荐具有相似兴趣的用户")
    public Result<List<Long>> recommendSimilarUsers(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId,
            @Parameter(description = "限制数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("推荐相似用户: 用户ID={}, 限制数量={}", userId, limit);
        return userInterestTagFacadeService.recommendSimilarUsers(userId, limit);
    }

    @GetMapping("/user/{userId}/interest-correlation")
    @Operation(summary = "用户兴趣关联分析", description = "获取用户兴趣标签的相关性分析")
    public Result<Map<String, Object>> getUserInterestCorrelation(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull @Positive Long userId) {
        log.debug("用户兴趣关联分析: 用户ID={}", userId);
        return userInterestTagFacadeService.getUserInterestCorrelation(userId);
    }

    // =================== 数据维护 ===================

    @PostMapping("/maintenance/cleanup-invalid")
    @Operation(summary = "清理无效用户兴趣标签", description = "清理不存在的用户或标签的关联数据")
    public Result<Integer> cleanupInvalidUserInterests(
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("清理无效用户兴趣标签: 操作人={}", operatorId);
        return userInterestTagFacadeService.cleanupInvalidUserInterests(operatorId);
    }

    @PostMapping("/maintenance/recalculate-scores")
    @Operation(summary = "重新计算用户兴趣分数", description = "基于用户行为重新计算兴趣分数")
    public Result<Integer> recalculateUserInterestScores(
            @Parameter(description = "用户ID", required = true)
            @RequestParam @NotNull @Positive Long userId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam @NotNull @Positive Long operatorId) {
        log.info("重新计算用户兴趣分数: 用户ID={}, 操作人={}", userId, operatorId);
        return userInterestTagFacadeService.recalculateUserInterestScores(userId, operatorId);
    }

    @GetMapping("/health")
    @Operation(summary = "用户兴趣标签系统健康检查", description = "检查用户兴趣标签系统的健康状态")
    public Result<String> healthCheck() {
        log.debug("用户兴趣标签系统健康检查");
        return userInterestTagFacadeService.healthCheck();
    }
}