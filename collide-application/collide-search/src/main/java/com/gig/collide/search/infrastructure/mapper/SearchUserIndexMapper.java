package com.gig.collide.search.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.search.domain.entity.SearchUserIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索用户索引 Mapper 接口
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper
public interface SearchUserIndexMapper extends BaseMapper<SearchUserIndex> {

    /**
     * 全文搜索用户
     * 
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param role 用户角色过滤
     * @param status 用户状态过滤
     * @param isVerified 是否认证过滤
     * @param location 地区过滤
     * @return 分页结果
     */
    IPage<SearchUserIndex> fullTextSearchUsers(
            @Param("page") Page<SearchUserIndex> page,
            @Param("keyword") String keyword,
            @Param("role") String role,
            @Param("status") String status,
            @Param("isVerified") Integer isVerified,
            @Param("location") String location
    );

    /**
     * 精确搜索用户
     * 
     * @param page 分页参数
     * @param username 用户名（精确匹配）
     * @param nickname 昵称（模糊匹配）
     * @param role 用户角色
     * @param status 用户状态
     * @return 分页结果
     */
    IPage<SearchUserIndex> exactSearchUsers(
            @Param("page") Page<SearchUserIndex> page,
            @Param("username") String username,
            @Param("nickname") String nickname,
            @Param("role") String role,
            @Param("status") String status
    );

    /**
     * 查询热门用户
     * 
     * @param role 用户角色过滤
     * @param limit 限制数量
     * @return 热门用户列表
     */
    List<SearchUserIndex> queryHotUsers(
            @Param("role") String role,
            @Param("limit") Integer limit
    );

    /**
     * 查询活跃用户
     * 
     * @param minActivityScore 最小活跃度评分
     * @param limit 限制数量
     * @return 活跃用户列表
     */
    List<SearchUserIndex> queryActiveUsers(
            @Param("minActivityScore") BigDecimal minActivityScore,
            @Param("limit") Integer limit
    );

    /**
     * 查询影响力用户
     * 
     * @param minInfluenceScore 最小影响力评分
     * @param limit 限制数量
     * @return 影响力用户列表
     */
    List<SearchUserIndex> queryInfluentialUsers(
            @Param("minInfluenceScore") BigDecimal minInfluenceScore,
            @Param("limit") Integer limit
    );

    /**
     * 根据标签搜索用户
     * 
     * @param page 分页参数
     * @param tags 标签列表
     * @param matchAll 是否需要匹配所有标签
     * @return 分页结果
     */
    IPage<SearchUserIndex> searchUsersByTags(
            @Param("page") Page<SearchUserIndex> page,
            @Param("tags") List<String> tags,
            @Param("matchAll") Boolean matchAll
    );

    /**
     * 同步用户索引数据
     * 
     * @param userId 用户ID
     * @param userIndex 用户索引信息
     * @return 更新数量
     */
    int syncUserIndex(@Param("userId") Long userId, @Param("userIndex") SearchUserIndex userIndex);

    /**
     * 批量同步用户索引数据
     * 
     * @param userIndexList 用户索引列表
     * @return 更新数量
     */
    int batchSyncUserIndex(@Param("userIndexList") List<SearchUserIndex> userIndexList);

    /**
     * 更新用户统计信息
     * 
     * @param userId 用户ID
     * @param followerCount 粉丝数
     * @param followingCount 关注数
     * @param contentCount 内容数
     * @param likeCount 点赞数
     * @param viewCount 浏览数
     * @return 更新数量
     */
    int updateUserStatistics(
            @Param("userId") Long userId,
            @Param("followerCount") Long followerCount,
            @Param("followingCount") Long followingCount,
            @Param("contentCount") Long contentCount,
            @Param("likeCount") Long likeCount,
            @Param("viewCount") Long viewCount
    );

    /**
     * 更新用户评分
     * 
     * @param userId 用户ID
     * @param activityScore 活跃度评分
     * @param influenceScore 影响力评分
     * @param qualityScore 质量评分
     * @param searchWeight 搜索权重
     * @return 更新数量
     */
    int updateUserScore(
            @Param("userId") Long userId,
            @Param("activityScore") BigDecimal activityScore,
            @Param("influenceScore") BigDecimal influenceScore,
            @Param("qualityScore") BigDecimal qualityScore,
            @Param("searchWeight") Double searchWeight
    );

    /**
     * 根据用户ID列表批量查询
     * 
     * @param userIds 用户ID列表
     * @return 用户索引列表
     */
    List<SearchUserIndex> queryByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 查询需要更新的用户索引
     * 
     * @param lastUpdateTime 最后更新时间阈值
     * @param limit 限制数量
     * @return 用户索引列表
     */
    List<SearchUserIndex> queryNeedUpdateUsers(
            @Param("lastUpdateTime") LocalDateTime lastUpdateTime,
            @Param("limit") Integer limit
    );

    /**
     * 清理无效用户索引
     * 
     * @param expireTime 过期时间
     * @return 清理数量
     */
    int cleanInvalidUserIndex(@Param("expireTime") LocalDateTime expireTime);
} 