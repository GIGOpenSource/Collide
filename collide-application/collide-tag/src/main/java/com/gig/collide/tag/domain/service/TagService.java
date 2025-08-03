package com.gig.collide.tag.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.api.tag.request.TagQueryRequest;

import java.util.List;
import java.util.Map;

/**
 * 标签领域服务接口
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface TagService {

    // =================== 基础CRUD操作 ===================

    /**
     * 创建标签
     * 
     * @param tag 标签实体
     * @return 创建的标签
     */
    Tag createTag(Tag tag);

    /**
     * 更新标签
     * 
     * @param tag 标签实体
     * @return 更新的标签
     */
    Tag updateTag(Tag tag);

    /**
     * 删除标签
     * 
     * @param tagId 标签ID
     * @return 删除是否成功
     */
    boolean deleteTag(Long tagId);

    /**
     * 根据ID查询标签
     * 
     * @param tagId 标签ID
     * @return 标签实体
     */
    Tag getTagById(Long tagId);

    /**
     * 根据名称查询标签
     * 
     * @param tagName 标签名称
     * @return 标签实体
     */
    Tag getTagByName(String tagName);

    // =================== 查询操作 ===================

    /**
     * 获取所有启用的标签
     * 
     * @return 启用的标签列表
     */
    List<Tag> getAllActiveTags();

    /**
     * 分页查询标签
     * 
     * @param request 查询请求
     * @return 分页标签列表
     */
    PageResponse<Tag> queryTags(TagQueryRequest request);

    /**
     * 获取热门标签
     * 
     * @param limit 数量限制
     * @return 热门标签列表
     */
    List<Tag> getHotTags(Integer limit);

    /**
     * 获取推荐标签
     * 
     * @param limit 数量限制
     * @return 推荐标签列表
     */
    List<Tag> getRecommendTags(Integer limit);

    /**
     * 搜索标签
     * 
     * @param keyword 搜索关键词
     * @param limit 数量限制
     * @return 匹配的标签列表
     */
    List<Tag> searchTags(String keyword, Integer limit);

    /**
     * 根据权重范围查询标签
     * 
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @param limit 数量限制
     * @return 标签列表
     */
    List<Tag> getTagsByWeightRange(Integer minWeight, Integer maxWeight, Integer limit);

    /**
     * 根据ID列表查询标签
     * 
     * @param tagIds 标签ID列表
     * @return 标签列表
     */
    List<Tag> getTagsByIds(List<Long> tagIds);

    // =================== 排行榜查询 ===================

    /**
     * 获取热度排行榜
     * 
     * @param limit 数量限制
     * @return 热度排行榜
     */
    List<Tag> getHotnessRanking(Integer limit);

    /**
     * 获取关注数排行榜
     * 
     * @param limit 数量限制
     * @return 关注数排行榜
     */
    List<Tag> getFollowCountRanking(Integer limit);

    /**
     * 获取内容数排行榜
     * 
     * @param limit 数量限制
     * @return 内容数排行榜
     */
    List<Tag> getContentCountRanking(Integer limit);

    // =================== 统计分析 ===================

    /**
     * 获取标签统计信息
     * 
     * @param tagId 标签ID
     * @return 统计信息
     */
    Map<String, Object> getTagStatistics(Long tagId);

    /**
     * 统计启用标签数量
     * 
     * @return 启用标签数量
     */
    Long countActiveTags();

    /**
     * 统计总关注数
     * 
     * @return 总关注数
     */
    Long sumTotalFollowCount();

    /**
     * 统计总内容数
     * 
     * @return 总内容数
     */
    Long sumTotalContentCount();

    /**
     * 获取平均热度值
     * 
     * @return 平均热度值
     */
    Double getAverageHotness();

    // =================== 状态管理 ===================

    /**
     * 更新标签状态
     * 
     * @param tagId 标签ID
     * @param status 状态
     * @return 更新是否成功
     */
    boolean updateTagStatus(Long tagId, Integer status);

    /**
     * 更新标签权重
     * 
     * @param tagId 标签ID
     * @param weight 权重
     * @return 更新是否成功
     */
    boolean updateTagWeight(Long tagId, Integer weight);

    /**
     * 批量更新标签状态
     * 
     * @param tagIds 标签ID列表
     * @param status 状态
     * @return 更新是否成功
     */
    boolean batchUpdateTagStatus(List<Long> tagIds, Integer status);

    // =================== 数据更新 ===================

    /**
     * 更新标签热度值
     * 
     * @param tagId 标签ID
     * @param hotness 热度值
     * @return 更新是否成功
     */
    boolean updateTagHotness(Long tagId, Long hotness);

    /**
     * 增加标签关注数
     * 
     * @param tagId 标签ID
     * @param increment 增加数量
     * @return 更新是否成功
     */
    boolean incrementFollowCount(Long tagId, Integer increment);

    /**
     * 减少标签关注数
     * 
     * @param tagId 标签ID
     * @param decrement 减少数量
     * @return 更新是否成功
     */
    boolean decrementFollowCount(Long tagId, Integer decrement);

    /**
     * 增加标签内容数
     * 
     * @param tagId 标签ID
     * @param increment 增加数量
     * @return 更新是否成功
     */
    boolean incrementContentCount(Long tagId, Integer increment);

    /**
     * 减少标签内容数
     * 
     * @param tagId 标签ID
     * @param decrement 减少数量
     * @return 更新是否成功
     */
    boolean decrementContentCount(Long tagId, Integer decrement);

    // =================== 验证方法 ===================

    /**
     * 检查标签名称是否可用
     * 
     * @param tagName 标签名称
     * @param excludeId 排除的标签ID
     * @return 是否可用
     */
    boolean checkTagNameAvailable(String tagName, Long excludeId);

    /**
     * 检查标签是否存在且启用
     * 
     * @param tagId 标签ID
     * @return 是否存在且启用
     */
    boolean existsActiveTag(Long tagId);

    /**
     * 获取有效的标签（存在且启用）
     * 
     * @param tagId 标签ID
     * @return 标签实体，不存在或禁用时返回null
     */
    Tag getActiveTag(Long tagId);

    // =================== 批量操作 ===================

    /**
     * 批量创建标签
     * 
     * @param tags 标签列表
     * @return 创建成功的标签列表
     */
    List<Tag> batchCreateTags(List<Tag> tags);

    /**
     * 手动更新所有标签热度值
     * 
     * @return 更新的标签数量
     */
    int updateAllTagHotness();

    /**
     * 手动更新指定标签的热度值
     * 
     * @param tagId 标签ID
     * @return 更新是否成功
     */
    boolean recalculateTagHotness(Long tagId);
}