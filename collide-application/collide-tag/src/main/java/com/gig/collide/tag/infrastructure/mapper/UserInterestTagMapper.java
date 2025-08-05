package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.UserInterestTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据标签ID获取关注用户列表
     */
    List<UserInterestTag> selectByTagId(@Param("tagId") Long tagId);

    /**
     * 获取用户高分兴趣标签（兴趣分数排序）
     */
    List<UserInterestTag> selectTopInterestsByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 批量更新用户标签状态
     */
    int batchUpdateStatus(@Param("userId") Long userId, @Param("tagIds") List<Long> tagIds, @Param("status") String status);

    /**
     * 获取用户兴趣标签统计（覆盖索引优化）
     */
    List<Map<String, Object>> getUserInterestStats(@Param("userId") Long userId, @Param("minScore") BigDecimal minScore);

    /**
     * 获取标签的热门关注用户（覆盖索引优化）
     */
    List<Map<String, Object>> getTagHotUsers(@Param("tagId") Long tagId, @Param("limit") Integer limit);
} 