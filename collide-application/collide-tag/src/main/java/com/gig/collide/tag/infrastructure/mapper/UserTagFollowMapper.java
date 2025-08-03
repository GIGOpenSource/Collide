package com.gig.collide.tag.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.tag.domain.entity.UserTagFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用户标签关注Mapper接口 - 对应 t_user_tag_follow 表
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Mapper
public interface UserTagFollowMapper extends BaseMapper<UserTagFollow> {

    // =================== 用户关注查询 ===================

    /**
     * 查询用户是否关注了指定标签
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否关注
     */
    boolean isUserFollowingTag(@Param("userId") Long userId, @Param("tagId") Long tagId);

    /**
     * 获取用户关注的标签ID列表
     * 
     * @param userId 用户ID
     * @return 标签ID列表
     */
    List<Long> getUserFollowedTagIds(@Param("userId") Long userId);

    /**
     * 获取用户关注的标签详情列表
     * 
     * @param userId 用户ID
     * @return 用户标签关注列表（含标签详情）
     */
    List<UserTagFollow> getUserFollowedTagsWithDetails(@Param("userId") Long userId);

    /**
     * 统计用户关注的标签数量
     * 
     * @param userId 用户ID
     * @return 关注标签数量
     */
    Integer countUserFollowedTags(@Param("userId") Long userId);

    /**
     * 获取用户最近关注的标签
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 最近关注的标签列表
     */
    List<UserTagFollow> getUserRecentFollowedTags(@Param("userId") Long userId, 
                                                 @Param("days") Integer days, 
                                                 @Param("limit") Integer limit);

    // =================== 标签关注者查询 ===================

    /**
     * 获取标签的关注者用户ID列表
     * 
     * @param tagId 标签ID
     * @param limit 数量限制
     * @return 关注者用户ID列表
     */
    List<Long> getTagFollowers(@Param("tagId") Long tagId, @Param("limit") Integer limit);

    /**
     * 统计标签的关注者数量
     * 
     * @param tagId 标签ID
     * @return 关注者数量
     */
    Long countTagFollowers(@Param("tagId") Long tagId);

    /**
     * 获取标签最近的关注者
     * 
     * @param tagId 标签ID
     * @param days 天数范围
     * @param limit 数量限制
     * @return 最近关注者用户ID列表
     */
    List<Long> getTagRecentFollowers(@Param("tagId") Long tagId, 
                                    @Param("days") Integer days, 
                                    @Param("limit") Integer limit);

    // =================== 协同过滤支持查询 ===================

    /**
     * 获取活跃的标签用户（用于协同过滤）
     * 
     * @param minTagCount 最小标签数量
     * @param days 活跃天数范围
     * @param limit 数量限制
     * @return 活跃用户ID列表
     */
    List<Long> getActiveTagUsers(@Param("minTagCount") Integer minTagCount, 
                                @Param("days") Integer days, 
                                @Param("limit") Integer limit);

    /**
     * 查找关注了指定标签的其他用户
     * 
     * @param tagIds 标签ID列表
     * @param excludeUserId 排除的用户ID
     * @param limit 数量限制
     * @return 其他用户ID列表
     */
    List<Long> findUsersWithSimilarTags(@Param("tagIds") List<Long> tagIds, 
                                       @Param("excludeUserId") Long excludeUserId, 
                                       @Param("limit") Integer limit);

    /**
     * 计算用户之间的共同标签数量
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 共同标签数量
     */
    Integer countCommonTags(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 获取两个用户的共同标签列表
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 共同标签ID列表
     */
    List<Long> getCommonTagIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // =================== 条件查询 ===================

    /**
     * 根据条件查询用户标签关注记录
     * 
     * @param userId 用户ID
     * @param tagId 标签ID（可选）
     * @param followStartDate 关注开始日期
     * @param followEndDate 关注结束日期
     * @param tagStatus 标签状态
     * @param sortField 排序字段
     * @param sortDirection 排序方向
     * @param offset 偏移量
     * @param pageSize 页面大小
     * @return 用户标签关注记录列表
     */
    List<UserTagFollow> findUserTagFollowsByCondition(@Param("userId") Long userId,
                                                      @Param("tagId") Long tagId,
                                                      @Param("followStartDate") LocalDate followStartDate,
                                                      @Param("followEndDate") LocalDate followEndDate,
                                                      @Param("tagStatus") Integer tagStatus,
                                                      @Param("sortField") String sortField,
                                                      @Param("sortDirection") String sortDirection,
                                                      @Param("offset") Integer offset,
                                                      @Param("pageSize") Integer pageSize);

    /**
     * 根据条件统计用户标签关注记录数量
     * 
     * @param userId 用户ID
     * @param tagId 标签ID（可选）
     * @param followStartDate 关注开始日期
     * @param followEndDate 关注结束日期
     * @param tagStatus 标签状态
     * @return 记录数量
     */
    Long countUserTagFollowsByCondition(@Param("userId") Long userId,
                                       @Param("tagId") Long tagId,
                                       @Param("followStartDate") LocalDate followStartDate,
                                       @Param("followEndDate") LocalDate followEndDate,
                                       @Param("tagStatus") Integer tagStatus);

    // =================== 批量操作 ===================

    /**
     * 批量检查用户是否关注了指定标签
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 已关注的标签ID列表
     */
    List<Long> batchCheckUserFollowing(@Param("userId") Long userId, @Param("tagIds") List<Long> tagIds);

    /**
     * 批量删除用户的标签关注
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 删除行数
     */
    int batchDeleteUserTagFollows(@Param("userId") Long userId, @Param("tagIds") List<Long> tagIds);

    /**
     * 批量插入用户标签关注记录
     * 
     * @param follows 用户标签关注记录列表
     * @return 插入行数
     */
    int batchInsertUserTagFollows(@Param("follows") List<UserTagFollow> follows);

    // =================== 统计分析 ===================

    /**
     * 获取用户标签关注的权重分布
     * 
     * @param userId 用户ID
     * @return 权重分布统计
     */
    List<Map<String, Object>> getUserTagWeightDistribution(@Param("userId") Long userId);

    /**
     * 获取用户关注标签的时间分布
     * 
     * @param userId 用户ID
     * @param days 天数范围
     * @return 时间分布统计
     */
    List<Map<String, Object>> getUserFollowTimeDistribution(@Param("userId") Long userId, @Param("days") Integer days);

    /**
     * 统计每日新增关注数
     * 
     * @param tagId 标签ID（可选）
     * @param days 天数范围
     * @return 每日统计数据
     */
    List<Map<String, Object>> getDailyFollowStats(@Param("tagId") Long tagId, @Param("days") Integer days);

    // =================== 数据清理 ===================

    /**
     * 清理无效的用户标签关注（针对已删除的标签）
     * 
     * @param userId 用户ID（可选，为空时清理所有用户）
     * @return 清理行数
     */
    int cleanupInvalidFollows(@Param("userId") Long userId);

    /**
     * 删除用户的所有标签关注
     * 
     * @param userId 用户ID
     * @return 删除行数
     */
    int deleteAllUserFollows(@Param("userId") Long userId);

    /**
     * 删除标签的所有关注记录
     * 
     * @param tagId 标签ID
     * @return 删除行数
     */
    int deleteAllTagFollows(@Param("tagId") Long tagId);
}