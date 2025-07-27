package com.gig.collide.category.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.category.domain.repository.CategoryRepository;
import com.gig.collide.category.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 分类数据仓库实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            categoryMapper.insert(category);
            log.info("创建分类成功，ID: {}", category.getId());
        } else {
            categoryMapper.updateById(category);
            log.info("更新分类成功，ID: {}", category.getId());
        }
        return category;
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        return Optional.ofNullable(category);
    }

    @Override
    public boolean deleteById(Long categoryId) {
        int result = categoryMapper.deleteById(categoryId);
        return result > 0;
    }

    @Override
    public PageResponse<Category> findByPage(CategoryQueryRequest request) {
        Page<Category> page = new Page<>(request.getPageNo(), request.getPageSize());
        
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据父分类ID查询
        if (request.getParentId() != null) {
            queryWrapper.eq(Category::getParentId, request.getParentId());
        }
        
        // 根据分类名称模糊查询
        if (StringUtils.hasText(request.getName())) {
            queryWrapper.like(Category::getName, request.getName());
        }
        
        // 根据状态查询
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(Category::getStatus, request.getStatus());
        }
        
        // 根据层级查询
        if (request.getLevel() != null) {
            queryWrapper.eq(Category::getLevel, request.getLevel());
        }
        
        // 注意：CategoryQueryRequest 暂时不支持按创建者ID查询
        // 如果需要此功能，请在 CategoryQueryRequest 中添加 creatorId 字段
        
        // 排序
        queryWrapper.orderByAsc(Category::getSortOrder)
                   .orderByDesc(Category::getCreateTime);
        
        IPage<Category> result = categoryMapper.selectPage(page, queryWrapper);
        
        return PageResponse.of(
            result.getRecords(),
            result.getTotal(),
            (int) result.getSize(),
            (int) result.getCurrent()
        );
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    public List<Category> findHotCategories(Integer limit) {
        return categoryMapper.selectHotCategories(limit);
    }

    @Override
    public List<Category> searchByKeyword(String keyword, Integer limit) {
        return categoryMapper.searchByName(keyword, limit);
    }

    @Override
    public List<Category> findByLevel(Integer level) {
        return categoryMapper.selectByLevel(level);
    }

    @Override
    public List<Category> findRootCategories() {
        return categoryMapper.selectRootCategories();
    }

    @Override
    public boolean existsByName(Long parentId, String name, Long excludeId) {
        int count = categoryMapper.checkNameExists(parentId, name, excludeId);
        return count > 0;
    }

    @Override
    public boolean updateContentCount(Long categoryId, Long delta) {
        int result = categoryMapper.updateContentCount(categoryId, delta);
        return result > 0;
    }

    @Override
    public boolean batchUpdateStatus(List<Long> categoryIds, String status) {
        int result = categoryMapper.updateStatusBatch(categoryIds, status);
        return result > 0;
    }

    @Override
    public String getCategoryPath(Long categoryId) {
        return categoryMapper.selectCategoryPath(categoryId);
    }

    @Override
    public List<Long> getAllChildrenIds(Long parentId) {
        // 应用层递归实现，避免数据库JOIN
        return getAllChildrenIdsRecursive(parentId);
    }
    
    /**
     * 递归获取所有子分类ID（应用层实现，严格避免JOIN）
     */
    private List<Long> getAllChildrenIdsRecursive(Long parentId) {
        List<Long> result = new ArrayList<>();
        
        // 获取直接子分类
        List<Long> directChildren = categoryMapper.selectAllChildrenIds(parentId);
        result.addAll(directChildren);
        
        // 递归获取子分类的子分类
        for (Long childId : directChildren) {
            result.addAll(getAllChildrenIdsRecursive(childId));
        }
        
        return result;
    }

    @Override
    public Long countByStatus(String status) {
        return categoryMapper.countCategories(status);
    }

    @Override
    public PageResponse<Category> findByCreatorId(Long creatorId, int pageNo, int pageSize) {
        long offset = (long) (pageNo - 1) * pageSize;
        List<Category> categories = categoryMapper.selectByCreatorId(creatorId, offset, pageSize);
        
        // 获取总数
        LambdaQueryWrapper<Category> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Category::getCreatorId, creatorId);
        Long total = categoryMapper.selectCount(countWrapper);
        
        return PageResponse.of(categories, total, pageSize, pageNo);
    }

    // ================================ 幂等性操作方法 ================================

    @Override
    public boolean updateCategoryIdempotent(Long categoryId, 
                                           String name, 
                                           String description,
                                           String iconUrl, 
                                           String coverUrl,
                                           Integer sortOrder,
                                           Long lastModifierId,
                                           String lastModifierName,
                                           Integer expectedVersion) {
        log.info("幂等性更新分类信息，分类ID：{}，版本：{}，修改者：{}", categoryId, expectedVersion, lastModifierName);
        
        int result = categoryMapper.updateCategoryIdempotent(
            categoryId, name, description, iconUrl, coverUrl, sortOrder, 
            lastModifierId, lastModifierName, expectedVersion
        );
        
        boolean success = result > 0;
        if (success) {
            log.info("幂等性更新分类信息成功，分类ID：{}", categoryId);
        } else {
            log.warn("幂等性更新分类信息失败，可能版本冲突，分类ID：{}，期望版本：{}", categoryId, expectedVersion);
        }
        
        return success;
    }

    @Override
    public boolean updateStatusIdempotent(Long categoryId, 
                                        String expectedStatus, 
                                        String newStatus, 
                                        Integer expectedVersion) {
        log.info("幂等性更新分类状态，分类ID：{}，期望状态：{}，新状态：{}，版本：{}", 
                categoryId, expectedStatus, newStatus, expectedVersion);
        
        int result = categoryMapper.updateStatusIdempotent(
            categoryId, expectedStatus, newStatus, expectedVersion
        );
        
        boolean success = result > 0;
        if (success) {
            log.info("幂等性更新分类状态成功，分类ID：{}", categoryId);
        } else {
            log.warn("幂等性更新分类状态失败，可能版本冲突，分类ID：{}，期望版本：{}", categoryId, expectedVersion);
        }
        
        return success;
    }
} 