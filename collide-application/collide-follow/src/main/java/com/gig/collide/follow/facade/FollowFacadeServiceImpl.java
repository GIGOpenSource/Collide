package com.gig.collide.follow.facade;

import com.gig.collide.api.follow.constant.FollowStatusEnum;
import com.gig.collide.api.follow.request.*;
import com.gig.collide.api.follow.response.FollowOperatorResponse;
import com.gig.collide.api.follow.response.FollowQueryResponse;
import com.gig.collide.api.follow.response.FollowStatusResponse;
import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowerInfo;
import com.gig.collide.api.follow.service.FollowFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.follow.domain.service.FollowService;
import com.gig.collide.follow.infrastructure.exception.FollowException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.gig.collide.follow.infrastructure.exception.FollowErrorCode.*;

/**
 * 关注门面服务实现
 * @author GIG
 */
@DubboService(version = "1.0.0")
public class FollowFacadeServiceImpl implements FollowFacadeService {

    @Autowired
    private FollowService followService;

    @Override
    public FollowOperatorResponse follow(FollowRequest followRequest) {
        try {
            if (followRequest.getFollowerUserId().equals(followRequest.getFollowedUserId())) {
                throw new FollowException(CANNOT_FOLLOW_SELF);
            }
            
            boolean result = followService.follow(
                followRequest.getFollowerUserId(), 
                followRequest.getFollowedUserId(),
                followRequest.getFollowType()
            );
            
            return new FollowOperatorResponse(result);
        } catch (Exception e) {
            FollowOperatorResponse response = new FollowOperatorResponse(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public FollowOperatorResponse unfollow(UnfollowRequest unfollowRequest) {
        try {
            boolean result = followService.unfollow(
                unfollowRequest.getFollowerUserId(), 
                unfollowRequest.getFollowedUserId()
            );
            
            return new FollowOperatorResponse(result);
        } catch (Exception e) {
            FollowOperatorResponse response = new FollowOperatorResponse(false);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    @Override
    public FollowStatusResponse queryFollowStatus(FollowStatusRequest followStatusRequest) {
        boolean isFollowed = followService.isFollowed(
            followStatusRequest.getFollowerUserId(), 
            followStatusRequest.getFollowedUserId()
        );
        
        boolean isMutualFollow = false;
        if (isFollowed) {
            isMutualFollow = followService.isFollowed(
                followStatusRequest.getFollowedUserId(), 
                followStatusRequest.getFollowerUserId()
            );
        }
        
        FollowStatusEnum status;
        if (!isFollowed) {
            status = FollowStatusEnum.NOT_FOLLOWED;
        } else if (isMutualFollow) {
            status = FollowStatusEnum.MUTUAL_FOLLOWED;
        } else {
            status = FollowStatusEnum.FOLLOWED;
        }
        
        FollowStatusResponse response = new FollowStatusResponse(status);
        return response;
    }

    @Override
    public PageResponse<FollowInfo> queryFollowList(FollowListRequest followListRequest) {
        return followService.getFollowList(
            followListRequest.getUserId(), 
            followListRequest.getCurrentPage(), 
            followListRequest.getPageSize(),
            followListRequest.getFollowType()
        );
    }

    @Override
    public PageResponse<FollowerInfo> queryFollowerList(FollowerListRequest followerListRequest) {
        return followService.getFollowerList(
            followerListRequest.getUserId(), 
            followerListRequest.getCurrentPage(), 
            followerListRequest.getPageSize()
        );
    }

    @Override
    public FollowQueryResponse<Integer> queryFollowCount(FollowStatisticsRequest followStatisticsRequest) {
        FollowQueryResponse<Integer> response = new FollowQueryResponse<>();
        int count = followService.getFollowingCount(followStatisticsRequest.getUserId());
        response.setData(count);
        return response;
    }

    @Override
    public FollowQueryResponse<Integer> queryFollowerCount(FollowerStatisticsRequest followerStatisticsRequest) {
        FollowQueryResponse<Integer> response = new FollowQueryResponse<>();
        int count = followService.getFollowerCount(followerStatisticsRequest.getUserId());
        response.setData(count);
        return response;
    }
} 