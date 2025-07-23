package com.gig.collide.like.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.like.domain.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 点赞数据访问接口
 * 
 * @author Collide
 * @since 1.0.0
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {
    
    /**
     * 根据用户ID和目标对象查询点赞记录
     */
    Like selectByUserAndTarget(@Param("userId") Long userId, 
                              @Param("targetId") Long targetId, 
                              @Param("targetType") String targetType);
    
    /**
     * 分页查询点赞记录
     */
    IPage<Like> selectLikePage(Page<Like> page,
                              @Param("userId") Long userId,
                              @Param("targetId") Long targetId,
                              @Param("targetType") String targetType,
                              @Param("actionType") Integer actionType,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计目标对象的点赞数
     */
    Long countLikesByTarget(@Param("targetId") Long targetId, 
                           @Param("targetType") String targetType, 
                           @Param("actionType") Integer actionType);
    
    /**
     * 获取用户的点赞历史
     */
    IPage<Like> selectUserLikeHistory(Page<Like> page,
                                     @Param("userId") Long userId,
                                     @Param("targetType") String targetType,
                                     @Param("actionType") Integer actionType,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 批量查询用户对多个对象的点赞状态
     */
    List<Like> selectBatchUserLikeStatus(@Param("userId") Long userId,
                                        @Param("targetIds") List<Long> targetIds,
                                        @Param("targetType") String targetType);
    
    /**
     * 获取对象的点赞用户列表
     */
    IPage<Like> selectLikeUsers(Page<Like> page,
                               @Param("targetId") Long targetId,
                               @Param("targetType") String targetType,
                               @Param("actionType") Integer actionType);
} 