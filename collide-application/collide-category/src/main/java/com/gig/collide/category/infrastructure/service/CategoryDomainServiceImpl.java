package com.gig.collide.category.infrastructure.service;

import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
import com.gig.collide.category.infrastructure.exception.CategoryBusinessException;
import com.gig.collide.category.infrastructure.exception.CategoryErrorCode;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.category.domain.convertor.CategoryConvertor;
import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.category.domain.repository.CategoryRepository;
import com.gig.collide.category.domain.service.CategoryDomainService;
import com.gig.collide.category.infrastructure.cache.CategoryCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类领域服务实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryDomainServiceImpl implements CategoryDomainService {

    private final CategoryRepository categoryRepository;
    private final CategoryCacheService cacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryCreateRequest request) {
        log.info("开始创建分类: {}", request.getName());
        
        // 参数校验
        validateCreateRequest(request);
        
        // 检查分类名称是否重复
        if (categoryRepository.existsByName(request.getParentId(), request.getName(), null)) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NAME_EXISTS);
        }
        
        // 转换并保存
        Category category = CategoryConvertor.toEntity(request);
        Category savedCategory = categoryRepository.save(category);
        
        // 清除相关缓存
        cacheService.evictCategoryCache(savedCategory.getId());
        
        log.info("创建分类成功，ID: {}, 名称: {}", savedCategory.getId(), savedCategory.getName());
        return savedCategory.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(CategoryUpdateRequest request) {
        log.info("开始更新分类: {}", request.getCategoryId());
        
        // 参数校验
        validateUpdateRequest(request);
        
        // 查询现有分类
        Category existingCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NOT_FOUND));
        
        // 检查分类名称是否重复（如果要更新名称）
        if (StringUtils.hasText(request.getName()) && 
            !request.getName().equals(existingCategory.getName())) {
            if (categoryRepository.existsByName(existingCategory.getParentId(), request.getName(), request.getCategoryId())) {
                throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NAME_EXISTS);
            }
        }
        
        // 使用幂等性更新
        boolean success = categoryRepository.updateCategoryIdempotent(
            request.getCategoryId(),
            request.getName(),
            request.getDescription(),
            request.getIconUrl(),
            request.getCoverUrl(),
            request.getSortOrder(),
            request.getLastModifierId(),
            request.getLastModifierName(),
            existingCategory.getVersion()
        );
        
        if (!success) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_UPDATE_CONFLICT);
        }
        
        // 清除相关缓存
        cacheService.evictCategoryCache(request.getCategoryId());
        
        log.info("更新分类成功: {}", request.getCategoryId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        log.info("开始删除分类: {}", categoryId);
        
        if (categoryId == null) {
            throw CategoryBusinessException.of(CategoryErrorCode.INVALID_PARAMETER, "分类ID不能为空");
        }
        
        // 检查分类是否存在
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NOT_FOUND));
        
        // 检查是否有子分类
        List<Category> children = categoryRepository.findByParentId(categoryId);
        if (!children.isEmpty()) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_HAS_CHILDREN);
        }
        
        // 检查是否有内容
        if (category.getContentCount() != null && category.getContentCount() > 0) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_HAS_CONTENT);
        }
        
        // 执行删除
        boolean success = categoryRepository.deleteById(categoryId);
        if (!success) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_DELETE_FAILED);
        }
        
        // 清除相关缓存
        cacheService.evictCategoryCache(categoryId);
        
        log.info("删除分类成功: {}", categoryId);
    }

    @Override
    public CategoryInfo getCategoryById(Long categoryId) {
        log.info("查询分类详情: {}", categoryId);
        
        if (categoryId == null) {
            throw CategoryBusinessException.of(CategoryErrorCode.INVALID_PARAMETER, "分类ID不能为空");
        }
        
        // 先尝试从缓存获取
        CategoryInfo cachedCategory = cacheService.getCachedCategoryInfo(categoryId);
        if (cachedCategory != null) {
            return cachedCategory;
        }
        
        // 缓存未命中，从数据库查询
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NOT_FOUND));
        
        CategoryInfo categoryInfo = CategoryConvertor.toInfo(category);
        
        // 缓存查询结果
        cacheService.cacheCategoryInfo(categoryId, categoryInfo);
        
        return categoryInfo;
    }

    @Override
    public PageResponse<CategoryInfo> queryCategories(CategoryQueryRequest request) {
        log.info("分页查询分类，参数: {}", request);
        
        // 设置默认值
        if (request.getPageNo() == null || request.getPageNo() < 1) {
            request.setPageNo(1);
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            request.setPageSize(10);
        }
        
        PageResponse<Category> pageResult = categoryRepository.findByPage(request);
        
        // 转换数据
        List<CategoryInfo> categoryInfos = CategoryConvertor.toInfoList(pageResult.getRecords());
        
        return PageResponse.of(categoryInfos, pageResult.getTotal(), pageResult.getPageSize(), pageResult.getCurrentPage());
    }

    @Override
    public List<CategoryTree> getCategoryTree(Long parentId) {
        log.info("获取分类树，父分类ID: {}", parentId);
        
        // 先尝试从缓存获取
        List<CategoryTree> cachedTree = cacheService.getCachedCategoryTree(parentId);
        if (cachedTree != null) {
            return cachedTree;
        }
        
        List<Category> categories;
        
        if (parentId == null) {
            // 获取所有分类构建完整树
            categories = categoryRepository.findRootCategories();
        } else {
            // 获取指定父分类的子分类
            categories = categoryRepository.findByParentId(parentId);
        }
        
        // 递归构建分类树
        List<CategoryTree> result = new ArrayList<>();
        for (Category category : categories) {
            CategoryTree tree = CategoryConvertor.toTree(category);
            buildCategoryTree(tree);
            result.add(tree);
        }
        
        // 缓存结果
        cacheService.cacheCategoryTree(parentId, result);
        
        return result;
    }

    @Override
    public List<CategoryInfo> getHotCategories(Integer limit) {
        log.info("获取热门分类，限制: {}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        
        // 先尝试从缓存获取
        List<CategoryInfo> cachedHotCategories = cacheService.getCachedHotCategories(limit);
        if (cachedHotCategories != null) {
            return cachedHotCategories;
        }
        
        // 缓存未命中，从数据库查询
        List<Category> hotCategories = categoryRepository.findHotCategories(limit);
        List<CategoryInfo> result = CategoryConvertor.toInfoList(hotCategories);
        
        // 缓存结果
        cacheService.cacheHotCategories(limit, result);
        
        return result;
    }

    @Override
    public List<CategoryInfo> searchCategories(String keyword) {
        log.info("搜索分类，关键词: {}", keyword);
        
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }
        
        String trimmedKeyword = keyword.trim();
        
        // 先尝试从缓存获取
        List<CategoryInfo> cachedResult = cacheService.getCachedSearchResult(trimmedKeyword);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 缓存未命中，从数据库查询
        List<Category> categories = categoryRepository.searchByKeyword(trimmedKeyword, 50);
        List<CategoryInfo> result = CategoryConvertor.toInfoList(categories);
        
        // 缓存结果
        cacheService.cacheSearchResult(trimmedKeyword, result);
        
        return result;
    }

    // ================================ 私有方法 ================================

    /**
     * 递归构建分类树
     */
    private void buildCategoryTree(CategoryTree tree) {
        List<Category> children = categoryRepository.findByParentId(tree.getCategoryId());
        if (!children.isEmpty()) {
            List<CategoryTree> childTrees = children.stream()
                    .map(CategoryConvertor::toTree)
                    .collect(Collectors.toList());
            
            // 递归构建子树
            for (CategoryTree childTree : childTrees) {
                buildCategoryTree(childTree);
            }
            
            tree.setChildren(childTrees);
        }
    }

    /**
     * 校验创建请求
     */
    private void validateCreateRequest(CategoryCreateRequest request) {
        if (request == null) {
            throw CategoryBusinessException.of(CategoryErrorCode.INVALID_PARAMETER, "请求参数不能为空");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NAME_EMPTY);
        }
        if (request.getName().length() > 50) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NAME_TOO_LONG);
        }
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_DESCRIPTION_TOO_LONG);
        }
    }

    /**
     * 校验更新请求
     */
    private void validateUpdateRequest(CategoryUpdateRequest request) {
        if (request == null) {
            throw CategoryBusinessException.of(CategoryErrorCode.INVALID_PARAMETER, "请求参数不能为空");
        }
        if (request.getCategoryId() == null) {
            throw CategoryBusinessException.of(CategoryErrorCode.INVALID_PARAMETER, "分类ID不能为空");
        }
        if (request.getName() != null && request.getName().length() > 50) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_NAME_TOO_LONG);
        }
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            throw CategoryBusinessException.of(CategoryErrorCode.CATEGORY_DESCRIPTION_TOO_LONG);
        }
    }
} 