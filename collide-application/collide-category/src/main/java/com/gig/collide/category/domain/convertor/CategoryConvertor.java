package com.gig.collide.category.domain.convertor;

import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.data.CategoryInfo;
import com.gig.collide.api.category.response.data.CategoryTree;
import com.gig.collide.category.domain.entity.Category;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类数据转换器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public class CategoryConvertor {

    /**
     * 创建请求转实体 - 严格去连表化
     */
    public static Category toEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .parentId(request.getParentId() != null ? request.getParentId() : 0L)
                .name(request.getName())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .coverUrl(request.getCoverUrl())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .level(calculateLevel(request.getParentId()))
                .path(buildPath(request.getParentId(), request.getName()))
                .contentCount(0L)
                .status(Category.Status.ACTIVE)
                .version(1)
                .creatorId(request.getCreatorId()) // 创建者ID（去连表化）
                .creatorName(request.getCreatorName()) // 创建者名称（冗余存储，避免连表）
                .build();
    }

    /**
     * 更新请求转实体（仅包含要更新的字段）
     */
    public static void updateEntity(Category category, CategoryUpdateRequest request) {
        if (request == null || category == null) {
            return;
        }

        if (StringUtils.hasText(request.getName())) {
            category.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            category.setDescription(request.getDescription());
        }
        if (StringUtils.hasText(request.getIconUrl())) {
            category.setIconUrl(request.getIconUrl());
        }
        if (StringUtils.hasText(request.getCoverUrl())) {
            category.setCoverUrl(request.getCoverUrl());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        if (StringUtils.hasText(request.getStatus())) {
            category.setStatus(request.getStatus());
        }
        
        // 更新修改者信息（去连表化）
        if (request.getLastModifierId() != null) {
            category.setLastModifierId(request.getLastModifierId());
        }
        if (StringUtils.hasText(request.getLastModifierName())) {
            category.setLastModifierName(request.getLastModifierName());
        }
    }

    /**
     * 实体转CategoryInfo - 严格去连表化
     */
    public static CategoryInfo toInfo(Category category) {
        if (category == null) {
            return null;
        }

        CategoryInfo info = new CategoryInfo();
        info.setCategoryId(category.getId());
        info.setParentId(category.getParentId());
        info.setName(category.getName());
        info.setDescription(category.getDescription());
        info.setIconUrl(category.getIconUrl());
        info.setCoverUrl(category.getCoverUrl());
        info.setSortOrder(category.getSortOrder());
        info.setLevel(category.getLevel());
        info.setPath(category.getPath());
        info.setContentCount(category.getContentCount());
        info.setStatus(category.getStatus());
        
        // 去连表化字段（已恢复）
        info.setVersion(category.getVersion());
        info.setCreatorId(category.getCreatorId());
        info.setCreatorName(category.getCreatorName());
        info.setLastModifierId(category.getLastModifierId());
        info.setLastModifierName(category.getLastModifierName());
        
        info.setCreateTime(category.getCreateTime());
        info.setUpdateTime(category.getUpdateTime());
        
        return info;
    }

    /**
     * 实体列表转CategoryInfo列表
     */
    public static List<CategoryInfo> toInfoList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(CategoryConvertor::toInfo)
                .collect(Collectors.toList());
    }

    /**
     * 实体转CategoryTree
     */
    public static CategoryTree toTree(Category category) {
        if (category == null) {
            return null;
        }

        CategoryTree tree = new CategoryTree();
        tree.setCategoryId(category.getId());
        tree.setParentId(category.getParentId());
        tree.setName(category.getName());
        tree.setDescription(category.getDescription());
        tree.setIconUrl(category.getIconUrl());
        tree.setCoverUrl(category.getCoverUrl());
        tree.setSortOrder(category.getSortOrder());
        tree.setLevel(category.getLevel());
        tree.setPath(category.getPath());
        tree.setContentCount(category.getContentCount());
        tree.setStatus(category.getStatus());
        tree.setCreateTime(category.getCreateTime());
        tree.setUpdateTime(category.getUpdateTime());

        // 转换子分类
        if (category.getChildren() != null) {
            tree.setChildren(category.getChildren().stream()
                    .map(CategoryConvertor::toTree)
                    .collect(Collectors.toList()));
        }

        return tree;
    }

    /**
     * 实体列表转CategoryTree列表
     */
    public static List<CategoryTree> toTreeList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(CategoryConvertor::toTree)
                .collect(Collectors.toList());
    }

    /**
     * 构建分类路径
     */
    private static String buildPath(Long parentId, String name) {
        if (parentId == null || parentId == 0L) {
            return "/" + name;
        }
        // 这里简化处理，实际应该查询父分类的完整路径
        return "/parent/" + name;
    }

    /**
     * 计算分类层级
     */
    private static Integer calculateLevel(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return 1;
        }
        // 这里简化处理，实际应该查询父分类的层级并+1
        return 2;
    }
} 