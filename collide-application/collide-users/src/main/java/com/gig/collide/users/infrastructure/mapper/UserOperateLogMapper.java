package com.gig.collide.users.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.users.domain.entity.UserOperateLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户操作日志数据访问映射器
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
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 操作日志列表
     */
    List<UserOperateLog> selectByUserId(@Param("userId") Long userId, 
                                       @Param("offset") int offset, 
                                       @Param("limit") int limit);

    /**
     * 根据操作类型查询日志
     *
     * @param operateType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 操作日志列表
     */
    List<UserOperateLog> selectByOperateType(@Param("operateType") String operateType,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    /**
     * 统计用户操作次数
     *
     * @param userId 用户ID
     * @param operateType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作次数
     */
    long countUserOperations(@Param("userId") Long userId,
                            @Param("operateType") String operateType,
                            @Param("startTime") LocalDateTime startTime,
                            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取操作统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    List<Map<String, Object>> getOperateStatistics(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 删除过期日志（物理删除）
     *
     * @param expireTime 过期时间
     * @return 删除条数
     */
    int deleteExpiredLogs(@Param("expireTime") LocalDateTime expireTime);
} 