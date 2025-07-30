package com.gig.collide.api.category.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分类创建请求 - 简洁版
 * 基于category-simple.sql的设计，支持层级分类
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
public class CategoryCreateRequest implements Serializable {

    /**
     * 分类名称（必填）
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    /**
     * 分类描述（可选）
     */
    @Size(max = 1000, message = "分类描述长度不能超过1000个字符")
    private String description;

    /**
     * 父分类ID（必填，0表示根分类）
     */
    @NotNull(message = "父分类ID不能为空")
    @Min(value = 0, message = "父分类ID不能小于0")
    private Long parentId = 0L;

    /**
     * 分类图标URL（可选）
     */
    @Size(max = 500, message = "分类图标URL长度不能超过500个字符")
    private String iconUrl;

    /**
     * 排序值（可选）
     */
    @Min(value = 0, message = "排序值不能小于0")
    @Max(value = 99999, message = "排序值不能超过99999")
    private Integer sort = 0;

    /**
     * 状态（默认为active）
     */
    private String status = "active";

    // =================== 业务方法 ===================

    /**
     * 是否为根分类
     */
    public boolean isRootCategory() {
        return parentId == null || parentId == 0;
    }

    /**
     * 是否为子分类
     */
    public boolean isChildCategory() {
        return !isRootCategory();
    }

    /**
     * 验证状态值
     */
    public boolean isValidStatus() {
        return "active".equals(status) || "inactive".equals(status);
    }

    /**
     * 获取分类名称长度
     */
    public int getNameLength() {
        return name != null ? name.trim().length() : 0;
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
    }

    /**
     * 设置默认值
     */
    public void setDefaults() {
        if (parentId == null) {
            parentId = 0L;
        }
        if (sort == null) {
            sort = 0;
        }
        if (status == null || status.trim().isEmpty()) {
            status = "active";
        }
    }
}