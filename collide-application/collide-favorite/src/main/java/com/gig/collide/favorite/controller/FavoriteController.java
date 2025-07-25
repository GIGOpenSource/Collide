package com.gig.collide.favorite.controller;

import com.gig.collide.api.favorite.request.FavoriteRequest;
import com.gig.collide.api.favorite.request.FavoriteQueryRequest;
import com.gig.collide.api.favorite.response.FavoriteResponse;
import com.gig.collide.api.favorite.response.data.FavoriteInfo;
import com.gig.collide.api.favorite.service.FavoriteFacadeService;
import com.gig.collide.base.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏服务控制器
 * 
 * @author Collide
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/favorite")
@RequiredArgsConstructor
@Tag(name = "收藏服务", description = "收藏相关接口")
public class FavoriteController {
    
    private final FavoriteFacadeService favoriteFacadeService;
    
    @PostMapping("/action")
    @Operation(summary = "收藏/取消收藏", description = "执行收藏或取消收藏操作")
    public FavoriteResponse favorite(@Valid @RequestBody FavoriteRequest favoriteRequest) {
        return favoriteFacadeService.favorite(favoriteRequest);
    }
    
    @PostMapping("/batch-action")
    @Operation(summary = "批量收藏", description = "批量收藏多个目标对象")
    public FavoriteResponse batchFavorite(@Valid @RequestBody FavoriteRequest favoriteRequest) {
        return favoriteFacadeService.batchFavorite(favoriteRequest);
    }
    
    @PatchMapping("/{favoriteId}/move")
    @Operation(summary = "移动收藏", description = "将收藏移动到其他收藏夹")
    public FavoriteResponse moveFavorite(
            @Parameter(description = "收藏ID") @PathVariable Long favoriteId,
            @Parameter(description = "目标收藏夹ID") @RequestParam Long targetFolderId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return favoriteFacadeService.moveFavorite(favoriteId, targetFolderId, userId);
    }
    
    @PostMapping("/query")
    @Operation(summary = "分页查询收藏列表", description = "根据条件分页查询用户收藏列表")
    public PageResponse<FavoriteInfo> pageQueryFavorites(@Valid @RequestBody FavoriteQueryRequest queryRequest) {
        return favoriteFacadeService.pageQueryFavorites(queryRequest);
    }
    
    @GetMapping("/{favoriteId}")
    @Operation(summary = "查询收藏详情", description = "获取指定收藏的详细信息")
    public FavoriteInfo queryFavoriteDetail(
            @Parameter(description = "收藏ID") @PathVariable Long favoriteId,
            @Parameter(description = "查询用户ID") @RequestParam Long userId) {
        return favoriteFacadeService.queryFavoriteDetail(favoriteId, userId);
    }
    
    @GetMapping("/check-status")
    @Operation(summary = "检查收藏状态", description = "检查用户是否已收藏指定对象")
    public Boolean checkFavorited(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return favoriteFacadeService.checkFavorited(favoriteType, targetId, userId);
    }
    
    @GetMapping("/users/{userId}/count")
    @Operation(summary = "统计用户收藏数量", description = "获取指定用户的收藏总数")
    public Long countUserFavorites(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "收藏类型") @RequestParam(required = false) String favoriteType) {
        return favoriteFacadeService.countUserFavorites(userId, favoriteType);
    }
    
    @GetMapping("/targets/{targetId}/count")
    @Operation(summary = "统计目标被收藏数量", description = "获取指定目标对象的被收藏总数")
    public Long countTargetFavorites(
            @Parameter(description = "收藏类型") @RequestParam String favoriteType,
            @Parameter(description = "目标ID") @PathVariable Long targetId) {
        return favoriteFacadeService.countTargetFavorites(favoriteType, targetId);
    }
} 