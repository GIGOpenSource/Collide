package com.gig.collide.business.facade;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.CategoryOperatorResponse;
import com.gig.collide.api.category.response.CategoryQueryResponse;
import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.business.domain.category.entity.Category;
import com.gig.collide.business.domain.category.service.CategoryDomainService;
import com.gig.collide.base.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类门面服务实现
 *
 * @author collide
 * @date 2024/12/19
 */
@DubboService(version = "1.0.0", timeout = 3000)
@RequiredArgsConstructor
@Slf4j
public class CategoryFacadeServiceImpl implements CategoryFacadeService {
    
    private final CategoryDomainService categoryDomainService;
    
    @Override
    public CategoryOperatorResponse createCategory(CategoryCreateRequest request) {
        try {
            // 参数验证
            if (request == null || !request.isValid()) {
                return CategoryOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "创建分类参数不能为空");
            }
            
            // 创建分类
            Category category = categoryDomainService.createCategory(
                request.getName(),
                request.getDescription(),
                request.getParentId(),
                request.getIconUrl(),
                request.getSort(),
                request.getStatus()
            );
            
            // 构建响应
            CategoryInfo categoryInfo = buildCategoryInfo(category);
            return CategoryOperatorResponse.success(categoryInfo.getCategoryId());
            
        } catch (Exception e) {
            log.error("创建分类失败：{}", e.getMessage(), e);
            return CategoryOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "创建分类失败：" + e.getMessage());
        }
    }
    
    @Override
    public CategoryOperatorResponse updateCategory(CategoryUpdateRequest request) {
        try {
            // 参数验证
            if (request == null || request.getCategoryId() == null) {
                return CategoryOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "更新分类参数不能为空");
            }
            
            // 更新分类
            Category category = categoryDomainService.updateCategory(
                request.getCategoryId(),
                request.getName(),
                request.getDescription(),
                request.getIconUrl(),
                request.getSort(),
                request.getStatus()
            );
            
            // 构建响应
            CategoryInfo categoryInfo = buildCategoryInfo(category);
            return CategoryOperatorResponse.success(categoryInfo.getCategoryId());
            
        } catch (Exception e) {
            log.error("更新分类失败：{}", e.getMessage(), e);
            return CategoryOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "更新分类失败：" + e.getMessage());
        }
    }
    
    @Override
    public CategoryOperatorResponse deleteCategory(Long categoryId) {
        try {
            // 参数验证
            if (categoryId == null) {
                return CategoryOperatorResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "分类ID不能为空");
            }
            
            // 删除分类
            categoryDomainService.deleteCategory(categoryId);
            return CategoryOperatorResponse.success(categoryId);
            
        } catch (Exception e) {
            log.error("删除分类失败：{}", e.getMessage(), e);
            return CategoryOperatorResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "删除分类失败：" + e.getMessage());
        }
    }
    
    @Override
    public CategoryQueryResponse<CategoryInfo> getCategoryById(Long categoryId) {
        try {
            // 参数验证
            if (categoryId == null) {
                return CategoryQueryResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "分类ID不能为空");
            }
            
            // 查询分类
            Category category = categoryDomainService.getCategoryById(categoryId);
            if (category == null) {
                return CategoryQueryResponse.error(CommonErrorCode.DATA_NOT_FOUND.getCode(), "分类不存在");
            }
            
            // 构建响应
            CategoryInfo categoryInfo = buildCategoryInfo(category);
            return CategoryQueryResponse.success(categoryInfo);
            
        } catch (Exception e) {
            log.error("获取分类失败：{}", e.getMessage(), e);
            return CategoryQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "获取分类失败：" + e.getMessage());
        }
    }
    
    @Override
    public CategoryQueryResponse<PageResponse<CategoryInfo>> queryCategories(CategoryQueryRequest request) {
        try {
            // 参数验证
            if (request == null) {
                return CategoryQueryResponse.error(CommonErrorCode.PARAM_INVALID.getCode(), "查询参数不能为空");
            }
            
            // 分页查询分类
            Page<Category> categoryPage = categoryDomainService.queryCategories(
                request.getName(),
                null,
                request.getStatus(),
                request.getPageNo(),
                request.getPageSize()
            );
            
            // 构建响应
            List<CategoryInfo> categoryInfos = categoryPage.getRecords().stream()
                .map(this::buildCategoryInfo)
                .collect(Collectors.toList());
            
            PageResponse<CategoryInfo> pageResponse = PageResponse.of(
                categoryInfos, 
                (int) categoryPage.getTotal(), 
                request.getPageSize() != null ? request.getPageSize() : 10,
                request.getPageNo() != null ? request.getPageNo() : 1
            );
            
            return CategoryQueryResponse.success(pageResponse);
            
        } catch (Exception e) {
            log.error("查询分类失败：{}", e.getMessage(), e);
            return CategoryQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "查询分类失败：" + e.getMessage());
        }
    }
    
    @Override
    public CategoryQueryResponse<List<CategoryTree>> getCategoryTree(Long parentId) {
        try {
            List<Category> categories = categoryDomainService.getCategoryTree(parentId);
            List<CategoryTree> categoryTrees = categories.stream()
                .map(this::buildCategoryTree)
                .collect(Collectors.toList());
            return CategoryQueryResponse.success(categoryTrees);
        } catch (Exception e) {
            log.error("获取分类树失败：{}", e.getMessage(), e);
            return CategoryQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "获取分类树失败：" + e.getMessage());
        }
    }

    @Override
    public CategoryQueryResponse<List<CategoryInfo>> getHotCategories(Integer limit) {
        try {
            List<Category> categories = categoryDomainService.getHotCategories(limit);
            List<CategoryInfo> categoryInfos = categories.stream()
                .map(this::buildCategoryInfo)
                .collect(Collectors.toList());
            return CategoryQueryResponse.success(categoryInfos);
        } catch (Exception e) {
            log.error("获取热门分类失败：{}", e.getMessage(), e);
            return CategoryQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "获取热门分类失败：" + e.getMessage());
        }
    }

    @Override
    public CategoryQueryResponse<List<CategoryInfo>> searchCategories(String keyword) {
        try {
            List<Category> categories = categoryDomainService.searchCategories(keyword);
            List<CategoryInfo> categoryInfos = categories.stream()
                .map(this::buildCategoryInfo)
                .collect(Collectors.toList());
            return CategoryQueryResponse.success(categoryInfos);
        } catch (Exception e) {
            log.error("搜索分类失败：{}", e.getMessage(), e);
            return CategoryQueryResponse.error(CommonErrorCode.SYSTEM_ERROR.getCode(), "搜索分类失败：" + e.getMessage());
        }
    }
    
    /**
     * 转换为CategoryInfo
     */
    private CategoryInfo convertToCategoryInfo(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryInfo categoryInfo = new CategoryInfo();
        categoryInfo.setCategoryId(category.getId());
        categoryInfo.setParentId(category.getParentId());
        categoryInfo.setName(category.getName());
        categoryInfo.setDescription(category.getDescription());
        categoryInfo.setIconUrl(category.getIconUrl());
        categoryInfo.setCoverUrl(category.getCoverUrl());
        categoryInfo.setSort(category.getSortOrder());
        categoryInfo.setLevel(category.getLevel());
        categoryInfo.setPath(category.getPath());
        categoryInfo.setContentCount(category.getContentCount());
        categoryInfo.setStatus(category.getStatus());
        categoryInfo.setCreateTime(category.getCreateTime());
        categoryInfo.setUpdateTime(category.getUpdateTime());
        
        return categoryInfo;
    }
    
    /**
     * 构建CategoryInfo（兼容性方法）
     */
    private CategoryInfo buildCategoryInfo(Category category) {
        return convertToCategoryInfo(category);
    }
    
    /**
     * 转换为CategoryTree
     */
    private CategoryTree convertToCategoryTree(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryTree categoryTree = new CategoryTree();
        categoryTree.setCategoryId(category.getId());
        categoryTree.setParentId(category.getParentId());
        categoryTree.setName(category.getName());
        categoryTree.setDescription(category.getDescription());
        categoryTree.setIconUrl(category.getIconUrl());
        categoryTree.setCoverUrl(category.getCoverUrl());
        categoryTree.setSort(category.getSortOrder());
        categoryTree.setLevel(category.getLevel());
        categoryTree.setPath(category.getPath());
        categoryTree.setContentCount(category.getContentCount());
        categoryTree.setStatus(category.getStatus());
        categoryTree.setCreateTime(category.getCreateTime());
        categoryTree.setUpdateTime(category.getUpdateTime());
        
        return categoryTree;
    }
    
    /**
     * 构建CategoryTree（兼容性方法）
     */
    private CategoryTree buildCategoryTree(Category category) {
        return convertToCategoryTree(category);
    }
    
    /**
     * 批量转换为CategoryInfo列表
     */
    private List<CategoryInfo> convertToCategoryInfoList(List<Category> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return new ArrayList<>();
        }
        
        return categories.stream()
            .map(this::convertToCategoryInfo)
            .collect(Collectors.toList());
    }
} 