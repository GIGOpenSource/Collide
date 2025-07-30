package com.gig.collide.favorite.facade;

import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.favorite.FavoriteFacadeService;
import com.gig.collide.api.favorite.request.*;
import com.gig.collide.api.favorite.response.FavoriteResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.favorite.domain.entity.Favorite;
import com.gig.collide.favorite.domain.service.FavoriteService;
import com.gig.collide.favorite.infrastructure.cache.FavoriteCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 收藏门面服务实现类 - 缓存增强版
 * 对齐follow模块设计风格，集成JetCache分布式缓存
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class FavoriteFacadeServiceImpl implements FavoriteFacadeService {

    private final FavoriteService favoriteService;

    @Override
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_STATUS_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_COUNT_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_STATISTICS_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.USER_FAVORITES_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.TARGET_FAVORITES_CACHE)
    public Result<FavoriteResponse> addFavorite(FavoriteCreateRequest request) {
        try {
            log.info("添加收藏请求: 用户={}, 类型={}, 目标={}",
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());
            long startTime = System.currentTimeMillis();

            // 转换请求对象为实体
            Favorite favorite = convertCreateRequestToEntity(request);
            
            // 调用业务逻辑
            Favorite savedFavorite = favoriteService.addFavorite(favorite);
            
            // 转换响应对象
            FavoriteResponse response = convertToResponse(savedFavorite);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("收藏添加成功: ID={}, 耗时={}ms", savedFavorite.getId(), duration);
            
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("收藏参数验证失败: 用户={}, 目标={}, 错误={}",
                    request.getUserId(), request.getTargetId(), e.getMessage());
            return Result.error("FAVORITE_PARAM_ERROR", e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("收藏状态检查失败: 用户={}, 目标={}, 错误={}",
                    request.getUserId(), request.getTargetId(), e.getMessage());
            return Result.error("FAVORITE_STATE_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("添加收藏失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("FAVORITE_CREATE_ERROR", "添加收藏失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_STATUS_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_COUNT_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_STATISTICS_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.USER_FAVORITES_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.TARGET_FAVORITES_CACHE)
    public Result<Void> removeFavorite(FavoriteDeleteRequest request) {
        try {
            log.info("取消收藏请求: 用户={}, 类型={}, 目标={}",
                    request.getUserId(), request.getFavoriteType(), request.getTargetId());
            long startTime = System.currentTimeMillis();

            boolean success = favoriteService.removeFavorite(
                    request.getUserId(), 
                    request.getFavoriteType(),
                    request.getTargetId(),
                    request.getCancelReason(),
                    request.getOperatorId()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            
            if (success) {
                log.info("收藏取消成功: 用户={}, 目标={}, 耗时={}ms",
                        request.getUserId(), request.getTargetId(), duration);
                return Result.success(null);
            } else {
                log.warn("收藏取消失败: 用户={}, 目标={}, 耗时={}ms",
                        request.getUserId(), request.getTargetId(), duration);
                return Result.error("UNFAVORITE_FAILED", "取消收藏失败");
            }
        } catch (Exception e) {
            log.error("取消收藏失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("UNFAVORITE_ERROR", "取消收藏失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_STATUS_CACHE, key = FavoriteCacheConstant.USER_FAVORITE_STATUS_KEY,
            expire = FavoriteCacheConstant.FAVORITE_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Boolean> checkFavoriteStatus(Long userId, String favoriteType, Long targetId) {
        try {
            log.debug("检查收藏状态: 用户={}, 类型={}, 目标={}", userId, favoriteType, targetId);
            long startTime = System.currentTimeMillis();
            
            boolean isFavorited = favoriteService.checkFavoriteStatus(userId, favoriteType, targetId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("收藏状态查询完成: 用户={}, 目标={}, 已收藏={}, 耗时={}ms",
                    userId, targetId, isFavorited, duration);
            
            return Result.success(isFavorited);
        } catch (Exception e) {
            log.error("检查收藏状态失败: 用户={}, 目标={}", userId, targetId, e);
            return Result.error("CHECK_FAVORITE_ERROR", "检查收藏状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_DETAIL_CACHE, key = FavoriteCacheConstant.FAVORITE_DETAIL_KEY,
            expire = FavoriteCacheConstant.FAVORITE_DETAIL_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<FavoriteResponse> getFavoriteDetail(Long userId, String favoriteType, Long targetId) {
        try {
            log.debug("获取收藏详情: 用户={}, 类型={}, 目标={}", userId, favoriteType, targetId);
            long startTime = System.currentTimeMillis();
            
            Favorite favorite = favoriteService.getFavoriteDetail(userId, favoriteType, targetId);
            if (favorite == null) {
                log.warn("收藏记录不存在: 用户={}, 目标={}", userId, targetId);
                return Result.error("FAVORITE_NOT_FOUND", "收藏记录不存在");
            }
            
            FavoriteResponse response = convertToResponse(favorite);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("收藏详情查询完成: ID={}, 耗时={}ms", favorite.getId(), duration);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取收藏详情失败: 用户={}, 目标={}", userId, targetId, e);
            return Result.error("GET_FAVORITE_ERROR", "获取收藏详情失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_RECORDS_CACHE, key = FavoriteCacheConstant.FAVORITE_RECORDS_KEY,
            expire = FavoriteCacheConstant.FAVORITE_RECORDS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FavoriteResponse>> queryFavorites(FavoriteQueryRequest request) {
        try {
            log.info("分页查询收藏记录: 页码={}, 大小={}", request.getPageNum(), request.getPageSize());
            long startTime = System.currentTimeMillis();

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
            PageResponse<FavoriteResponse> pageResponse = convertToPageResponse(favoritePage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("收藏记录查询完成: 总数={}, 耗时={}ms", pageResponse.getTotal(), duration);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询收藏记录失败", e);
            return Result.error("FAVORITE_QUERY_ERROR", "查询收藏记录失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.USER_FAVORITES_CACHE, key = FavoriteCacheConstant.USER_FAVORITES_KEY,
            expire = FavoriteCacheConstant.USER_FAVORITES_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FavoriteResponse>> getUserFavorites(Long userId, String favoriteType, 
                                                                 Integer pageNum, Integer pageSize) {
        try {
            log.debug("获取用户收藏列表: 用户={}, 类型={}, 页码={}, 大小={}", userId, favoriteType, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Favorite> favoritePage = favoriteService.getUserFavorites(userId, favoriteType, pageNum, pageSize);
            PageResponse<FavoriteResponse> pageResponse = convertToPageResponse(favoritePage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("用户收藏列表查询完成: 用户={}, 总数={}, 耗时={}ms", userId, pageResponse.getTotal(), duration);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取用户收藏列表失败: 用户={}", userId, e);
            return Result.error("GET_USER_FAVORITES_ERROR", "获取用户收藏列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.TARGET_FAVORITES_CACHE, key = FavoriteCacheConstant.TARGET_FAVORITES_KEY,
            expire = FavoriteCacheConstant.TARGET_FAVORITES_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FavoriteResponse>> getTargetFavorites(String favoriteType, Long targetId,
                                                                   Integer pageNum, Integer pageSize) {
        try {
            log.debug("获取目标收藏用户列表: 类型={}, 目标={}, 页码={}, 大小={}", favoriteType, targetId, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Favorite> favoritePage = favoriteService.getTargetFavorites(favoriteType, targetId, pageNum, pageSize);
            PageResponse<FavoriteResponse> pageResponse = convertToPageResponse(favoritePage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("目标收藏用户列表查询完成: 目标={}, 总数={}, 耗时={}ms", targetId, pageResponse.getTotal(), duration);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取目标收藏列表失败: 目标={}", targetId, e);
            return Result.error("GET_TARGET_FAVORITES_ERROR", "获取目标收藏列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_COUNT_CACHE, key = FavoriteCacheConstant.USER_FAVORITE_COUNT_KEY,
            expire = FavoriteCacheConstant.FAVORITE_COUNT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> getUserFavoriteCount(Long userId, String favoriteType) {
        try {
            log.debug("获取用户收藏数量: 用户={}, 类型={}", userId, favoriteType);
            long startTime = System.currentTimeMillis();
            
            Long count = favoriteService.getUserFavoriteCount(userId, favoriteType);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("用户收藏数量查询完成: 用户={}, 类型={}, 数量={}, 耗时={}ms", userId, favoriteType, count, duration);
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户收藏数量失败: 用户={}, 类型={}", userId, favoriteType, e);
            return Result.error("GET_USER_FAVORITE_COUNT_ERROR", "获取用户收藏数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_COUNT_CACHE, key = FavoriteCacheConstant.TARGET_FAVORITE_COUNT_KEY,
            expire = FavoriteCacheConstant.FAVORITE_COUNT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> getTargetFavoriteCount(String favoriteType, Long targetId) {
        try {
            log.debug("获取目标被收藏数量: 类型={}, 目标={}", favoriteType, targetId);
            long startTime = System.currentTimeMillis();
            
            Long count = favoriteService.getTargetFavoriteCount(favoriteType, targetId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("目标被收藏数量查询完成: 目标={}, 类型={}, 数量={}, 耗时={}ms", targetId, favoriteType, count, duration);
            
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取目标被收藏数量失败: 目标={}, 类型={}", targetId, favoriteType, e);
            return Result.error("GET_TARGET_FAVORITE_COUNT_ERROR", "获取目标被收藏数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_STATISTICS_CACHE, key = FavoriteCacheConstant.USER_FAVORITE_STATISTICS_KEY,
            expire = FavoriteCacheConstant.FAVORITE_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getUserFavoriteStatistics(Long userId) {
        try {
            log.debug("获取用户收藏统计: 用户={}", userId);
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> statistics = favoriteService.getUserFavoriteStatistics(userId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("用户收藏统计查询完成: 用户={}, 耗时={}ms", userId, duration);
            
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取收藏统计失败: 用户={}", userId, e);
            return Result.error("GET_STATISTICS_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.BATCH_FAVORITE_STATUS_CACHE, key = FavoriteCacheConstant.BATCH_FAVORITE_STATUS_KEY,
            expire = FavoriteCacheConstant.BATCH_FAVORITE_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Map<Long, Boolean>> batchCheckFavoriteStatus(Long userId, String favoriteType, List<Long> targetIds) {
        try {
            log.info("批量检查收藏状态: 用户={}, 类型={}, 目标数量={}",
                    userId, favoriteType, targetIds != null ? targetIds.size() : 0);
            long startTime = System.currentTimeMillis();
            
            Map<Long, Boolean> statusMap = favoriteService.batchCheckFavoriteStatus(userId, favoriteType, targetIds);
            
            long duration = System.currentTimeMillis() - startTime;
            if (statusMap != null) {
                log.info("批量收藏状态检查完成: 用户={}, 检查数量={}, 耗时={}ms",
                        userId, statusMap.size(), duration);
            }
            
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查收藏状态失败: 用户={}, 类型={}", userId, favoriteType, e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查收藏状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_SEARCH_CACHE, key = FavoriteCacheConstant.FAVORITE_SEARCH_KEY,
            expire = FavoriteCacheConstant.FAVORITE_SEARCH_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FavoriteResponse>> searchFavoritesByTitle(Long userId, String titleKeyword, 
                                                                       String favoriteType, Integer pageNum, Integer pageSize) {
        try {
            log.info("搜索收藏: 用户={}, 关键词={}, 类型={}, 页码={}, 大小={}",
                    userId, titleKeyword, favoriteType, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Favorite> favoritePage = favoriteService.searchFavoritesByTitle(userId, titleKeyword, favoriteType, pageNum, pageSize);
            PageResponse<FavoriteResponse> pageResponse = convertToPageResponse(favoritePage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("收藏搜索完成: 用户={}, 关键词={}, 结果数={}, 耗时={}ms",
                    userId, titleKeyword, pageResponse.getTotal(), duration);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("搜索收藏失败: 用户={}, 关键词={}", userId, titleKeyword, e);
            return Result.error("SEARCH_FAVORITES_ERROR", "搜索收藏失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = FavoriteCacheConstant.FAVORITE_STATISTICS_CACHE, key = FavoriteCacheConstant.POPULAR_FAVORITES_KEY,
            expire = FavoriteCacheConstant.FAVORITE_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<FavoriteResponse>> getPopularFavorites(String favoriteType, Integer pageNum, Integer pageSize) {
        try {
            log.info("获取热门收藏: 类型={}, 页码={}, 大小={}", favoriteType, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Favorite> favoritePage = favoriteService.getPopularFavorites(favoriteType, pageNum, pageSize);
            PageResponse<FavoriteResponse> pageResponse = convertToPageResponse(favoritePage);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("热门收藏查询完成: 类型={}, 结果数={}, 耗时={}ms",
                    favoriteType, pageResponse.getTotal(), duration);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("获取热门收藏失败: 类型={}", favoriteType, e);
            return Result.error("GET_POPULAR_ERROR", "获取热门收藏失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_STATUS_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_COUNT_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_STATISTICS_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.FAVORITE_RECORDS_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.USER_FAVORITES_CACHE)
    @CacheInvalidate(name = FavoriteCacheConstant.TARGET_FAVORITES_CACHE)
    public Result<Integer> cleanCancelledFavorites(Integer days) {
        try {
            log.info("清理已取消的收藏记录: 保留天数={}", days);
            long startTime = System.currentTimeMillis();
            
            int cleanedCount = favoriteService.cleanCancelledFavorites(days);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("收藏记录清理完成: 清理数量={}, 耗时={}ms", cleanedCount, duration);
            
            return Result.success(cleanedCount);
        } catch (Exception e) {
            log.error("清理已取消收藏记录失败: 保留天数={}", days, e);
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
     * 构建分页结果 - 对齐follow模块命名规范
     */
    private PageResponse<FavoriteResponse> convertToPageResponse(IPage<Favorite> favoritePage) {
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