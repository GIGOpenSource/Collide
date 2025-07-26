package com.gig.collide.like.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.like.domain.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 点赞数据访问接口 - 去连表化设计
 * 所有查询都基于单表，避免复杂JOIN操作
 * 
 * @author Collide
 * @since 2.0.0
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {
    
    // ========== 核心查询方法（单表查询，去连表化） ==========
    
    /**
     * 根据用户ID和目标对象查询点赞记录
     * 基于唯一索引的高效查询
     */
    Like selectByUserAndTarget(@Param("userId") Long userId, 
                              @Param("targetId") Long targetId, 
                              @Param("targetType") String targetType);
    
    /**
     * 分页查询点赞记录（包含完整冗余信息）
     * 无需额外查询用户和目标信息
     */
    IPage<Like> selectLikePage(Page<Like> page,
                              @Param("userId") Long userId,
                              @Param("targetId") Long targetId,
                              @Param("targetType") String targetType,
                              @Param("actionType") Integer actionType,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计目标对象的点赞数（单表聚合查询）
     */
    Long countLikesByTarget(@Param("targetId") Long targetId, 
                           @Param("targetType") String targetType, 
                           @Param("actionType") Integer actionType);
    
    /**
     * 获取用户的点赞历史（包含冗余目标信息）
     * 无需关联查询目标表
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
     * 获取对象的点赞用户列表（包含冗余用户信息）
     * 无需关联查询用户表
     */
    IPage<Like> selectLikeUsers(Page<Like> page,
                               @Param("targetId") Long targetId,
                               @Param("targetType") String targetType,
                               @Param("actionType") Integer actionType);
    
    // ========== 高级查询方法 ==========
    
    /**
     * 获取用户最近点赞的内容（包含冗余信息）
     */
    List<Like> selectUserRecentLikes(@Param("userId") Long userId,
                                    @Param("targetType") String targetType,
                                    @Param("limit") Integer limit);
    
    /**
     * 获取热门点赞内容排行（基于统计字段）
     */
    List<Map<String, Object>> selectPopularTargets(@Param("targetType") String targetType,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime,
                                                   @Param("limit") Integer limit);
    
    /**
     * 获取活跃点赞用户排行（基于冗余字段）
     */
    List<Map<String, Object>> selectActiveUsers(@Param("targetType") String targetType,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("minLikeCount") Integer minLikeCount,
                                               @Param("limit") Integer limit);
    
    // ========== 统计查询方法（单表聚合） ==========
    
    /**
     * 获取目标对象的详细统计信息
     */
    Map<String, Object> selectTargetStatistics(@Param("targetId") Long targetId,
                                              @Param("targetType") String targetType);
    
    /**
     * 获取用户的点赞统计信息
     */
    Map<String, Object> selectUserStatistics(@Param("userId") Long userId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 获取时间段内的点赞趋势
     */
    List<Map<String, Object>> selectLikeTrend(@Param("targetType") String targetType,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);
    
    // ========== 数据更新方法（冗余字段更新） ==========
    
    /**
     * 更新内容表的点赞统计（去连表化关键）
     */
    @Update("UPDATE t_content SET like_count = like_count + #{delta}, " +
            "like_rate = CASE WHEN (like_count + dislike_count + #{delta}) > 0 " +
            "THEN ROUND(((like_count + #{delta}) * 100.0) / (like_count + dislike_count + #{delta}), 2) " +
            "ELSE 0.00 END, " +
            "last_like_time = CASE WHEN #{delta} > 0 THEN NOW() ELSE last_like_time END, " +
            "updated_time = NOW() " +
            "WHERE id = #{contentId} AND like_count + #{delta} >= 0")
    int updateContentLikeCount(@Param("contentId") Long contentId, @Param("delta") int delta);
    
    /**
     * 更新内容表的点踩统计
     */
    @Update("UPDATE t_content SET dislike_count = dislike_count + #{delta}, " +
            "like_rate = CASE WHEN (like_count + dislike_count + #{delta}) > 0 " +
            "THEN ROUND((like_count * 100.0) / (like_count + dislike_count + #{delta}), 2) " +
            "ELSE 0.00 END, " +
            "updated_time = NOW() " +
            "WHERE id = #{contentId} AND dislike_count + #{delta} >= 0")
    int updateContentDislikeCount(@Param("contentId") Long contentId, @Param("delta") int delta);
    
    /**
     * 更新评论表的点赞统计
     */
    @Update("UPDATE t_comment SET like_count = like_count + #{delta}, " +
            "like_rate = CASE WHEN (like_count + dislike_count + #{delta}) > 0 " +
            "THEN ROUND(((like_count + #{delta}) * 100.0) / (like_count + dislike_count + #{delta}), 2) " +
            "ELSE 0.00 END, " +
            "last_like_time = CASE WHEN #{delta} > 0 THEN NOW() ELSE last_like_time END, " +
            "updated_time = NOW() " +
            "WHERE id = #{commentId} AND like_count + #{delta} >= 0")
    int updateCommentLikeCount(@Param("commentId") Long commentId, @Param("delta") int delta);
    
    /**
     * 更新评论表的点踩统计
     */
    @Update("UPDATE t_comment SET dislike_count = dislike_count + #{delta}, " +
            "like_rate = CASE WHEN (like_count + dislike_count + #{delta}) > 0 " +
            "THEN ROUND((like_count * 100.0) / (like_count + dislike_count + #{delta}), 2) " +
            "ELSE 0.00 END, " +
            "updated_time = NOW() " +
            "WHERE id = #{commentId} AND dislike_count + #{delta} >= 0")
    int updateCommentDislikeCount(@Param("commentId") Long commentId, @Param("delta") int delta);
    
    // ========== 数据维护方法 ==========
    
    /**
     * 批量更新冗余用户信息（用户信息变更时同步）
     */
    int updateUserInfoBatch(@Param("userId") Long userId,
                           @Param("userNickname") String userNickname,
                           @Param("userAvatar") String userAvatar);
    
    /**
     * 批量更新冗余目标信息（目标信息变更时同步）
     */
    int updateTargetInfoBatch(@Param("targetId") Long targetId,
                             @Param("targetType") String targetType,
                             @Param("targetTitle") String targetTitle,
                             @Param("targetAuthorId") Long targetAuthorId);
    
    /**
     * 清理过期数据（定期维护）
     */
    int cleanupExpiredData(@Param("expireTime") LocalDateTime expireTime,
                          @Param("batchSize") Integer batchSize);
} 