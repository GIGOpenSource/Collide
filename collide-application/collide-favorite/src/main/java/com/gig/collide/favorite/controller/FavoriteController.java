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
 * 收藏管理控制器 - 缓存增强版
 * 对齐follow模块设计风格，通过门面服务提供HTTP接口
 * 包含缓存功能、统一响应格式、错误处理
 * 
 * 主要功能：
 * - 用户收藏/取消收藏操作 💡 缓存优化
 * - 收藏关系查询与管理 💡 缓存优化
 * - 收藏列表获取（支持多种类型）💡 缓存优化
 * - 收藏统计信息查询 💡 缓存优化
 * - 特殊的内容收藏检测接口 🔥 新功能
 * 
 * 支持收藏类型：
 * - CONTENT: 内容收藏（小说、漫画、视频等）
 * - USER: 用户收藏
 * - DYNAMIC: 动态收藏
 * - COMMENT: 评论收藏
 * - GOODS: 商品收藏
 * 
 * 注意：控制器层主要负责HTTP请求处理和参数验证，
 * 具体的业务逻辑由FavoriteFacadeService处理，包含分布式缓存
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
@Tag(name = "收藏管理", description = "收藏相关的API接口 - 缓存增强版")
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
    @Operation(summary = "添加收藏 💡 缓存优化", description = "用户收藏内容、用户、动态等，支持多种收藏类型")
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
    @Operation(summary = "取消收藏 💡 缓存优化", description = "用户取消收藏，移除收藏关系")
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
    @Operation(summary = "检查收藏状态 💡 缓存优化", description = "检查用户是否已收藏指定内容")
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
    @Operation(summary = "获取收藏详情 💡 缓存优化", description = "获取收藏的详细信息")
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
    @Operation(summary = "分页查询收藏记录 💡 缓存优化", description = "支持多种条件的收藏记录分页查询")
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
            request.setCurrentPage(page);
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
    @Operation(summary = "获取用户收藏列表 💡 缓存优化", description = "获取用户的收藏分页列表，支持按类型筛选")
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
    @Operation(summary = "获取内容收藏用户列表 💡 缓存优化", description = "获取收藏了指定内容的用户分页列表")
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
    @Operation(summary = "获取用户收藏统计 💡 缓存优化", description = "获取用户的收藏统计信息，包括各种类型的收藏数量")
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
    @Operation(summary = "获取用户收藏数量 💡 缓存优化", description = "获取用户的收藏数量统计")
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
    @Operation(summary = "获取内容被收藏数量 💡 缓存优化", description = "获取指定内容的被收藏数量统计")
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

    // =================== 特殊内容收藏检测接口 🔥 ===================

    /**
     * 检测内容是否被特定用户收藏
     * 
     * @param contentId 内容ID
     * @param checkUserId 检测用户ID（潜在收藏者）
     * @return 是否被收藏
     */
    @GetMapping("/detect/content/is-favorited-by")
    @Operation(summary = "检测内容是否被特定用户收藏 🔥", description = "检测内容是否被特定用户收藏")
    public Result<Boolean> isContentFavoritedBy(
            @Parameter(description = "内容ID") @RequestParam Long contentId,
            @Parameter(description = "检测用户ID（潜在收藏者）") @RequestParam Long checkUserId) {
        try {
            log.debug("HTTP检测内容是否被收藏: contentId={}, checkUserId={}", contentId, checkUserId);
            
            // 实际上就是检查checkUserId是否收藏了contentId
            return favoriteFacadeService.checkFavoriteStatus(checkUserId, "CONTENT", contentId);
        } catch (Exception e) {
            log.error("检测内容是否被收藏失败", e);
            return Result.error("DETECT_CONTENT_FAVORITED_ERROR", "检测内容是否被收藏失败: " + e.getMessage());
        }
    }

    /**
     * 检测用户是否收藏了特定内容
     * 
     * @param userId 用户ID（收藏者）
     * @param contentId 内容ID
     * @return 是否已收藏
     */
    @GetMapping("/detect/user/is-favoriting")
    @Operation(summary = "检测用户是否收藏了特定内容 🔥", description = "检测用户是否收藏了特定内容")
    public Result<Boolean> isUserFavoritingContent(
            @Parameter(description = "用户ID（收藏者）") @RequestParam Long userId,
            @Parameter(description = "内容ID") @RequestParam Long contentId) {
        try {
            log.debug("HTTP检测用户是否收藏内容: userId={}, contentId={}", userId, contentId);
            
            return favoriteFacadeService.checkFavoriteStatus(userId, "CONTENT", contentId);
        } catch (Exception e) {
            log.error("检测用户是否收藏内容失败", e);
            return Result.error("DETECT_USER_FAVORITING_ERROR", "检测用户是否收藏内容失败: " + e.getMessage());
        }
    }

    /**
     * 检测内容收藏关系状态
     * 
     * @param contentId 内容ID
     * @param userId 用户ID
     * @return 收藏关系详细状态
     */
    @GetMapping("/detect/content/relationship")
    @Operation(summary = "检测内容收藏关系状态 🔥", description = "检测内容与用户之间的收藏关系状态")
    public Result<Map<String, Object>> detectContentFavoriteRelationship(
            @Parameter(description = "内容ID") @RequestParam Long contentId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            log.debug("HTTP检测内容收藏关系: contentId={}, userId={}", contentId, userId);
            
            // 检查用户是否收藏了内容
            Result<Boolean> isFavorited = favoriteFacadeService.checkFavoriteStatus(userId, "CONTENT", contentId);
            // 获取内容被收藏总数
            Result<Long> favoriteCount = favoriteFacadeService.getTargetFavoriteCount("CONTENT", contentId);
            
            if (!isFavorited.getSuccess() || !favoriteCount.getSuccess()) {
                return Result.error("DETECT_RELATIONSHIP_ERROR", "检测收藏关系失败");
            }
            
            Map<String, Object> relationship = Map.of(
                "contentId", contentId,
                "userId", userId,
                "isFavorited", isFavorited.getData(),
                "totalFavoriteCount", favoriteCount.getData(),
                "favoriteType", "CONTENT"
            );
            
            log.debug("内容收藏关系检测完成: contentId={}, userId={}, favorited={}", 
                    contentId, userId, relationship.get("isFavorited"));
            
            return Result.success(relationship);
        } catch (Exception e) {
            log.error("检测内容收藏关系失败", e);
            return Result.error("DETECT_RELATIONSHIP_ERROR", "检测内容收藏关系失败: " + e.getMessage());
        }
    }

    /**
     * 批量检测内容收藏状态
     * 
     * @param userId 当前用户ID
     * @param contentIds 内容ID列表
     * @return 收藏状态映射和统计信息
     */
    @PostMapping("/detect/content/batch-status")
    @Operation(summary = "批量检测内容收藏状态 🔥", description = "批量检测用户对多个内容的收藏状态")
    public Result<Map<String, Object>> batchDetectContentFavoriteStatus(
            @Parameter(description = "当前用户ID") @RequestParam Long userId,
            @RequestBody List<Long> contentIds) {
        try {
            log.info("HTTP批量检测内容收藏状态: userId={}, 内容数量={}", userId, 
                    contentIds != null ? contentIds.size() : 0);
            
            Result<Map<Long, Boolean>> batchResult = favoriteFacadeService.batchCheckFavoriteStatus(
                    userId, "CONTENT", contentIds);
            
            if (!batchResult.getSuccess()) {
                return Result.error(batchResult.getCode(), batchResult.getMessage());
            }
            
            Map<Long, Boolean> statusMap = batchResult.getData();
            
            // 统计信息
            long favoritedCount = statusMap.values().stream().mapToLong(b -> b ? 1 : 0).sum();
            long notFavoritedCount = statusMap.size() - favoritedCount;
            
            Map<String, Object> result = Map.of(
                "statusMap", statusMap,
                "statistics", Map.of(
                    "totalChecked", statusMap.size(),
                    "favoritedCount", favoritedCount,
                    "notFavoritedCount", notFavoritedCount,
                    "favoritedRate", statusMap.isEmpty() ? 0.0 : (double) favoritedCount / statusMap.size()
                )
            );
            
            log.info("批量内容收藏状态检测完成: userId={}, 收藏数={}/{}", userId, favoritedCount, statusMap.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量检测内容收藏状态失败", e);
            return Result.error("BATCH_DETECT_CONTENT_ERROR", "批量检测内容收藏状态失败: " + e.getMessage());
        }
    }

    /**
     * 检测内容收藏热度
     * 
     * @param contentId 内容ID
     * @param days 统计天数
     * @return 内容收藏热度信息
     */
    @GetMapping("/detect/content/popularity")
    @Operation(summary = "检测内容收藏热度 🔥", description = "检测内容在指定时间内的收藏热度")
    public Result<Map<String, Object>> detectContentFavoritePopularity(
            @Parameter(description = "内容ID") @RequestParam Long contentId,
            @Parameter(description = "统计天数") @RequestParam(defaultValue = "7") Integer days) {
        try {
            log.debug("HTTP检测内容收藏热度: contentId={}, days={}", contentId, days);
            
            // 获取内容被收藏总数
            Result<Long> totalCount = favoriteFacadeService.getTargetFavoriteCount("CONTENT", contentId);
            if (!totalCount.getSuccess()) {
                return Result.error(totalCount.getCode(), totalCount.getMessage());
            }
            
            // 获取收藏该内容的用户列表（用于分析）
            Result<PageResponse<FavoriteResponse>> favoriters = favoriteFacadeService.getTargetFavorites(
                    "CONTENT", contentId, 1, 10);
            
            if (!favoriters.getSuccess()) {
                return Result.error(favoriters.getCode(), favoriters.getMessage());
            }
            
            // 构建热度信息
            long count = totalCount.getData();
            String popularityLevel = calculatePopularityLevel(count);
            
            Map<String, Object> popularityInfo = Map.of(
                "contentId", contentId,
                "statisticsDays", days,
                "totalFavoriteCount", count,
                "popularityLevel", popularityLevel,
                "recentFavoriters", favoriters.getData().getDatas().size(),
                "recommendations", generateContentRecommendations(count)
            );
            
            log.debug("内容收藏热度检测完成: contentId={}, level={}, count={}", 
                    contentId, popularityLevel, count);
            
            return Result.success(popularityInfo);
        } catch (Exception e) {
            log.error("检测内容收藏热度失败", e);
            return Result.error("DETECT_POPULARITY_ERROR", "检测内容收藏热度失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 计算内容热度等级
     */
    private String calculatePopularityLevel(long favoriteCount) {
        if (favoriteCount >= 1000) {
            return "VERY_HIGH";
        } else if (favoriteCount >= 100) {
            return "HIGH";
        } else if (favoriteCount >= 20) {
            return "MEDIUM";
        } else if (favoriteCount >= 5) {
            return "LOW";
        } else {
            return "VERY_LOW";
        }
    }

    /**
     * 生成内容推广建议
     */
    private List<String> generateContentRecommendations(long favoriteCount) {
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (favoriteCount == 0) {
            recommendations.add("内容需要更多曝光来获得首批收藏");
            recommendations.add("可以考虑在热门时段发布");
        } else if (favoriteCount < 10) {
            recommendations.add("内容获得了初步关注，继续保持质量");
            recommendations.add("可以尝试增加与用户的互动");
        } else if (favoriteCount < 50) {
            recommendations.add("内容表现良好，可以考虑推广");
            recommendations.add("建议制作相关系列内容");
        } else if (favoriteCount < 200) {
            recommendations.add("内容非常受欢迎，继续这类风格");
            recommendations.add("可以考虑商业化推广");
        } else {
            recommendations.add("内容已成为爆款，值得深度运营");
            recommendations.add("可以开发周边产品或衍生内容");
        }
        
        return recommendations;
    }
}