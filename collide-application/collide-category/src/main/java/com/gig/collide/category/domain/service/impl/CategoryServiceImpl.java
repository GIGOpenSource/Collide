package com.gig.collide.category.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.category.domain.service.CategoryService;
import com.gig.collide.category.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类业务服务实现 - 简洁版
 * 基于category-simple.sql的设计，包含完整业务逻辑
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        log.info("创建分类：{}", category);
        
        // 验证基础参数
        validateCategoryForCreate(category);
        
        // 检查同级分类名称唯一性
        if (existsCategoryName(category.getName(), category.getParentId(), null)) {
            throw new RuntimeException("同级分类下已存在相同名称的分类");
        }
        
        // 初始化默认值
        category.initDefaults();
        
        // 保存分类
        int result = categoryMapper.insert(category);
        if (result > 0) {
            log.info("分类创建成功，ID：{}", category.getId());
            return category;
        }
        
        throw new RuntimeException("分类创建失败");
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        log.info("更新分类：{}", category);
        
        // 验证分类是否存在
        Category existing = getCategoryById(category.getId(), true);
        if (existing == null) {
            throw new RuntimeException("分类不存在，ID：" + category.getId());
        }
        
        // 验证名称唯一性（排除自己）
        if (category.getName() != null && 
            existsCategoryName(category.getName(), category.getParentId(), category.getId())) {
            throw new RuntimeException("同级分类下已存在相同名称的分类");
        }
        
        // 验证父分类移动时的循环引用
        if (category.getParentId() != null && 
            wouldCreateCycle(category.getId(), category.getParentId())) {
            throw new RuntimeException("移动分类会形成循环引用");
        }
        
        // 更新时间
        category.setUpdateTime(LocalDateTime.now());
        
        int result = categoryMapper.updateById(category);
        if (result > 0) {
            log.info("分类更新成功，ID：{}", category.getId());
            return categoryMapper.selectById(category.getId());
        }
        
        throw new RuntimeException("分类更新失败");
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long categoryId, Long operatorId) {
        log.info("删除分类，ID：{}，操作人：{}", categoryId, operatorId);
        
        Category category = getCategoryById(categoryId, true);
        if (category == null) {
            log.warn("分类不存在，无法删除，ID：{}", categoryId);
            return false;
        }
        
        // 验证权限
        if (!validateCategoryPermission(categoryId, operatorId, "delete")) {
            throw new RuntimeException("无权限删除该分类");
        }
        
        // 检查是否可以删除（没有子分类和内容）
        if (!category.canBeDeleted()) {
            throw new RuntimeException("分类包含子分类或内容，无法删除");
        }
        
        // 逻辑删除（更新状态为inactive）
        category.deactivate();
        int result = categoryMapper.updateById(category);
        
        log.info("分类删除成功，ID：{}", categoryId);
        return result > 0;
    }

    @Override
    public Category getCategoryById(Long categoryId, Boolean includeInactive) {
        if (categoryId == null) {
            return null;
        }
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getId, categoryId);
        
        if (!Boolean.TRUE.equals(includeInactive)) {
            wrapper.eq(Category::getStatus, "active");
        }
        
        return categoryMapper.selectOne(wrapper);
    }

    @Override
    public List<Category> getCategoriesByIds(List<Long> categoryIds, Boolean includeInactive) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Category::getId, categoryIds);
        
        if (!Boolean.TRUE.equals(includeInactive)) {
            wrapper.eq(Category::getStatus, "active");
        }
        
        wrapper.orderByAsc(Category::getSort);
        
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public IPage<Category> queryCategories(Long parentId, String status, Integer currentPage, Integer pageSize,
                                         String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        
        return categoryMapper.selectCategoriesPage(page, parentId, status, orderBy, orderDirection);
    }

    @Override
    public IPage<Category> searchCategories(String keyword, Long parentId, Integer currentPage, Integer pageSize,
                                          String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active"; // 搜索只返回激活状态的分类
        
        return categoryMapper.searchCategories(page, keyword, parentId, status, orderBy, orderDirection);
    }

    @Override
    public IPage<Category> getRootCategories(Integer currentPage, Integer pageSize, String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active";
        
        return categoryMapper.selectRootCategories(page, status, orderBy, orderDirection);
    }

    @Override
    public IPage<Category> getChildCategories(Long parentId, Integer currentPage, Integer pageSize,
                                            String orderBy, String orderDirection) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active";
        
        return categoryMapper.selectCategoriesPage(page, parentId, status, orderBy, orderDirection);
    }

    @Override
    public List<Category> getCategoryTree(Long rootId, Integer maxDepth, Boolean includeInactive) {
        String status = Boolean.TRUE.equals(includeInactive) ? null : "active";
        
        // 获取所有相关分类
        List<Category> allCategories = categoryMapper.selectCategoryTree(rootId, maxDepth, status, "sort", "ASC");
        
        // 构建树形结构
        return buildCategoryTree(allCategories, maxDepth);
    }

    @Override
    public List<Category> getCategoryPath(Long categoryId) {
        return categoryMapper.selectCategoryPath(categoryId);
    }

    @Override
    public List<Category> getCategoryAncestors(Long categoryId, Boolean includeInactive) {
        return categoryMapper.selectCategoryAncestors(categoryId, includeInactive);
    }

    @Override
    public List<Category> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive) {
        return categoryMapper.selectCategoryDescendants(categoryId, maxDepth, includeInactive);
    }

    @Override
    public List<Category> buildCategoryTree(List<Category> categories, Integer maxDepth) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按parent_id分组
        Map<Long, List<Category>> categoryMap = categories.stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        
        // 获取根分类（parent_id = 0）
        List<Category> rootCategories = categoryMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建树形结构
        for (Category root : rootCategories) {
            buildCategoryChildren(root, categoryMap, maxDepth, 1);
        }
        
        return rootCategories;
    }

    @Override
    @Transactional
    public boolean updateCategoryStatus(Long categoryId, String status, Long operatorId) {
        log.info("更新分类状态，ID：{}，状态：{}，操作人：{}", categoryId, status, operatorId);
        
        Category category = getCategoryById(categoryId, true);
        if (category == null) {
            return false;
        }
        
        // 验证权限
        if (!validateCategoryPermission(categoryId, operatorId, "status")) {
            throw new RuntimeException("无权限修改分类状态");
        }
        
        // 验证状态值
        if (!"active".equals(status) && !"inactive".equals(status)) {
            throw new RuntimeException("无效的状态值：" + status);
        }
        
        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());
        
        return categoryMapper.updateById(category) > 0;
    }

    @Override
    @Transactional
    public int batchUpdateCategoryStatus(List<Long> categoryIds, String status, Long operatorId) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return 0;
        }
        
        log.info("批量更新分类状态，数量：{}，状态：{}", categoryIds.size(), status);
        
        // 验证状态值
        if (!"active".equals(status) && !"inactive".equals(status)) {
            throw new RuntimeException("无效的状态值：" + status);
        }
        
        return categoryMapper.batchUpdateStatus(categoryIds, status);
    }

    @Override
    public boolean activateCategory(Long categoryId, Long operatorId) {
        return updateCategoryStatus(categoryId, "active", operatorId);
    }

    @Override
    public boolean deactivateCategory(Long categoryId, Long operatorId) {
        return updateCategoryStatus(categoryId, "inactive", operatorId);
    }

    @Override
    @Transactional
    public long updateContentCount(Long categoryId, Long increment) {
        if (categoryId == null || increment == null) {
            return 0;
        }
        
        int result = categoryMapper.updateContentCount(categoryId, increment);
        if (result > 0) {
            Category category = categoryMapper.selectById(categoryId);
            return category != null ? category.getSafeContentCount() : 0L;
        }
        
        return 0;
    }

    @Override
    public Map<String, Object> getCategoryStatistics(Long categoryId) {
        return categoryMapper.selectCategoryStatistics(categoryId);
    }

    @Override
    public long countCategories(Long parentId, String status) {
        return categoryMapper.countCategories(parentId, status);
    }

    @Override
    public IPage<Category> getPopularCategories(Long parentId, Integer currentPage, Integer pageSize) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active";
        
        return categoryMapper.selectPopularCategories(page, parentId, status);
    }

    @Override
    @Transactional
    public boolean adjustCategorySort(Long categoryId, Integer newSort, Long operatorId) {
        log.info("调整分类排序，ID：{}，新排序：{}，操作人：{}", categoryId, newSort, operatorId);
        
        Category category = getCategoryById(categoryId, true);
        if (category == null) {
            return false;
        }
        
        // 验证权限
        if (!validateCategoryPermission(categoryId, operatorId, "sort")) {
            throw new RuntimeException("无权限调整分类排序");
        }
        
        category.updateSort(newSort);
        return categoryMapper.updateById(category) > 0;
    }

    @Override
    @Transactional
    public int batchAdjustSort(Map<Long, Integer> sortMappings, Long operatorId) {
        if (sortMappings == null || sortMappings.isEmpty()) {
            return 0;
        }
        
        log.info("批量调整排序，数量：{}", sortMappings.size());
        
        return categoryMapper.batchUpdateSort(sortMappings);
    }

    @Override
    @Transactional
    public boolean moveCategory(Long categoryId, Long newParentId, Long operatorId) {
        log.info("移动分类，ID：{}，新父分类：{}，操作人：{}", categoryId, newParentId, operatorId);
        
        // 验证循环引用
        if (wouldCreateCycle(categoryId, newParentId)) {
            throw new RuntimeException("移动分类会形成循环引用");
        }
        
        // 验证权限
        if (!validateCategoryPermission(categoryId, operatorId, "move")) {
            throw new RuntimeException("无权限移动分类");
        }
        
        int result = categoryMapper.updateParentCategory(categoryId, newParentId);
        return result > 0;
    }

    @Override
    public boolean existsCategoryName(String name, Long parentId, Long excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        return categoryMapper.existsCategoryName(name.trim(), parentId, excludeId);
    }

    @Override
    public IPage<Category> getLeafCategories(Long parentId, Integer currentPage, Integer pageSize) {
        Page<Category> page = new Page<>(currentPage, pageSize);
        String status = "active";
        
        return categoryMapper.selectLeafCategories(page, parentId, status);
    }

    @Override
    public List<Category> getCategorySuggestions(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String status = "active";
        return categoryMapper.selectCategorySuggestions(keyword.trim(), limit, status);
    }

    @Override
    @Transactional
    public Category cloneCategory(Long sourceId, Long newParentId, String newName, Long operatorId) {
        log.info("克隆分类，源ID：{}，新父分类：{}，新名称：{}", sourceId, newParentId, newName);
        
        // 获取源分类
        Category source = getCategoryById(sourceId, true);
        if (source == null) {
            throw new RuntimeException("源分类不存在");
        }
        
        // 创建新分类
        Category newCategory = new Category();
        newCategory.setName(newName);
        newCategory.setDescription(source.getDescription());
        newCategory.setParentId(newParentId);
        newCategory.setIconUrl(source.getIconUrl());
        newCategory.setSort(source.getSort());
        newCategory.setContentCount(0L); // 克隆的分类内容数量为0
        
        return createCategory(newCategory);
    }

    @Override
    @Transactional
    public boolean mergeCategories(Long sourceId, Long targetId, Long operatorId) {
        log.info("合并分类，源ID：{}，目标ID：{}，操作人：{}", sourceId, targetId, operatorId);
        
        Category source = getCategoryById(sourceId, true);
        Category target = getCategoryById(targetId, true);
        
        if (source == null || target == null) {
            throw new RuntimeException("源分类或目标分类不存在");
        }
        
        // 验证权限
        if (!validateCategoryPermission(sourceId, operatorId, "merge") ||
            !validateCategoryPermission(targetId, operatorId, "merge")) {
            throw new RuntimeException("无权限合并分类");
        }
        
        // 将源分类的内容数量合并到目标分类
        target.updateContentCount(source.getSafeContentCount());
        categoryMapper.updateById(target);
        
        // 删除源分类
        return deleteCategory(sourceId, operatorId);
    }

    @Override
    @Transactional
    public int recalculateContentCount(Long categoryId) {
        log.info("重新计算内容数量，分类ID：{}", categoryId);
        
        return categoryMapper.recalculateContentCount(categoryId);
    }

    @Override
    @Transactional
    public int syncCategoryHierarchy() {
        log.info("同步分类层级关系");
        
        return categoryMapper.syncCategoryHierarchy();
    }

    @Override
    public boolean validateCategoryPermission(Long categoryId, Long operatorId, String operation) {
        // 简化的权限验证，实际应该结合权限系统
        if (categoryId == null || operatorId == null) {
            return false;
        }
        
        Category category = getCategoryById(categoryId, true);
        if (category == null) {
            return false;
        }
        
        // 这里应该检查用户权限，暂时返回true
        return true;
    }

    @Override
    public boolean wouldCreateCycle(Long categoryId, Long newParentId) {
        if (categoryId == null || newParentId == null || newParentId == 0 || categoryId.equals(newParentId)) {
            return false;
        }
        
        // 检查新父分类是否为当前分类的后代
        List<Category> descendants = getCategoryDescendants(categoryId, null, true);
        return descendants.stream().anyMatch(desc -> desc.getId().equals(newParentId));
    }

    // =================== 私有方法 ===================

    /**
     * 验证分类创建参数
     */
    private void validateCategoryForCreate(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("分类名称不能为空");
        }
        
        if (category.getParentId() == null) {
            category.setParentId(0L); // 默认为根分类
        }
        
        // 验证父分类是否存在（非根分类时）
        if (category.getParentId() != 0) {
            Category parent = getCategoryById(category.getParentId(), false);
            if (parent == null) {
                throw new RuntimeException("父分类不存在或已停用");
            }
        }
    }

    /**
     * 递归构建分类子节点
     */
    private void buildCategoryChildren(Category parent, Map<Long, List<Category>> categoryMap, 
                                     Integer maxDepth, int currentDepth) {
        if (maxDepth != null && currentDepth >= maxDepth) {
            return;
        }
        
        List<Category> children = categoryMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            parent.initChildren();
            for (Category child : children) {
                child.setLevel(currentDepth);
                child.setParentName(parent.getName());
                parent.addChild(child);
                buildCategoryChildren(child, categoryMap, maxDepth, currentDepth + 1);
            }
        }
    }
}