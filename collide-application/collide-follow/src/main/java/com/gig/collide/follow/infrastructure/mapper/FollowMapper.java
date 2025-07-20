package com.gig.collide.follow.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.follow.constant.FollowTypeEnum;
import com.gig.collide.follow.domain.entity.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 关注数据访问层
 * @author GIG
 */
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    /**
     * 查询关注关系
     */
    Follow selectByFollowerAndFollowed(@Param("followerUserId") Long followerUserId, 
                                      @Param("followedUserId") Long followedUserId);

    /**
     * 分页查询关注列表
     */
    IPage<Follow> selectFollowList(Page<Follow> page, 
                                  @Param("userId") Long userId, 
                                  @Param("followType") FollowTypeEnum followType);

    /**
     * 分页查询粉丝列表
     */
    IPage<Follow> selectFollowerList(Page<Follow> page, @Param("userId") Long userId);

    /**
     * 统计关注数量
     */
    int countFollowing(@Param("userId") Long userId);

    /**
     * 统计粉丝数量
     */
    int countFollowers(@Param("userId") Long userId);
} 