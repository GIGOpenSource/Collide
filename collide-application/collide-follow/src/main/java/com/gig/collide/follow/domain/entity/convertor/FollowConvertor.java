package com.gig.collide.follow.domain.entity.convertor;

import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowerInfo;
import com.gig.collide.follow.domain.entity.Follow;

/**
 * 关注实体转换器
 * @author GIG
 */
public class FollowConvertor {

    /**
     * 转换为关注信息
     */
    public static FollowInfo toFollowInfo(Follow follow) {
        if (follow == null) {
            return null;
        }
        
        FollowInfo followInfo = new FollowInfo();
        followInfo.setFollowId(follow.getId());
        followInfo.setFollowerUserId(follow.getFollowerUserId());
        followInfo.setFollowedUserId(follow.getFollowedUserId());
        followInfo.setFollowType(follow.getFollowType());
        followInfo.setFollowTime(follow.getCreatedTime());
        // 其他字段需要从用户服务获取用户信息
        
        return followInfo;
    }

    /**
     * 转换为粉丝信息
     */
    public static FollowerInfo toFollowerInfo(Follow follow) {
        if (follow == null) {
            return null;
        }
        
        FollowerInfo followerInfo = new FollowerInfo();
        followerInfo.setFollowId(follow.getId());
        followerInfo.setFollowerUserId(follow.getFollowerUserId());
        followerInfo.setFollowedUserId(follow.getFollowedUserId());
        followerInfo.setFollowType(follow.getFollowType());
        followerInfo.setFollowTime(follow.getCreatedTime());
        // 其他字段需要从用户服务获取用户信息
        
        return followerInfo;
    }
} 