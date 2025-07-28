package com.gig.collide.api.tag.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * 内容标签管理请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ContentTagManageRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    @Positive(message = "内容ID必须为正数")
    private Long contentId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签ID列表（用于批量操作）
     */
    private List<Long> tagIds;

    /**
     * 操作类型（add、remove、replace、clear）
     */
    @NotBlank(message = "操作类型不能为空")
    private String operation;

    /**
     * 自动标签推荐（是否使用AI推荐标签）
     */
    private Boolean autoRecommend = false;

    /**
     * 最大推荐标签数量
     */
    @Max(value = 20, message = "最大推荐标签数量不能超过20")
    private Integer maxRecommendCount = 5;

    /**
     * 为内容添加标签
     */
    public static ContentTagManageRequest addTag(Long contentId, Long tagId) {
        ContentTagManageRequest request = new ContentTagManageRequest();
        request.setContentId(contentId);
        request.setTagId(tagId);
        request.setOperation("add");
        return request;
    }

    /**
     * 为内容移除标签
     */
    public static ContentTagManageRequest removeTag(Long contentId, Long tagId) {
        ContentTagManageRequest request = new ContentTagManageRequest();
        request.setContentId(contentId);
        request.setTagId(tagId);
        request.setOperation("remove");
        return request;
    }

    /**
     * 批量为内容添加标签
     */
    public static ContentTagManageRequest batchAddTags(Long contentId, List<Long> tagIds) {
        ContentTagManageRequest request = new ContentTagManageRequest();
        request.setContentId(contentId);
        request.setTagIds(tagIds);
        request.setOperation("batch_add");
        return request;
    }

    /**
     * 替换内容的所有标签
     */
    public static ContentTagManageRequest replaceTags(Long contentId, List<Long> tagIds) {
        ContentTagManageRequest request = new ContentTagManageRequest();
        request.setContentId(contentId);
        request.setTagIds(tagIds);
        request.setOperation("replace");
        return request;
    }

    /**
     * 清除内容的所有标签
     */
    public static ContentTagManageRequest clearTags(Long contentId) {
        ContentTagManageRequest request = new ContentTagManageRequest();
        request.setContentId(contentId);
        request.setOperation("clear");
        return request;
    }

    /**
     * 自动推荐内容标签
     */
    public static ContentTagManageRequest autoRecommend(Long contentId, Integer maxCount) {
        ContentTagManageRequest request = new ContentTagManageRequest();
        request.setContentId(contentId);
        request.setAutoRecommend(true);
        request.setMaxRecommendCount(maxCount);
        request.setOperation("auto_recommend");
        return request;
    }
} 