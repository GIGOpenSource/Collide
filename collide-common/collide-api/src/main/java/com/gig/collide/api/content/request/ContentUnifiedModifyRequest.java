package com.gig.collide.api.content.request;

import com.gig.collide.api.content.enums.ContentStatusEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 内容修改请求
 * 用于修改已存在的内容
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
public class ContentUnifiedModifyRequest extends BaseRequest {

    /**
     * 内容ID（必填）
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 操作用户ID（必填）
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorId;

    /**
     * 内容标题（可选）
     */
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    /**
     * 内容描述（可选）
     */
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;

    /**
     * 内容数据（JSON格式）
     */
    private String contentData;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 标签列表
     */
    @Size(max = 10, message = "标签数量不能超过10个")
    private List<@Size(max = 20, message = "标签名称长度不能超过20个字符") String> tags;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 内容状态
     */
    private ContentStatusEnum status;

    /**
     * 是否允许评论
     */
    private Boolean allowComment;

    /**
     * 是否允许分享
     */
    private Boolean allowShare;

    /**
     * 是否推荐（管理员权限）
     */
    private Boolean isRecommended;

    /**
     * 是否置顶（管理员权限）
     */
    private Boolean isPinned;

    /**
     * 权重分数（管理员权限）
     */
    private Double weightScore;

    /**
     * 修改原因/备注
     */
    @Size(max = 500, message = "修改原因长度不能超过500个字符")
    private String modifyReason;

    // ===================== 便捷构造器 =====================

    /**
     * 修改基本信息
     */
    public static ContentUnifiedModifyRequest basicInfo(Long contentId, Long operatorId, 
                                                       String title, String description) {
        ContentUnifiedModifyRequest request = new ContentUnifiedModifyRequest();
        request.setContentId(contentId);
        request.setOperatorId(operatorId);
        request.setTitle(title);
        request.setDescription(description);
        return request;
    }

    /**
     * 修改内容数据
     */
    public static ContentUnifiedModifyRequest contentData(Long contentId, Long operatorId, String contentData) {
        ContentUnifiedModifyRequest request = new ContentUnifiedModifyRequest();
        request.setContentId(contentId);
        request.setOperatorId(operatorId);
        request.setContentData(contentData);
        return request;
    }

    /**
     * 修改状态
     */
    public static ContentUnifiedModifyRequest status(Long contentId, Long operatorId, 
                                                    ContentStatusEnum status, String reason) {
        ContentUnifiedModifyRequest request = new ContentUnifiedModifyRequest();
        request.setContentId(contentId);
        request.setOperatorId(operatorId);
        request.setStatus(status);
        request.setModifyReason(reason);
        return request;
    }

    /**
     * 设置推荐
     */
    public static ContentUnifiedModifyRequest recommend(Long contentId, Long operatorId, boolean recommend) {
        ContentUnifiedModifyRequest request = new ContentUnifiedModifyRequest();
        request.setContentId(contentId);
        request.setOperatorId(operatorId);
        request.setIsRecommended(recommend);
        return request;
    }

    /**
     * 设置置顶
     */
    public static ContentUnifiedModifyRequest pin(Long contentId, Long operatorId, boolean pin) {
        ContentUnifiedModifyRequest request = new ContentUnifiedModifyRequest();
        request.setContentId(contentId);
        request.setOperatorId(operatorId);
        request.setIsPinned(pin);
        return request;
    }

    /**
     * 链式设置标签
     */
    public ContentUnifiedModifyRequest withTags(String... tags) {
        this.tags = List.of(tags);
        return this;
    }

    /**
     * 链式设置分类
     */
    public ContentUnifiedModifyRequest withCategory(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    /**
     * 链式设置封面
     */
    public ContentUnifiedModifyRequest withCover(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    /**
     * 链式设置修改原因
     */
    public ContentUnifiedModifyRequest withReason(String reason) {
        this.modifyReason = reason;
        return this;
    }
} 