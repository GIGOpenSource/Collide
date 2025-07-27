package com.gig.collide.business.domain.category.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.business.domain.category.entity.Category;
import com.gig.collide.business.infrastructure.mapper.CategoryMapper;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.BizErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类领域服务
 * 
 * @author collide
 * @date 2024/12/19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryDomainService {
    
    private final CategoryMapper categoryMapper;
    
    /**
     * 创建分类
     */
    @Transactional(rollbackFor = Exception.class)
    public Category createCategory(String name, String description, Long parentId, 
                                  String iconUrl, String coverUrl, Integer sortOrder) {
        
        // 检查分类名称是否已存在（同一父级下）
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name)
               .eq(Category::getParentId, parentId != null ? parentId : 0)
               .eq(Category::getStatus, "active");
        
        Category existingCategory = categoryMapper.selectOne(wrapper);
        if (existingCategory != null) {
            throw new BizException("该层级下分类名称已存在", BizErrorCode.DUPLICATED);
        }
        
        // 确定层级和路径
        Integer level = 1;
        String path = "";
        
        if (parentId != null && parentId > 0) {
            Category parentCategory = getCategoryById(parentId);
            level = parentCategory.getLevel() + 1;
            path = parentCategory.getPath() + "/" + parentId;
            
            // 检查层级深度（限制最多5层）
            if (level > 5) {
                throw new BizException("分类层级不能超过5层", BizErrorCode.DUPLICATED);
            }
        } else {
            parentId = 0L;
            path = "/0";
        }
        
        // 创建新分类
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(name);
        category.setDescription(description);
        category.setIconUrl(iconUrl);
        category.setCoverUrl(coverUrl);
        category.setSortOrder(sortOrder != null ? sortOrder : 0);
        category.setLevel(level);
        category.setPath(path);
        category.setContentCount(0L);
        category.setStatus("active");
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.insert(category);
        
        // 更新路径（包含自己的ID）
        category.setPath(path + "/" + category.getId());
        categoryMapper.updateById(category);
        
        log.info("创建分类成功，分类ID：{}，名称：{}，层级：{}", category.getId(), category.getName(), level);
        
        return category;
    }
    
    /**
     * 创建分类（支持状态参数）
     */
    @Transactional(rollbackFor = Exception.class)
    public Category createCategory(String name, String description, Long parentId, 
                                  String iconUrl, Integer sort, Integer status) {
        
        // 转换状态
        String statusStr = status != null && status == 1 ? "active" : "inactive";
        
        // 检查分类名称是否已存在（同一父级下）
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name)
               .eq(Category::getParentId, parentId != null ? parentId : 0)
               .eq(Category::getStatus, "active");
        
        Category existingCategory = categoryMapper.selectOne(wrapper);
        if (existingCategory != null) {
            throw new BizException("该层级下分类名称已存在", BizErrorCode.DUPLICATED);
        }
        
        // 确定层级和路径
        Integer level = 1;
        String path = "";
        
        if (parentId != null && parentId > 0) {
            Category parentCategory = getCategoryById(parentId);
            level = parentCategory.getLevel() + 1;
            path = parentCategory.getPath() + "/" + parentId;
            
            // 检查层级深度（限制最多5层）
            if (level > 5) {
                throw new BizException("分类层级不能超过5层", BizErrorCode.DUPLICATED);
            }
        } else {
            parentId = 0L;
            path = "/0";
        }
        
        // 创建新分类
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(name);
        category.setDescription(description);
        category.setIconUrl(iconUrl);
        category.setSortOrder(sort != null ? sort : 0);
        category.setLevel(level);
        category.setPath(path);
        category.setContentCount(0L);
        category.setStatus(statusStr);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.insert(category);
        
        // 更新路径（包含自己的ID）
        category.setPath(path + "/" + category.getId());
        categoryMapper.updateById(category);
        
        log.info("创建分类成功，分类ID：{}，名称：{}，层级：{}", category.getId(), category.getName(), level);
        
        return category;
    }
    
    /**
     * 更新分类信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(Long categoryId, String name, String description, 
                                  String iconUrl, String coverUrl, Integer sortOrder) {
        
        Category category = getCategoryById(categoryId);
        
        // 如果修改了名称，检查是否重复
        if (StringUtils.hasText(name) && !name.equals(category.getName())) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getName, name)
                   .eq(Category::getParentId, category.getParentId())
                   .eq(Category::getStatus, "active")
                   .ne(Category::getId, categoryId);
            
            Category existingCategory = categoryMapper.selectOne(wrapper);
            if (existingCategory != null) {
                throw new BizException("该层级下分类名称已存在", BizErrorCode.DUPLICATED);
            }
            category.setName(name);
        }
        
        if (StringUtils.hasText(description)) {
            category.setDescription(description);
        }
        if (StringUtils.hasText(iconUrl)) {
            category.setIconUrl(iconUrl);
        }
        if (StringUtils.hasText(coverUrl)) {
            category.setCoverUrl(coverUrl);
        }
        if (sortOrder != null) {
            category.setSortOrder(sortOrder);
        }
        
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
        
        log.info("更新分类成功，分类ID：{}", categoryId);
        return category;
    }
    
    /**
     * 更新分类信息（支持状态参数）
     */
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(Long categoryId, String name, String description, 
                                  String iconUrl, Integer sort, String status) {
        
        Category category = getCategoryById(categoryId);
        
        // 如果修改了名称，检查是否重复
        if (StringUtils.hasText(name) && !name.equals(category.getName())) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getName, name)
                   .eq(Category::getParentId, category.getParentId())
                   .eq(Category::getStatus, "active")
                   .ne(Category::getId, categoryId);
            
            Category existingCategory = categoryMapper.selectOne(wrapper);
            if (existingCategory != null) {
                throw new BizException("该层级下分类名称已存在", BizErrorCode.DUPLICATED);
            }
            category.setName(name);
        }
        
        if (StringUtils.hasText(description)) {
            category.setDescription(description);
        }
        if (StringUtils.hasText(iconUrl)) {
            category.setIconUrl(iconUrl);
        }
        if (sort != null) {
            category.setSortOrder(sort);
        }
        if (StringUtils.hasText(status)) {
            category.setStatus(status);
        }
        
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
        
        log.info("更新分类成功，分类ID：{}", categoryId);
        return category;
    }
    
    /**
     * 查询分类列表（支持多参数）
     */
    public Page<Category> queryCategories(String name, Integer status, String statusStr,
                                         Integer pageNo, Integer pageSize) {
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(Category::getName, name);
        }
        if (StringUtils.hasText(statusStr)) {
            wrapper.eq(Category::getStatus, statusStr);
        } else {
            wrapper.eq(Category::getStatus, "active");
        }
        
        wrapper.orderByAsc(Category::getSortOrder)
               .orderByDesc(Category::getCreateTime);
        
        Page<Category> page = new Page<>(pageNo != null ? pageNo : 1, pageSize != null ? pageSize : 10);
        return categoryMapper.selectPage(page, wrapper);
    }
    
    /**
     * 获取分类树（无参数版本）
     */
    public List<Category> getCategoryTree() {
        return getCategoryTree(null);
    }
    
    /**
     * 获取分类树（带根节点ID）
     */
    public List<Category> getCategoryTree(Long rootId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, "active");
        
        if (rootId != null && rootId > 0) {
            wrapper.eq(Category::getParentId, rootId);
        } else {
            wrapper.eq(Category::getParentId, 0);
        }
        
        wrapper.orderByAsc(Category::getSortOrder)
               .orderByDesc(Category::getCreateTime);
        
        return categoryMapper.selectList(wrapper);
    }
    
    /**
     * 搜索分类（完整参数版本）
     */
    public List<Category> searchCategories(String keyword, Integer limit, Integer offset) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, "active");
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Category::getName, keyword);
        }
        
        wrapper.orderByAsc(Category::getSortOrder)
               .orderByDesc(Category::getCreateTime);
        
        if (limit != null && offset != null) {
            wrapper.last("LIMIT " + offset + ", " + limit);
        } else if (limit != null) {
            wrapper.last("LIMIT " + limit);
        }
        
        return categoryMapper.selectList(wrapper);
    }
    
    /**
     * 搜索分类（简化版本）
     */
    public List<Category> searchCategories(String keyword) {
        return searchCategories(keyword, 20, null);
    }
    
    /**
     * 获取热门分类（完整参数版本）
     */
    public List<Category> getHotCategories(Integer limit, Integer offset) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, "active")
               .orderByDesc(Category::getContentCount)
               .orderByAsc(Category::getSortOrder);
        
        if (limit != null && offset != null) {
            wrapper.last("LIMIT " + offset + ", " + limit);
        } else if (limit != null) {
            wrapper.last("LIMIT " + limit);
        }
        
        return categoryMapper.selectList(wrapper);
    }
    
    /**
     * 获取热门分类（简化版本）
     */
    public List<Category> getHotCategories(Integer limit) {
        return getHotCategories(limit, null);
    }
    
    /**
     * 根据ID获取分类
     */
    public Category getCategoryById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || "inactive".equals(category.getStatus())) {
            throw new BizException("分类不存在", BizErrorCode.DUPLICATED);
        }
        return category;
    }
    
    /**
     * 获取所有根分类
     */
    public List<Category> getRootCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, 0)
               .eq(Category::getStatus, "active")
               .orderByAsc(Category::getSortOrder)
               .orderByDesc(Category::getContentCount)
               .orderByAsc(Category::getCreateTime);
        
        return categoryMapper.selectList(wrapper);
    }
    
    /**
     * 获取子分类
     */
    public List<Category> getChildCategories(Long parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId != null ? parentId : 0)
               .eq(Category::getStatus, "active")
               .orderByAsc(Category::getSortOrder)
               .orderByDesc(Category::getContentCount)
               .orderByAsc(Category::getCreateTime);
        
        return categoryMapper.selectList(wrapper);
    }
    

    
    /**
     * 获取分类路径（从根到当前分类）
     */
    public List<Category> getCategoryPath(Long categoryId) {
        Category category = getCategoryById(categoryId);
        
        if (category.getLevel() == 1) {
            return List.of(category);
        }
        
        // 解析路径获取所有父级ID
        String[] pathParts = category.getPath().split("/");
        List<Long> pathIds = new ArrayList<>();
        
        for (String part : pathParts) {
            if (StringUtils.hasText(part) && !"0".equals(part)) {
                pathIds.add(Long.valueOf(part));
            }
        }
        
        if (CollectionUtils.isEmpty(pathIds)) {
            return List.of(category);
        }
        
        // 获取路径上的所有分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Category::getId, pathIds)
               .eq(Category::getStatus, "active")
               .orderByAsc(Category::getLevel);
        
        List<Category> pathCategories = categoryMapper.selectList(wrapper);
        
        // 添加当前分类（如果不在路径中）
        boolean currentCategoryInPath = pathCategories.stream()
            .anyMatch(c -> c.getId().equals(categoryId));
        
        if (!currentCategoryInPath) {
            pathCategories.add(category);
        }
        
        return pathCategories;
    }
    
    /**
     * 分页查询分类
     */
    public Page<Category> queryCategories(Integer pageNo, Integer pageSize, String name, 
                                         Long parentId, Integer level, String status) {
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(Category::getName, name);
        }
        if (parentId != null) {
            wrapper.eq(Category::getParentId, parentId);
        }
        if (level != null) {
            wrapper.eq(Category::getLevel, level);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Category::getStatus, status);
        } else {
            wrapper.eq(Category::getStatus, "active");
        }
        
        wrapper.orderByAsc(Category::getLevel)
               .orderByAsc(Category::getSortOrder)
               .orderByDesc(Category::getContentCount)
               .orderByAsc(Category::getCreateTime);
        
        Page<Category> page = new Page<>(pageNo, pageSize);
        return categoryMapper.selectPage(page, wrapper);
    }
    
    /**
     * 批量获取分类
     */
    public List<Category> getCategoriesByIds(List<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Category::getId, categoryIds)
               .eq(Category::getStatus, "active");
        
        return categoryMapper.selectList(wrapper);
    }
    
    /**
     * 增加分类内容数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void incrementContentCount(Long categoryId) {
        Category category = getCategoryById(categoryId);
        category.setContentCount(category.getContentCount() + 1);
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.updateById(category);
        
        // 递归更新父级分类的内容数量
        if (category.getParentId() != null && category.getParentId() > 0) {
            incrementContentCount(category.getParentId());
        }
        
        log.info("分类内容数量增加，分类ID：{}，当前数量：{}", categoryId, category.getContentCount());
    }
    
    /**
     * 减少分类内容数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void decrementContentCount(Long categoryId) {
        Category category = getCategoryById(categoryId);
        if (category.getContentCount() > 0) {
            category.setContentCount(category.getContentCount() - 1);
            category.setUpdateTime(LocalDateTime.now());
            
            categoryMapper.updateById(category);
            
            // 递归更新父级分类的内容数量
            if (category.getParentId() != null && category.getParentId() > 0) {
                decrementContentCount(category.getParentId());
            }
            
            log.info("分类内容数量减少，分类ID：{}，当前数量：{}", categoryId, category.getContentCount());
        }
    }
    
    /**
     * 移动分类（更改父级）
     */
    @Transactional(rollbackFor = Exception.class)
    public void moveCategory(Long categoryId, Long newParentId) {
        Category category = getCategoryById(categoryId);
        
        // 不能移动到自己的子级下
        if (newParentId != null && newParentId > 0) {
            Category newParent = getCategoryById(newParentId);
            if (newParent.getPath().contains("/" + categoryId + "/")) {
                throw new BizException("不能移动到自己的子分类下", BizErrorCode.DUPLICATED);
            }
            
            // 检查新的层级深度
            if (newParent.getLevel() >= 5) {
                throw new BizException("移动后层级将超过限制", BizErrorCode.DUPLICATED);
            }
        }
        
        Long oldParentId = category.getParentId();
        category.setParentId(newParentId != null ? newParentId : 0);
        
        // 重新计算层级和路径
        updateCategoryHierarchy(category);
        
        log.info("移动分类成功，分类ID：{}，从{}移动到{}", categoryId, oldParentId, newParentId);
    }
    
    /**
     * 更新分类层级信息
     */
    private void updateCategoryHierarchy(Category category) {
        Integer level = 1;
        String path = "/0";
        
        if (category.getParentId() != null && category.getParentId() > 0) {
            Category parentCategory = getCategoryById(category.getParentId());
            level = parentCategory.getLevel() + 1;
            path = parentCategory.getPath();
        }
        
        category.setLevel(level);
        category.setPath(path + "/" + category.getId());
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.updateById(category);
        
        // 递归更新所有子分类
        List<Category> children = getChildCategories(category.getId());
        for (Category child : children) {
            updateCategoryHierarchy(child);
        }
    }
    
    /**
     * 删除分类（软删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        
        // 检查是否有子分类
        List<Category> children = getChildCategories(categoryId);
        if (!CollectionUtils.isEmpty(children)) {
            throw new BizException("存在子分类，不能删除", BizErrorCode.DUPLICATED);
        }
        
        // 检查是否有内容
        if (category.getContentCount() > 0) {
            throw new BizException("分类下存在内容，不能删除", BizErrorCode.DUPLICATED);
        }
        
        category.setStatus("inactive");
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.updateById(category);
        log.info("删除分类成功，分类ID：{}", categoryId);
    }
    

} 