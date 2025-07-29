package com.gig.collide.like.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.like.request.*;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.like.domain.service.LikeService;
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
 * 点赞门面服务实现类 - 简洁版
 * 基于Dubbo RPC，提供点赞相关的远程服务调用
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class LikeFacadeServiceImpl implements LikeFacadeService {

    private final LikeService likeService;

    @Override
    public Result<LikeResponse> addLike(LikeRequest request) {
        try {
            log.info("RPC添加点赞: userId={}, likeType={}, targetId={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 转换请求对象为实体
            Like like = convertToEntity(request);
            
            // 调用业务逻辑
            Like savedLike = likeService.addLike(like);
            
            // 转换响应对象
            LikeResponse response = convertToResponse(savedLike);
            
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("点赞参数验证失败: {}", e.getMessage());
            return Result.error("LIKE_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("添加点赞失败", e);
            return Result.error("LIKE_ADD_ERROR", "添加点赞失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> cancelLike(LikeCancelRequest request) {
        try {
            log.info("RPC取消点赞: userId={}, likeType={}, targetId={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            boolean success = likeService.cancelLike(
                    request.getUserId(), 
                    request.getLikeType(), 
                    request.getTargetId()
            );
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("LIKE_CANCEL_FAILED", "取消点赞失败，可能未找到对应的点赞记录");
            }
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return Result.error("LIKE_CANCEL_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @Override
    public Result<LikeResponse> toggleLike(LikeToggleRequest request) {
        try {
            log.info("RPC切换点赞状态: userId={}, likeType={}, targetId={}", 
                    request.getUserId(), request.getLikeType(), request.getTargetId());

            // 转换请求对象为实体
            Like like = convertToggleRequestToEntity(request);
            
            // 调用业务逻辑
            Like resultLike = likeService.toggleLike(like);
            
            if (resultLike != null) {
                // 点赞操作，返回点赞记录
                LikeResponse response = convertToResponse(resultLike);
                return Result.success(response);
            } else {
                // 取消点赞操作，返回空响应
                return Result.success(null);
            }
        } catch (IllegalArgumentException e) {
            log.warn("切换点赞参数验证失败: {}", e.getMessage());
            return Result.error("LIKE_TOGGLE_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("切换点赞状态失败", e);
            return Result.error("LIKE_TOGGLE_ERROR", "切换点赞状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkLikeStatus(Long userId, String likeType, Long targetId) {
        try {
            log.info("RPC检查点赞状态: userId={}, likeType={}, targetId={}", userId, likeType, targetId);

            boolean isLiked = likeService.checkLikeStatus(userId, likeType, targetId);
            return Result.success(isLiked);
        } catch (Exception e) {
            log.error("检查点赞状态失败", e);
            return Result.error("LIKE_CHECK_ERROR", "检查点赞状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<LikeResponse>> queryLikes(LikeQueryRequest request) {
        try {
            log.info("RPC分页查询点赞记录: pageNum={}, pageSize={}", request.getPageNum(), request.getPageSize());

            // 调用业务逻辑进行分页查询
            IPage<Like> likePage = likeService.queryLikes(
                    request.getPageNum(),
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
            PageResponse<LikeResponse> pageResponse = new PageResponse<>();
            List<LikeResponse> responseList = likePage.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            pageResponse.setDatas(responseList);
            pageResponse.setTotal(likePage.getTotal());
            pageResponse.setCurrentPage((int) likePage.getCurrent());
            pageResponse.setPageSize((int) likePage.getSize());
            pageResponse.setTotalPage((int) likePage.getPages());

            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询点赞记录失败", e);
            return Result.error("LIKE_QUERY_ERROR", "查询点赞记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getLikeCount(String likeType, Long targetId) {
        try {
            log.info("RPC获取点赞数量: likeType={}, targetId={}", likeType, targetId);

            Long count = likeService.getLikeCount(likeType, targetId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取点赞数量失败", e);
            return Result.error("LIKE_COUNT_ERROR", "获取点赞数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> getUserLikeCount(Long userId, String likeType) {
        try {
            log.info("RPC获取用户点赞数量: userId={}, likeType={}", userId, likeType);

            Long count = likeService.getUserLikeCount(userId, likeType);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取用户点赞数量失败", e);
            return Result.error("USER_LIKE_COUNT_ERROR", "获取用户点赞数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckLikeStatus(Long userId, String likeType, List<Long> targetIds) {
        try {
            log.info("RPC批量检查点赞状态: userId={}, likeType={}, targetCount={}", 
                    userId, likeType, targetIds != null ? targetIds.size() : 0);

            Map<Long, Boolean> statusMap = likeService.batchCheckLikeStatus(userId, likeType, targetIds);
            return Result.success(statusMap);
        } catch (Exception e) {
            log.error("批量检查点赞状态失败", e);
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
}