package com.gig.collide.api.category.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 分类更新请求 - 简洁版
 * 支持分类信息、状态和统计数据更新
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryUpdateRequest {

    /**
     * 分类ID（必填）
     */
    @NotNull(message = "分类ID不能为空")
    private Long id;

    /**
     * 分类名称
     */
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    /**
     * 分类描述
     */
    @Size(max = 1000, message = "分类描述长度不能超过1000个字符")
    private String description;

    /**
     * 父分类ID
     */
    @Min(value = 0, message = "父分类ID不能小于0")
    private Long parentId;

    /**
     * 分类图标URL
     */
    @Size(max = 500, message = "分类图标URL长度不能超过500个字符")
    private String iconUrl;

    /**
     * 排序值
     */
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 99999, message = "排序值不能超过99999")
    private Integer sort;

    /**
     * 内容数量（用于统计数据更新）
     */
    @Min(value = 0, message = "内容数量不能小于0")
    private Long contentCount;

    /**
     * 状态：active、inactive
     */
    private String status;

    /**
     * 操作人ID（用于权限验证）
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;

    /**
     * 更新类型：info、status、sort、stats、parent
     */
    private String updateType;

    // =================== 业务方法 ===================

    /**
     * 是否更新基本信息
     */
    public boolean isUpdateInfo() {
        return "info".equals(updateType) || name != null || description != null || iconUrl != null;
    }

    /**
     * 是否更新状态
     */
    public boolean isUpdateStatus() {
        return "status".equals(updateType) || status != null;
    }

    /**
     * 是否更新排序
     */
    public boolean isUpdateSort() {
        return "sort".equals(updateType) || sort != null;
    }

    /**
     * 是否更新统计
     */
    public boolean isUpdateStats() {
        return "stats".equals(updateType) || contentCount != null;
    }

    /**
     * 是否移动分类（更新父分类）
     */
    public boolean isMoveCategory() {
        return "parent".equals(updateType) || parentId != null;
    }

    /**
     * 验证状态值
     */
    public boolean isValidStatus() {
        if (status == null) {
            return true;
        }
        return "active".equals(status) || "inactive".equals(status);
    }

    /**
     * 是否有效的更新请求
     */
    public boolean isValidUpdate() {
        return name != null || description != null || parentId != null 
               || iconUrl != null || sort != null || contentCount != null || status != null;
    }

    /**
     * 获取分类名称长度
     */
    public int getNameLength() {
        return name != null ? name.trim().length() : 0;
    }

    /**
     * 是否为根分类
     */
    public boolean isRootCategory() {
        return parentId != null && parentId == 0;
    }

    /**
     * 是否有描述
     */
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    /**
     * 是否有图标
     */
    public boolean hasIcon() {
        return iconUrl != null && !iconUrl.trim().isEmpty();
    }

    /**
     * 清理字段值
     */
    public void trimFields() {
        if (name != null) {
            name = name.trim();
        }
        if (description != null) {
            description = description.trim();
        }
        if (iconUrl != null) {
            iconUrl = iconUrl.trim();
        }
        if (status != null) {
            status = status.trim();
        }
    }
}