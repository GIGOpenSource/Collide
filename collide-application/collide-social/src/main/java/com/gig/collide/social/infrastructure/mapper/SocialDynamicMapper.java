package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.social.domain.entity.SocialDynamic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社交动态Mapper接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface SocialDynamicMapper extends BaseMapper<SocialDynamic> {

    /**
     * 根据用户ID查询动态列表
     */
    List<SocialDynamic> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据动态类型查询动态列表
     */
    List<SocialDynamic> selectByDynamicType(@Param("dynamicType") String dynamicType, @Param("limit") Integer limit);

    /**
     * 搜索动态（按内容模糊搜索）
     */
    List<SocialDynamic> searchByContent(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 获取热门动态（按点赞数排序）
     */
    List<SocialDynamic> selectHotDynamics(@Param("limit") Integer limit);

    /**
     * 获取关注用户的动态流
     */
    List<SocialDynamic> selectFollowingDynamics(@Param("userId") Long userId, 
                                               @Param("offset") Integer offset, 
                                               @Param("limit") Integer limit);

    /**
     * 增加点赞数
     */
    int increaseLikeCount(@Param("dynamicId") Long dynamicId);

    /**
     * 减少点赞数
     */
    int decreaseLikeCount(@Param("dynamicId") Long dynamicId);

    /**
     * 增加评论数
     */
    int increaseCommentCount(@Param("dynamicId") Long dynamicId);

    /**
     * 增加分享数
     */
    int increaseShareCount(@Param("dynamicId") Long dynamicId);

    /**
     * 批量查询用户的动态数量
     */
    List<Long> selectUserDynamicCounts(@Param("userIds") List<Long> userIds);
} 