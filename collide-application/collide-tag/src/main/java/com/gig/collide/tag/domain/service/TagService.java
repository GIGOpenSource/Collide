package com.gig.collide.tag.domain.service;

import com.gig.collide.tag.domain.entity.Tag;

import java.util.List;
import java.util.Map;

/**
 * 标签服务接口 - 严格对应TagMapper
 * 基于TagMapper的所有方法，提供标签相关的业务逻辑
 *
 * @author GIG Team
 * @version 3.0.0
 */
public interface TagService {

    // =================== 基础CRUD操作（继承自BaseMapper） ===================

    /**
     * 创建标签
     */
    Tag createTag(Tag tag);

    /**
     * 更新标签
     */
    Tag updateTag(Tag tag);

    /**
     * 根据ID删除标签
     */
    boolean deleteTagById(Long tagId);

    /**
     * 根据ID查询标签
     */
    Tag getTagById(Long tagId);

    /**
     * 查询所有标签
     */
    List<Tag> getAllTags();

    // =================== 核心查询方法（对应Mapper自定义方法） ===================

    /**
     * 根据类型查询标签列表
     * 对应Mapper: selectByTagType
     */
    List<Tag> selectByTagType(String tagType);

    /**
     * 按名称模糊搜索标签（全文搜索）
     * 对应Mapper: searchByName
     */
    List<Tag> searchByName(String keyword, Integer limit);

    /**
     * 按名称精确搜索标签（大小写不敏感）
     * 对应Mapper: searchByNameExact
     */
    List<Tag> searchByNameExact(String keyword, Integer limit);

    /**
     * 获取热门标签（按使用次数排序）
     * 对应Mapper: selectHotTags
     */
    List<Tag> selectHotTags(Integer limit);

    /**
     * 根据分类查询标签
     * 对应Mapper: selectByCategoryId
     */
    List<Tag> selectByCategoryId(Long categoryId);

    // =================== 统计和计数方法 ===================

    /**
     * 检查标签名称是否存在
     * 对应Mapper: countByNameAndType
     */
    boolean existsByNameAndType(String name, String tagType);

    /**
     * 获取标签使用统计（性能优化）
     * 对应Mapper: getTagUsageStats
     */
    List<Map<String, Object>> getTagUsageStats(String tagType, Integer limit);

    /**
     * 批量获取标签基本信息（覆盖索引优化）
     * 对应Mapper: selectTagSummary
     */
    List<Map<String, Object>> selectTagSummary(List<Long> tagIds);

    // =================== 更新操作方法 ===================

    /**
     * 增加标签使用次数
     * 对应Mapper: increaseUsageCount
     */
    boolean increaseUsageCount(Long tagId);

    /**
     * 减少标签使用次数
     * 对应Mapper: decreaseUsageCount
     */
    boolean decreaseUsageCount(Long tagId);

    /**
     * 批量更新标签状态
     * 对应Mapper: batchUpdateStatus
     */
    int batchUpdateStatus(List<Long> tagIds, String status);

    // =================== 业务逻辑方法 ===================

    /**
     * 创建标签（带唯一性检查）
     */
    Tag createTagSafely(String name, String tagType, String description, Long categoryId);

    /**
     * 激活标签
     */
    boolean activateTag(Long tagId);

    /**
     * 停用标签
     */
    boolean deactivateTag(Long tagId);

    /**
     * 获取分类下的活跃标签
     */
    List<Tag> getActiveTags(Long categoryId);

    /**
     * 搜索标签（智能搜索，先精确后模糊）
     */
    List<Tag> intelligentSearch(String keyword, Integer limit);
} 