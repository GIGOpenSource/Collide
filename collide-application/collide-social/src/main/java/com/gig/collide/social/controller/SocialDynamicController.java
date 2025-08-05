package com.gig.collide.social.controller;

import com.gig.collide.api.social.SocialFacadeService;
import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态管理控制器 - 严格对应接口版
 * 严格按照SocialFacadeService接口设计，提供HTTP REST接口
 * 包含完整的CRUD功能和统计查询功能
 * 
 * 核心功能：
 * - 业务CRUD操作
 * - 核心查询方法
 * - 统计计数方法
 * - 互动统计更新
 * - 状态管理
 * - 用户信息同步
 * - 数据清理
 * - 特殊查询
 * - 系统健康检查
 *
 * @author GIG Team
 * @version 3.0.0 (严格对应接口版)
 * @since 2024-01-30
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/social/dynamics")
@RequiredArgsConstructor
@Tag(name = "社交动态管理", description = "社交动态管理相关接口 - 严格对应接口版")
public class SocialDynamicController {

    private final SocialFacadeService socialFacadeService;

    // =================== 业务CRUD操作 ===================

    @PostMapping("/create")
    @Operation(summary = "创建动态", description = "发布新的社交动态")
    public Result<SocialDynamicResponse> createDynamic(@Validated @RequestBody SocialDynamicCreateRequest request) {
        log.info("REST创建动态: 用户ID={}, 内容={}", request.getUserId(), request.getContent());
        return socialFacadeService.createDynamic(request);
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量创建动态", description = "批量发布多个社交动态")
    public Result<Integer> batchCreateDynamics(@Validated @RequestBody List<SocialDynamicCreateRequest> requests,
                                             @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST批量创建动态: 数量={}, 操作人={}", requests.size(), operatorId);
        return socialFacadeService.batchCreateDynamics(requests, operatorId);
    }

    @PostMapping("/create-share")
    @Operation(summary = "创建分享动态", description = "创建分享其他内容的动态")
    public Result<SocialDynamicResponse> createShareDynamic(@Validated @RequestBody SocialDynamicCreateRequest request) {
        log.info("REST创建分享动态: 用户ID={}, 分享目标={}", request.getUserId(), request.getShareTargetId());
        return socialFacadeService.createShareDynamic(request);
    }

    @PutMapping("/update")
    @Operation(summary = "更新动态内容", description = "只允许更新动态内容，其他字段不允许修改")
    public Result<SocialDynamicResponse> updateDynamic(@Validated @RequestBody SocialDynamicUpdateRequest request) {
        log.info("REST更新动态: ID={}, 用户ID={}", request.getId(), request.getUserId());
        return socialFacadeService.updateDynamic(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除动态", description = "逻辑删除动态（设为deleted状态）")
    public Result<Void> deleteDynamic(@PathVariable("id") Long dynamicId,
                                     @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST删除动态: ID={}, 操作人={}", dynamicId, operatorId);
        return socialFacadeService.deleteDynamic(dynamicId, operatorId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取动态详情", description = "根据ID获取动态详情")
    public Result<SocialDynamicResponse> getDynamicById(@PathVariable("id") Long dynamicId,
                                                       @Parameter(description = "是否包含已删除的动态") @RequestParam(defaultValue = "false") Boolean includeDeleted) {
        log.debug("REST获取动态详情: ID={}, 包含已删除={}", dynamicId, includeDeleted);
        return socialFacadeService.getDynamicById(dynamicId, includeDeleted);
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询动态列表", description = "支持多条件分页查询动态")
    public Result<PageResponse<SocialDynamicResponse>> queryDynamics(@Validated @RequestBody SocialDynamicQueryRequest request) {
        log.debug("REST分页查询动态: 页码={}, 大小={}", request.getCurrentPage(), request.getPageSize());
        return socialFacadeService.queryDynamics(request);
    }

    // =================== 核心查询方法（严格对应Service层7个） ===================

    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询动态", description = "获取指定用户的动态列表")
    public Result<PageResponse<SocialDynamicResponse>> selectByUserId(
            @PathVariable Long userId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "动态类型") @RequestParam(required = false) String dynamicType) {
        log.debug("REST根据用户ID查询动态: 用户={}, 页码={}, 大小={}", userId, currentPage, pageSize);
        return socialFacadeService.selectByUserId(currentPage, pageSize, userId, status, dynamicType);
    }

    @GetMapping("/type/{dynamicType}")
    @Operation(summary = "根据动态类型查询", description = "获取指定类型的动态列表")
    public Result<PageResponse<SocialDynamicResponse>> selectByDynamicType(
            @PathVariable String dynamicType,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST根据动态类型查询: 类型={}, 页码={}, 大小={}", dynamicType, currentPage, pageSize);
        return socialFacadeService.selectByDynamicType(currentPage, pageSize, dynamicType, status);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询动态", description = "获取指定状态的动态列表")
    public Result<PageResponse<SocialDynamicResponse>> selectByStatus(
            @PathVariable String status,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST根据状态查询动态: 状态={}, 页码={}, 大小={}", status, currentPage, pageSize);
        return socialFacadeService.selectByStatus(currentPage, pageSize, status);
    }

    @GetMapping("/following/{userId}")
    @Operation(summary = "获取关注用户的动态流", description = "获取用户关注的人发布的动态")
    public Result<PageResponse<SocialDynamicResponse>> selectFollowingDynamics(
            @PathVariable Long userId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST查询关注用户动态: 用户={}, 页码={}, 大小={}", userId, currentPage, pageSize);
        return socialFacadeService.selectFollowingDynamics(currentPage, pageSize, userId, status);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索动态内容", description = "按关键词搜索动态内容")
    public Result<PageResponse<SocialDynamicResponse>> searchByContent(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST搜索动态内容: 关键词={}, 页码={}, 大小={}", keyword, currentPage, pageSize);
        return socialFacadeService.searchByContent(currentPage, pageSize, keyword, status);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门动态", description = "按互动数排序获取热门动态")
    public Result<PageResponse<SocialDynamicResponse>> selectHotDynamics(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "动态类型") @RequestParam(required = false) String dynamicType) {
        log.debug("REST查询热门动态: 页码={}, 大小={}, 状态={}, 类型={}", currentPage, pageSize, status, dynamicType);
        return socialFacadeService.selectHotDynamics(currentPage, pageSize, status, dynamicType);
    }

    @GetMapping("/share-target")
    @Operation(summary = "根据分享目标查询分享动态", description = "获取分享指定内容的动态列表")
    public Result<PageResponse<SocialDynamicResponse>> selectByShareTarget(
            @Parameter(description = "分享目标类型") @RequestParam String shareTargetType,
            @Parameter(description = "分享目标ID") @RequestParam Long shareTargetId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST根据分享目标查询: 类型={}, 目标ID={}, 页码={}, 大小={}", shareTargetType, shareTargetId, currentPage, pageSize);
        return socialFacadeService.selectByShareTarget(currentPage, pageSize, shareTargetType, shareTargetId, status);
    }

    // =================== 统计计数方法（严格对应Service层3个） ===================

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "统计用户动态数量", description = "统计指定用户的动态数量")
    public Result<Long> countByUserId(
            @PathVariable Long userId,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "动态类型") @RequestParam(required = false) String dynamicType) {
        log.debug("REST统计用户动态数量: 用户ID={}, 状态={}, 类型={}", userId, status, dynamicType);
        return socialFacadeService.countByUserId(userId, status, dynamicType);
    }

    @GetMapping("/count/type/{dynamicType}")
    @Operation(summary = "统计动态类型数量", description = "统计指定类型的动态数量")
    public Result<Long> countByDynamicType(
            @PathVariable String dynamicType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST统计动态类型数量: 类型={}, 状态={}", dynamicType, status);
        return socialFacadeService.countByDynamicType(dynamicType, status);
    }

    @GetMapping("/count/time-range")
    @Operation(summary = "统计时间范围内动态数量", description = "统计指定时间范围内的动态数量")
    public Result<Long> countByTimeRange(
            @Parameter(description = "开始时间") @RequestParam LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam LocalDateTime endTime,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST统计时间范围内动态数量: 开始时间={}, 结束时间={}, 状态={}", startTime, endTime, status);
        return socialFacadeService.countByTimeRange(startTime, endTime, status);
    }

    // =================== 互动统计更新（严格对应Service层5个） ===================

    @PostMapping("/{id}/like")
    @Operation(summary = "增加点赞数", description = "为指定动态增加点赞数")
    public Result<Integer> increaseLikeCount(@PathVariable("id") Long dynamicId,
                                           @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST增加点赞数: 动态ID={}, 操作人={}", dynamicId, operatorId);
        return socialFacadeService.increaseLikeCount(dynamicId, operatorId);
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "减少点赞数", description = "为指定动态减少点赞数")
    public Result<Integer> decreaseLikeCount(@PathVariable("id") Long dynamicId,
                                           @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST减少点赞数: 动态ID={}, 操作人={}", dynamicId, operatorId);
        return socialFacadeService.decreaseLikeCount(dynamicId, operatorId);
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "增加评论数", description = "为指定动态增加评论数")
    public Result<Integer> increaseCommentCount(@PathVariable("id") Long dynamicId,
                                              @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST增加评论数: 动态ID={}, 操作人={}", dynamicId, operatorId);
        return socialFacadeService.increaseCommentCount(dynamicId, operatorId);
    }

    @PostMapping("/{id}/share")
    @Operation(summary = "增加分享数", description = "为指定动态增加分享数")
    public Result<Integer> increaseShareCount(@PathVariable("id") Long dynamicId,
                                            @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST增加分享数: 动态ID={}, 操作人={}", dynamicId, operatorId);
        return socialFacadeService.increaseShareCount(dynamicId, operatorId);
    }

    @PutMapping("/{id}/statistics")
    @Operation(summary = "批量更新统计数据", description = "批量更新动态的统计数据")
    public Result<Integer> updateStatistics(@PathVariable("id") Long dynamicId,
                                           @Parameter(description = "点赞数") @RequestParam(required = false) Long likeCount,
                                           @Parameter(description = "评论数") @RequestParam(required = false) Long commentCount,
                                           @Parameter(description = "分享数") @RequestParam(required = false) Long shareCount,
                                           @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST批量更新统计数据: 动态ID={}, 点赞数={}, 评论数={}, 分享数={}, 操作人={}", 
                dynamicId, likeCount, commentCount, shareCount, operatorId);
        return socialFacadeService.updateStatistics(dynamicId, likeCount, commentCount, shareCount, operatorId);
    }

    // =================== 状态管理（严格对应Service层2个） ===================

    @PutMapping("/{id}/status")
    @Operation(summary = "更新动态状态", description = "更新指定动态的状态")
    public Result<Integer> updateStatus(@PathVariable("id") Long dynamicId,
                                       @Parameter(description = "新状态") @RequestParam String status,
                                       @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST更新动态状态: 动态ID={}, 状态={}, 操作人={}", dynamicId, status, operatorId);
        return socialFacadeService.updateStatus(dynamicId, status, operatorId);
    }

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新动态状态", description = "批量更新多个动态的状态")
    public Result<Integer> batchUpdateStatus(@RequestBody List<Long> dynamicIds,
                                           @Parameter(description = "新状态") @RequestParam String status,
                                           @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST批量更新动态状态: 数量={}, 状态={}, 操作人={}", dynamicIds.size(), status, operatorId);
        return socialFacadeService.batchUpdateStatus(dynamicIds, status, operatorId);
    }

    // =================== 用户信息同步（严格对应Service层1个） ===================

    @PutMapping("/user/{userId}/info")
    @Operation(summary = "更新用户冗余信息", description = "同步更新动态中的用户信息")
    public Result<Integer> updateUserInfo(@PathVariable Long userId,
                                        @Parameter(description = "用户昵称") @RequestParam(required = false) String userNickname,
                                        @Parameter(description = "用户头像") @RequestParam(required = false) String userAvatar,
                                        @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.debug("REST更新用户冗余信息: 用户ID={}, 昵称={}, 头像={}, 操作人={}", userId, userNickname, userAvatar, operatorId);
        return socialFacadeService.updateUserInfo(userId, userNickname, userAvatar, operatorId);
    }

    // =================== 数据清理（严格对应Service层1个） ===================

    @DeleteMapping("/cleanup")
    @Operation(summary = "数据清理", description = "物理删除指定状态的历史动态")
    public Result<Integer> deleteByStatusAndTime(@Parameter(description = "状态") @RequestParam String status,
                                                @Parameter(description = "截止时间") @RequestParam LocalDateTime beforeTime,
                                                @Parameter(description = "限制数量") @RequestParam(required = false) Integer limit,
                                                @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.warn("REST执行数据清理: 状态={}, 截止时间={}, 限制数量={}, 操作人={}", status, beforeTime, limit, operatorId);
        return socialFacadeService.deleteByStatusAndTime(status, beforeTime, limit, operatorId);
    }

    // =================== 特殊查询（严格对应Service层3个） ===================

    @GetMapping("/latest")
    @Operation(summary = "查询最新动态", description = "获取最新发布的动态列表")
    public Result<List<SocialDynamicResponse>> selectLatestDynamics(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST查询最新动态: 限制数量={}, 状态={}", limit, status);
        return socialFacadeService.selectLatestDynamics(limit, status);
    }

    @GetMapping("/user/{userId}/latest")
    @Operation(summary = "查询用户最新动态", description = "获取指定用户最新发布的动态列表")
    public Result<List<SocialDynamicResponse>> selectUserLatestDynamics(
            @PathVariable Long userId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST查询用户最新动态: 用户ID={}, 限制数量={}, 状态={}", userId, limit, status);
        return socialFacadeService.selectUserLatestDynamics(userId, limit, status);
    }

    @GetMapping("/share/{shareTargetType}")
    @Operation(summary = "查询分享动态列表", description = "获取指定类型的分享动态列表")
    public Result<List<SocialDynamicResponse>> selectShareDynamics(
            @PathVariable String shareTargetType,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        log.debug("REST查询分享动态列表: 目标类型={}, 限制数量={}, 状态={}", shareTargetType, limit, status);
        return socialFacadeService.selectShareDynamics(shareTargetType, limit, status);
    }

    // =================== 系统健康检查 ===================

    @GetMapping("/health")
    @Operation(summary = "系统健康检查", description = "检查社交动态系统运行状态")
    public Result<String> healthCheck() {
        log.debug("REST执行系统健康检查");
        return socialFacadeService.healthCheck();
    }
} 