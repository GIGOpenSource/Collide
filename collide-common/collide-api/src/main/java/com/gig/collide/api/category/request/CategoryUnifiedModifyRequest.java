package com.gig.collide.api.category.request;

import com.gig.collide.api.category.enums.CategoryStatusEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 分类修改请求
 * 用于修改已存在的分类
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUnifiedModifyRequest extends BaseRequest {

    /**
     * 分类ID（必填）
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 操作用户ID（必填）
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorId;

    /**
     * 操作用户名称（可选，如果不提供会自动查询）
     */
    @Size(max = 50, message = "操作用户名称长度不能超过50个字符")
    private String operatorName;

    /**
     * 版本号（乐观锁，可选）
     */
    private Integer version;

    /**
     * 分类名称（可选）
     */
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_\\.\\(\\)]+$", 
             message = "分类名称只能包含中文、英文、数字和常用符号")
    private String name;

    /**
     * 分类描述（可选）
     */
    @Size(max = 500, message = "分类描述长度不能超过500个字符")
    private String description;

    /**
     * 父分类ID（可选）
     */
    @Min(value = 0, message = "父分类ID不能小于0")
    private Long parentId;

    /**
     * 分类图标URL（可选）
     */
    @Size(max = 255, message = "分类图标URL长度不能超过255个字符")
    private String iconUrl;

    /**
     * 分类封面URL（可选）
     */
    @Size(max = 255, message = "分类封面URL长度不能超过255个字符")
    private String coverUrl;

    /**
     * 排序顺序（可选）
     */
    @Min(value = 0, message = "排序顺序不能小于0")
    @Max(value = 99999, message = "排序顺序不能超过99999")
    private Integer sortOrder;

    /**
     * 分类状态（可选）
     */
    private CategoryStatusEnum status;

    /**
     * 修改原因/备注（可选）
     */
    @Size(max = 200, message = "修改原因长度不能超过200个字符")
    private String modifyReason;

    // ===================== 便捷构造器 =====================

    /**
     * 修改基本信息
     */
    public static CategoryUnifiedModifyRequest basicInfo(Long categoryId, Long operatorId, 
                                                        String name, String description) {
        CategoryUnifiedModifyRequest request = new CategoryUnifiedModifyRequest();
        request.setCategoryId(categoryId);
        request.setOperatorId(operatorId);
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    /**
     * 修改分类状态
     */
    public static CategoryUnifiedModifyRequest status(Long categoryId, Long operatorId, 
                                                     CategoryStatusEnum status, String reason) {
        CategoryUnifiedModifyRequest request = new CategoryUnifiedModifyRequest();
        request.setCategoryId(categoryId);
        request.setOperatorId(operatorId);
        request.setStatus(status);
        request.setModifyReason(reason);
        return request;
    }

    /**
     * 移动分类
     */
    public static CategoryUnifiedModifyRequest move(Long categoryId, Long operatorId, 
                                                   Long newParentId, String reason) {
        CategoryUnifiedModifyRequest request = new CategoryUnifiedModifyRequest();
        request.setCategoryId(categoryId);
        request.setOperatorId(operatorId);
        request.setParentId(newParentId);
        request.setModifyReason(reason);
        return request;
    }

    /**
     * 修改排序
     */
    public static CategoryUnifiedModifyRequest sort(Long categoryId, Long operatorId, 
                                                   Integer sortOrder, String reason) {
        CategoryUnifiedModifyRequest request = new CategoryUnifiedModifyRequest();
        request.setCategoryId(categoryId);
        request.setOperatorId(operatorId);
        request.setSortOrder(sortOrder);
        request.setModifyReason(reason);
        return request;
    }

    /**
     * 修改图标和封面
     */
    public static CategoryUnifiedModifyRequest media(Long categoryId, Long operatorId, 
                                                    String iconUrl, String coverUrl) {
        CategoryUnifiedModifyRequest request = new CategoryUnifiedModifyRequest();
        request.setCategoryId(categoryId);
        request.setOperatorId(operatorId);
        request.setIconUrl(iconUrl);
        request.setCoverUrl(coverUrl);
        return request;
    }

    /**
     * 启用分类
     */
    public static CategoryUnifiedModifyRequest enable(Long categoryId, Long operatorId, String reason) {
        return status(categoryId, operatorId, CategoryStatusEnum.ACTIVE, reason);
    }

    /**
     * 禁用分类
     */
    public static CategoryUnifiedModifyRequest disable(Long categoryId, Long operatorId, String reason) {
        return status(categoryId, operatorId, CategoryStatusEnum.INACTIVE, reason);
    }

    /**
     * 完整修改
     */
    public static CategoryUnifiedModifyRequest fullModify(Long categoryId, Long operatorId, Integer version,
                                                         String name, String description, Long parentId,
                                                         String iconUrl, String coverUrl, Integer sortOrder,
                                                         CategoryStatusEnum status, String modifyReason) {
        CategoryUnifiedModifyRequest request = new CategoryUnifiedModifyRequest();
        request.setCategoryId(categoryId);
        request.setOperatorId(operatorId);
        request.setVersion(version);
        request.setName(name);
        request.setDescription(description);
        request.setParentId(parentId);
        request.setIconUrl(iconUrl);
        request.setCoverUrl(coverUrl);
        request.setSortOrder(sortOrder);
        request.setStatus(status);
        request.setModifyReason(modifyReason);
        return request;
    }

    // ===================== 链式设置方法 =====================

    /**
     * 设置版本号
     */
    public CategoryUnifiedModifyRequest withVersion(Integer version) {
        this.version = version;
        return this;
    }

    /**
     * 设置名称
     */
    public CategoryUnifiedModifyRequest withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 设置描述
     */
    public CategoryUnifiedModifyRequest withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * 设置父分类
     */
    public CategoryUnifiedModifyRequest withParent(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * 设置图标
     */
    public CategoryUnifiedModifyRequest withIcon(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    /**
     * 设置封面
     */
    public CategoryUnifiedModifyRequest withCover(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    /**
     * 设置排序
     */
    public CategoryUnifiedModifyRequest withSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * 设置状态
     */
    public CategoryUnifiedModifyRequest withStatus(CategoryStatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * 设置操作者名称
     */
    public CategoryUnifiedModifyRequest withOperatorName(String operatorName) {
        this.operatorName = operatorName;
        return this;
    }

    /**
     * 设置修改原因
     */
    public CategoryUnifiedModifyRequest withReason(String modifyReason) {
        this.modifyReason = modifyReason;
        return this;
    }

    // ===================== 验证方法 =====================

    /**
     * 判断是否为移动到根分类
     */
    public boolean isMoveToRoot() {
        return parentId != null && parentId == 0L;
    }

    /**
     * 判断是否修改了名称
     */
    public boolean isNameChanged() {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * 判断是否修改了状态
     */
    public boolean isStatusChanged() {
        return status != null;
    }

    /**
     * 判断是否为移动操作
     */
    public boolean isMoveOperation() {
        return parentId != null;
    }

    /**
     * 判断是否为排序操作
     */
    public boolean isSortOperation() {
        return sortOrder != null;
    }

    /**
     * 获取规范化的分类名称（去除首尾空格）
     */
    public String getNormalizedName() {
        return name != null ? name.trim() : null;
    }

    /**
     * 获取规范化的分类描述（去除首尾空格）
     */
    public String getNormalizedDescription() {
        return description != null ? description.trim() : null;
    }

    /**
     * 验证分类名称格式
     */
    public boolean isValidName() {
        if (name == null || name.trim().isEmpty()) {
            return true; // 可选字段，为空时认为有效
        }
        return name.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_\\.\\(\\)]+$");
    }
} 