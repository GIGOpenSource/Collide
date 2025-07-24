package com.gig.collide.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.business.domain.tag.entity.UserInterestTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签 Mapper 接口
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface UserInterestTagMapper extends BaseMapper<UserInterestTag> {

    /**
     * 根据用户ID查询兴趣标签列表
     */
    List<UserInterestTag> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和标签ID查询兴趣标签
     */
    UserInterestTag selectByUserIdAndTagId(@Param("userId") Long userId, @Param("tagId") Long tagId);

    /**
     * 更新用户标签兴趣分数
     */
    int updateInterestScore(@Param("userId") Long userId, @Param("tagId") Long tagId, 
                           @Param("interestScore") BigDecimal interestScore);

    /**
     * 批量删除用户兴趣标签
     */
    int deleteByUserIdAndTagIds(@Param("userId") Long userId, @Param("tagIds") List<Long> tagIds);

    /**
     * 获取用户兴趣标签的详细信息（关联查询标签信息）
     */
    List<UserInterestTag> selectUserInterestTagsWithDetail(@Param("userId") Long userId);

    /**
     * 根据标签ID查询感兴趣的用户列表
     */
    List<UserInterestTag> selectUsersByTagId(@Param("tagId") Long tagId);

    /**
     * 获取高兴趣分数的用户标签
     */
    List<UserInterestTag> selectHighInterestTags(@Param("userId") Long userId, 
                                               @Param("minScore") BigDecimal minScore);
} 