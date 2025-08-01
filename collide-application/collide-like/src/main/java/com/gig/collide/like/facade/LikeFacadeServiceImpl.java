package com.gig.collide.like.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.like.request.*;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.like.domain.service.LikeService;
import com.gig.collide.like.infrastructure.cache.LikeCacheConstant;
import com.gig.collide.web.vo.Result;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.comment.CommentFacadeService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
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
 * 点赞门面服务实现类 - 缓存增强版
 * 对齐order模块设计风格，提供完整的点赞服务
 * 包含缓存功能、跨模块集成、错误处理、数据转换
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class LikeFacadeServiceImpl implements LikeFacadeService {

    private final LikeService likeService;
    
    // =================== 跨模块服务注入（预留扩展） ===================
    // 注：跨模块服务调用根据业务需要可在此添加
    @Autowired
    private UserFacadeService userFacadeService;
    
    @Autowired
    private ContentFacadeService contentFacadeService;
    
    @Autowired
    private CommentFacadeService commentFacadeService;

    // =================== 点赞核心功能 ===================

    @Override
    @CacheInvalidate(name = LikeCacheConstant.LIKE_STATUS_CACHE)
    @CacheInvalidate(name = LikeCacheConstant.LIKE_COUNT_CACHE)
    @CacheInvalidate(name = LikeCacheConstant.LIKE_STATISTICS_CACHE)
    public Result<LikeResponse> addLike(LikeRequest request) {
        try {
            log.info("添加点赞请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());
            long startTime = System.currentTimeMillis();

            // 1. 请求参数转换为实体
            Like like = convertToEntity(request);
            
            // 2. 调用业务服务添加点赞
            Like savedLike = likeService.addLike(like);
            
            // 3. 实体转换为响应对象
            LikeResponse response = convertToResponse(savedLike);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("点赞添加成功: ID={}, 耗时={}ms", savedLike.getId(), duration);
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("点赞参数验证失败: 用户={}, 目标={}, 错误={}", 
                    request.getUserId(), request.getTargetId(), e.getMessage());
            return Result.error("LIKE_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("添加点赞失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_ADD_ERROR", "添加点赞失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = LikeCacheConstant.LIKE_STATUS_CACHE)
    @CacheInvalidate(name = LikeCacheConstant.LIKE_COUNT_CACHE)
    @CacheInvalidate(name = LikeCacheConstant.LIKE_STATISTICS_CACHE)
    public Result<Void> cancelLike(LikeCancelRequest request) {
        try {
            log.info("取消点赞请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());
            long startTime = System.currentTimeMillis();

            boolean success = likeService.cancelLike(
                    request.getUserId(), 
                    request.getLikeType(), 
                    request.getTargetId()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("点赞取消成功: 用户={}, 目标={}, 耗时={}ms", 
                        request.getUserId(), request.getTargetId(), duration);
                return Result.success(null);
            } else {
                log.warn("点赞取消失败: 用户={}, 目标={}, 原因=未找到记录", 
                        request.getUserId(), request.getTargetId());
                return Result.error("LIKE_CANCEL_FAILED", "取消点赞失败，可能未找到对应的点赞记录");
            }
        } catch (Exception e) {
            log.error("取消点赞失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_CANCEL_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = LikeCacheConstant.LIKE_STATUS_CACHE)
    @CacheInvalidate(name = LikeCacheConstant.LIKE_COUNT_CACHE)
    @CacheInvalidate(name = LikeCacheConstant.LIKE_STATISTICS_CACHE)
    public Result<LikeResponse> toggleLike(LikeToggleRequest request) {
        try {
            log.info("切换点赞状态请求: 用户={}, 类型={}, 目标={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());
            long startTime = System.currentTimeMillis();

            // 1. 请求参数转换为实体
            Like like = convertToggleRequestToEntity(request);
            
            // 2. 调用业务逻辑切换点赞状态
            Like resultLike = likeService.toggleLike(like);
            
            long duration = System.currentTimeMillis() - startTime;
            if (resultLike != null) {
                // 点赞操作，返回点赞记录
                LikeResponse response = convertToResponse(resultLike);
                log.info("点赞切换成功(添加): ID={}, 耗时={}ms", resultLike.getId(), duration);
                return Result.success(response);
            } else {
                // 取消点赞操作，返回空响应
                log.info("点赞切换成功(取消): 用户={}, 目标={}, 耗时={}ms", 
                        request.getUserId(), request.getTargetId(), duration);
                return Result.success(null);
            }
        } catch (IllegalArgumentException e) {
            log.warn("切换点赞参数验证失败: 用户={}, 目标={}, 错误={}", 
                    request.getUserId(), request.getTargetId(), e.getMessage());
            return Result.error("LIKE_TOGGLE_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("切换点赞状态失败: 用户={}, 目标={}", request.getUserId(), request.getTargetId(), e);
            return Result.error("LIKE_TOGGLE_ERROR", "切换点赞状态失败: " + e.getMessage());
        }
    }

    // =================== 点赞查询功能 ===================

    @Override
    @Cached(name = LikeCacheConstant.LIKE_STATUS_CACHE, key = LikeCacheConstant.USER_LIKE_STATUS_KEY,
            expire = LikeCacheConstant.LIKE_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Boolean> checkLikeStatus(Long userId, String likeType, Long targetId) {
        try {
            log.debug("检查点赞状态: 用户={}, 类型={}, 目标={}", userId, likeType, targetId);

            boolean isLiked = likeService.checkLikeStatus(userId, likeType, targetId);
            log.debug("点赞状态查询完成: 用户={}, 目标={}, 已点赞={}", userId, targetId, isLiked);
            return Result.success(isLiked);
        } catch (Exception e) {
            log.error("检查点赞状态失败: 用户={}, 目标={}", userId, targetId, e);
            return Result.error("LIKE_CHECK_ERROR", "检查点赞状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_RECORDS_CACHE, key = LikeCacheConstant.LIKE_RECORDS_KEY,
            expire = LikeCacheConstant.LIKE_RECORDS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<LikeResponse>> queryLikes(LikeQueryRequest request) {
        try {
            log.info("分页查询点赞记录: 页码={}, 页大小={}, 用户={}, 类型={}", 
                    request.getCurrentPage(), request.getPageSize(), request.getUserId(), request.getLikeType());
            long startTime = System.currentTimeMillis();

            // 调用业务逻辑进行分页查询
            IPage<Like> likePage = likeService.queryLikes(
                    request.getCurrentPage(),
                    request.getPageSize(),
                    request.getUserId(),
                    request.getLikeType(),
                    request.getTargetId(),
                    request.getTargetAuthorId(),
                    request.getStatus(),
                    request.getOrderBy(),
                    request.getOrderDirection()
            );

            // 转换分页响应
            PageResponse<LikeResponse> pageResponse = convertToPageResponse(likePage);

            long duration = System.currentTimeMillis() - startTime;
            log.info("点赞记录查询完成: 总数={}, 当前页={}, 耗时={}ms", 
                    pageResponse.getTotal(), pageResponse.getCurrentPage(), duration);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询点赞记录失败: 页码={}, 页大小={}", request.getCurrentPage(), request.getPageSize(), e);
            return Result.error("LIKE_QUERY_ERROR", "查询点赞记录失败: " + e.getMessage());
        }
    }

    // =================== 点赞统计功能 ===================

    @Override
    @Cached(name = LikeCacheConstant.LIKE_COUNT_CACHE, key = LikeCacheConstant.TARGET_LIKE_COUNT_KEY,
            expire = LikeCacheConstant.LIKE_COUNT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> getLikeCount(String likeType, Long targetId) {
        try {
            log.debug("获取点赞数量: 类型={}, 目标={}", likeType, targetId);

            Long count = likeService.getLikeCount(likeType, targetId);
            log.debug("点赞数量查询完成: 目标={}, 数量={}", targetId, count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取点赞数量失败: 类型={}, 目标={}", likeType, targetId, e);
            return Result.error("LIKE_COUNT_ERROR", "获取点赞数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_COUNT_CACHE, key = LikeCacheConstant.USER_LIKE_COUNT_KEY,
            expire = LikeCacheConstant.LIKE_COUNT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> getUserLikeCount(Long userId, String likeType) {
        try {
            log.debug("获取用户点赞数量: 用户={}, 类型={}", userId, likeType);

            Long count = likeService.getUserLikeCount(userId, likeType);
            log.debug("用户点赞数量查询完成: 用户={}, 数量={}", userId, count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户点赞数量失败: 用户={}, 类型={}", userId, likeType, e);
            return Result.error("USER_LIKE_COUNT_ERROR", "获取用户点赞数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.BATCH_LIKE_STATUS_CACHE, key = LikeCacheConstant.BATCH_LIKE_STATUS_KEY,
            expire = LikeCacheConstant.BATCH_LIKE_STATUS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Map<Long, Boolean>> batchCheckLikeStatus(Long userId, String likeType, List<Long> targetIds) {
        try {
            log.info("批量检查点赞状态: 用户={}, 类型={}, 目标数量={}", 
                    userId, likeType, targetIds != null ? targetIds.size() : 0);
            long startTime = System.currentTimeMillis();

            Map<Long, Boolean> statusMap = likeService.batchCheckLikeStatus(userId, likeType, targetIds);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("批量点赞状态查询完成: 用户={}, 检查数量={}, 耗时={}ms", 
                    userId, targetIds != null ? targetIds.size() : 0, duration);
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查点赞状态失败: 用户={}, 类型={}", userId, likeType, e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查点赞状态失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 将LikeRequest转换为Like实体
     */
    private Like convertToEntity(LikeRequest request) {
        Like like = new Like();
        BeanUtils.copyProperties(request, like);
        return like;
    }

    /**
     * 将LikeToggleRequest转换为Like实体
     */
    private Like convertToggleRequestToEntity(LikeToggleRequest request) {
        Like like = new Like();
        BeanUtils.copyProperties(request, like);
        return like;
    }

    /**
     * 将Like实体转换为LikeResponse
     */
    private LikeResponse convertToResponse(Like like) {
        LikeResponse response = new LikeResponse();
        BeanUtils.copyProperties(like, response);
        return response;
    }

    /**
     * 将分页实体转换为分页响应对象
     * 对齐order模块的分页转换风格
     */
    private PageResponse<LikeResponse> convertToPageResponse(IPage<Like> likePage) {
        PageResponse<LikeResponse> pageResponse = new PageResponse<>();
        
        // 转换数据列表
        List<LikeResponse> responseList = likePage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 设置分页信息，对齐order模块风格
        pageResponse.setDatas(responseList);
        pageResponse.setTotal(likePage.getTotal());
        pageResponse.setCurrentPage((int) likePage.getCurrent());
        pageResponse.setPageSize((int) likePage.getSize());
        pageResponse.setTotalPage((int) likePage.getPages());
        
        return pageResponse;
    }
}