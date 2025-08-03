package com.gig.collide.tag.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagResponse;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.domain.entity.Tag;
import com.gig.collide.tag.domain.service.TagService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;
import com.gig.collide.tag.infrastructure.cache.TagCacheConstant;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * 标签门面服务实现类 - 缓存增强版
 * 基于JetCache双级缓存，实现高性能标签管理服务
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 */
@Slf4j
@DubboService(version = "1.0.0")
public class TagFacadeServiceImpl implements TagFacadeService {

    @Autowired
    private TagService tagService;

    @Autowired
    private UserFacadeService userFacadeService;

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_LIST_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_TYPE_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    public Result<Void> createTag(TagCreateRequest request) {
        try {
            log.info("创建标签请求: {}", request.getName());
            
            Tag tag = new Tag();
            BeanUtils.copyProperties(request, tag);
            
            Tag savedTag = tagService.createTag(tag);
            
            log.info("标签创建成功: ID={}", savedTag.getId());
            return Result.success(null);
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return Result.error("TAG_CREATE_ERROR", "创建标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = TagCacheConstant.TAG_DETAIL_CACHE,
                 key = TagCacheConstant.TAG_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = TagCacheConstant.TAG_LIST_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_TYPE_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_SEARCH_CACHE)
    public Result<TagResponse> updateTag(TagUpdateRequest request) {
        try {
            log.info("更新标签请求: ID={}", request.getId());
            
            Tag tag = new Tag();
            BeanUtils.copyProperties(request, tag);
            
            Tag updatedTag = tagService.updateTag(tag);
            TagResponse response = convertToResponse(updatedTag);
            
            log.info("标签更新成功: ID={}", updatedTag.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return Result.error("TAG_UPDATE_ERROR", "更新标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_LIST_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_TYPE_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_SEARCH_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_CONTENT_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_USER_INTEREST_CACHE)
    public Result<Void> deleteTag(Long tagId) {
        try {
            log.info("删除标签请求: ID={}", tagId);
            
            tagService.deleteTag(tagId);
            
            log.info("标签删除成功: ID={}", tagId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return Result.error("TAG_DELETE_ERROR", "删除标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_DETAIL_CACHE,
            key = TagCacheConstant.TAG_DETAIL_KEY,
            expire = TagCacheConstant.TAG_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<TagResponse> getTagById(Long tagId) {
        try {
            log.debug("获取标签详情: ID={}", tagId);
            
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            TagResponse response = convertToResponse(tag);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "查询标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_LIST_CACHE,
            key = TagCacheConstant.TAG_LIST_KEY,
            expire = TagCacheConstant.TAG_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<TagResponse>> queryTags(TagQueryRequest request) {
        try {
            log.debug("分页查询标签: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            IPage<Tag> page = tagService.queryTags(
                request.getCurrentPage(),
                request.getPageSize(),
                request.getName(),
                request.getTagType(),
                request.getCategoryId(),
                request.getStatus()
            );
            
            PageResponse<TagResponse> result = convertToPageResponse(page);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "分页查询标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_TYPE_CACHE,
            key = TagCacheConstant.TAG_TYPE_KEY,
            expire = TagCacheConstant.TAG_TYPE_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getTagsByType(String tagType) {
        try {
            log.debug("根据类型查询标签: tagType={}", tagType);
            
            List<Tag> tags = tagService.getTagsByType(tagType);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据类型查询标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "根据类型查询标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_SEARCH_CACHE,
            key = TagCacheConstant.TAG_SEARCH_KEY,
            expire = TagCacheConstant.TAG_SEARCH_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> searchTags(String keyword, Integer limit) {
        try {
            log.debug("搜索标签: keyword={}, limit={}", keyword, limit);
            
            List<Tag> tags = tagService.searchTags(keyword, limit);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("搜索标签失败", e);
            return Result.error("TAG_SEARCH_ERROR", "搜索标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_HOT_CACHE,
            key = TagCacheConstant.TAG_HOT_KEY,
            expire = TagCacheConstant.TAG_HOT_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getHotTags(Integer limit) {
        try {
            log.debug("获取热门标签: limit={}", limit);
            
            List<Tag> tags = tagService.getHotTags(limit);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取热门标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取热门标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_USER_INTEREST_CACHE,
            key = TagCacheConstant.TAG_USER_INTEREST_KEY,
            expire = TagCacheConstant.TAG_USER_INTEREST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getUserInterestTags(Long userId) {
        try {
            log.debug("获取用户兴趣标签: userId={}", userId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法获取兴趣标签: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            List<Tag> tags = tagService.getUserInterestTags(userId);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户兴趣标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_USER_INTEREST_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    public Result<Void> addUserInterestTag(Long userId, Long tagId, Double interestScore) {
        try {
            log.info("添加用户兴趣标签: userId={}, tagId={}, score={}", userId, tagId, interestScore);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法添加兴趣标签: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            // 验证标签是否存在
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                log.warn("标签不存在，无法添加兴趣标签: tagId={}", tagId);
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            BigDecimal score = interestScore != null ? BigDecimal.valueOf(interestScore) : null;
            tagService.addUserInterestTag(userId, tagId, score);
            
            log.info("用户兴趣标签添加成功: userId={}, tagId={}, tagName={}", userId, tagId, tag.getName());
            return Result.success(null);
        } catch (Exception e) {
            log.error("添加用户兴趣标签失败", e);
            return Result.error("TAG_INTEREST_ERROR", "添加用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_USER_INTEREST_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    public Result<Void> removeUserInterestTag(Long userId, Long tagId) {
        try {
            log.info("移除用户兴趣标签: userId={}, tagId={}", userId, tagId);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法移除兴趣标签: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            // 获取标签信息（用于日志）
            Tag tag = tagService.getTagById(tagId);
            String tagName = tag != null ? tag.getName() : "未知标签";
            
            tagService.removeUserInterestTag(userId, tagId);
            
            log.info("用户兴趣标签移除成功: userId={}, tagId={}, tagName={}", userId, tagId, tagName);
            return Result.success(null);
        } catch (Exception e) {
            log.error("移除用户兴趣标签失败", e);
            return Result.error("TAG_INTEREST_ERROR", "移除用户兴趣标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_USER_INTEREST_CACHE)
    public Result<Void> updateUserInterestScore(Long userId, Long tagId, Double interestScore) {
        try {
            log.info("更新用户兴趣分数: userId={}, tagId={}, score={}", userId, tagId, interestScore);
            
            // 验证用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(userId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("用户不存在，无法更新兴趣分数: userId={}", userId);
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            // 验证标签是否存在
            Tag tag = tagService.getTagById(tagId);
            if (tag == null) {
                log.warn("标签不存在，无法更新兴趣分数: tagId={}", tagId);
                return Result.error("TAG_NOT_FOUND", "标签不存在");
            }
            
            BigDecimal score = BigDecimal.valueOf(interestScore);
            tagService.updateUserInterestScore(userId, tagId, score);
            
            log.info("用户兴趣分数更新成功: userId={}, tagId={}, tagName={}, newScore={}", 
                    userId, tagId, tag.getName(), interestScore);
            return Result.success(null);
        } catch (Exception e) {
            log.error("更新用户兴趣分数失败", e);
            return Result.error("TAG_INTEREST_ERROR", "更新用户兴趣分数失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_CONTENT_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    public Result<Void> addContentTag(Long contentId, Long tagId) {
        try {
            log.info("为内容添加标签: contentId={}, tagId={}", contentId, tagId);
            
            tagService.addContentTag(contentId, tagId);
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("为内容添加标签失败", e);
            return Result.error("TAG_CONTENT_ERROR", "为内容添加标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_CONTENT_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    public Result<Void> removeContentTag(Long contentId, Long tagId) {
        try {
            log.info("移除内容标签: contentId={}, tagId={}", contentId, tagId);
            
            tagService.removeContentTag(contentId, tagId);
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("移除内容标签失败", e);
            return Result.error("TAG_CONTENT_ERROR", "移除内容标签失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TagCacheConstant.TAG_CONTENT_CACHE,
            key = TagCacheConstant.TAG_CONTENT_KEY,
            expire = TagCacheConstant.TAG_CONTENT_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<List<TagResponse>> getContentTags(Long contentId) {
        try {
            log.debug("获取内容标签: contentId={}", contentId);
            
            List<Tag> tags = tagService.getContentTags(contentId);
            List<TagResponse> responses = tags.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取内容标签失败", e);
            return Result.error("TAG_QUERY_ERROR", "获取内容标签失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = TagCacheConstant.TAG_DETAIL_CACHE)
    @CacheInvalidate(name = TagCacheConstant.TAG_HOT_CACHE)
    public Result<Void> increaseTagUsage(Long tagId) {
        try {
            log.debug("增加标签使用次数: tagId={}", tagId);
            
            tagService.increaseTagUsage(tagId);
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("增加标签使用次数失败", e);
            return Result.error("TAG_UPDATE_ERROR", "增加标签使用次数失败: " + e.getMessage());
        }
    }

    /**
     * 转换为响应对象
     */
    private TagResponse convertToResponse(Tag tag) {
        TagResponse response = new TagResponse();
        BeanUtils.copyProperties(tag, response);
        return response;
    }

    /**
     * 转换分页结果
     */
    private PageResponse<TagResponse> convertToPageResponse(IPage<Tag> page) {
        PageResponse<TagResponse> response = new PageResponse<>();
        response.setSuccess(true);  // 设置成功状态
        response.setDatas(convertToResponseList(page.getRecords()));
        response.setTotalPage((int) page.getPages());
        response.setCurrentPage((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setTotal(page.getTotal());
        return response;
    }

    /**
     * 转换实体列表为响应列表
     */
    private List<TagResponse> convertToResponseList(List<Tag> tags) {
        if (tags == null) {
            return null;
        }
        return tags.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
} 