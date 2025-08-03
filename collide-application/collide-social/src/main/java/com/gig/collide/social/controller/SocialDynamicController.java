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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 社交动态管理控制器 - 缓存增强版
 * 基于social-simple.sql的设计，提供HTTP REST接口
 * 对齐content模块设计风格
 * 
 * 核心功能：
 * - 创建动态
 * - 查询最新动态列表
 * - 根据userId查询动态
 * - 点赞评论记录
 * - 删除动态
 * - 更新动态内容（仅内容字段）
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/social/dynamics")
@RequiredArgsConstructor
@Tag(name = "社交动态管理", description = "社交动态管理相关接口 - 缓存增强版")
public class SocialDynamicController {

    @Autowired
    private SocialFacadeService socialFacadeService;

    // =================== 动态管理 ===================

    @PostMapping("/create")
    @Operation(summary = "创建动态", description = "发布新的社交动态")
    public Result<Void> createDynamic(@Validated @RequestBody SocialDynamicCreateRequest request) {
        log.info("REST创建动态: {}", request.getContent());
        return socialFacadeService.createDynamic(request);
    }

    @PutMapping("/update")
    @Operation(summary = "更新动态内容", description = "只允许更新动态内容，其他字段不允许修改")
    public Result<SocialDynamicResponse> updateDynamic(@Validated @RequestBody SocialDynamicUpdateRequest request) {
        log.info("REST更新动态: ID={}", request.getId());
        return socialFacadeService.updateDynamic(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除动态", description = "逻辑删除动态（设为deleted状态）")
    public Result<Void> deleteDynamic(@PathVariable("id") Long dynamicId,
                                     @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST删除动态: ID={}, 操作人={}", dynamicId, operatorId);
        return socialFacadeService.deleteDynamic(dynamicId, operatorId);
    }

    // =================== 动态查询 ===================

    @PostMapping("/query")
    @Operation(summary = "分页查询动态列表", description = "支持多条件分页查询动态")
    public PageResponse<SocialDynamicResponse> queryDynamics(@Validated @RequestBody SocialDynamicQueryRequest request) {
        log.debug("REST分页查询动态: 页码={}, 大小={}", request.getCurrentPage(), request.getPageSize());
        return socialFacadeService.queryDynamics(request);
    }

    @GetMapping("/latest")
    @Operation(summary = "查询最新动态列表", description = "获取最新发布的动态列表")
    public PageResponse<SocialDynamicResponse> getLatestDynamics(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "动态类型") @RequestParam(required = false) String dynamicType) {
        log.debug("REST查询最新动态: 页码={}, 大小={}, 类型={}", currentPage, pageSize, dynamicType);
        return socialFacadeService.getLatestDynamics(currentPage, pageSize, dynamicType);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取动态详情", description = "根据ID获取动态详情")
    public Result<SocialDynamicResponse> getDynamicById(@PathVariable("id") Long dynamicId,
                                                       @Parameter(description = "是否包含已删除的动态") @RequestParam(defaultValue = "false") Boolean includeDeleted) {
        log.debug("REST获取动态详情: ID={}", dynamicId);
        return socialFacadeService.getDynamicById(dynamicId, includeDeleted);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询动态", description = "获取指定用户的动态列表")
    public PageResponse<SocialDynamicResponse> getUserDynamics(
            @PathVariable Long userId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "动态类型") @RequestParam(required = false) String dynamicType) {
        log.debug("REST查询用户动态: 用户={}, 页码={}, 大小={}", userId, currentPage, pageSize);
        return socialFacadeService.getUserDynamics(userId, currentPage, pageSize, dynamicType);
    }

    // =================== 互动功能 ===================

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞动态", description = "为指定动态点赞")
    public Result<Void> likeDynamic(@PathVariable("id") Long dynamicId, 
                                   @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.info("REST点赞动态: 动态={}, 用户={}", dynamicId, userId);
        return socialFacadeService.likeDynamic(dynamicId, userId);
    }

    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞", description = "取消对指定动态的点赞")
    public Result<Void> unlikeDynamic(@PathVariable("id") Long dynamicId, 
                                     @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.info("REST取消点赞: 动态={}, 用户={}", dynamicId, userId);
        return socialFacadeService.unlikeDynamic(dynamicId, userId);
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "评论动态", description = "为指定动态添加评论")
    public Result<Void> commentDynamic(@PathVariable("id") Long dynamicId,
                                      @Parameter(description = "用户ID") @RequestParam Long userId,
                                      @Parameter(description = "评论内容") @RequestParam String content) {
        log.info("REST评论动态: 动态={}, 用户={}", dynamicId, userId);
        return socialFacadeService.commentDynamic(dynamicId, userId, content);
    }

    @GetMapping("/{id}/likes")
    @Operation(summary = "获取动态点赞记录", description = "分页查询动态的点赞用户列表")
    public PageResponse<Object> getDynamicLikes(@PathVariable("id") Long dynamicId,
                                               @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
                                               @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST查询动态点赞: 动态={}", dynamicId);
        return socialFacadeService.getDynamicLikes(dynamicId, currentPage, pageSize);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "获取动态评论记录", description = "分页查询动态的评论列表")
    public PageResponse<Object> getDynamicComments(@PathVariable("id") Long dynamicId,
                                                  @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
                                                  @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST查询动态评论: 动态={}", dynamicId);
        return socialFacadeService.getDynamicComments(dynamicId, currentPage, pageSize);
    }

    // =================== 统计功能 ===================

    @GetMapping("/{id}/statistics")
    @Operation(summary = "获取动态统计信息", description = "获取动态的点赞数、评论数、分享数等统计信息")
    public Result<Object> getDynamicStatistics(@PathVariable("id") Long dynamicId) {
        log.debug("REST查询动态统计: 动态={}", dynamicId);
        return socialFacadeService.getDynamicStatistics(dynamicId);
    }

    // =================== 聚合功能 ===================

    @GetMapping("/user/{userId}/interactions")
    @Operation(summary = "获取用户互动记录聚合列表", 
               description = "包含用户点赞别人、被别人点赞、用户评论别人、被别人评论的所有记录，按最新时间排序")
    public PageResponse<Object> getUserInteractions(
            @PathVariable Long userId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST查询用户互动记录: 用户={}, 页码={}, 大小={}", userId, currentPage, pageSize);
        return socialFacadeService.getUserInteractions(userId, currentPage, pageSize);
    }
} 