package com.gig.collide.like.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.like.constant.LikeType;
import com.gig.collide.api.like.request.LikeQueryRequest;
import com.gig.collide.api.like.request.LikeRequest;
import com.gig.collide.api.like.response.LikeQueryResponse;
import com.gig.collide.api.like.response.LikeResponse;
import com.gig.collide.api.like.response.data.LikeInfo;
import com.gig.collide.api.like.service.LikeFacadeService;
import com.gig.collide.like.domain.entity.Like;
import com.gig.collide.like.domain.service.LikeDomainService;
import com.gig.collide.like.infrastructure.converter.LikeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 点赞服务门面实现
 * 无连表设计，基于冗余统计
 * 
 * @author Collide
 * @since 1.0.0
 */
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
@Slf4j
public class LikeFacadeServiceImpl implements LikeFacadeService {
    
    private final LikeDomainService likeDomainService;
    private final LikeConverter likeConverter;
    
    @Override
    public LikeResponse likeAction(LikeRequest likeRequest) {
        try {
            log.info("处理点赞操作，用户ID：{}，目标对象ID：{}，类型：{}，动作：{}", 
                    likeRequest.getUserId(), likeRequest.getTargetId(), 
                    likeRequest.getLikeType(), likeRequest.getAction());
            
            // 执行点赞操作
            Like likeRecord = likeDomainService.performLikeAction(likeRequest);
            
            // 获取最新统计信息（直接从点赞表统计）
            LikeDomainService.LikeStatistics statistics = likeDomainService.getLikeStatistics(
                    likeRequest.getTargetId(), likeRequest.getLikeType());
            
            Long likeCount = statistics != null ? statistics.getTotalLikeCount() : 0L;
            Long dislikeCount = statistics != null ? statistics.getTotalDislikeCount() : 0L;
            String userLikeStatus = getActionTypeDescription(likeRecord.getActionType());
            
            return LikeResponse.success(likeCount, dislikeCount, userLikeStatus);
            
        } catch (Exception e) {
            log.error("点赞操作失败，用户ID：{}，目标对象ID：{}", 
                    likeRequest.getUserId(), likeRequest.getTargetId(), e);
            return LikeResponse.error("LIKE_ACTION_ERROR", "点赞操作失败：" + e.getMessage());
        }
    }
    
    @Override
    public LikeResponse batchLikeAction(List<LikeRequest> likeRequests) {
        try {
            log.info("处理批量点赞操作，请求数量：{}", likeRequests.size());
            
            // 参数验证
            if (likeRequests == null || likeRequests.isEmpty()) {
                return LikeResponse.error("PARAM_ERROR", "批量操作请求列表不能为空");
            }
            
            if (likeRequests.size() > 100) {
                return LikeResponse.error("PARAM_ERROR", "单次批量操作不能超过100个");
            }
            
            // 使用全局事务处理批量操作
            return likeDomainService.batchLikeAction(likeRequests);
            
        } catch (Exception e) {
            log.error("批量点赞操作失败", e);
            return LikeResponse.error("BATCH_LIKE_ERROR", "批量点赞操作失败：" + e.getMessage());
        }
    }
    
    @Override
    public LikeQueryResponse queryLikes(LikeQueryRequest queryRequest) {
        try {
            log.info("查询点赞记录，用户ID：{}，目标对象ID：{}，类型：{}", 
                    queryRequest.getUserId(), queryRequest.getTargetId(), queryRequest.getLikeType());
            
            IPage<Like> likePage = likeDomainService.queryLikes(queryRequest);
            
            List<LikeInfo> likeInfos = likePage.getRecords().stream()
                    .map(likeConverter::toInfo)
                    .collect(Collectors.toList());
            
            return LikeQueryResponse.success(
                    likeInfos,
                    likePage.getTotal(),
                    queryRequest.getPageSize(),
                    queryRequest.getCurrentPage()
            );
            
        } catch (Exception e) {
            log.error("查询点赞记录失败", e);
            return LikeQueryResponse.error("LIKE_QUERY_ERROR", "查询点赞记录失败：" + e.getMessage());
        }
    }
    
    @Override
    public LikeResponse checkUserLikeStatus(Long userId, Long targetId, String likeType) {
        try {
            log.info("检查用户点赞状态，用户ID：{}，目标对象ID：{}，类型：{}", userId, targetId, likeType);
            
            LikeType type = LikeType.fromCode(likeType);
            Like like = likeDomainService.getUserLikeStatus(userId, targetId, type);
            
            String status = like != null ? getActionTypeDescription(like.getActionType()) : "UNLIKED";
            
            // 获取统计信息
            LikeDomainService.LikeStatistics statistics = likeDomainService.getLikeStatistics(targetId, type);
            Long likeCount = statistics != null ? statistics.getTotalLikeCount() : 0L;
            Long dislikeCount = statistics != null ? statistics.getTotalDislikeCount() : 0L;
            
            return LikeResponse.success(likeCount, dislikeCount, status);
            
        } catch (Exception e) {
            log.error("检查用户点赞状态失败，用户ID：{}，目标对象ID：{}", userId, targetId, e);
            return LikeResponse.error("CHECK_LIKE_STATUS_ERROR", "检查点赞状态失败：" + e.getMessage());
        }
    }
    
    @Override
    public LikeResponse getLikeStatistics(Long targetId, String likeType) {
        try {
            log.info("获取点赞统计，目标对象ID：{}，类型：{}", targetId, likeType);
            
            LikeType type = LikeType.fromCode(likeType);
            LikeDomainService.LikeStatistics statistics = likeDomainService.getLikeStatistics(targetId, type);
            
            Long likeCount = statistics != null ? statistics.getTotalLikeCount() : 0L;
            Long dislikeCount = statistics != null ? statistics.getTotalDislikeCount() : 0L;
            
            return LikeResponse.success(likeCount, dislikeCount, "");
            
        } catch (Exception e) {
            log.error("获取点赞统计失败，目标对象ID：{}", targetId, e);
            return LikeResponse.error("GET_LIKE_STATISTICS_ERROR", "获取点赞统计失败：" + e.getMessage());
        }
    }
    
    @Override
    public LikeQueryResponse getUserLikeHistory(LikeQueryRequest queryRequest) {
        try {
            log.info("获取用户点赞历史，用户ID：{}，类型：{}", queryRequest.getUserId(), queryRequest.getLikeType());
            
            IPage<Like> likePage = likeDomainService.getUserLikeHistory(queryRequest);
            
            List<LikeInfo> likeInfos = likePage.getRecords().stream()
                    .map(likeConverter::toInfo)
                    .collect(Collectors.toList());
            
            return LikeQueryResponse.success(
                    likeInfos,
                    likePage.getTotal(),
                    queryRequest.getPageSize(),
                    queryRequest.getCurrentPage()
            );
            
        } catch (Exception e) {
            log.error("获取用户点赞历史失败，用户ID：{}", queryRequest.getUserId(), e);
            return LikeQueryResponse.error("GET_USER_LIKE_HISTORY_ERROR", "获取用户点赞历史失败：" + e.getMessage());
        }
    }
    
    /**
     * 将操作类型转换为描述
     */
    private String getActionTypeDescription(Integer actionType) {
        if (actionType == null) {
            return "UNLIKED";
        }
        return switch (actionType) {
            case 1 -> "LIKED";
            case -1 -> "DISLIKED";
            default -> "UNLIKED";
        };
    }
} 