package com.gig.collide.category.domain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.category.domain.service.CategoryService;
import com.gig.collide.category.infrastructure.mapper.CategoryMapper;
import com.gig.collide.base.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类业务服务实现 - 规范版
 * 参考Content模块设计，分页查询返回PageResponse，非分页查询返回List
 * 支持完整的分类功能：查询、层级、统计、管理
 * 
 * @author Collide
 * @version 5.0.0 (与Content模块一致版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    // =================== 业务常量 ===================
    
    private static final String DEFAULT_STATUS = "active";
    private static final String DEFAULT_ORDER_BY = "sort";
    private static final String DEFAULT_ORDER_DIRECTION = "ASC";
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_CURRENT_PAGE = 1;

    // =================== 基础查询 ===================

    @Override
    public Category getCategoryById(Long categoryId) {
        log.info("获取分类详情: categoryId={}", categoryId);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        Category category = categoryMapper.selectById(categoryId);
        
        log.info("获取分类详情完成: categoryId={}, found={}", categoryId, category != null);
        return category;
    }

    @Override
    public PageResponse<Category> queryCategories(Long parentId, String status, Integer currentPage, Integer pageSize,
                                                 String orderBy, String orderDirection) {
        log.info("查询分类列表: parentId={}, status={}, page={}/{}", parentId, status, currentPage, pageSize);
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        orderBy = StringUtils.hasText(orderBy) ? orderBy : DEFAULT_ORDER_BY;
        orderDirection = StringUtils.hasText(orderDirection) ? orderDirection : DEFAULT_ORDER_DIRECTION;
        currentPage = currentPage != null && currentPage > 0 ? currentPage : DEFAULT_CURRENT_PAGE;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        
        // 使用MyBatis-Plus分页
        Page<Category> page = new Page<>(currentPage, pageSize);
        IPage<Category> result = categoryMapper.selectCategoriesPage(page, parentId, status, orderBy, orderDirection);
        
        // 转换为PageResponse
        PageResponse<Category> pageResponse = convertToPageResponse(result);
        
        log.info("查询分类列表完成: 查询到{}条记录，总计{}条", result.getRecords().size(), result.getTotal());
        return pageResponse;
    }

    @Override
    public PageResponse<Category> searchCategories(String keyword, Long parentId, String status, 
                                                  Integer currentPage, Integer pageSize,
                                                  String orderBy, String orderDirection) {
        log.info("搜索分类: keyword={}, parentId={}, status={}, page={}/{}", 
                keyword, parentId, status, currentPage, pageSize);
        
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        orderBy = StringUtils.hasText(orderBy) ? orderBy : DEFAULT_ORDER_BY;
        orderDirection = StringUtils.hasText(orderDirection) ? orderDirection : DEFAULT_ORDER_DIRECTION;
        currentPage = currentPage != null && currentPage > 0 ? currentPage : DEFAULT_CURRENT_PAGE;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        
        // 使用MyBatis-Plus分页
        Page<Category> page = new Page<>(currentPage, pageSize);
        IPage<Category> result = categoryMapper.searchCategories(page, keyword.trim(), parentId, status, 
                                                                orderBy, orderDirection);
        
        // 转换为PageResponse
        PageResponse<Category> pageResponse = convertToPageResponse(result);
        
        log.info("搜索分类完成: keyword={}, 查询到{}条记录，总计{}条", keyword, result.getRecords().size(), result.getTotal());
        return pageResponse;
    }

    // =================== 层级查询 ===================

    @Override
    public PageResponse<Category> getRootCategories(String status, Integer currentPage, Integer pageSize, 
                                                   String orderBy, String orderDirection) {
        log.info("获取根分类列表: status={}, page={}/{}", status, currentPage, pageSize);
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        orderBy = StringUtils.hasText(orderBy) ? orderBy : DEFAULT_ORDER_BY;
        orderDirection = StringUtils.hasText(orderDirection) ? orderDirection : DEFAULT_ORDER_DIRECTION;
        currentPage = currentPage != null && currentPage > 0 ? currentPage : DEFAULT_CURRENT_PAGE;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        
        // 使用MyBatis-Plus分页
        Page<Category> page = new Page<>(currentPage, pageSize);
        IPage<Category> result = categoryMapper.selectRootCategories(page, status, orderBy, orderDirection);
        
        // 转换为PageResponse
        PageResponse<Category> pageResponse = convertToPageResponse(result);
        
        log.info("获取根分类列表完成: 查询到{}条记录，总计{}条", result.getRecords().size(), result.getTotal());
        return pageResponse;
    }

    @Override
    public PageResponse<Category> getChildCategories(Long parentId, String status, 
                                                    Integer currentPage, Integer pageSize,
                                                    String orderBy, String orderDirection) {
        log.info("获取子分类列表: parentId={}, status={}, page={}/{}", 
                parentId, status, currentPage, pageSize);
        
        if (parentId == null) {
            throw new IllegalArgumentException("父分类ID不能为空");
        }
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        orderBy = StringUtils.hasText(orderBy) ? orderBy : DEFAULT_ORDER_BY;
        orderDirection = StringUtils.hasText(orderDirection) ? orderDirection : DEFAULT_ORDER_DIRECTION;
        currentPage = currentPage != null && currentPage > 0 ? currentPage : DEFAULT_CURRENT_PAGE;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        
        // 使用MyBatis-Plus分页
        Page<Category> page = new Page<>(currentPage, pageSize);
        IPage<Category> result = categoryMapper.selectCategoriesPage(page, parentId, status, orderBy, orderDirection);
        
        // 转换为PageResponse
        PageResponse<Category> pageResponse = convertToPageResponse(result);
        
        log.info("获取子分类列表完成: parentId={}, 查询到{}条记录，总计{}条", parentId, result.getRecords().size(), result.getTotal());
        return pageResponse;
    }

    @Override
    public List<Category> getCategoryTree(Long rootId, Integer maxDepth, String status,
                                         String orderBy, String orderDirection) {
        log.info("获取分类树: rootId={}, maxDepth={}, status={}", rootId, maxDepth, status);
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        orderBy = StringUtils.hasText(orderBy) ? orderBy : DEFAULT_ORDER_BY;
        orderDirection = StringUtils.hasText(orderDirection) ? orderDirection : DEFAULT_ORDER_DIRECTION;
        
        List<Category> allCategories = categoryMapper.selectCategoryTree(rootId, maxDepth, status, 
                                                                        orderBy, orderDirection);
        
        // 构建树形结构
        List<Category> tree = buildCategoryTree(allCategories, maxDepth);
        
        log.info("获取分类树完成: rootId={}, 构建{}个根节点", rootId, tree.size());
        return tree;
    }

    @Override
    public List<Category> getCategoryPath(Long categoryId) {
        log.info("获取分类路径: categoryId={}", categoryId);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        List<Category> path = categoryMapper.selectCategoryPath(categoryId);
        
        log.info("获取分类路径完成: categoryId={}, 路径长度={}", categoryId, path.size());
        return path != null ? path : new ArrayList<>();
    }

    @Override
    public List<Category> getCategoryAncestors(Long categoryId, Boolean includeInactive) {
        log.info("获取分类祖先: categoryId={}, includeInactive={}", categoryId, includeInactive);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        List<Category> ancestors = categoryMapper.selectCategoryAncestors(categoryId, includeInactive);
        
        log.info("获取分类祖先完成: categoryId={}, 祖先数量={}", categoryId, ancestors.size());
        return ancestors != null ? ancestors : new ArrayList<>();
    }

    @Override
    public List<Category> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive) {
        log.info("获取分类后代: categoryId={}, maxDepth={}, includeInactive={}", 
                categoryId, maxDepth, includeInactive);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        List<Category> descendants = categoryMapper.selectCategoryDescendants(categoryId, maxDepth, includeInactive);
        
        log.info("获取分类后代完成: categoryId={}, 后代数量={}", categoryId, descendants.size());
        return descendants != null ? descendants : new ArrayList<>();
    }

    // =================== 高级查询 ===================

    @Override
    public PageResponse<Category> getPopularCategories(Long parentId, String status, 
                                                      Integer currentPage, Integer pageSize) {
        log.info("获取热门分类: parentId={}, status={}, page={}/{}", 
                parentId, status, currentPage, pageSize);
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        currentPage = currentPage != null && currentPage > 0 ? currentPage : DEFAULT_CURRENT_PAGE;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        
        // 使用MyBatis-Plus分页
        Page<Category> page = new Page<>(currentPage, pageSize);
        IPage<Category> result = categoryMapper.selectPopularCategories(page, parentId, status);
        
        // 转换为PageResponse
        PageResponse<Category> pageResponse = convertToPageResponse(result);
        
        log.info("获取热门分类完成: 查询到{}条记录，总计{}条", result.getRecords().size(), result.getTotal());
        return pageResponse;
    }

    @Override
    public PageResponse<Category> getLeafCategories(Long parentId, String status, 
                                                   Integer currentPage, Integer pageSize) {
        log.info("获取叶子分类: parentId={}, status={}, page={}/{}", 
                parentId, status, currentPage, pageSize);
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        currentPage = currentPage != null && currentPage > 0 ? currentPage : DEFAULT_CURRENT_PAGE;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        
        // 使用MyBatis-Plus分页
        Page<Category> page = new Page<>(currentPage, pageSize);
        IPage<Category> result = categoryMapper.selectLeafCategories(page, parentId, status);
        
        // 转换为PageResponse
        PageResponse<Category> pageResponse = convertToPageResponse(result);
        
        log.info("获取叶子分类完成: 查询到{}条记录，总计{}条", result.getRecords().size(), result.getTotal());
        return pageResponse;
    }

    @Override
    public List<Category> getCategorySuggestions(String keyword, Integer limit, String status) {
        log.info("获取分类建议: keyword={}, limit={}, status={}", keyword, limit, status);
        
        if (!StringUtils.hasText(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        limit = limit != null && limit > 0 ? limit : 10;
        
        List<Category> suggestions = categoryMapper.selectCategorySuggestions(keyword.trim(), limit, status);
        
        log.info("获取分类建议完成: keyword={}, 建议数量={}", keyword, suggestions.size());
        return suggestions != null ? suggestions : new ArrayList<>();
    }

    // =================== 统计功能 ===================

    @Override
    public Long countCategories(Long parentId, String status) {
        log.info("统计分类数量: parentId={}, status={}", parentId, status);
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        
        Long count = categoryMapper.countCategories(parentId, status);
        
        log.info("统计分类数量完成: parentId={}, status={}, count={}", parentId, status, count);
        return count != null ? count : 0L;
    }

    @Override
    public Long countChildCategories(Long parentId, String status) {
        log.info("统计子分类数量: parentId={}, status={}", parentId, status);
        
        if (parentId == null) {
            throw new IllegalArgumentException("父分类ID不能为空");
        }
        
        // 设置默认值
        status = StringUtils.hasText(status) ? status : DEFAULT_STATUS;
        
        Long count = categoryMapper.countChildCategories(parentId, status);
        
        log.info("统计子分类数量完成: parentId={}, status={}, count={}", parentId, status, count);
        return count != null ? count : 0L;
    }

    @Override
    public Map<String, Object> getCategoryStatistics(Long categoryId) {
        log.info("获取分类统计信息: categoryId={}", categoryId);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        Map<String, Object> statistics = categoryMapper.selectCategoryStatistics(categoryId);
        
        log.info("获取分类统计信息完成: categoryId={}", categoryId);
        return statistics != null ? statistics : new HashMap<>();
    }

    // =================== 验证功能 ===================

    @Override
    public boolean existsCategoryName(String name, Long parentId, Long excludeId) {
        log.info("检查分类名称是否存在: name={}, parentId={}, excludeId={}", name, parentId, excludeId);
        
        if (!StringUtils.hasText(name) || parentId == null) {
            throw new IllegalArgumentException("分类名称和父分类ID不能为空");
        }
        
        boolean exists = categoryMapper.existsCategoryName(name.trim(), parentId, excludeId);
        
        log.info("检查分类名称是否存在完成: name={}, parentId={}, exists={}", name, parentId, exists);
        return exists;
    }

    // =================== 管理功能 ===================

    @Override
    @Transactional
    public Category createCategory(Category category) {
        log.info("创建分类: category={}", category);
        
        if (category == null) {
            throw new IllegalArgumentException("分类对象不能为空");
        }
        
        // 参数验证
        validateCategoryForCreate(category);
        
        // 检查名称是否重复
        if (existsCategoryName(category.getName(), category.getParentId(), null)) {
            throw new IllegalArgumentException("同级分类下已存在相同名称的分类");
        }
        
        try {
            // 初始化默认值
            initCategoryDefaults(category);
            
            // 插入数据库
            int result = categoryMapper.insert(category);
            if (result > 0) {
                log.info("创建分类成功: categoryId={}", category.getId());
                return category;
            } else {
                throw new RuntimeException("创建分类失败：数据库插入返回0");
            }
        } catch (Exception e) {
            log.error("创建分类失败: category={}", category, e);
            throw new RuntimeException("创建分类失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        log.info("更新分类: category={}", category);
        
        if (category == null || category.getId() == null) {
            throw new IllegalArgumentException("分类对象或ID不能为空");
        }
        
        // 参数验证
        validateCategoryForUpdate(category);
        
        // 检查分类是否存在
        Category existing = categoryMapper.selectById(category.getId());
        if (existing == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        
        // 检查名称是否重复（排除自己）
        if (existsCategoryName(category.getName(), category.getParentId(), category.getId())) {
            throw new IllegalArgumentException("同级分类下已存在相同名称的分类");
        }
        
        try {
            // 更新数据库
            int result = categoryMapper.updateById(category);
            if (result > 0) {
                log.info("更新分类成功: categoryId={}", category.getId());
                return categoryMapper.selectById(category.getId());
            } else {
                throw new RuntimeException("更新分类失败：数据库更新返回0");
            }
        } catch (Exception e) {
            log.error("更新分类失败: category={}", category, e);
            throw new RuntimeException("更新分类失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long categoryId) {
        log.info("删除分类: categoryId={}", categoryId);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        // 检查分类是否存在
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            log.warn("分类不存在: categoryId={}", categoryId);
            return false;
        }
        
        // 检查是否有子分类
        Long childCount = countChildCategories(categoryId, null);
        if (childCount > 0) {
            throw new IllegalArgumentException("分类存在子分类，无法删除");
        }
        
        try {
            // 删除分类
            int result = categoryMapper.deleteById(categoryId);
            if (result > 0) {
                log.info("删除分类成功: categoryId={}", categoryId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("删除分类失败: categoryId={}", categoryId, e);
            throw new RuntimeException("删除分类失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int batchUpdateStatus(List<Long> categoryIds, String status) {
        log.info("批量更新分类状态: categoryIds={}, status={}", categoryIds, status);
        
        if (CollectionUtils.isEmpty(categoryIds)) {
            throw new IllegalArgumentException("分类ID列表不能为空");
        }
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("状态不能为空");
        }
        
        try {
            int result = categoryMapper.batchUpdateStatus(categoryIds, status);
            log.info("批量更新分类状态成功: 影响行数={}", result);
            return result;
        } catch (Exception e) {
            log.error("批量更新分类状态失败: categoryIds={}, status={}", categoryIds, status, e);
            throw new RuntimeException("批量更新分类状态失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean updateContentCount(Long categoryId, Long increment) {
        log.info("更新分类内容数量: categoryId={}, increment={}", categoryId, increment);
        
        if (categoryId == null || increment == null) {
            throw new IllegalArgumentException("分类ID和增量值不能为空");
        }
        
        try {
            int result = categoryMapper.updateContentCount(categoryId, increment);
            if (result > 0) {
                log.info("更新分类内容数量成功: categoryId={}, increment={}", categoryId, increment);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("更新分类内容数量失败: categoryId={}, increment={}", categoryId, increment, e);
            throw new RuntimeException("更新分类内容数量失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public int recalculateContentCount(Long categoryId) {
        log.info("重新计算分类内容数量: categoryId={}", categoryId);
        
        try {
            int result = categoryMapper.recalculateContentCount(categoryId);
            log.info("重新计算分类内容数量成功: categoryId={}, 影响行数={}", categoryId, result);
            return result;
        } catch (Exception e) {
            log.error("重新计算分类内容数量失败: categoryId={}", categoryId, e);
            throw new RuntimeException("重新计算分类内容数量失败：" + e.getMessage(), e);
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 验证分类创建参数
     */
    private void validateCategoryForCreate(Category category) {
        if (!StringUtils.hasText(category.getName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (category.getParentId() == null) {
            throw new IllegalArgumentException("父分类ID不能为空");
        }
    }

    /**
     * 验证分类更新参数
     */
    private void validateCategoryForUpdate(Category category) {
        if (!StringUtils.hasText(category.getName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
    }

    /**
     * 初始化分类默认值
     */
    private void initCategoryDefaults(Category category) {
        if (category.getStatus() == null) {
            category.setStatus(DEFAULT_STATUS);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getContentCount() == null) {
            category.setContentCount(0L);
        }
    }

    /**
     * 构建分类树形结构
     */
    private List<Category> buildCategoryTree(List<Category> categories, Integer maxDepth) {
        if (CollectionUtils.isEmpty(categories)) {
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

    /**
     * 递归构建分类子节点
     */
    private void buildCategoryChildren(Category parent, Map<Long, List<Category>> categoryMap, 
                                     Integer maxDepth, int currentDepth) {
        if (maxDepth != null && currentDepth >= maxDepth) {
            return;
        }
        
        List<Category> children = categoryMap.get(parent.getId());
        if (!CollectionUtils.isEmpty(children)) {
            parent.initChildren();
            for (Category child : children) {
                child.setLevel(currentDepth);
                child.setParentName(parent.getName());
                parent.addChild(child);
                buildCategoryChildren(child, categoryMap, maxDepth, currentDepth + 1);
            }
        }
    }

    /**
     * 转换MyBatis-Plus分页结果为PageResponse
     */
    private PageResponse<Category> convertToPageResponse(IPage<Category> page) {
        PageResponse<Category> pageResponse = new PageResponse<>();
        pageResponse.setDatas(page.getRecords());
        pageResponse.setTotal(page.getTotal());
        pageResponse.setCurrentPage((int) page.getCurrent());
        pageResponse.setPageSize((int) page.getSize());
        pageResponse.setTotalPage((int) page.getPages());
        pageResponse.setSuccess(true);
        return pageResponse;
    }
}