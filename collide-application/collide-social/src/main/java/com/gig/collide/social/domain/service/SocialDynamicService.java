package com.gig.collide.social.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.social.domain.entity.SocialDynamic;

import java.util.List;

/**
 * 社交动态服务接口 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
public interface SocialDynamicService {

    /**
     * 发布动态
     */
    SocialDynamic createDynamic(SocialDynamic dynamic);

    /**
     * 更新动态
     */
    SocialDynamic updateDynamic(SocialDynamic dynamic);

    /**
     * 删除动态（逻辑删除）
     */
    void deleteDynamic(Long dynamicId);

    /**
     * 根据ID查询动态
     */
    SocialDynamic getDynamicById(Long dynamicId);

    /**
     * 分页查询动态
     */
    IPage<SocialDynamic> queryDynamics(int pageNum, int pageSize, 
                                       Long userId, String dynamicType, String status, 
                                       String keyword, Long minLikeCount, 
                                       String sortBy, String sortDirection);

    /**
     * 获取用户动态列表
     */
    List<SocialDynamic> getUserDynamics(Long userId, Integer limit);

    /**
     * 获取关注用户的动态流
     */
    IPage<SocialDynamic> getFollowingDynamics(Long userId, int pageNum, int pageSize);

    /**
     * 根据类型获取动态列表
     */
    List<SocialDynamic> getDynamicsByType(String dynamicType, Integer limit);

    /**
     * 搜索动态
     */
    List<SocialDynamic> searchDynamics(String keyword, Integer limit);

    /**
     * 获取热门动态
     */
    List<SocialDynamic> getHotDynamics(Integer limit);

    /**
     * 点赞动态
     */
    void likeDynamic(Long dynamicId, Long userId);

    /**
     * 取消点赞
     */
    void unlikeDynamic(Long dynamicId, Long userId);

    /**
     * 评论动态
     */
    void commentDynamic(Long dynamicId, Long userId, String content);

    /**
     * 分享动态
     */
    SocialDynamic shareDynamic(Long dynamicId, Long userId, String shareContent);

    /**
     * 增加统计数量
     */
    void increaseStatCount(Long dynamicId, String statType);
} 