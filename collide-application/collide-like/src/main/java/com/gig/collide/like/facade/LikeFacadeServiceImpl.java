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
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.comment.CommentFacadeService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 点赞门面服务实现类 - MySQL 8.0 优化版
 * 完全对应LikeFacadeService接口，与底层Service层保持一致
 * 
 * 实现特性：
 * - 与LikeFacadeService接口完全对应
 * - 支持用户、目标对象、作者三个维度的查询
 * - 支持时间范围查询和批量操作
 * - 完整的缓存策略和跨模块集成
 * - 统一的错误处理和数据转换
 * 
 * @author GIG Team
 * @version 2.0.0 (MySQL 8.0 优化版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class LikeFacadeServiceImpl implements LikeFacadeService {

    private final LikeService likeService;
    private final UserFacadeService userFacadeService;
    private final ContentFacadeService contentFacadeService;
    private final CommentFacadeService commentFacadeService;

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

            // 1. 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法添加点赞: userId={}", request.getUserId());
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            // 2. 请求参数转换为实体
            Like like = convertToEntity(request);
            
            // 3. 调用业务服务添加点赞
            Like savedLike = likeService.addLike(like);
            
            // 4. 实体转换为响应对象
            LikeResponse response = convertToResponse(savedLike);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("点赞添加成功: ID={}, 用户={}({}), 耗时={}ms", 
                    savedLike.getId(), request.getUserId(), userResult.getData().getNickname(), duration);
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

            // 1. 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法取消点赞: userId={}", request.getUserId());
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            boolean success = likeService.cancelLike(
                    request.getUserId(), 
                    request.getLikeType(), 
                    request.getTargetId()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("点赞取消成功: 用户={}({}), 目标={}, 耗时={}ms", 
                        request.getUserId(), userResult.getData().getNickname(), 
                        request.getTargetId(), duration);
                return Result.success(null);
            } else {
                log.warn("点赞取消失败: 用户={}({}), 目标={}, 原因=未找到记录", 
                        request.getUserId(), userResult.getData().getNickname(), request.getTargetId());
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

            // 1. 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getUserId());
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法切换点赞状态: userId={}", request.getUserId());
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            // 2. 请求参数转换为实体
            Like like = convertToggleRequestToEntity(request);
            
            // 3. 调用业务逻辑切换点赞状态
            Like resultLike = likeService.toggleLike(like);
            
            long duration = System.currentTimeMillis() - startTime;
            if (resultLike != null) {
                // 点赞操作，返回点赞记录
                LikeResponse response = convertToResponse(resultLike);
                log.info("点赞切换成功(添加): ID={}, 用户={}({}), 耗时={}ms", 
                        resultLike.getId(), request.getUserId(), userResult.getData().getNickname(), duration);
                return Result.success(response);
            } else {
                // 取消点赞操作，返回空响应
                log.info("点赞切换成功(取消): 用户={}({}), 目标={}, 耗时={}ms", 
                        request.getUserId(), userResult.getData().getNickname(), 
                        request.getTargetId(), duration);
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

            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法检查点赞状态: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            boolean isLiked = likeService.checkLikeStatus(userId, likeType, targetId);
            log.debug("点赞状态查询完成: 用户={}({}), 目标={}, 已点赞={}", 
                    userId, userResult.getData().getNickname(), targetId, isLiked);
            return Result.success(isLiked);
        } catch (Exception e) {
            log.error("检查点赞状态失败: 用户={}, 目标={}", userId, targetId, e);
            return Result.error("LIKE_CHECK_ERROR", "检查点赞状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_RECORDS_CACHE, key = LikeCacheConstant.LIKE_RECORDS_KEY,
            expire = LikeCacheConstant.LIKE_RECORDS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<LikeResponse>> findUserLikes(Long userId, String likeType, String status, 
                                                           Integer currentPage, Integer pageSize) {
        try {
            log.info("分页查询用户点赞记录: userId={}, likeType={}, status={}, 页码={}, 页大小={}", 
                    userId, likeType, status, currentPage, pageSize);
            long startTime = System.currentTimeMillis();

            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法查询点赞记录: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            // 调用业务逻辑进行分页查询
            IPage<Like> likePage = likeService.findUserLikes(currentPage, pageSize, userId, likeType, status);

            // 转换分页响应
            PageResponse<LikeResponse> pageResponse = convertToPageResponse(likePage);

            long duration = System.currentTimeMillis() - startTime;
            log.info("用户点赞记录查询完成: 用户={}({}), 总数={}, 当前页={}, 耗时={}ms", 
                    userId, userResult.getData().getNickname(), pageResponse.getTotal(), pageResponse.getCurrentPage(), duration);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询用户点赞记录失败: userId={}, 页码={}, 页大小={}", userId, currentPage, pageSize, e);
            return Result.error("USER_LIKES_QUERY_ERROR", "查询用户点赞记录失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_RECORDS_CACHE, key = LikeCacheConstant.LIKE_RECORDS_KEY,
            expire = LikeCacheConstant.LIKE_RECORDS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<LikeResponse>> findTargetLikes(Long targetId, String likeType, String status,
                                                             Integer currentPage, Integer pageSize) {
        try {
            log.info("分页查询目标对象点赞记录: targetId={}, likeType={}, status={}, 页码={}, 页大小={}", 
                    targetId, likeType, status, currentPage, pageSize);
            long startTime = System.currentTimeMillis();

            // 调用业务逻辑进行分页查询
            IPage<Like> likePage = likeService.findTargetLikes(currentPage, pageSize, targetId, likeType, status);

            // 转换分页响应
            PageResponse<LikeResponse> pageResponse = convertToPageResponse(likePage);

            long duration = System.currentTimeMillis() - startTime;
            log.info("目标对象点赞记录查询完成: targetId={}, 总数={}, 当前页={}, 耗时={}ms", 
                    targetId, pageResponse.getTotal(), pageResponse.getCurrentPage(), duration);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询目标对象点赞记录失败: targetId={}, 页码={}, 页大小={}", targetId, currentPage, pageSize, e);
            return Result.error("TARGET_LIKES_QUERY_ERROR", "查询目标对象点赞记录失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_RECORDS_CACHE, key = LikeCacheConstant.LIKE_RECORDS_KEY,
            expire = LikeCacheConstant.LIKE_RECORDS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<LikeResponse>> findAuthorLikes(Long targetAuthorId, String likeType, String status,
                                                             Integer currentPage, Integer pageSize) {
        try {
            log.info("分页查询作者作品点赞记录: targetAuthorId={}, likeType={}, status={}, 页码={}, 页大小={}", 
                    targetAuthorId, likeType, status, currentPage, pageSize);
            long startTime = System.currentTimeMillis();

            // 验证作者是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(targetAuthorId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("作者不存在，无法查询作品点赞记录: targetAuthorId={}", targetAuthorId);
                return Result.error("AUTHOR_NOT_FOUND", "作者不存在");
            }

            // 调用业务逻辑进行分页查询
            IPage<Like> likePage = likeService.findAuthorLikes(currentPage, pageSize, targetAuthorId, likeType, status);

            // 转换分页响应
            PageResponse<LikeResponse> pageResponse = convertToPageResponse(likePage);

            long duration = System.currentTimeMillis() - startTime;
            log.info("作者作品点赞记录查询完成: 作者={}({}), 总数={}, 当前页={}, 耗时={}ms", 
                    targetAuthorId, userResult.getData().getNickname(), pageResponse.getTotal(), pageResponse.getCurrentPage(), duration);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询作者作品点赞记录失败: targetAuthorId={}, 页码={}, 页大小={}", targetAuthorId, currentPage, pageSize, e);
            return Result.error("AUTHOR_LIKES_QUERY_ERROR", "查询作者作品点赞记录失败: " + e.getMessage());
        }
    }

    // =================== 点赞统计功能 ===================

    @Override
    @Cached(name = LikeCacheConstant.LIKE_COUNT_CACHE, key = LikeCacheConstant.TARGET_LIKE_COUNT_KEY,
            expire = LikeCacheConstant.LIKE_COUNT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> countTargetLikes(Long targetId, String likeType) {
        try {
            log.debug("统计目标对象点赞数量: targetId={}, likeType={}", targetId, likeType);

            Long count = likeService.countTargetLikes(targetId, likeType);
            log.debug("目标对象点赞数量统计完成: targetId={}, 数量={}", targetId, count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计目标对象点赞数量失败: targetId={}, likeType={}", targetId, likeType, e);
            return Result.error("TARGET_LIKE_COUNT_ERROR", "统计目标对象点赞数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_COUNT_CACHE, key = LikeCacheConstant.USER_LIKE_COUNT_KEY,
            expire = LikeCacheConstant.LIKE_COUNT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> countUserLikes(Long userId, String likeType) {
        try {
            log.debug("统计用户点赞数量: userId={}, likeType={}", userId, likeType);

            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法统计用户点赞数量: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            Long count = likeService.countUserLikes(userId, likeType);
            log.debug("用户点赞数量统计完成: 用户={}({}), 数量={}", 
                    userId, userResult.getData().getNickname(), count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户点赞数量失败: userId={}, likeType={}", userId, likeType, e);
            return Result.error("USER_LIKE_COUNT_ERROR", "统计用户点赞数量失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_COUNT_CACHE, key = LikeCacheConstant.TARGET_LIKE_COUNT_KEY,
            expire = LikeCacheConstant.LIKE_COUNT_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Long> countAuthorLikes(Long targetAuthorId, String likeType) {
        try {
            log.debug("统计作者作品被点赞数量: targetAuthorId={}, likeType={}", targetAuthorId, likeType);

            // 验证作者是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(targetAuthorId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("作者不存在，无法统计作品被点赞数量: targetAuthorId={}", targetAuthorId);
                return Result.error("AUTHOR_NOT_FOUND", "作者不存在");
            }

            Long count = likeService.countAuthorLikes(targetAuthorId, likeType);
            log.debug("作者作品被点赞数量统计完成: 作者={}({}), 数量={}", 
                    targetAuthorId, userResult.getData().getNickname(), count);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计作者作品被点赞数量失败: targetAuthorId={}, likeType={}", targetAuthorId, likeType, e);
            return Result.error("AUTHOR_LIKE_COUNT_ERROR", "统计作者作品被点赞数量失败: " + e.getMessage());
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

            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法批量检查点赞状态: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }

            Map<Long, Boolean> statusMap = likeService.batchCheckLikeStatus(userId, likeType, targetIds);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("批量点赞状态查询完成: 用户={}({}), 检查数量={}, 耗时={}ms", 
                    userId, userResult.getData().getNickname(), 
                    targetIds != null ? targetIds.size() : 0, duration);
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查点赞状态失败: 用户={}, 类型={}", userId, likeType, e);
            return Result.error("BATCH_CHECK_ERROR", "批量检查点赞状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = LikeCacheConstant.LIKE_RECORDS_CACHE, key = LikeCacheConstant.LIKE_RECORDS_KEY,
            expire = LikeCacheConstant.LIKE_RECORDS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<List<LikeResponse>> findByTimeRange(java.time.LocalDateTime startTime, 
                                                     java.time.LocalDateTime endTime,
                                                     String likeType, String status) {
        try {
            log.info("查询时间范围内的点赞记录: startTime={}, endTime={}, likeType={}, status={}", 
                    startTime, endTime, likeType, status);
            long queryStartTime = System.currentTimeMillis();

            // 调用业务逻辑进行时间范围查询
            List<Like> likeList = likeService.findByTimeRange(startTime, endTime, likeType, status);

            // 转换响应对象
            List<LikeResponse> responseList = likeList.stream()
                    .map(this::convertToResponse)
                    .collect(java.util.stream.Collectors.toList());

            long duration = System.currentTimeMillis() - queryStartTime;
            log.info("时间范围点赞记录查询完成: startTime={}, endTime={}, 数量={}, 耗时={}ms", 
                    startTime, endTime, responseList.size(), duration);
            return Result.success(responseList);
        } catch (Exception e) {
            log.error("查询时间范围内的点赞记录失败: startTime={}, endTime={}", startTime, endTime, e);
            return Result.error("TIME_RANGE_QUERY_ERROR", "查询时间范围内的点赞记录失败: " + e.getMessage());
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