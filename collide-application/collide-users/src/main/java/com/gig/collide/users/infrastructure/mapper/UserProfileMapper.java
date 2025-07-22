package com.gig.collide.users.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.users.domain.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户扩展信息数据访问映射器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    /**
     * 根据用户ID查询用户扩展信息
     *
     * @param userId 用户ID
     * @return 用户扩展信息
     */
    UserProfile selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID删除用户扩展信息
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 更新用户统计信息
     *
     * @param userId        用户ID
     * @param field         字段名
     * @param incrementValue 增量值（正数为增加，负数为减少）
     * @return 影响行数
     */
    int updateCountField(@Param("userId") Long userId, 
                        @Param("field") String field, 
                        @Param("incrementValue") Long incrementValue);
} 