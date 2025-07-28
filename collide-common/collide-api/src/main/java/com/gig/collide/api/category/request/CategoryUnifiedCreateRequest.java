package com.gig.collide.api.category.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 分类创建请求
 * 用于创建新的分类
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
public class CategoryUnifiedCreateRequest extends BaseRequest {

    /**
     * 分类名称（必填）
     */
    @NotBlank(message = "分类名称不能为空")
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
     * 父分类ID（必填，0表示根分类）
     */
    @NotNull(message = "父分类ID不能为空")
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
     * 创建者ID（必填）
     */
    @NotNull(message = "创建者ID不能为空")
    private Long creatorId;

    /**
     * 创建者名称（可选，如果不提供会自动查询）
     */
    @Size(max = 50, message = "创建者名称长度不能超过50个字符")
    private String creatorName;

    /**
     * 创建原因/备注（可选）
     */
    @Size(max = 200, message = "创建原因长度不能超过200个字符")
    private String createReason;

    // ===================== 便捷构造器 =====================

    /**
     * 创建根分类
     */
    public static CategoryUnifiedCreateRequest rootCategory(String name, String description, Long creatorId) {
        CategoryUnifiedCreateRequest request = new CategoryUnifiedCreateRequest();
        request.setName(name);
        request.setDescription(description);
        request.setParentId(0L);
        request.setCreatorId(creatorId);
        request.setSortOrder(0);
        return request;
    }

    /**
     * 创建子分类
     */
    public static CategoryUnifiedCreateRequest subCategory(String name, String description, 
                                                           Long parentId, Long creatorId) {
        CategoryUnifiedCreateRequest request = new CategoryUnifiedCreateRequest();
        request.setName(name);
        request.setDescription(description);
        request.setParentId(parentId);
        request.setCreatorId(creatorId);
        return request;
    }

    /**
     * 创建带图标的分类
     */
    public static CategoryUnifiedCreateRequest categoryWithIcon(String name, String description, 
                                                               Long parentId, String iconUrl, Long creatorId) {
        CategoryUnifiedCreateRequest request = new CategoryUnifiedCreateRequest();
        request.setName(name);
        request.setDescription(description);
        request.setParentId(parentId);
        request.setIconUrl(iconUrl);
        request.setCreatorId(creatorId);
        return request;
    }

    /**
     * 创建完整信息的分类
     */
    public static CategoryUnifiedCreateRequest fullCategory(String name, String description, Long parentId,
                                                           String iconUrl, String coverUrl, Integer sortOrder,
                                                           Long creatorId, String createReason) {
        CategoryUnifiedCreateRequest request = new CategoryUnifiedCreateRequest();
        request.setName(name);
        request.setDescription(description);
        request.setParentId(parentId);
        request.setIconUrl(iconUrl);
        request.setCoverUrl(coverUrl);
        request.setSortOrder(sortOrder);
        request.setCreatorId(creatorId);
        request.setCreateReason(createReason);
        return request;
    }

    // ===================== 链式设置方法 =====================

    /**
     * 设置图标
     */
    public CategoryUnifiedCreateRequest withIcon(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    /**
     * 设置封面
     */
    public CategoryUnifiedCreateRequest withCover(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    /**
     * 设置排序
     */
    public CategoryUnifiedCreateRequest withSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * 设置创建者名称
     */
    public CategoryUnifiedCreateRequest withCreatorName(String creatorName) {
        this.creatorName = creatorName;
        return this;
    }

    /**
     * 设置创建原因
     */
    public CategoryUnifiedCreateRequest withReason(String createReason) {
        this.createReason = createReason;
        return this;
    }

    // ===================== 验证方法 =====================

    /**
     * 判断是否为根分类
     */
    public boolean isRootCategory() {
        return parentId != null && parentId == 0L;
    }

    /**
     * 验证分类名称格式
     */
    public boolean isValidName() {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_\\.\\(\\)]+$");
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
} 