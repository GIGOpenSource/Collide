package com.gig.collide.api.tag.service;

import com.gig.collide.api.tag.request.*;
import com.gig.collide.api.tag.response.*;
import com.gig.collide.api.tag.response.data.UserInterestTagInfo;
import com.gig.collide.api.tag.response.data.BasicTagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.BaseResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签门面服务接口
 * 提供用户兴趣标签管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface UserInterestTagFacadeService {

    /**
     * 添加用户兴趣标签
     * 
     * @param manageRequest 管理请求
     * @return 添加响应
     */
    UserInterestTagResponse addUserInterestTag(UserInterestTagManageRequest manageRequest);

    /**
     * 移除用户兴趣标签
     * 
     * @param manageRequest 管理请求
     * @return 移除响应
     */
    BaseResponse removeUserInterestTag(UserInterestTagManageRequest manageRequest);

    /**
     * 更新用户兴趣分数
     * 
     * @param manageRequest 管理请求
     * @return 更新响应
     */
    UserInterestTagResponse updateInterestScore(UserInterestTagManageRequest manageRequest);

    /**
     * 获取用户兴趣标签
     * 
     * @param userId 用户ID
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页响应
     */
    PageResponse<UserInterestTagInfo> getUserInterestTags(Long userId, String status, Integer pageNum, Integer pageSize);

    /**
     * 获取用户高兴趣标签（分数大于指定值）
     * 
     * @param userId 用户ID
     * @param minScore 最小分数
     * @param limit 限制数量
     * @return 高兴趣标签列表
     */
    TagUnifiedQueryResponse<List<BasicTagInfo>> getUserHighInterestTags(Long userId, BigDecimal minScore, Integer limit);

    /**
     * 为用户推荐兴趣标签
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @param algorithm 推荐算法（behavior、similarity、content）
     * @return 推荐标签列表
     */
    TagUnifiedQueryResponse<List<BasicTagInfo>> recommendInterestTags(Long userId, Integer limit, String algorithm);

    /**
     * 批量设置用户兴趣标签
     * 
     * @param manageRequest 管理请求
     * @return 操作结果
     */
    BaseResponse batchSetUserInterestTags(UserInterestTagManageRequest manageRequest);

    /**
     * 根据用户行为自动更新兴趣分数
     * 
     * @param userId 用户ID
     * @param tagId 标签ID
     * @param behaviorType 行为类型（view、like、share、comment）
     * @param weight 权重
     * @return 更新结果
     */
    BaseResponse updateInterestByBehavior(Long userId, Long tagId, String behaviorType, BigDecimal weight);

    /**
     * 获取标签的兴趣用户排行
     * 
     * @param tagId 标签ID
     * @param limit 排行数量
     * @return 用户排行列表
     */
    TagUnifiedQueryResponse<List<UserInterestTagInfo>> getTagInterestUserRanking(Long tagId, Integer limit);

    /**
     * 清理用户低分兴趣标签
     * 
     * @param userId 用户ID
     * @param threshold 阈值分数
     * @return 清理结果
     */
    BaseResponse cleanLowScoreInterestTags(Long userId, BigDecimal threshold);

    /**
     * 获取相似兴趣用户
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 相似用户列表
     */
    TagUnifiedQueryResponse<List<UserInterestTagInfo>> getSimilarInterestUsers(Long userId, Integer limit);

    /**
     * 导入用户兴趣标签（从外部系统）
     * 
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @param source 来源
     * @return 导入结果
     */
    BaseResponse importUserInterestTags(Long userId, List<Long> tagIds, String source);
} 