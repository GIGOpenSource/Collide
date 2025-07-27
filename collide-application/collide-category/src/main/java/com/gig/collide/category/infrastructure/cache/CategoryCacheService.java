package com.gig.collide.category.infrastructure.cache;

import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
import com.gig.collide.category.domain.convertor.CategoryConvertor;
import com.gig.collide.category.domain.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 分类缓存服务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 缓存key前缀
    private static final String CATEGORY_PREFIX = "category:";
    private static final String CATEGORY_INFO_PREFIX = CATEGORY_PREFIX + "info:";
    private static final String CATEGORY_TREE_PREFIX = CATEGORY_PREFIX + "tree:";
    private static final String CATEGORY_HOT_PREFIX = CATEGORY_PREFIX + "hot:";
    private static final String CATEGORY_SEARCH_PREFIX = CATEGORY_PREFIX + "search:";
    private static final String CATEGORY_PARENT_PREFIX = CATEGORY_PREFIX + "parent:";

    // 缓存过期时间
    private static final Duration CACHE_TTL = Duration.ofHours(2);
    private static final Duration HOT_CACHE_TTL = Duration.ofMinutes(30);
    private static final Duration SEARCH_CACHE_TTL = Duration.ofMinutes(10);

    /**
     * 缓存分类详情
     */
    public void cacheCategoryInfo(Long categoryId, CategoryInfo categoryInfo) {
        try {
            String key = CATEGORY_INFO_PREFIX + categoryId;
            redisTemplate.opsForValue().set(key, categoryInfo, CACHE_TTL);
            log.debug("缓存分类详情成功，ID: {}", categoryId);
        } catch (Exception e) {
            log.error("缓存分类详情失败，ID: {}", categoryId, e);
        }
    }

    /**
     * 获取缓存的分类详情
     */
    public CategoryInfo getCachedCategoryInfo(Long categoryId) {
        try {
            String key = CATEGORY_INFO_PREFIX + categoryId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof CategoryInfo) {
                log.debug("命中分类详情缓存，ID: {}", categoryId);
                return (CategoryInfo) cached;
            }
        } catch (Exception e) {
            log.error("获取分类详情缓存失败，ID: {}", categoryId, e);
        }
        return null;
    }

    /**
     * 缓存分类树
     */
    public void cacheCategoryTree(Long parentId, List<CategoryTree> categoryTree) {
        try {
            String key = CATEGORY_TREE_PREFIX + (parentId != null ? parentId : "root");
            redisTemplate.opsForValue().set(key, categoryTree, CACHE_TTL);
            log.debug("缓存分类树成功，父ID: {}", parentId);
        } catch (Exception e) {
            log.error("缓存分类树失败，父ID: {}", parentId, e);
        }
    }

    /**
     * 获取缓存的分类树
     */
    @SuppressWarnings("unchecked")
    public List<CategoryTree> getCachedCategoryTree(Long parentId) {
        try {
            String key = CATEGORY_TREE_PREFIX + (parentId != null ? parentId : "root");
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof List) {
                log.debug("命中分类树缓存，父ID: {}", parentId);
                return (List<CategoryTree>) cached;
            }
        } catch (Exception e) {
            log.error("获取分类树缓存失败，父ID: {}", parentId, e);
        }
        return null;
    }

    /**
     * 缓存热门分类
     */
    public void cacheHotCategories(Integer limit, List<CategoryInfo> categories) {
        try {
            String key = CATEGORY_HOT_PREFIX + limit;
            redisTemplate.opsForValue().set(key, categories, HOT_CACHE_TTL);
            log.debug("缓存热门分类成功，限制: {}", limit);
        } catch (Exception e) {
            log.error("缓存热门分类失败，限制: {}", limit, e);
        }
    }

    /**
     * 获取缓存的热门分类
     */
    @SuppressWarnings("unchecked")
    public List<CategoryInfo> getCachedHotCategories(Integer limit) {
        try {
            String key = CATEGORY_HOT_PREFIX + limit;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof List) {
                log.debug("命中热门分类缓存，限制: {}", limit);
                return (List<CategoryInfo>) cached;
            }
        } catch (Exception e) {
            log.error("获取热门分类缓存失败，限制: {}", limit, e);
        }
        return null;
    }

    /**
     * 缓存搜索结果
     */
    public void cacheSearchResult(String keyword, List<CategoryInfo> categories) {
        try {
            String key = CATEGORY_SEARCH_PREFIX + keyword.hashCode();
            redisTemplate.opsForValue().set(key, categories, SEARCH_CACHE_TTL);
            log.debug("缓存搜索结果成功，关键词: {}", keyword);
        } catch (Exception e) {
            log.error("缓存搜索结果失败，关键词: {}", keyword, e);
        }
    }

    /**
     * 获取缓存的搜索结果
     */
    @SuppressWarnings("unchecked")
    public List<CategoryInfo> getCachedSearchResult(String keyword) {
        try {
            String key = CATEGORY_SEARCH_PREFIX + keyword.hashCode();
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof List) {
                log.debug("命中搜索结果缓存，关键词: {}", keyword);
                return (List<CategoryInfo>) cached;
            }
        } catch (Exception e) {
            log.error("获取搜索结果缓存失败，关键词: {}", keyword, e);
        }
        return null;
    }

    /**
     * 缓存父分类的子分类列表
     */
    public void cacheChildrenByParent(Long parentId, List<CategoryInfo> children) {
        try {
            String key = CATEGORY_PARENT_PREFIX + parentId;
            redisTemplate.opsForValue().set(key, children, CACHE_TTL);
            log.debug("缓存子分类列表成功，父ID: {}", parentId);
        } catch (Exception e) {
            log.error("缓存子分类列表失败，父ID: {}", parentId, e);
        }
    }

    /**
     * 获取缓存的子分类列表
     */
    @SuppressWarnings("unchecked")
    public List<CategoryInfo> getCachedChildrenByParent(Long parentId) {
        try {
            String key = CATEGORY_PARENT_PREFIX + parentId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof List) {
                log.debug("命中子分类列表缓存，父ID: {}", parentId);
                return (List<CategoryInfo>) cached;
            }
        } catch (Exception e) {
            log.error("获取子分类列表缓存失败，父ID: {}", parentId, e);
        }
        return null;
    }

    /**
     * 删除分类相关缓存
     */
    public void evictCategoryCache(Long categoryId) {
        try {
            // 删除分类详情缓存
            String infoKey = CATEGORY_INFO_PREFIX + categoryId;
            redisTemplate.delete(infoKey);

            // 删除相关的分类树缓存（模糊匹配删除）
            Set<String> treeKeys = redisTemplate.keys(CATEGORY_TREE_PREFIX + "*");
            if (treeKeys != null && !treeKeys.isEmpty()) {
                redisTemplate.delete(treeKeys);
            }

            // 删除热门分类缓存
            Set<String> hotKeys = redisTemplate.keys(CATEGORY_HOT_PREFIX + "*");
            if (hotKeys != null && !hotKeys.isEmpty()) {
                redisTemplate.delete(hotKeys);
            }

            // 删除搜索结果缓存
            Set<String> searchKeys = redisTemplate.keys(CATEGORY_SEARCH_PREFIX + "*");
            if (searchKeys != null && !searchKeys.isEmpty()) {
                redisTemplate.delete(searchKeys);
            }

            // 删除父子关系缓存
            Set<String> parentKeys = redisTemplate.keys(CATEGORY_PARENT_PREFIX + "*");
            if (parentKeys != null && !parentKeys.isEmpty()) {
                redisTemplate.delete(parentKeys);
            }

            log.info("清除分类相关缓存成功，ID: {}", categoryId);
        } catch (Exception e) {
            log.error("清除分类相关缓存失败，ID: {}", categoryId, e);
        }
    }

    /**
     * 清除所有分类缓存
     */
    public void evictAllCategoryCache() {
        try {
            Set<String> keys = redisTemplate.keys(CATEGORY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("清除所有分类缓存成功，共删除 {} 个缓存", keys.size());
            }
        } catch (Exception e) {
            log.error("清除所有分类缓存失败", e);
        }
    }

    /**
     * 设置缓存过期时间
     */
    public void expireCache(String key, Duration duration) {
        try {
            redisTemplate.expire(key, duration);
        } catch (Exception e) {
            log.error("设置缓存过期时间失败，key: {}", key, e);
        }
    }

    /**
     * 检查缓存是否存在
     */
    public boolean existsCache(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查缓存是否存在失败，key: {}", key, e);
            return false;
        }
    }
} 