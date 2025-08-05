package com.gig.collide.tag.domain.service.impl;

import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.infrastructure.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 标签服务实现类 - 严格对应TagMapper
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    // =================== 基础CRUD操作 ===================

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        log.debug("创建标签: 名称={}, 类型={}", tag.getName(), tag.getTagType());
        
        // 设置默认值
        if (!StringUtils.hasText(tag.getStatus())) {
            tag.setStatus("active");
        }
        if (tag.getUsageCount() == null) {
            tag.setUsageCount(0L);
        }
        if (!StringUtils.hasText(tag.getTagType())) {
            tag.setTagType("content");
        }
        
        int result = tagMapper.insert(tag);
        if (result > 0) {
            log.info("标签创建成功: ID={}, 名称={}", tag.getId(), tag.getName());
            return tag;
        } else {
            throw new RuntimeException("标签创建失败");
        }
    }

    @Override
    @Transactional
    public Tag updateTag(Tag tag) {
        log.debug("更新标签: ID={}, 名称={}", tag.getId(), tag.getName());
        
        int result = tagMapper.updateById(tag);
        if (result > 0) {
            log.info("标签更新成功: ID={}", tag.getId());
            return tag;
        } else {
            throw new RuntimeException("标签更新失败");
        }
    }

    @Override
    @Transactional
    public boolean deleteTagById(Long tagId) {
        log.debug("删除标签: ID={}", tagId);
        
        int result = tagMapper.deleteById(tagId);
        boolean success = result > 0;
        if (success) {
            log.info("标签删除成功: ID={}", tagId);
        } else {
            log.warn("标签删除失败: ID={}", tagId);
        }
        return success;
    }

    @Override
    public Tag getTagById(Long tagId) {
        log.debug("查询标签: ID={}", tagId);
        return tagMapper.selectById(tagId);
    }

    @Override
    public List<Tag> getAllTags() {
        log.debug("查询所有标签");
        return tagMapper.selectList(null);
    }

    // =================== 核心查询方法 ===================

    @Override
    public List<Tag> selectByTagType(String tagType) {
        log.debug("根据类型查询标签: 类型={}", tagType);
        return tagMapper.selectByTagType(tagType);
    }

    @Override
    public List<Tag> searchByName(String keyword, Integer limit) {
        log.debug("按名称模糊搜索标签: 关键词={}, 限制数量={}", keyword, limit);
        return tagMapper.searchByName(keyword, limit);
    }

    @Override
    public List<Tag> searchByNameExact(String keyword, Integer limit) {
        log.debug("按名称精确搜索标签: 关键词={}, 限制数量={}", keyword, limit);
        return tagMapper.searchByNameExact(keyword, limit);
    }

    @Override
    public List<Tag> selectHotTags(Integer limit) {
        log.debug("获取热门标签: 限制数量={}", limit);
        return tagMapper.selectHotTags(limit);
    }

    @Override
    public List<Tag> selectByCategoryId(Long categoryId) {
        log.debug("根据分类查询标签: 分类ID={}", categoryId);
        return tagMapper.selectByCategoryId(categoryId);
    }

    // =================== 统计和计数方法 ===================

    @Override
    public boolean existsByNameAndType(String name, String tagType) {
        log.debug("检查标签名称是否存在: 名称={}, 类型={}", name, tagType);
        int count = tagMapper.countByNameAndType(name, tagType);
        return count > 0;
    }

    @Override
    public List<Map<String, Object>> getTagUsageStats(String tagType, Integer limit) {
        log.debug("获取标签使用统计: 类型={}, 限制数量={}", tagType, limit);
        return tagMapper.getTagUsageStats(tagType, limit);
    }

    @Override
    public List<Map<String, Object>> selectTagSummary(List<Long> tagIds) {
        log.debug("批量获取标签基本信息: 标签数量={}", tagIds.size());
        return tagMapper.selectTagSummary(tagIds);
    }

    // =================== 更新操作方法 ===================

    @Override
    @Transactional
    public boolean increaseUsageCount(Long tagId) {
        log.debug("增加标签使用次数: ID={}", tagId);
        int result = tagMapper.increaseUsageCount(tagId);
        boolean success = result > 0;
        if (success) {
            log.info("标签使用次数增加成功: ID={}", tagId);
        } else {
            log.warn("标签使用次数增加失败: ID={}", tagId);
        }
        return success;
    }

    @Override
    @Transactional
    public boolean decreaseUsageCount(Long tagId) {
        log.debug("减少标签使用次数: ID={}", tagId);
        int result = tagMapper.decreaseUsageCount(tagId);
        boolean success = result > 0;
        if (success) {
            log.info("标签使用次数减少成功: ID={}", tagId);
        } else {
            log.warn("标签使用次数减少失败: ID={}", tagId);
        }
        return success;
    }

    @Override
    @Transactional
    public int batchUpdateStatus(List<Long> tagIds, String status) {
        log.debug("批量更新标签状态: 数量={}, 状态={}", tagIds.size(), status);
        int result = tagMapper.batchUpdateStatus(tagIds, status);
        log.info("批量更新标签状态完成: 更新数量={}", result);
        return result;
    }

    // =================== 业务逻辑方法 ===================

    @Override
    @Transactional
    public Tag createTagSafely(String name, String tagType, String description, Long categoryId) {
        log.debug("安全创建标签: 名称={}, 类型={}", name, tagType);
        
        // 检查名称是否已存在
        if (existsByNameAndType(name, tagType)) {
            throw new RuntimeException("标签名称已存在: " + name);
        }
        
        Tag tag = new Tag();
        tag.setName(name);
        tag.setTagType(tagType);
        tag.setDescription(description);
        tag.setCategoryId(categoryId);
        
        return createTag(tag);
    }

    @Override
    @Transactional
    public boolean activateTag(Long tagId) {
        log.debug("激活标签: ID={}", tagId);
        return batchUpdateStatus(List.of(tagId), "active") > 0;
    }

    @Override
    @Transactional
    public boolean deactivateTag(Long tagId) {
        log.debug("停用标签: ID={}", tagId);
        return batchUpdateStatus(List.of(tagId), "inactive") > 0;
    }

    @Override
    public List<Tag> getActiveTags(Long categoryId) {
        log.debug("获取分类下的活跃标签: 分类ID={}", categoryId);
        List<Tag> allTags = selectByCategoryId(categoryId);
        return allTags.stream()
                .filter(tag -> "active".equals(tag.getStatus()))
                .toList();
    }

    @Override
    public List<Tag> intelligentSearch(String keyword, Integer limit) {
        log.debug("智能搜索标签: 关键词={}, 限制数量={}", keyword, limit);
        
        // 先尝试精确搜索
        List<Tag> exactResults = searchByNameExact(keyword, limit);
        if (!exactResults.isEmpty()) {
            log.info("精确搜索到 {} 个结果", exactResults.size());
            return exactResults;
        }
        
        // 精确搜索无结果，使用模糊搜索
        List<Tag> fuzzyResults = searchByName(keyword, limit);
        log.info("模糊搜索到 {} 个结果", fuzzyResults.size());
        return fuzzyResults;
    }
}