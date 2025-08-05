package com.gig.collide.social.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.SocialDynamic;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态服务接口 - 严格对应Mapper层
 * 仅包含与SocialDynamicMapper完全对应的25个方法
 *
 * @author GIG Team
 * @version 3.0.0
 */
public interface SocialDynamicService {

    // =================== 动态创建方法（对应Mapper层3个） ===================

    /**
     * 创建动态
     * 对应Mapper: insertDynamic
     * 包含业务验证、用户信息填充、默认值设置
     */
    SocialDynamic createDynamic(SocialDynamic dynamic);

    /**
     * 批量创建动态
     * 对应Mapper: batchInsertDynamics
     */
    int batchCreateDynamics(List<SocialDynamic> dynamics);

    /**
     * 创建分享动态
     * 对应Mapper: insertShareDynamic
     * 包含分享目标验证、原动态分享数更新
     */
    SocialDynamic createShareDynamic(SocialDynamic dynamic);

    // =================== 核心查询方法（对应Mapper层7个） ===================

    /**
     * 根据用户ID分页查询动态
     * 对应Mapper: selectByUserId
     */
    IPage<SocialDynamic> selectByUserId(Page<SocialDynamic> page, Long userId, String status, String dynamicType);

    /**
     * 根据动态类型分页查询
     * 对应Mapper: selectByDynamicType
     */
    IPage<SocialDynamic> selectByDynamicType(Page<SocialDynamic> page, String dynamicType, String status);

    /**
     * 根据状态分页查询动态
     * 对应Mapper: selectByStatus
     */
    IPage<SocialDynamic> selectByStatus(Page<SocialDynamic> page, String status);

    /**
     * 获取关注用户的动态流
     * 对应Mapper: selectFollowingDynamics
     */
    IPage<SocialDynamic> selectFollowingDynamics(Page<SocialDynamic> page, List<Long> userIds, String status);

    /**
     * 搜索动态（按内容搜索）
     * 对应Mapper: searchByContent
     */
    IPage<SocialDynamic> searchByContent(Page<SocialDynamic> page, String keyword, String status);

    /**
     * 获取热门动态（按互动数排序）
     * 对应Mapper: selectHotDynamics
     */
    IPage<SocialDynamic> selectHotDynamics(Page<SocialDynamic> page, String status, String dynamicType);

    /**
     * 根据分享目标查询分享动态
     * 对应Mapper: selectByShareTarget
     */
    IPage<SocialDynamic> selectByShareTarget(Page<SocialDynamic> page, String shareTargetType, Long shareTargetId, String status);

    // =================== 统计计数方法（对应Mapper层3个） ===================

    /**
     * 统计用户动态数量
     * 对应Mapper: countByUserId
     */
    Long countByUserId(Long userId, String status, String dynamicType);

    /**
     * 统计动态类型数量
     * 对应Mapper: countByDynamicType
     */
    Long countByDynamicType(String dynamicType, String status);

    /**
     * 统计指定时间范围内的动态数量
     * 对应Mapper: countByTimeRange
     */
    Long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status);

    // =================== 互动统计更新（对应Mapper层5个） ===================

    /**
     * 增加点赞数
     * 对应Mapper: increaseLikeCount
     */
    int increaseLikeCount(Long dynamicId);

    /**
     * 减少点赞数
     * 对应Mapper: decreaseLikeCount
     */
    int decreaseLikeCount(Long dynamicId);

    /**
     * 增加评论数
     * 对应Mapper: increaseCommentCount
     */
    int increaseCommentCount(Long dynamicId);

    /**
     * 增加分享数
     * 对应Mapper: increaseShareCount
     */
    int increaseShareCount(Long dynamicId);

    /**
     * 批量更新统计数据
     * 对应Mapper: updateStatistics
     */
    int updateStatistics(Long dynamicId, Long likeCount, Long commentCount, Long shareCount);

    // =================== 状态管理（对应Mapper层2个） ===================

    /**
     * 更新动态状态
     * 对应Mapper: updateStatus
     */
    int updateStatus(Long dynamicId, String status);

    /**
     * 批量更新动态状态
     * 对应Mapper: batchUpdateStatus
     */
    int batchUpdateStatus(List<Long> dynamicIds, String status);

    // =================== 用户信息同步（对应Mapper层1个） ===================

    /**
     * 批量更新用户冗余信息
     * 对应Mapper: updateUserInfo
     */
    int updateUserInfo(Long userId, String userNickname, String userAvatar);

    // =================== 数据清理（对应Mapper层1个） ===================

    /**
     * 物理删除指定状态的历史动态
     * 对应Mapper: deleteByStatusAndTime
     */
    int deleteByStatusAndTime(String status, LocalDateTime beforeTime, Integer limit);

    // =================== 特殊查询（对应Mapper层3个） ===================

    /**
     * 查询最新动态（全局）
     * 对应Mapper: selectLatestDynamics
     */
    List<SocialDynamic> selectLatestDynamics(Integer limit, String status);

    /**
     * 查询用户最新动态
     * 对应Mapper: selectUserLatestDynamics
     */
    List<SocialDynamic> selectUserLatestDynamics(Long userId, Integer limit, String status);

    /**
     * 查询分享动态列表
     * 对应Mapper: selectShareDynamics
     */
    List<SocialDynamic> selectShareDynamics(String shareTargetType, Integer limit, String status);
} 