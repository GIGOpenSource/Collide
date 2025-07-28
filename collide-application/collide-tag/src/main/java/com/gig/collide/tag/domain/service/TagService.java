package com.gig.collide.tag.domain.service;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;

import java.math.BigDecimal;
import java.util.List;

/**
 * 标签业务服务接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
public interface TagService {

    /**
     * 创建标签
     * 
     * @param request 创建请求
     * @return 标签实体
     */
    Tag createTag(TagCreateRequest request);

    /**
     * 更新标签
     * 
     * @param tagId 标签ID
     * @param request 更新请求
     * @return 标签实体
     */
    Tag updateTag(Long tagId, TagUpdateRequest request);

    /**
     * 删除标签
     * 
     * @param tagId 标签ID
     * @return 是否成功
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
     * 根据名称和类型查询标签
     * 
     * @param name 标签名称
     * @param tagType 标签类型
     * @return 标签实体
     */
    Tag getTagByNameAndType(String name, String tagType);

    /**
     * 分页查询标签
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    PageResponse<Tag> queryTags(TagQueryRequest request);

    /**
     * 根据类型查询标签列表
     * 
     * @param tagType 标签类型
     * @return 标签列表
     */
    List<Tag> getTagsByType(String tagType);

    /**
     * 查询热门标签
     * 
     * @param tagType 标签类型（可选）
     * @param limit 限制数量
     * @return 热门标签列表
     */
    List<Tag> getHotTags(String tagType, Integer limit);

    /**
     * 搜索标签
     * 
     * @param keyword 关键词
     * @param tagType 标签类型（可选）
     * @return 标签列表
     */
    List<Tag> searchTags(String keyword, String tagType);

    /**
     * 根据ID列表批量查询标签
     * 
     * @param tagIds 标签ID列表
     * @return 标签列表
     */
    List<Tag> getTagsByIds(List<Long> tagIds);

    /**
     * 更新标签使用次数
     * 
     * @param tagId 标签ID
     * @param increment 增量
     * @return 是否成功
     */
    boolean updateUsageCount(Long tagId, Long increment);

    /**
     * 更新标签热度分数
     * 
     * @param tagId 标签ID
     * @param heatScore 热度分数
     * @return 是否成功
     */
    boolean updateHeatScore(Long tagId, BigDecimal heatScore);

    /**
     * 批量更新标签热度分数
     * 
     * @return 是否成功
     */
    boolean batchUpdateHeatScores();

    /**
     * 检查标签名称是否存在
     * 
     * @param name 标签名称
     * @param tagType 标签类型
     * @param excludeId 排除的标签ID（用于更新时检查）
     * @return 是否存在
     */
    boolean isTagNameExists(String name, String tagType, Long excludeId);
} 