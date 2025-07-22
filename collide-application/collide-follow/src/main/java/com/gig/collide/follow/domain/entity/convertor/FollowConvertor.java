package com.gig.collide.follow.domain.entity.convertor;

import com.gig.collide.api.follow.response.data.FollowInfo;
import com.gig.collide.api.follow.response.data.FollowStatistics;
import com.gig.collide.follow.domain.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 关注实体转换器
 * 使用 MapStruct 进行 Follow 实体和 API 对象之间的转换
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FollowConvertor {

    FollowConvertor INSTANCE = Mappers.getMapper(FollowConvertor.class);

    /**
     * Follow 实体转换为 FollowInfo
     *
     * @param follow Follow 实体
     * @return FollowInfo
     */
    @Mapping(target = "followerUser", ignore = true)
    @Mapping(target = "followedUser", ignore = true) 
    @Mapping(target = "mutualFollow", ignore = true)
    FollowInfo toFollowInfo(Follow follow);

    /**
     * Follow 实体列表转换为 FollowInfo 列表
     *
     * @param follows Follow 实体列表
     * @return FollowInfo 列表
     */
    List<FollowInfo> toFollowInfoList(List<Follow> follows);

    /**
     * FollowStatistics 实体转换为 API FollowStatistics
     *
     * @param followStatistics FollowStatistics 实体
     * @return API FollowStatistics
     */
    @Mapping(target = "mutualFollowCount", constant = "0")
    com.gig.collide.api.follow.response.data.FollowStatistics toFollowStatistics(
        com.gig.collide.follow.domain.entity.FollowStatistics followStatistics);

    /**
     * FollowStatistics 实体列表转换为 API FollowStatistics 列表
     *
     * @param followStatisticsList FollowStatistics 实体列表
     * @return API FollowStatistics 列表
     */
    List<com.gig.collide.api.follow.response.data.FollowStatistics> toFollowStatisticsList(
        List<com.gig.collide.follow.domain.entity.FollowStatistics> followStatisticsList);
} 