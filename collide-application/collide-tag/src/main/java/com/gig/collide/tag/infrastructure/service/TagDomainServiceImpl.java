package com.gig.collide.tag.infrastructure.service;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.request.UserInterestTagRequest;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.repository.ContentTagRepository;
import com.gig.collide.tag.domain.repository.TagRepository;
import com.gig.collide.tag.domain.repository.UserInterestTagRepository;
import com.gig.collide.tag.domain.service.TagDomainService;
import com.gig.collide.tag.infrastructure.entity.ContentTagEntity;
import com.gig.collide.tag.infrastructure.entity.TagEntity;
import com.gig.collide.tag.infrastructure.entity.UserInterestTagEntity;
import com.gig.collide.tag.infrastructure.service.IdempotentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签领域服务实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagDomainServiceImpl implements TagDomainService {

    private final TagRepository tagRepository;
    private final UserInterestTagRepository userInterestTagRepository;
    private final ContentTagRepository contentTagRepository;
    private final IdempotentService idempotentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTag(TagCreateRequest request) {
        log.info("开始创建标签: {}", request.getName());
        
        // 生成幂等键并执行幂等性检查
        String tagType = StringUtils.hasText(request.getTagType()) ? request.getTagType() : "content";
        String idempotentKey = IdempotentService.generateTagCreateIdempotentKey(request.getName(), tagType);
        
        return idempotentService.executeIdempotent(idempotentKey, () -> {
            // 1. 参数验证
            if (!StringUtils.hasText(request.getName())) {
                throw new IllegalArgumentException("标签名称不能为空");
            }
            
            // 2. 检查标签是否已存在
            boolean exists = tagRepository.existsByNameAndType(request.getName(), tagType, null);
            if (exists) {
                throw new IllegalArgumentException("标签名称已存在");
            }
            
            // 3. 创建标签实体
            TagEntity tagEntity = new TagEntity();
            tagEntity.setName(request.getName());
            tagEntity.setDescription(request.getDescription());
            tagEntity.setColor(StringUtils.hasText(request.getColor()) ? request.getColor() : "#1890ff");
            tagEntity.setIconUrl(request.getIconUrl());
            tagEntity.setTagType(tagType);
            tagEntity.setCategoryId(request.getCategoryId());
            tagEntity.setUsageCount(0L);
            tagEntity.setHeatScore(BigDecimal.ZERO);
            tagEntity.setStatus("active");
            tagEntity.setCreateTime(LocalDateTime.now());
            tagEntity.setUpdateTime(LocalDateTime.now());
            tagEntity.setVersion(1);
            
            // 4. 保存标签
            TagEntity savedTag = tagRepository.save(tagEntity);
            
            log.info("标签创建成功，ID: {}, 名称: {}", savedTag.getId(), savedTag.getName());
            return savedTag.getId();
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(TagUpdateRequest request) {
        log.info("开始更新标签: {}", request.getTagId());
        
        // 1. 参数验证
        if (request.getTagId() == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }
        
        // 2. 查询标签是否存在
        TagEntity tagEntity = tagRepository.findById(request.getTagId());
        if (tagEntity == null) {
            throw new IllegalArgumentException("标签不存在");
        }
        
        // 3. 检查名称是否重复（如果有更新名称）
        if (StringUtils.hasText(request.getName()) && !request.getName().equals(tagEntity.getName())) {
            boolean exists = tagRepository.existsByNameAndType(request.getName(), 
                    tagEntity.getTagType(), request.getTagId());
            if (exists) {
                throw new IllegalArgumentException("标签名称已存在");
            }
            tagEntity.setName(request.getName());
        }
        
        // 4. 更新其他字段
        if (StringUtils.hasText(request.getDescription())) {
            tagEntity.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getColor())) {
            tagEntity.setColor(request.getColor());
        }
        if (StringUtils.hasText(request.getIconUrl())) {
            tagEntity.setIconUrl(request.getIconUrl());
        }
        if (request.getCategoryId() != null) {
            tagEntity.setCategoryId(request.getCategoryId());
        }
        if (StringUtils.hasText(request.getStatus())) {
            tagEntity.setStatus(request.getStatus());
        }
        
        tagEntity.setUpdateTime(LocalDateTime.now());
        
        // 5. 保存更新
        boolean success = tagRepository.update(tagEntity);
        if (!success) {
            throw new RuntimeException("更新标签失败");
        }
        
        log.info("标签更新成功: {}", request.getTagId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        log.info("开始删除标签: {}", tagId);
        
        // 1. 参数验证
        if (tagId == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }
        
        // 2. 查询标签是否存在
        TagEntity tagEntity = tagRepository.findById(tagId);
        if (tagEntity == null) {
            throw new IllegalArgumentException("标签不存在");
        }
        
        // 3. 删除相关关联数据
        contentTagRepository.deleteAllByTagId(tagId);
        
        // 4. 删除用户兴趣标签关联（软删除，设置为inactive）
        List<UserInterestTagEntity> userInterestTags = userInterestTagRepository.findByTagId(tagId, null);
        for (UserInterestTagEntity userTag : userInterestTags) {
            userTag.setStatus("inactive");
            userInterestTagRepository.save(userTag);
        }
        
        // 5. 删除标签（软删除）
        tagEntity.setStatus("inactive");
        tagEntity.setUpdateTime(LocalDateTime.now());
        boolean success = tagRepository.update(tagEntity);
        
        if (!success) {
            throw new RuntimeException("删除标签失败");
        }
        
        log.info("标签删除成功: {}", tagId);
    }

    @Override
    public TagInfo getTagById(Long tagId) {
        log.info("开始查询标签: {}", tagId);
        
        // 1. 参数验证
        if (tagId == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }
        
        // 2. 查询标签
        TagEntity tagEntity = tagRepository.findById(tagId);
        if (tagEntity == null || "inactive".equals(tagEntity.getStatus())) {
            return null;
        }
        
        // 3. 转换为DTO
        TagInfo tagInfo = convertToTagInfo(tagEntity);
        
        log.info("标签查询成功: {}", tagId);
        return tagInfo;
    }

    @Override
    public PageResponse<TagInfo> queryTags(TagQueryRequest request) {
        log.info("开始分页查询标签，页码: {}, 页大小: {}", request.getPageNo(), request.getPageSize());
        
        // 1. 查询标签
        PageResponse<TagEntity> entityPage = tagRepository.findTagPage(request);
        
        // 2. 转换为DTO
        List<TagInfo> tagInfoList = entityPage.getRecords().stream()
                .map(this::convertToTagInfo)
                .collect(Collectors.toList());
        
        PageResponse<TagInfo> result = PageResponse.of(tagInfoList, 
                entityPage.getTotal(), 
                entityPage.getPageSize(), 
                entityPage.getCurrentPage());
        
        log.info("分页查询标签完成，共查询到 {} 条记录", result.getTotal());
        return result;
    }

    @Override
    public List<TagInfo> getTagsByType(String tagType) {
        log.info("开始根据类型获取标签: {}", tagType);
        
        // 1. 参数验证
        if (!StringUtils.hasText(tagType)) {
            throw new IllegalArgumentException("标签类型不能为空");
        }
        
        // 2. 查询标签
        List<TagEntity> tagEntities = tagRepository.findByTagType(tagType);
        
        // 3. 转换为DTO
        List<TagInfo> tagInfoList = tagEntities.stream()
                .map(this::convertToTagInfo)
                .collect(Collectors.toList());
        
        log.info("根据类型获取标签完成，类型: {}, 数量: {}", tagType, tagInfoList.size());
        return tagInfoList;
    }

    @Override
    public List<TagInfo> getHotTags(String tagType, Integer limit) {
        log.info("开始获取热门标签，类型: {}, 限制: {}", tagType, limit);
        
        // 1. 设置默认限制
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        
        // 2. 查询热门标签
        List<TagEntity> tagEntities = tagRepository.findHotTags(tagType, limit);
        
        // 3. 转换为DTO
        List<TagInfo> tagInfoList = tagEntities.stream()
                .map(this::convertToTagInfo)
                .collect(Collectors.toList());
        
        log.info("获取热门标签完成，类型: {}, 数量: {}", tagType, tagInfoList.size());
        return tagInfoList;
    }

    @Override
    public List<TagInfo> searchTags(String keyword, String tagType) {
        log.info("开始搜索标签，关键词: {}, 类型: {}", keyword, tagType);
        
        // 1. 参数验证
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 2. 搜索标签
        List<TagEntity> tagEntities = tagRepository.searchTags(keyword, tagType);
        
        // 3. 转换为DTO
        List<TagInfo> tagInfoList = tagEntities.stream()
                .map(this::convertToTagInfo)
                .collect(Collectors.toList());
        
        log.info("搜索标签完成，关键词: {}, 数量: {}", keyword, tagInfoList.size());
        return tagInfoList;
    }

    @Override
    public List<TagInfo> getUserInterestTags(Long userId) {
        log.info("开始获取用户兴趣标签: {}", userId);
        
        // 1. 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 2. 查询用户兴趣标签
        List<UserInterestTagEntity> userInterestTags = userInterestTagRepository.findByUserId(userId);
        
        // 3. 提取标签ID
        List<Long> tagIds = userInterestTags.stream()
                .map(UserInterestTagEntity::getTagId)
                .collect(Collectors.toList());
        
        if (tagIds.isEmpty()) {
            return List.of();
        }
        
        // 4. 查询标签详情
        List<TagEntity> tagEntities = tagRepository.findByIds(tagIds);
        
        // 5. 转换为DTO
        List<TagInfo> tagInfoList = tagEntities.stream()
                .filter(tag -> "active".equals(tag.getStatus()))
                .map(this::convertToTagInfo)
                .collect(Collectors.toList());
        
        log.info("获取用户兴趣标签完成，用户: {}, 数量: {}", userId, tagInfoList.size());
        return tagInfoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserInterestTags(UserInterestTagRequest request) {
        log.info("开始设置用户兴趣标签: {}", request.getUserId());
        
        // 生成幂等键并执行幂等性检查
        String idempotentKey = IdempotentService.generateUserInterestTagIdempotentKey(
                request.getUserId(), "set:" + String.join(",", request.getTagIds().stream().map(String::valueOf).toArray(String[]::new)));
        
        idempotentService.executeIdempotent(idempotentKey, () -> {
            setUserInterestTagsInternal(request);
            return null;
        });
    }
    
    /**
     * 设置用户兴趣标签内部实现（用于幂等性处理）
     */
    private void setUserInterestTagsInternal(UserInterestTagRequest request) {
        // 1. 参数验证
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (request.getTagIds() == null || request.getTagIds().isEmpty()) {
            throw new IllegalArgumentException("标签ID列表不能为空");
        }
        
        // 2. 清空用户现有兴趣标签
        userInterestTagRepository.deleteAllByUserId(request.getUserId());
        
        // 3. 批量添加新的兴趣标签
        List<UserInterestTagEntity> entities = new ArrayList<>();
        for (Long tagId : request.getTagIds()) {
            UserInterestTagEntity entity = new UserInterestTagEntity();
            entity.setUserId(request.getUserId());
            entity.setTagId(tagId);
            entity.setInterestScore(new BigDecimal("1.0"));
            entity.setSource("manual");
            entity.setStatus("active");
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            entity.setVersion(1);
            entities.add(entity);
        }
        
        boolean success = userInterestTagRepository.batchSave(entities);
        if (!success) {
            throw new RuntimeException("设置用户兴趣标签失败");
        }
        
        log.info("设置用户兴趣标签完成，用户: {}, 标签数量: {}", request.getUserId(), request.getTagIds().size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserInterestTag(Long userId, Long tagId, Double interestScore) {
        log.info("开始添加用户兴趣标签: {}, {}, {}", userId, tagId, interestScore);
        
        // 1. 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (tagId == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }
        if (interestScore == null || interestScore < 0 || interestScore > 5) {
            interestScore = 1.0;
        }
        
        // 2. 检查标签是否存在
        TagEntity tagEntity = tagRepository.findById(tagId);
        if (tagEntity == null || !"active".equals(tagEntity.getStatus())) {
            throw new IllegalArgumentException("标签不存在或已禁用");
        }
        
        // 3. 检查用户是否已添加该标签
        UserInterestTagEntity existingTag = userInterestTagRepository.findByUserIdAndTagId(userId, tagId);
        if (existingTag != null) {
            // 更新兴趣分数
            existingTag.setInterestScore(new BigDecimal(interestScore.toString()));
            existingTag.setUpdateTime(LocalDateTime.now());
            userInterestTagRepository.save(existingTag);
        } else {
            // 新增兴趣标签
            UserInterestTagEntity newTag = new UserInterestTagEntity();
            newTag.setUserId(userId);
            newTag.setTagId(tagId);
            newTag.setInterestScore(new BigDecimal(interestScore.toString()));
            newTag.setSource("manual");
            newTag.setStatus("active");
            newTag.setCreateTime(LocalDateTime.now());
            newTag.setUpdateTime(LocalDateTime.now());
            newTag.setVersion(1);
            userInterestTagRepository.save(newTag);
        }
        
        // 4. 增加标签使用次数
        tagRepository.increaseUsageCount(tagId, 1L);
        
        log.info("添加用户兴趣标签成功: {}, {}", userId, tagId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserInterestTag(Long userId, Long tagId) {
        log.info("开始移除用户兴趣标签: {}, {}", userId, tagId);
        
        // 1. 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (tagId == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }
        
        // 2. 删除用户兴趣标签
        boolean success = userInterestTagRepository.deleteByUserIdAndTagId(userId, tagId);
        if (!success) {
            log.warn("移除用户兴趣标签失败，可能不存在: {}, {}", userId, tagId);
        }
        
        log.info("移除用户兴趣标签完成: {}, {}", userId, tagId);
    }

    @Override
    public List<TagInfo> recommendTagsToUser(Long userId, Integer limit) {
        log.info("开始推荐标签给用户: {}, {}", userId, limit);
        
        // 1. 参数验证
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        // 2. 获取用户现有兴趣标签
        List<UserInterestTagEntity> userInterestTags = userInterestTagRepository.findByUserId(userId);
        List<Long> existingTagIds = userInterestTags.stream()
                .map(UserInterestTagEntity::getTagId)
                .collect(Collectors.toList());
        
        // 3. 获取热门标签作为推荐（排除用户已有的）
        List<TagEntity> hotTags = tagRepository.findHotTags(null, limit * 2);
        List<TagEntity> recommendedTags = hotTags.stream()
                .filter(tag -> !existingTagIds.contains(tag.getId()))
                .limit(limit)
                .collect(Collectors.toList());
        
        // 4. 转换为DTO
        List<TagInfo> tagInfoList = recommendedTags.stream()
                .map(this::convertToTagInfo)
                .collect(Collectors.toList());
        
        log.info("推荐标签给用户完成，用户: {}, 推荐数量: {}", userId, tagInfoList.size());
        return tagInfoList;
    }

    /**
     * 将标签实体转换为TagInfo
     * 严格遵循去连表设计，只设置单表字段
     */
    private TagInfo convertToTagInfo(TagEntity tagEntity) {
        if (tagEntity == null) {
            return null;
        }
        
        TagInfo tagInfo = new TagInfo();
        tagInfo.setId(tagEntity.getId());
        tagInfo.setName(tagEntity.getName());
        tagInfo.setDescription(tagEntity.getDescription());
        tagInfo.setColor(tagEntity.getColor());
        tagInfo.setIconUrl(tagEntity.getIconUrl());
        tagInfo.setTagType(tagEntity.getTagType());
        // 仅设置分类ID，不做连表查询获取分类名称
        tagInfo.setCategoryId(tagEntity.getCategoryId());
        tagInfo.setUsageCount(tagEntity.getUsageCount());
        tagInfo.setHeatScore(tagEntity.getHeatScore());
        tagInfo.setStatus(tagEntity.getStatus());
        tagInfo.setCreateTime(tagEntity.getCreateTime());
        tagInfo.setUpdateTime(tagEntity.getUpdateTime());
        
        // 注意：不设置任何需要连表查询的字段，如分类名称等
        return tagInfo;
    }
} 