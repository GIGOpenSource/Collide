package com.gig.collide.tag.domain.service.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.CommonErrorCode;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.cache.constant.CacheConstant;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.entity.convertor.TagConvertor;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.tag.infrastructure.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 标签业务服务实现类
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    private static final String DEFAULT_STATUS = "active";
    private static final String INACTIVE_STATUS = "inactive";
    private static final BigDecimal DEFAULT_HEAT_SCORE = BigDecimal.ZERO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.TAG_TYPE_CACHE, key = "#request.tagType")
    @CacheInvalidate(name = CacheConstant.TAG_HOT_CACHE, key = "'hot:' + #request.tagType + '*'")
    @CacheInvalidate(name = CacheConstant.TAG_HOT_CACHE, key = "'hot:all*'")
    public Tag createTag(TagCreateRequest request) {
        log.info("创建标签开始，请求参数：{}", request);
        
        // 参数验证
        if (!request.isValid()) {
            throw new BizException("标签名称不能为空", CommonErrorCode.PARAM_INVALID);
        }
        
        // 检查标签名称是否已存在
        if (isTagNameExists(request.getName(), request.getTagType(), null)) {
            throw new BizException("标签名称已存在", CommonErrorCode.RESOURCE_ALREADY_EXISTS);
        }
        
        // 转换为实体
        Tag tag = TagConvertor.INSTANCE.createRequestToEntity(request);
        
        // 设置默认值
        tag.setStatus("active".equals(request.getStatus()) ? DEFAULT_STATUS : INACTIVE_STATUS);
        tag.setUsageCount(0L);
        tag.setHeatScore(DEFAULT_HEAT_SCORE);
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        tag.setVersion(1);
        
        // 插入数据库
        int result = tagMapper.insert(tag);
        if (result != 1) {
            throw new BizException("创建标签失败", CommonErrorCode.SYSTEM_ERROR);
        }
        
        log.info("创建标签成功，标签ID：{}", tag.getId());
        return tag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.TAG_INFO_CACHE, key = "#tagId")
    @CacheInvalidate(name = CacheConstant.TAG_TYPE_CACHE, key = "#existingTag.tagType")
    @CacheInvalidate(name = CacheConstant.TAG_HOT_CACHE, key = "'hot:' + #existingTag.tagType + '*'")
    @CacheInvalidate(name = CacheConstant.TAG_HOT_CACHE, key = "'hot:all*'")
    public Tag updateTag(Long tagId, TagUpdateRequest request) {
        log.info("更新标签开始，标签ID：{}，请求参数：{}", tagId, request);
        
        // 参数验证
        if (tagId == null) {
            throw new BizException("标签ID不能为空", CommonErrorCode.PARAM_INVALID);
        }
        
        // 查询现有标签
        Tag existingTag = tagMapper.selectById(tagId);
        if (existingTag == null) {
            throw new BizException("标签不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // 检查标签名称是否重复（如果修改了名称）
        if (StringUtils.hasText(request.getName()) && 
            !Objects.equals(request.getName(), existingTag.getName())) {
            if (isTagNameExists(request.getName(), existingTag.getTagType(), tagId)) {
                throw new BizException("标签名称已存在", CommonErrorCode.RESOURCE_ALREADY_EXISTS);
            }
        }
        
        // 更新实体字段
        TagConvertor.INSTANCE.updateRequestToEntity(request, existingTag);
        existingTag.setUpdateTime(LocalDateTime.now());
        
        // 处理状态转换
        if (request.getStatus() != null) {
            existingTag.setStatus("active".equals(request.getStatus()) ? DEFAULT_STATUS : INACTIVE_STATUS);
        }
        
        // 更新数据库
        int result = tagMapper.updateById(existingTag);
        if (result != 1) {
            throw new BizException("更新标签失败", CommonErrorCode.SYSTEM_ERROR);
        }
        
        log.info("更新标签成功，标签ID：{}", tagId);
        return existingTag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = CacheConstant.TAG_INFO_CACHE, key = "#tagId")
    @CacheInvalidate(name = CacheConstant.TAG_TYPE_CACHE, key = "#tag.tagType")
    @CacheInvalidate(name = CacheConstant.TAG_HOT_CACHE, key = "'hot:' + #tag.tagType + '*'")
    @CacheInvalidate(name = CacheConstant.TAG_HOT_CACHE, key = "'hot:all*'")
    public boolean deleteTag(Long tagId) {
        log.info("删除标签开始，标签ID：{}", tagId);
        
                if (tagId == null) {
            throw new BizException("标签ID不能为空", CommonErrorCode.PARAM_INVALID);
        }

        // 软删除：将状态设置为inactive
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BizException("标签不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }
        
        tag.setStatus(INACTIVE_STATUS);
        tag.setUpdateTime(LocalDateTime.now());
        
        int result = tagMapper.updateById(tag);
        boolean success = result == 1;
        
        log.info("删除标签{}，标签ID：{}", success ? "成功" : "失败", tagId);
        return success;
    }

    @Override
    @Cached(name = CacheConstant.TAG_INFO_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#tagId", 
            expire = CacheConstant.TAG_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    @CacheRefresh(refresh = CacheConstant.TAG_CACHE_EXPIRE, timeUnit = TimeUnit.MINUTES)
    public Tag getTagById(Long tagId) {
        if (tagId == null) {
            return null;
        }
        log.debug("从数据库查询标签信息，标签ID：{}", tagId);
        return tagMapper.selectById(tagId);
    }

    @Override
    public Tag getTagByNameAndType(String name, String tagType) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(tagType)) {
            return null;
        }
        return tagMapper.selectByNameAndType(name, tagType);
    }

    @Override
    public PageResponse<Tag> queryTags(TagQueryRequest request) {
        log.info("分页查询标签开始，请求参数：{}", request);
        
        // 设置默认分页参数
        int current = request.getPageNo() != null && request.getPageNo() > 0 ? request.getPageNo() : 1;
        int size = request.getPageSize() != null && request.getPageSize() > 0 ? request.getPageSize() : 20;
        
        Page<Tag> page = new Page<>(current, size);
        
        // 执行分页查询
        IPage<Tag> result = tagMapper.selectTagsPage(page, 
                request.getTagType(), 
                request.getCategoryId(), 
                "active".equals(request.getStatus()) ? DEFAULT_STATUS : null,
                request.getKeyword());
        
                log.info("分页查询标签完成，总数：{}，当前页：{}", result.getTotal(), result.getCurrent());

        return PageResponse.of(result.getRecords(), (int) result.getTotal(), (int) result.getSize(), (int) result.getCurrent());
    }

    @Override
    @Cached(name = CacheConstant.TAG_TYPE_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "#tagType", 
            expire = CacheConstant.TAG_TYPE_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    @CacheRefresh(refresh = CacheConstant.TAG_TYPE_CACHE_EXPIRE, timeUnit = TimeUnit.MINUTES)
    public List<Tag> getTagsByType(String tagType) {
        if (!StringUtils.hasText(tagType)) {
            return List.of();
        }
        log.debug("从数据库查询标签类型，标签类型：{}", tagType);
        return tagMapper.selectByTagType(tagType, DEFAULT_STATUS);
    }

    @Override
    @Cached(name = CacheConstant.TAG_HOT_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "'hot:' + (#tagType == null ? 'all' : #tagType) + ':' + #limit", 
            expire = CacheConstant.TAG_HOT_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    @CacheRefresh(refresh = CacheConstant.TAG_HOT_CACHE_EXPIRE, timeUnit = TimeUnit.MINUTES)
    public List<Tag> getHotTags(String tagType, Integer limit) {
        int limitSize = limit != null && limit > 0 ? limit : 50;
        log.debug("从数据库查询热门标签，标签类型：{}，限制数量：{}", tagType, limitSize);
        return tagMapper.selectHotTags(tagType, DEFAULT_STATUS, limitSize);
    }

    @Override
    @Cached(name = CacheConstant.TAG_SEARCH_CACHE, 
            cacheType = CacheType.BOTH, 
            key = "'search:' + #keyword + ':' + (#tagType == null ? 'all' : #tagType)", 
            expire = CacheConstant.TAG_SEARCH_CACHE_EXPIRE, 
            localExpire = CacheConstant.LOCAL_CACHE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheNullValue = true)
    public List<Tag> searchTags(String keyword, String tagType) {
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        log.debug("从数据库搜索标签，关键词：{}，标签类型：{}", keyword, tagType);
        return tagMapper.searchTags(keyword, tagType, DEFAULT_STATUS);
    }

    @Override
    public List<Tag> getTagsByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        return tagMapper.selectByIdList(tagIds, DEFAULT_STATUS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUsageCount(Long tagId, Long increment) {
        if (tagId == null || increment == null) {
            return false;
        }
        
        int result = tagMapper.updateUsageCount(tagId, increment);
        return result == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHeatScore(Long tagId, BigDecimal heatScore) {
        if (tagId == null || heatScore == null) {
            return false;
        }
        
        int result = tagMapper.updateHeatScore(tagId, heatScore);
        return result == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateHeatScores() {
        log.info("批量更新标签热度分数开始");
        
        try {
            // 这里可以实现具体的热度分数计算逻辑
            // 例如：基于使用次数、时间衰减等因素计算热度分数
            
            // 简单实现：将使用次数作为热度分数的基础
            List<Tag> activeTags = tagMapper.selectByTagType(null, DEFAULT_STATUS);
            
            for (Tag tag : activeTags) {
                if (tag.getUsageCount() != null && tag.getUsageCount() > 0) {
                    // 简单的热度计算：使用次数 * 时间衰减因子
                    long daysSinceCreate = java.time.Duration.between(tag.getCreateTime(), LocalDateTime.now()).toDays();
                    double timeFactor = Math.pow(0.95, daysSinceCreate / 7.0); // 每周衰减5%
                    BigDecimal newHeatScore = BigDecimal.valueOf(tag.getUsageCount() * timeFactor);
                    
                    updateHeatScore(tag.getId(), newHeatScore);
                }
            }
            
            log.info("批量更新标签热度分数完成");
            return true;
        } catch (Exception e) {
            log.error("批量更新标签热度分数失败", e);
            return false;
        }
    }

    @Override
    public boolean isTagNameExists(String name, String tagType, Long excludeId) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(tagType)) {
            return false;
        }
        
        Tag existingTag = tagMapper.selectByNameAndType(name, tagType);
        if (existingTag == null) {
            return false;
        }
        
        // 如果是更新操作，排除自身
        return excludeId == null || !Objects.equals(existingTag.getId(), excludeId);
    }
} 