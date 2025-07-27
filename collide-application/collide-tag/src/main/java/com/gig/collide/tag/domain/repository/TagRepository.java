package com.gig.collide.tag.domain.repository;

import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.infrastructure.entity.TagEntity;

import java.util.List;

/**
 * 标签仓储接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public interface TagRepository {

    /**
     * 保存标签
     *
     * @param tagEntity 标签实体
     * @return 标签实体
     */
    TagEntity save(TagEntity tagEntity);

    /**
     * 根据ID查询标签
     *
     * @param tagId 标签ID
     * @return 标签实体
     */
    TagEntity findById(Long tagId);

    /**
     * 根据名称和类型查询标签
     *
     * @param name    标签名称
     * @param tagType 标签类型
     * @return 标签实体
     */
    TagEntity findByNameAndType(String name, String tagType);

    /**
     * 分页查询标签
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResponse<TagEntity> findTagPage(TagQueryRequest request);

    /**
     * 根据类型获取标签列表
     *
     * @param tagType 标签类型
     * @return 标签列表
     */
    List<TagEntity> findByTagType(String tagType);

    /**
     * 获取热门标签列表
     *
     * @param tagType 标签类型（可选）
     * @param limit   限制数量
     * @return 热门标签列表
     */
    List<TagEntity> findHotTags(String tagType, Integer limit);

    /**
     * 搜索标签
     *
     * @param keyword 关键词
     * @param tagType 标签类型（可选）
     * @return 标签列表
     */
    List<TagEntity> searchTags(String keyword, String tagType);

    /**
     * 批量查询标签
     *
     * @param tagIds 标签ID列表
     * @return 标签列表
     */
    List<TagEntity> findByIds(List<Long> tagIds);

    /**
     * 更新标签
     *
     * @param tagEntity 标签实体
     * @return 是否成功
     */
    boolean update(TagEntity tagEntity);

    /**
     * 删除标签
     *
     * @param tagId 标签ID
     * @return 是否成功
     */
    boolean deleteById(Long tagId);

    /**
     * 增加标签使用次数
     *
     * @param tagId 标签ID
     * @param count 增加数量
     * @return 是否成功
     */
    boolean increaseUsageCount(Long tagId, Long count);

    /**
     * 更新标签热度分数
     *
     * @param tagId     标签ID
     * @param heatScore 热度分数
     * @return 是否成功
     */
    boolean updateHeatScore(Long tagId, Double heatScore);

    /**
     * 检查标签名称是否存在
     *
     * @param name    标签名称
     * @param tagType 标签类型
     * @param excludeId 排除的标签ID（更新时使用）
     * @return 是否存在
     */
    boolean existsByNameAndType(String name, String tagType, Long excludeId);
} 