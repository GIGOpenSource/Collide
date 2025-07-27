package com.gig.collide.tag.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.repository.TagRepository;
import com.gig.collide.tag.infrastructure.entity.TagEntity;
import com.gig.collide.tag.infrastructure.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签仓储实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

    private final TagMapper tagMapper;

    @Override
    public TagEntity save(TagEntity tagEntity) {
        if (tagEntity.getId() == null) {
            tagMapper.insert(tagEntity);
        } else {
            tagMapper.updateById(tagEntity);
        }
        return tagEntity;
    }

    @Override
    public TagEntity findById(Long tagId) {
        return tagMapper.selectById(tagId);
    }

    @Override
    public TagEntity findByNameAndType(String name, String tagType) {
        return tagMapper.selectByNameAndType(name, tagType);
    }

    @Override
    public PageResponse<TagEntity> findTagPage(TagQueryRequest request) {
        Page<TagEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<TagEntity> result = tagMapper.selectTagPage(page, 
                request.getTagType(), 
                request.getStatus(), 
                request.getKeyword());
        
        return PageResponse.of(result.getRecords(), result.getTotal(), 
                request.getPageSize(), request.getPageNo());
    }

    @Override
    public List<TagEntity> findByTagType(String tagType) {
        return tagMapper.selectByTagType(tagType, "active");
    }

    @Override
    public List<TagEntity> findHotTags(String tagType, Integer limit) {
        return tagMapper.selectHotTags(tagType, limit);
    }

    @Override
    public List<TagEntity> searchTags(String keyword, String tagType) {
        return tagMapper.searchTags(keyword, tagType);
    }

    @Override
    public List<TagEntity> findByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        return tagMapper.selectByIds(tagIds);
    }

    @Override
    public boolean update(TagEntity tagEntity) {
        int result = tagMapper.updateById(tagEntity);
        return result > 0;
    }

    @Override
    public boolean deleteById(Long tagId) {
        int result = tagMapper.deleteById(tagId);
        return result > 0;
    }

    @Override
    public boolean increaseUsageCount(Long tagId, Long count) {
        int result = tagMapper.increaseUsageCount(tagId, count);
        return result > 0;
    }

    @Override
    public boolean updateHeatScore(Long tagId, Double heatScore) {
        int result = tagMapper.updateHeatScore(tagId, heatScore);
        return result > 0;
    }

    @Override
    public boolean existsByNameAndType(String name, String tagType, Long excludeId) {
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagEntity::getName, name)
               .eq(TagEntity::getTagType, tagType);
        
        if (excludeId != null) {
            wrapper.ne(TagEntity::getId, excludeId);
        }
        
        Long count = tagMapper.selectCount(wrapper);
        return count > 0;
    }
} 