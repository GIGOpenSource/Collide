package com.gig.collide.follow.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.follow.request.FollowRequest;
import com.gig.collide.api.follow.request.FollowQueryRequest;
import com.gig.collide.api.follow.request.UnfollowRequest;
import com.gig.collide.api.follow.response.FollowResponse;
import com.gig.collide.api.follow.response.FollowQueryResponse;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowStatistics;
import com.gig.collide.api.follow.service.FollowFacadeService;
import com.gig.collide.rpc.facade.Facade;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.follow.domain.entity.Follow;
import com.gig.collide.follow.domain.entity.convertor.FollowConvertor;
import com.gig.collide.follow.domain.service.FollowDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关注服务 Facade 实现
 * 对外提供 RPC 服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class FollowFacadeServiceImpl implements FollowFacadeService {

    private final FollowDomainService followDomainService;

    @Override
    @Facade
    public FollowResponse follow(FollowRequest followRequest) {
        try {
            log.info("处理关注请求，关注者: {}, 被关注者: {}", 
                followRequest.getFollowerUserId(), followRequest.getFollowedUserId());

            Follow follow = followDomainService.followUser(
                followRequest.getFollowerUserId(),
                followRequest.getFollowedUserId(),
                followRequest.getFollowType()
            );

            // 检查是否相互关注
            boolean mutualFollow = followDomainService.isMutualFollowing(
                followRequest.getFollowerUserId(), 
                followRequest.getFollowedUserId()
            );

            FollowResponse response = new FollowResponse();
            response.setSuccess(true);
            response.setResponseMessage("关注成功");
            response.setFollowId(follow.getId());
            response.setNewFollow(follow.getId() != null);
            response.setMutualFollow(mutualFollow);

            log.info("关注操作完成，关注ID: {}, 相互关注: {}", follow.getId(), mutualFollow);
            return response;

        } catch (Exception e) {
            log.error("关注操作失败", e);
            FollowResponse response = new FollowResponse();
            response.setSuccess(false);
            response.setResponseCode("FOLLOW_ERROR");
            response.setResponseMessage("关注失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    @Facade
    public FollowResponse unfollow(UnfollowRequest unfollowRequest) {
        try {
            log.info("处理取消关注请求，关注者: {}, 被关注者: {}", 
                unfollowRequest.getFollowerUserId(), unfollowRequest.getFollowedUserId());

            boolean success = followDomainService.unfollowUser(
                unfollowRequest.getFollowerUserId(),
                unfollowRequest.getFollowedUserId()
            );

            FollowResponse response = new FollowResponse();
            response.setSuccess(success);
            response.setResponseMessage(success ? "取消关注成功" : "关注关系不存在");

            log.info("取消关注操作完成，结果: {}", success);
            return response;

        } catch (Exception e) {
            log.error("取消关注操作失败", e);
            FollowResponse response = new FollowResponse();
            response.setSuccess(false);
            response.setResponseCode("UNFOLLOW_ERROR");
            response.setResponseMessage("取消关注失败: " + e.getMessage());
            return response;
        }
    }

    @Override
    @Facade
    public FollowQueryResponse<FollowInfo> queryFollow(FollowQueryRequest queryRequest) {
        try {
            boolean isFollowing = followDomainService.isFollowing(
                queryRequest.getFollowerUserId(),
                queryRequest.getFollowedUserId()
            );

            FollowInfo followInfo = new FollowInfo();
            followInfo.setFollowerUserId(queryRequest.getFollowerUserId());
            followInfo.setFollowedUserId(queryRequest.getFollowedUserId());
            
            if (queryRequest.getCheckMutualFollow()) {
                boolean mutualFollow = followDomainService.isMutualFollowing(
                    queryRequest.getFollowerUserId(),
                    queryRequest.getFollowedUserId()
                );
                followInfo.setMutualFollow(mutualFollow);
            }

            return FollowQueryResponse.success(isFollowing ? followInfo : null);

        } catch (Exception e) {
            log.error("查询关注关系失败", e);
            return FollowQueryResponse.success(null);
        }
    }

    @Override
    @Facade
    public PageResponse<FollowInfo> pageQueryFollowing(FollowQueryRequest queryRequest) {
        try {
            log.info("查询关注列表，用户: {}, 页码: {}, 每页大小: {}", 
                queryRequest.getFollowerUserId(), queryRequest.getPageNo(), queryRequest.getPageSize());

            IPage<Follow> page = followDomainService.getFollowingList(
                queryRequest.getFollowerUserId(),
                queryRequest.getPageNo(),
                queryRequest.getPageSize()
            );

            List<FollowInfo> followInfoList = FollowConvertor.INSTANCE.toFollowInfoList(page.getRecords());

            return PageResponse.of(followInfoList, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());

        } catch (Exception e) {
            log.error("查询关注列表失败", e);
            return PageResponse.of(List.of(), 0, queryRequest.getPageSize(), queryRequest.getPageNo());
        }
    }

    @Override
    @Facade
    public PageResponse<FollowInfo> pageQueryFollowers(FollowQueryRequest queryRequest) {
        try {
            log.info("查询粉丝列表，用户: {}, 页码: {}, 每页大小: {}", 
                queryRequest.getFollowedUserId(), queryRequest.getPageNo(), queryRequest.getPageSize());

            IPage<Follow> page = followDomainService.getFollowersList(
                queryRequest.getFollowedUserId(),
                queryRequest.getPageNo(),
                queryRequest.getPageSize()
            );

            List<FollowInfo> followInfoList = FollowConvertor.INSTANCE.toFollowInfoList(page.getRecords());

            return PageResponse.of(followInfoList, (int) page.getTotal(), (int) page.getSize(), (int) page.getCurrent());

        } catch (Exception e) {
            log.error("查询粉丝列表失败", e);
            return PageResponse.of(List.of(), 0, queryRequest.getPageSize(), queryRequest.getPageNo());
        }
    }

    @Override
    @Facade
    public FollowQueryResponse<FollowStatistics> getFollowStatistics(Long userId) {
        try {
            log.info("查询关注统计，用户: {}", userId);

            com.gig.collide.follow.domain.entity.FollowStatistics statistics = 
                followDomainService.getFollowStatistics(userId);

            FollowStatistics followStatistics = FollowConvertor.INSTANCE.toFollowStatistics(statistics);

            return FollowQueryResponse.success(followStatistics);

        } catch (Exception e) {
            log.error("查询关注统计失败，用户: {}", userId, e);
            return FollowQueryResponse.success(null);
        }
    }

    @Override
    @Facade
    public FollowQueryResponse<List<FollowStatistics>> batchGetFollowStatistics(FollowQueryRequest queryRequest) {
        try {
            log.info("批量查询关注统计，用户列表: {}", queryRequest.getUserIds());

            List<com.gig.collide.follow.domain.entity.FollowStatistics> statisticsList = 
                followDomainService.getFollowStatistics(queryRequest.getUserIds());

            List<FollowStatistics> followStatisticsList = 
                FollowConvertor.INSTANCE.toFollowStatisticsList(statisticsList);

            return FollowQueryResponse.success(followStatisticsList, (long) followStatisticsList.size());

        } catch (Exception e) {
            log.error("批量查询关注统计失败", e);
            return FollowQueryResponse.success(List.of(), 0L);
        }
    }

    @Override
    @Facade
    public FollowQueryResponse<List<FollowInfo>> batchCheckFollowRelations(FollowQueryRequest queryRequest) {
        try {
            log.info("批量检查关注关系，关注者: {}, 被关注者列表: {}", 
                queryRequest.getFollowerUserId(), queryRequest.getUserIds());

            List<Follow> followRelations = followDomainService.getFollowRelations(
                queryRequest.getFollowerUserId(), 
                queryRequest.getUserIds()
            );

            List<FollowInfo> followInfoList = FollowConvertor.INSTANCE.toFollowInfoList(followRelations);

            return FollowQueryResponse.success(followInfoList, (long) followInfoList.size());

        } catch (Exception e) {
            log.error("批量检查关注关系失败", e);
            return FollowQueryResponse.success(List.of(), 0L);
        }
    }

    @Override
    @Facade
    public FollowQueryResponse<List<FollowInfo>> getMutualFollows(FollowQueryRequest queryRequest) {
        try {
            log.info("查询相互关注列表，用户: {}", queryRequest.getFollowerUserId());

            List<Follow> mutualFollows = followDomainService.getMutualFollows(queryRequest.getFollowerUserId());
            List<FollowInfo> followInfoList = FollowConvertor.INSTANCE.toFollowInfoList(mutualFollows);

            return FollowQueryResponse.success(followInfoList, (long) followInfoList.size());

        } catch (Exception e) {
            log.error("查询相互关注列表失败", e);
            return FollowQueryResponse.success(List.of(), 0L);
        }
    }
} 