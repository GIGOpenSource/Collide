package com.gig.collide.favorite.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.favorite.FavoriteFacadeService;
import com.gig.collide.api.favorite.request.*;
import com.gig.collide.api.favorite.response.FavoriteResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.favorite.domain.entity.Favorite;
import com.gig.collide.favorite.domain.service.FavoriteService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏门面服务实现类 - 简洁版
 * 基于Dubbo RPC，提供收藏相关的远程服务调用
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class FavoriteFacadeServiceImpl implements FavoriteFacadeService {

    private final FavoriteService favoriteService;

    @Override
    public Result<FavoriteResponse> addFavorite(FavoriteCreateRequest request) {
        try {
            log.info("RPC添加收藏: userId={}, favoriteType={}, targetId={}", 
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());

            // 转换请求对象为实体
            Favorite favorite = convertCreateRequestToEntity(request);
            
            // 调用业务逻辑
            Favorite savedFavorite = favoriteService.addFavorite(favorite);
            
            // 转换响应对象
            FavoriteResponse response = convertToResponse(savedFavorite);
            
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("收藏参数验证失败: {}", e.getMessage());
            return Result.error("FAVORITE_PARAM_ERROR", e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("收藏状态检查失败: {}", e.getMessage());
            return Result.error("FAVORITE_STATE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("添加收藏失败", e);
            return Result.error("FAVORITE_CREATE_ERROR", "添加收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> removeFavorite(FavoriteDeleteRequest request) {
        try {
            log.info("RPC取消收藏: userId={}, favoriteType={}, targetId={}", 
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());

            boolean success = favoriteService.removeFavorite(
                    request.getUserId(), 
                    request.getFavoriteType(),
                    request.getTargetId(),
                    request.getCancelReason(),
                    request.getOperatorId()
            );
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("UNFAVORITE_FAILED", "取消收藏失败");
            }
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return Result.error("UNFAVORITE_ERROR", "取消收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkFavoriteStatus(Long userId, String favoriteType, Long targetId) {
        try {
            boolean isFavorited = favoriteService.checkFavoriteStatus(userId, favoriteType, targetId);
            return Result.success(isFavorited);
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return Result.error("CHECK_FAVORITE_ERROR", "检查收藏状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<FavoriteResponse> getFavoriteDetail(Long userId, String favoriteType, Long targetId) {
        try {
            Favorite favorite = favoriteService.getFavoriteDetail(userId, favoriteType, targetId);
            if (favorite == null) {
                return Result.error("FAVORITE_NOT_FOUND", "收藏记录不存在");
            }
            
            FavoriteResponse response = convertToResponse(favorite);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取收藏详情失败", e);
            return Result.error("GET_FAVORITE_ERROR", "获取收藏详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FavoriteResponse>> queryFavorites(FavoriteQueryRequest request) {
        try {
            log.info("RPC分页查询收藏记录: pageNum={}, pageSize={}", request.getPageNum(), request.getPageSize());

            IPage<Favorite> favoritePage = favoriteService.queryFavorites(
                    request.getPageNum(),
                    request.getPageSize(),
                    request.getUserId(),
                    request.getFavoriteType(),
                    request.getTargetId(),
                    request.getTargetTitle(),
                    request.getTargetAuthorId(),
                    request.getUserNickname(),
                    request.getStatus(),
                    request.getQueryType(),
                    request.getOrderBy(),
                    request.getOrderDirection()
            );

            // 转换分页响应
            PageResponse<FavoriteResponse> pageResponse = buildPageResult(favoritePage);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询收藏记录失败", e);
            return Result.error("FAVORITE_QUERY_ERROR", "查询收藏记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FavoriteResponse>> getUserFavorites(Long userId, String favoriteType, 
                                                                 Integer pageNum, Integer pageSize) {
        try {
            IPage<Favorite> favoritePage = favoriteService.getUserFavorites(userId, favoriteType, pageNum, pageSize);
            return Result.success(buildPageResult(favoritePage));
        } catch (Exception e) {
            log.error("获取用户收藏列表失败", e);
            return Result.error("GET_USER_FAVORITES_ERROR", "获取用户收藏列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FavoriteResponse>> getTargetFavorites(String favoriteType, Long targetId,
                                                                   Integer pageNum, Integer pageSize) {
        try {
            IPage<Favorite> favoritePage = favoriteService.getTargetFavorites(favoriteType, targetId, pageNum, pageSize);
            return Result.success(buildPageResult(favoritePage));
        } catch (Exception e) {
            log.error("获取目标收藏列表失败", e);
            return Result.error("GET_TARGET_FAVORITES_ERROR", "获取目标收藏列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getUserFavoriteCount(Long userId, String favoriteType) {
        try {
            Long count = favoriteService.getUserFavoriteCount(userId, favoriteType);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户收藏数量失败", e);
            return Result.error("GET_USER_FAVORITE_COUNT_ERROR", "获取用户收藏数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getTargetFavoriteCount(String favoriteType, Long targetId) {
        try {
            Long count = favoriteService.getTargetFavoriteCount(favoriteType, targetId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取目标被收藏数量失败", e);
            return Result.error("GET_TARGET_FAVORITE_COUNT_ERROR", "获取目标被收藏数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getUserFavoriteStatistics(Long userId) {
        try {
            Map<String, Object> statistics = favoriteService.getUserFavoriteStatistics(userId);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取收藏统计失败", e);
            return Result.error("GET_STATISTICS_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckFavoriteStatus(Long userId, String favoriteType, List<Long> targetIds) {
        try {
            Map<Long, Boolean> statusMap = favoriteService.batchCheckFavoriteStatus(userId, favoriteType, targetIds);
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查收藏状态失败", e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查收藏状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FavoriteResponse>> searchFavoritesByTitle(Long userId, String titleKeyword, 
                                                                       String favoriteType, Integer pageNum, Integer pageSize) {
        try {
            IPage<Favorite> favoritePage = favoriteService.searchFavoritesByTitle(userId, titleKeyword, favoriteType, pageNum, pageSize);
            return Result.success(buildPageResult(favoritePage));
        } catch (Exception e) {
            log.error("搜索收藏失败", e);
            return Result.error("SEARCH_FAVORITES_ERROR", "搜索收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<FavoriteResponse>> getPopularFavorites(String favoriteType, Integer pageNum, Integer pageSize) {
        try {
            IPage<Favorite> favoritePage = favoriteService.getPopularFavorites(favoriteType, pageNum, pageSize);
            return Result.success(buildPageResult(favoritePage));
        } catch (Exception e) {
            log.error("获取热门收藏失败", e);
            return Result.error("GET_POPULAR_ERROR", "获取热门收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> cleanCancelledFavorites(Integer days) {
        try {
            log.info("RPC清理已取消的收藏记录: days={}", days);
            int cleanedCount = favoriteService.cleanCancelledFavorites(days);
            return Result.success(cleanedCount);
        } catch (Exception e) {
            log.error("清理已取消收藏记录失败", e);
            return Result.error("CLEAN_FAVORITES_ERROR", "清理记录失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 将FavoriteCreateRequest转换为Favorite实体
     */
    private Favorite convertCreateRequestToEntity(FavoriteCreateRequest request) {
        Favorite favorite = new Favorite();
        BeanUtils.copyProperties(request, favorite);
        return favorite;
    }

    /**
     * 将Favorite实体转换为FavoriteResponse
     */
    private FavoriteResponse convertToResponse(Favorite favorite) {
        FavoriteResponse response = new FavoriteResponse();
        BeanUtils.copyProperties(favorite, response);
        return response;
    }

    /**
     * 构建分页结果
     */
    private PageResponse<FavoriteResponse> buildPageResult(IPage<Favorite> favoritePage) {
        PageResponse<FavoriteResponse> pageResponse = new PageResponse<>();
        List<FavoriteResponse> responseList = favoritePage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        pageResponse.setDatas(responseList);
        pageResponse.setTotal(favoritePage.getTotal());
        pageResponse.setCurrentPage((int) favoritePage.getCurrent());
        pageResponse.setPageSize((int) favoritePage.getSize());
        pageResponse.setTotalPage((int) favoritePage.getPages());

        return pageResponse;
    }
}