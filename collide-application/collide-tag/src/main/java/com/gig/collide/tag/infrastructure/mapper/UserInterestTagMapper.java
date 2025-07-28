package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签Mapper接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserInterestTagMapper extends BaseMapper<UserInterestTag> {

    /**
     * 获取用户兴趣标签列表
     */
    List<UserInterestTag> selectByUserId(@Param("userId") Long userId);

    /**
     * 更新用户兴趣分数
     */
    int updateInterestScore(@Param("userId") Long userId, 
                           @Param("tagId") Long tagId, 
                           @Param("interestScore") BigDecimal interestScore);

    /**
     * 检查用户是否已关注标签
     */
    int countByUserIdAndTagId(@Param("userId") Long userId, @Param("tagId") Long tagId);
} 