package com.gig.collide.favorite.controller;

import com.gig.collide.api.favorite.FavoriteFacadeService;
import com.gig.collide.api.favorite.request.*;
import com.gig.collide.api.favorite.response.FavoriteResponse;
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
 * 收藏管理控制器
 * 提供收藏相关的HTTP REST API接口
 * 
 * 主要功能：
 * - 用户收藏/取消收藏操作
 * - 收藏关系查询与管理
 * - 收藏列表获取（支持多种类型）
 * - 收藏统计信息查询
 * 
 * 支持收藏类型：
 * - CONTENT: 内容收藏（小说、漫画、视频等）
 * - USER: 用户收藏
 * - DYNAMIC: 动态收藏
 * - COMMENT: 评论收藏
 * - GOODS: 商品收藏
 * 
 * 注意：控制器层主要负责HTTP请求处理和参数验证，
 * 具体的业务逻辑由FavoriteFacadeService处理
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
@Tag(name = "收藏管理", description = "收藏相关的API接口")
public class FavoriteController {

    @Autowired
    private FavoriteFacadeService favoriteFacadeService;

    // =================== 收藏操作 ===================

    /**
     * 添加收藏
     * 
     * @param request 收藏请求参数，包含用户ID、收藏类型、目标ID等
     * @return 收藏操作结果，包含收藏信息
     */
    @PostMapping("/add")
    @Operation(summary = "添加收藏", description = "用户收藏内容、用户、动态等，支持多种收藏类型")
    public Result<FavoriteResponse> addFavorite(@Validated @RequestBody FavoriteCreateRequest request) {
        try {
            log.info("HTTP添加收藏: userId={}, favoriteType={}, targetId={}", 
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());
            
            // 委托给facade服务处理
            return favoriteFacadeService.addFavorite(request);
        } catch (Exception e) {
            log.error("添加收藏失败", e);
            return Result.error("ADD_FAVORITE_ERROR", "添加收藏失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏
     * 
     * @param request 取消收藏请求参数
     * @return 取消收藏操作结果
     */
    @PostMapping("/remove")
    @Operation(summary = "取消收藏", description = "用户取消收藏，移除收藏关系")
    public Result<Void> removeFavorite(@Validated @RequestBody FavoriteDeleteRequest request) {
        try {
            log.info("HTTP取消收藏: userId={}, favoriteType={}, targetId={}", 
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());
            
            // 委托给facade服务处理
            return favoriteFacadeService.removeFavorite(request);
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return Result.error("REMOVE_FAVORITE_ERROR", "取消收藏失败: " + e.getMessage());
        }
    }

    // =================== 收藏查询 ===================

    /**
     * 检查收藏状态
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否已收藏
     */
    @GetMapping("/check")
    @Operation(summary = "检查收藏状态", description = "检查用户是否已收藏指定内容")
    public Result<Boolean> checkFavoriteStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        try {
            log.debug("HTTP检查收藏状态: userId={}, favoriteType={}, targetId={}", 
                    userId, favoriteType, targetId);
            
            return favoriteFacadeService.checkFavoriteStatus(userId, favoriteType, targetId);
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return Result.error("CHECK_FAVORITE_ERROR", "检查收藏状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取收藏详情
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 收藏详细信息
     */
    @GetMapping("/detail")
    @Operation(summary = "获取收藏详情", description = "获取收藏的详细信息")
    public Result<FavoriteResponse> getFavoriteDetail(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        try {
            log.debug("HTTP获取收藏详情: userId={}, favoriteType={}, targetId={}", 
                    userId, favoriteType, targetId);
            
            return favoriteFacadeService.getFavoriteDetail(userId, favoriteType, targetId);
        } catch (Exception e) {
            log.error("获取收藏详情失败", e);
            return Result.error("GET_FAVORITE_ERROR", "获取收藏详情失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询收藏记录
     * 
     * @param userId 用户ID（可选）
     * @param favoriteType 收藏类型（可选）
     * @param targetId 目标ID（可选）
     * @param status 收藏状态（可选）
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 收藏记录分页列表
     */
    @GetMapping("/query")
    @Operation(summary = "分页查询收藏记录", description = "支持多种条件的收藏记录分页查询")
    public Result<PageResponse<FavoriteResponse>> queryFavorites(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "收藏类型") @RequestParam(required = false) String favoriteType,
            @Parameter(description = "目标ID") @RequestParam(required = false) Long targetId,
            @Parameter(description = "收藏状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP分页查询收藏记录: userId={}, favoriteType={}, page={}, size={}", 
                    userId, favoriteType, page, size);
            
            // 创建查询请求
            FavoriteQueryRequest request = new FavoriteQueryRequest();
            request.setUserId(userId);
            request.setFavoriteType(favoriteType);
            request.setTargetId(targetId);
            request.setStatus(status);
            request.setPageNum(page);
            request.setPageSize(size);
            
            return favoriteFacadeService.queryFavorites(request);
        } catch (Exception e) {
            log.error("分页查询收藏记录失败", e);
            return Result.error("QUERY_FAVORITES_ERROR", "分页查询收藏记录失败: " + e.getMessage());
        }
    }

    // =================== 收藏列表 ===================

    /**
     * 获取用户收藏列表
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 用户收藏分页列表
     */
    @GetMapping("/user")
    @Operation(summary = "获取用户收藏列表", description = "获取用户的收藏分页列表，支持按类型筛选")
    public Result<PageResponse<FavoriteResponse>> getUserFavorites(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型，不传则查询所有类型") @RequestParam(required = false) String favoriteType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP获取用户收藏列表: userId={}, favoriteType={}, page={}, size={}", 
                    userId, favoriteType, page, size);
            
            return favoriteFacadeService.getUserFavorites(userId, favoriteType, page, size);
        } catch (Exception e) {
            log.error("获取用户收藏列表失败", e);
            return Result.error("GET_USER_FAVORITES_ERROR", "获取用户收藏列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取内容的收藏用户列表
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标内容ID
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 收藏了该内容的用户分页列表
     */
    @GetMapping("/target")
    @Operation(summary = "获取内容收藏用户列表", description = "获取收藏了指定内容的用户分页列表")
    public Result<PageResponse<FavoriteResponse>> getTargetFavorites(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标内容ID") @RequestParam Long targetId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP获取内容收藏用户列表: favoriteType={}, targetId={}, page={}, size={}", 
                    favoriteType, targetId, page, size);
            
            return favoriteFacadeService.getTargetFavorites(favoriteType, targetId, page, size);
        } catch (Exception e) {
            log.error("获取内容收藏用户列表失败", e);
            return Result.error("GET_TARGET_FAVORITES_ERROR", "获取内容收藏用户列表失败: " + e.getMessage());
        }
    }

    // =================== 统计信息 ===================

    /**
     * 获取用户收藏统计
     * 
     * @param userId 用户ID
     * @return 用户收藏统计信息，包含各类型收藏数量
     */
    @GetMapping("/user/statistics")
    @Operation(summary = "获取用户收藏统计", description = "获取用户的收藏统计信息，包括各种类型的收藏数量")
    public Result<Map<String, Object>> getUserFavoriteStatistics(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            log.debug("HTTP获取用户收藏统计: userId={}", userId);
            
            return favoriteFacadeService.getUserFavoriteStatistics(userId);
        } catch (Exception e) {
            log.error("获取用户收藏统计失败", e);
            return Result.error("GET_USER_STATISTICS_ERROR", "获取用户收藏统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏数量
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 用户收藏数量
     */
    @GetMapping("/user/count")
    @Operation(summary = "获取用户收藏数量", description = "获取用户的收藏数量统计")
    public Result<Long> getUserFavoriteCount(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型，不传则统计所有类型") @RequestParam(required = false) String favoriteType) {
        try {
            log.debug("HTTP获取用户收藏数量: userId={}, favoriteType={}", userId, favoriteType);
            
            return favoriteFacadeService.getUserFavoriteCount(userId, favoriteType);
        } catch (Exception e) {
            log.error("获取用户收藏数量失败", e);
            return Result.error("GET_USER_COUNT_ERROR", "获取用户收藏数量失败: " + e.getMessage());
        }
    }

    /**
     * 获取内容被收藏数量
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标内容ID
     * @return 内容被收藏数量
     */
    @GetMapping("/target/count")
    @Operation(summary = "获取内容被收藏数量", description = "获取指定内容的被收藏数量统计")
    public Result<Long> getTargetFavoriteCount(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标内容ID") @RequestParam Long targetId) {
        try {
            log.debug("HTTP获取内容被收藏数量: favoriteType={}, targetId={}", favoriteType, targetId);
            
            return favoriteFacadeService.getTargetFavoriteCount(favoriteType, targetId);
        } catch (Exception e) {
            log.error("获取内容被收藏数量失败", e);
            return Result.error("GET_TARGET_COUNT_ERROR", "获取内容被收藏数量失败: " + e.getMessage());
        }
    }
}