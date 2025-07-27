package com.gig.collide.tag.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gig.collide.tag.domain.repository.ContentTagRepository;
import com.gig.collide.tag.infrastructure.entity.ContentTagEntity;
import com.gig.collide.tag.infrastructure.mapper.ContentTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 内容标签关联仓储实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentTagRepositoryImpl implements ContentTagRepository {

    private final ContentTagMapper contentTagMapper;

    @Override
    public ContentTagEntity save(ContentTagEntity entity) {
        if (entity.getId() == null) {
            contentTagMapper.insert(entity);
        } else {
            contentTagMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public List<ContentTagEntity> findByContentId(Long contentId) {
        return contentTagMapper.selectByContentId(contentId);
    }

    @Override
    public List<ContentTagEntity> findByTagId(Long tagId, Integer limit) {
        return contentTagMapper.selectByTagId(tagId, limit);
    }

    @Override
    public boolean batchSave(List<ContentTagEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return true;
        }

        try {
            int result = contentTagMapper.batchInsert(entities);
            return result > 0;
        } catch (Exception e) {
            log.error("批量保存内容标签关联失败", e);
            return false;
        }
    }

    @Override
    public boolean deleteByContentIdAndTagId(Long contentId, Long tagId) {
        LambdaQueryWrapper<ContentTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContentTagEntity::getContentId, contentId)
               .eq(ContentTagEntity::getTagId, tagId);

        int result = contentTagMapper.delete(wrapper);
        return result > 0;
    }

    @Override
    public boolean batchDeleteByContentIdAndTagIds(Long contentId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }

        int result = contentTagMapper.batchDeleteByContentIdAndTagIds(contentId, tagIds);
        return result > 0;
    }

    @Override
    public boolean deleteAllByContentId(Long contentId) {
        int result = contentTagMapper.deleteAllByContentId(contentId);
        return result >= 0; // 即使没有记录删除也算成功
    }

    @Override
    public boolean deleteAllByTagId(Long tagId) {
        int result = contentTagMapper.deleteAllByTagId(tagId);
        return result >= 0; // 即使没有记录删除也算成功
    }

    @Override
    public ContentTagEntity findByContentIdAndTagId(Long contentId, Long tagId) {
        return contentTagMapper.selectByContentIdAndTagId(contentId, tagId);
    }

    @Override
    public Long countByTagId(Long tagId) {
        return contentTagMapper.countByTagId(tagId);
    }

    @Override
    public Long countByContentId(Long contentId) {
        return contentTagMapper.countByContentId(contentId);
    }

    @Override
    public boolean existsByContentIdAndTagId(Long contentId, Long tagId) {
        LambdaQueryWrapper<ContentTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContentTagEntity::getContentId, contentId)
               .eq(ContentTagEntity::getTagId, tagId);

        Long count = contentTagMapper.selectCount(wrapper);
        return count > 0;
    }
} 