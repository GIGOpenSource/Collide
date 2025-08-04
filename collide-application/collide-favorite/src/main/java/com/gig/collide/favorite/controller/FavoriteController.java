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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏管理控制器 - 简洁版
 * 基于favorite-simple.sql的单表设计，实现核心收藏功能
 * 通过门面服务提供HTTP接口，包含分布式缓存和统一响应格式
 * 
 * 主要功能：
 * - 基础收藏操作：添加、取消、查询
 * - 收藏列表：用户收藏、目标收藏、复合条件查询
 * - 统计功能：收藏数量、统计信息、批量检查
 * - 搜索功能：标题搜索、热门排行
 * - 管理功能：数据同步、关系检查、重新激活
 * 
 * 支持收藏类型：
 * - CONTENT: 内容收藏（小说、漫画、视频等）
 * - GOODS: 商品收藏
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
@Validated
public class FavoriteController {

    private final FavoriteFacadeService favoriteFacadeService;

    // =================== 收藏操作 ===================

    /**
     * 添加收藏
     * 
     * @param request 收藏请求参数
     * @return 收藏操作结果
     */
    @PostMapping("/add")
    @Operation(summary = "添加收藏", description = "支持收藏对象和用户信息冗余存储")
    public Result<FavoriteResponse> addFavorite(@RequestBody FavoriteCreateRequest request) {
        try {
            log.info("REST请求 - 添加收藏: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());
            
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
    @Operation(summary = "取消收藏", description = "将收藏状态更新为cancelled")
    public Result<Void> removeFavorite(@RequestBody FavoriteDeleteRequest request) {
        try {
            log.info("REST请求 - 取消收藏: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());
            
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
    @Operation(summary = "检查收藏状态", description = "查询用户是否已收藏目标对象")
    public Result<Boolean> checkFavoriteStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        try {
            log.info("REST请求 - 检查收藏状态: 用户={}, 类型={}, 目标={}", 
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
            log.info("REST请求 - 获取收藏详情: 用户={}, 类型={}, 目标={}", 
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
     * @param targetTitle 目标标题关键词（可选）
     * @param targetAuthorId 目标作者ID（可选）
     * @param userNickname 用户昵称关键词（可选）
     * @param status 收藏状态（可选）
     * @param queryType 查询类型（可选）
     * @param orderBy 排序字段（可选）
     * @param orderDirection 排序方向（可选）
     * @param currentPage 页码
     * @param pageSize 每页大小
     * @return 收藏记录分页列表
     */
    @GetMapping("/query")
    @Operation(summary = "分页查询收藏记录", description = "支持按用户、类型、状态等条件查询")
    public Result<PageResponse<FavoriteResponse>> queryFavorites(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "收藏类型") @RequestParam(required = false) String favoriteType,
            @Parameter(description = "目标ID") @RequestParam(required = false) Long targetId,
            @Parameter(description = "目标标题关键词") @RequestParam(required = false) String targetTitle,
            @Parameter(description = "目标作者ID") @RequestParam(required = false) Long targetAuthorId,
            @Parameter(description = "用户昵称关键词") @RequestParam(required = false) String userNickname,
            @Parameter(description = "收藏状态") @RequestParam(required = false) String status,
            @Parameter(description = "查询类型") @RequestParam(required = false) String queryType,
            @Parameter(description = "排序字段") @RequestParam(required = false) String orderBy,
            @Parameter(description = "排序方向") @RequestParam(required = false) String orderDirection,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 分页查询收藏记录: 用户={}, 类型={}, 页码={}/{}", 
                    userId, favoriteType, currentPage, pageSize);
            
            // 创建查询请求
            FavoriteQueryRequest request = new FavoriteQueryRequest();
            request.setUserId(userId);
            request.setFavoriteType(favoriteType);
            request.setTargetId(targetId);
            request.setTargetTitle(targetTitle);
            request.setTargetAuthorId(targetAuthorId);
            request.setUserNickname(userNickname);
            request.setStatus(status);
            request.setQueryType(queryType);
            request.setOrderBy(orderBy);
            request.setOrderDirection(orderDirection);
            request.setCurrentPage(currentPage);
            request.setPageSize(pageSize);
            
            return favoriteFacadeService.queryFavorites(request);
        } catch (Exception e) {
            log.error("分页查询收藏记录失败", e);
            return Result.error("QUERY_FAVORITES_ERROR", "分页查询收藏记录失败: " + e.getMessage());
        }
    }

    // =================== 收藏列表 ===================

    /**
     * 获取用户的收藏列表
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 收藏列表
     */
    @GetMapping("/user")
    @Operation(summary = "获取用户的收藏列表", description = "查询某用户的所有收藏")
    public Result<PageResponse<FavoriteResponse>> getUserFavorites(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型（可选）") @RequestParam(required = false) String favoriteType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 获取用户收藏列表: 用户={}, 类型={}, 页码={}/{}", 
                    userId, favoriteType, currentPage, pageSize);
            
            return favoriteFacadeService.getUserFavorites(userId, favoriteType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取用户收藏列表失败", e);
            return Result.error("GET_USER_FAVORITES_ERROR", "获取用户收藏列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取目标对象的收藏列表
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 收藏用户列表
     */
    @GetMapping("/target")
    @Operation(summary = "获取目标对象的收藏列表", description = "查询收藏某个对象的所有用户")
    public Result<PageResponse<FavoriteResponse>> getTargetFavorites(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 获取目标收藏列表: 类型={}, 目标={}, 页码={}/{}", 
                    favoriteType, targetId, currentPage, pageSize);
            
            return favoriteFacadeService.getTargetFavorites(favoriteType, targetId, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取目标收藏列表失败", e);
            return Result.error("GET_TARGET_FAVORITES_ERROR", "获取目标收藏列表失败: " + e.getMessage());
        }
    }

    // =================== 统计信息 ===================

    /**
     * 获取用户收藏数量
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型（可选）
     * @return 收藏数量
     */
    @GetMapping("/user/count")
    @Operation(summary = "获取用户收藏数量", description = "统计用户收藏的数量")
    public Result<Long> getUserFavoriteCount(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型（可选）") @RequestParam(required = false) String favoriteType) {
        try {
            log.info("REST请求 - 获取用户收藏数量: 用户={}, 类型={}", userId, favoriteType);
            
            return favoriteFacadeService.getUserFavoriteCount(userId, favoriteType);
        } catch (Exception e) {
            log.error("获取用户收藏数量失败", e);
            return Result.error("GET_USER_COUNT_ERROR", "获取用户收藏数量失败: " + e.getMessage());
        }
    }

    /**
     * 获取目标对象被收藏数量
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 被收藏数量
     */
    @GetMapping("/target/count")
    @Operation(summary = "获取目标对象被收藏数量", description = "统计某个对象被收藏的次数")
    public Result<Long> getTargetFavoriteCount(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        try {
            log.info("REST请求 - 获取目标被收藏数量: 类型={}, 目标={}", favoriteType, targetId);
            
            return favoriteFacadeService.getTargetFavoriteCount(favoriteType, targetId);
        } catch (Exception e) {
            log.error("获取目标被收藏数量失败", e);
            return Result.error("GET_TARGET_COUNT_ERROR", "获取目标被收藏数量失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息Map (type -> count)
     */
    @GetMapping("/user/statistics")
    @Operation(summary = "获取用户收藏统计信息", description = "包含各类型收藏数量统计")
    public Result<Map<String, Object>> getUserFavoriteStatistics(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            log.info("REST请求 - 获取用户收藏统计: 用户={}", userId);
            
            return favoriteFacadeService.getUserFavoriteStatistics(userId);
        } catch (Exception e) {
            log.error("获取用户收藏统计失败", e);
            return Result.error("GET_USER_STATISTICS_ERROR", "获取用户收藏统计失败: " + e.getMessage());
        }
    }

    /**
     * 批量检查收藏状态
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetIds 目标ID列表
     * @return 收藏状态Map (targetId -> isFavorited)
     */
    @PostMapping("/batch-check")
    @Operation(summary = "批量检查收藏状态", description = "检查用户对多个目标对象的收藏状态")
    public Result<Map<Long, Boolean>> batchCheckFavoriteStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @RequestBody List<Long> targetIds) {
        try {
            log.info("REST请求 - 批量检查收藏状态: 用户={}, 类型={}, 数量={}", 
                    userId, favoriteType, targetIds != null ? targetIds.size() : 0);
            
            return favoriteFacadeService.batchCheckFavoriteStatus(userId, favoriteType, targetIds);
        } catch (Exception e) {
            log.error("批量检查收藏状态失败", e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查收藏状态失败: " + e.getMessage());
        }
    }

    // =================== 搜索功能 ===================

    /**
     * 根据标题搜索收藏
     * 
     * @param userId 用户ID
     * @param titleKeyword 标题关键词
     * @param favoriteType 收藏类型（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "根据标题搜索收藏", description = "根据收藏对象标题进行模糊搜索")
    public Result<PageResponse<FavoriteResponse>> searchFavoritesByTitle(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "标题关键词") @RequestParam String titleKeyword,
            @Parameter(description = "收藏类型（可选）") @RequestParam(required = false) String favoriteType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 搜索收藏: 用户={}, 关键词={}, 类型={}, 页码={}/{}", 
                    userId, titleKeyword, favoriteType, currentPage, pageSize);
            
            return favoriteFacadeService.searchFavoritesByTitle(userId, titleKeyword, favoriteType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("搜索收藏失败", e);
            return Result.error("SEARCH_FAVORITES_ERROR", "搜索收藏失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门收藏对象
     * 
     * @param favoriteType 收藏类型
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 热门收藏对象列表
     */
    @GetMapping("/popular")
    @Operation(summary = "获取热门收藏对象", description = "查询被收藏次数最多的对象")
    public Result<PageResponse<FavoriteResponse>> getPopularFavorites(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 获取热门收藏: 类型={}, 页码={}/{}", 
                    favoriteType, currentPage, pageSize);
            
            return favoriteFacadeService.getPopularFavorites(favoriteType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("获取热门收藏失败", e);
            return Result.error("GET_POPULAR_ERROR", "获取热门收藏失败: " + e.getMessage());
        }
    }

    // =================== 管理功能 ===================

    /**
     * 清理已取消的收藏记录
     * 
     * @param days 保留天数
     * @return 清理数量
     */
    @PostMapping("/clean")
    @Operation(summary = "清理已取消的收藏记录", description = "物理删除cancelled状态的记录（可选功能）")
    public Result<Integer> cleanCancelledFavorites(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer days) {
        try {
            log.info("REST请求 - 清理已取消收藏记录: 保留天数={}", days);
            
            return favoriteFacadeService.cleanCancelledFavorites(days);
        } catch (Exception e) {
            log.error("清理已取消收藏记录失败", e);
            return Result.error("CLEAN_FAVORITES_ERROR", "清理记录失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息（冗余字段同步）
     * 
     * @param userId 用户ID
     * @param nickname 新昵称
     * @return 更新成功的记录数
     */
    @PutMapping("/user/info")
    @Operation(summary = "更新用户信息", description = "当用户信息变更时，同步更新收藏表中的冗余信息")
    public Result<Integer> updateUserInfo(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "新昵称") @RequestParam String nickname) {
        try {
            log.info("REST请求 - 更新用户信息: 用户={}, 昵称={}", userId, nickname);
            
            return favoriteFacadeService.updateUserInfo(userId, nickname);
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("UPDATE_USER_INFO_ERROR", "更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新目标对象信息（冗余字段同步）
     * 
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @param title 新标题
     * @param cover 新封面
     * @param authorId 新作者ID
     * @return 更新成功的记录数
     */
    @PutMapping("/target/info")
    @Operation(summary = "更新目标对象信息", description = "当目标对象信息变更时，同步更新收藏表中的冗余信息")
    public Result<Integer> updateTargetInfo(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "新标题") @RequestParam(required = false) String title,
            @Parameter(description = "新封面") @RequestParam(required = false) String cover,
            @Parameter(description = "新作者ID") @RequestParam(required = false) Long authorId) {
        try {
            log.info("REST请求 - 更新目标对象信息: 类型={}, 目标={}, 标题={}", 
                    favoriteType, targetId, title);
            
            return favoriteFacadeService.updateTargetInfo(favoriteType, targetId, title, cover, authorId);
        } catch (Exception e) {
            log.error("更新目标对象信息失败", e);
            return Result.error("UPDATE_TARGET_INFO_ERROR", "更新目标对象信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据作者查询收藏作品
     * 
     * @param targetAuthorId 作者ID
     * @param favoriteType 收藏类型（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 收藏作品列表
     */
    @GetMapping("/author")
    @Operation(summary = "根据作者查询收藏作品", description = "查询某作者的作品被收藏情况")
    public Result<PageResponse<FavoriteResponse>> getFavoritesByAuthor(
            @Parameter(description = "作者ID") @RequestParam Long targetAuthorId,
            @Parameter(description = "收藏类型（可选）") @RequestParam(required = false) String favoriteType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        try {
            log.info("REST请求 - 查询作者收藏作品: 作者={}, 类型={}, 页码={}/{}", 
                    targetAuthorId, favoriteType, currentPage, pageSize);
            
            return favoriteFacadeService.getFavoritesByAuthor(targetAuthorId, favoriteType, currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询作者收藏作品失败", e);
            return Result.error("GET_FAVORITES_BY_AUTHOR_ERROR", "查询作者收藏作品失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否已经存在收藏关系
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否存在收藏关系
     */
    @GetMapping("/relation/exists")
    @Operation(summary = "检查收藏关系是否存在", description = "包括已取消的收藏关系")
    public Result<Boolean> existsFavoriteRelation(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        try {
            log.info("REST请求 - 检查收藏关系: 用户={}, 类型={}, 目标={}", 
                    userId, favoriteType, targetId);
            
            return favoriteFacadeService.existsFavoriteRelation(userId, favoriteType, targetId);
        } catch (Exception e) {
            log.error("检查收藏关系失败", e);
            return Result.error("CHECK_RELATION_ERROR", "检查收藏关系失败: " + e.getMessage());
        }
    }

    /**
     * 重新激活已取消的收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 是否成功重新激活
     */
    @PostMapping("/reactivate")
    @Operation(summary = "重新激活已取消的收藏", description = "将cancelled状态的收藏重新设置为active")
    public Result<Boolean> reactivateFavorite(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        try {
            log.info("REST请求 - 重新激活收藏: 用户={}, 类型={}, 目标={}", 
                    userId, favoriteType, targetId);
            
            return favoriteFacadeService.reactivateFavorite(userId, favoriteType, targetId);
        } catch (Exception e) {
            log.error("重新激活收藏失败", e);
            return Result.error("REACTIVATE_ERROR", "重新激活收藏失败: " + e.getMessage());
        }
    }

    /**
     * 验证收藏请求参数
     * 
     * @param request 收藏请求
     * @return 验证结果信息
     */
    @PostMapping("/validate")
    @Operation(summary = "验证收藏请求参数", description = "校验请求参数的有效性")
    public Result<String> validateFavoriteRequest(@RequestBody FavoriteCreateRequest request) {
        try {
            log.info("REST请求 - 验证收藏请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());
            
            return favoriteFacadeService.validateFavoriteRequest(request);
        } catch (Exception e) {
            log.error("验证收藏请求失败", e);
            return Result.error("VALIDATION_ERROR", "验证收藏请求失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否可以收藏
     * 
     * @param userId 用户ID
     * @param favoriteType 收藏类型
     * @param targetId 目标ID
     * @return 检查结果信息
     */
    @GetMapping("/can-favorite")
    @Operation(summary = "检查是否可以收藏", description = "检查业务规则是否允许收藏")
    public Result<String> checkCanFavorite(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId) {
        try {
            log.info("REST请求 - 检查收藏权限: 用户={}, 类型={}, 目标={}", 
                    userId, favoriteType, targetId);
            
            return favoriteFacadeService.checkCanFavorite(userId, favoriteType, targetId);
        } catch (Exception e) {
            log.error("检查收藏权限失败", e);
            return Result.error("CHECK_PERMISSION_ERROR", "检查收藏权限失败: " + e.getMessage());
        }
    }
}