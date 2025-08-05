package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.SocialDynamic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态Mapper接口 - 重新设计版
 * 专注于核心的数据访问操作，包含完整的CRUD功能
 * 基于t_social_dynamic表结构设计，共25个方法
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Mapper
public interface SocialDynamicMapper extends BaseMapper<SocialDynamic> {

    // =================== 动态创建方法 ===================

    /**
     * 创建动态并返回完整信息
     * 插入后自动填充创建时间、更新时间等字段
     * 
     * @param dynamic 动态实体
     * @return 影响行数
     */
    int insertDynamic(SocialDynamic dynamic);

    /**
     * 批量创建动态
     * 用于批量导入或批量操作场景
     * 
     * @param dynamics 动态列表
     * @return 影响行数
     */
    int batchInsertDynamics(@Param("dynamics") List<SocialDynamic> dynamics);

    /**
     * 创建分享动态
     * 专门用于分享场景，包含分享目标信息
     * 
     * @param dynamic 分享动态实体
     * @return 影响行数
     */
    int insertShareDynamic(SocialDynamic dynamic);

    // =================== 核心查询方法 ===================

    /**
     * 根据用户ID分页查询动态
     * 使用索引: idx_user_id, idx_create_time
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 状态（可选）
     * @param dynamicType 动态类型（可选）
     * @return 分页结果
     */
    IPage<SocialDynamic> selectByUserId(Page<SocialDynamic> page, 
                                       @Param("userId") Long userId,
                                       @Param("status") String status,
                                       @Param("dynamicType") String dynamicType);

    /**
     * 根据动态类型分页查询
     * 使用索引: idx_dynamic_type, idx_create_time
     */
    IPage<SocialDynamic> selectByDynamicType(Page<SocialDynamic> page, 
                                            @Param("dynamicType") String dynamicType,
                                            @Param("status") String status);

    /**
     * 根据状态分页查询动态
     * 使用索引: idx_status, idx_create_time
     */
    IPage<SocialDynamic> selectByStatus(Page<SocialDynamic> page, 
                                       @Param("status") String status);

    /**
     * 获取关注用户的动态流
     * 通过Service层传入关注的用户ID列表，避免JOIN
     * 使用索引: idx_user_id, idx_create_time
     */
    IPage<SocialDynamic> selectFollowingDynamics(Page<SocialDynamic> page,
                                                 @Param("userIds") List<Long> userIds,
                                                 @Param("status") String status);

    /**
     * 搜索动态（按内容搜索）
     * 使用全文搜索或LIKE查询
     */
    IPage<SocialDynamic> searchByContent(Page<SocialDynamic> page,
                                        @Param("keyword") String keyword,
                                        @Param("status") String status);

    /**
     * 获取热门动态（按互动数排序）
     * 使用综合排序：点赞数+评论数+分享数
     */
    IPage<SocialDynamic> selectHotDynamics(Page<SocialDynamic> page,
                                          @Param("status") String status,
                                          @Param("dynamicType") String dynamicType);

    /**
     * 根据分享目标查询分享动态
     * 用于查看某个内容或商品被分享的动态
     */
    IPage<SocialDynamic> selectByShareTarget(Page<SocialDynamic> page,
                                            @Param("shareTargetType") String shareTargetType,
                                            @Param("shareTargetId") Long shareTargetId,
                                            @Param("status") String status);

    // =================== 统计计数方法 ===================

    /**
     * 统计用户动态数量
     * 按状态和类型统计
     */
    Long countByUserId(@Param("userId") Long userId,
                      @Param("status") String status,
                      @Param("dynamicType") String dynamicType);

    /**
     * 统计动态类型数量
     */
    Long countByDynamicType(@Param("dynamicType") String dynamicType,
                           @Param("status") String status);

    /**
     * 统计指定时间范围内的动态数量
     */
    Long countByTimeRange(@Param("startTime") LocalDateTime startTime,
                         @Param("endTime") LocalDateTime endTime,
                         @Param("status") String status);

    // =================== 互动统计更新 ===================

    /**
     * 增加点赞数
     * 使用PRIMARY KEY索引
     */
    int increaseLikeCount(@Param("dynamicId") Long dynamicId);

    /**
     * 减少点赞数
     * 使用PRIMARY KEY索引
     */
    int decreaseLikeCount(@Param("dynamicId") Long dynamicId);

    /**
     * 增加评论数
     * 使用PRIMARY KEY索引
     */
    int increaseCommentCount(@Param("dynamicId") Long dynamicId);

    /**
     * 增加分享数
     * 使用PRIMARY KEY索引
     */
    int increaseShareCount(@Param("dynamicId") Long dynamicId);

    /**
     * 批量更新统计数据
     * 用于统计数据修正
     */
    int updateStatistics(@Param("dynamicId") Long dynamicId,
                        @Param("likeCount") Long likeCount,
                        @Param("commentCount") Long commentCount,
                        @Param("shareCount") Long shareCount);

    // =================== 状态管理 ===================

    /**
     * 更新动态状态
     */
    int updateStatus(@Param("dynamicId") Long dynamicId,
                    @Param("status") String status);

    /**
     * 批量更新动态状态
     */
    int batchUpdateStatus(@Param("dynamicIds") List<Long> dynamicIds,
                         @Param("status") String status);

    // =================== 用户信息同步 ===================

    /**
     * 批量更新用户冗余信息
     * 当用户更新昵称或头像时，同步更新所有动态记录
     */
    int updateUserInfo(@Param("userId") Long userId,
                      @Param("userNickname") String userNickname,
                      @Param("userAvatar") String userAvatar);

    // =================== 数据清理 ===================

    /**
     * 物理删除指定状态的历史动态
     * 用于数据清理，谨慎使用
     */
    int deleteByStatusAndTime(@Param("status") String status,
                             @Param("beforeTime") LocalDateTime beforeTime,
                             @Param("limit") Integer limit);

    // =================== 特殊查询 ===================

    /**
     * 查询最新动态（全局）
     * 用于首页动态流
     */
    List<SocialDynamic> selectLatestDynamics(@Param("limit") Integer limit,
                                           @Param("status") String status);

    /**
     * 查询用户最新动态
     * 用于用户主页
     */
    List<SocialDynamic> selectUserLatestDynamics(@Param("userId") Long userId,
                                               @Param("limit") Integer limit,
                                               @Param("status") String status);

    /**
     * 查询分享动态列表
     * 用于分享动态展示
     */
    List<SocialDynamic> selectShareDynamics(@Param("shareTargetType") String shareTargetType,
                                           @Param("limit") Integer limit,
                                           @Param("status") String status);
} 