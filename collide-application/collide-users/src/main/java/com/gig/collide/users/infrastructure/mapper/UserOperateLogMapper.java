package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserOperateLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户操作日志Mapper
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Mapper
public interface UserOperateLogMapper extends BaseMapper<UserOperateLog> {

    /**
     * 根据用户ID查询操作日志
     * 
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 操作日志列表
     */
    List<UserOperateLog> findByUserIdLimit(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据操作类型统计数量
     * 
     * @param userId 用户ID
     * @param operateType 操作类型
     * @return 统计数量
     */
    Integer countByUserIdAndOperateType(@Param("userId") Long userId, @Param("operateType") String operateType);
} 