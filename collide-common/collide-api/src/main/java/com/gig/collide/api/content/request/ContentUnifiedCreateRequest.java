package com.gig.collide.api.content.request;

import com.gig.collide.api.content.enums.ContentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 内容创建请求
 * 用于创建新的内容
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
public class ContentUnifiedCreateRequest extends BaseRequest {

    /**
     * 内容标题（必填）
     */
    @NotBlank(message = "内容标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    /**
     * 内容描述（可选）
     */
    @Size(max = 1000, message = "描述长度不能超过1000个字符")
    private String description;

    /**
     * 内容类型（必填）
     */
    @NotNull(message = "内容类型不能为空")
    private ContentTypeEnum contentType;

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
     * 作者ID（必填）
     */
    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    /**
     * 是否允许评论
     */
    private Boolean allowComment = true;

    /**
     * 是否允许分享
     */
    private Boolean allowShare = true;

    /**
     * 是否立即发布
     * true: 提交审核，审核通过后自动发布
     * false: 保存为草稿
     */
    private Boolean publishImmediately = false;

    /**
     * 设置为推荐内容（管理员权限）
     */
    private Boolean setAsRecommended = false;

    /**
     * 设置为置顶内容（管理员权限）
     */
    private Boolean setAsPinned = false;

    /**
     * 权重分数（管理员权限）
     */
    private Double weightScore;

    // ===================== 便捷构造器 =====================

    /**
     * 创建小说内容
     */
    public static ContentUnifiedCreateRequest novel(String title, String description, Long authorId) {
        ContentUnifiedCreateRequest request = new ContentUnifiedCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setContentType(ContentTypeEnum.NOVEL);
        request.setAuthorId(authorId);
        return request;
    }

    /**
     * 创建漫画内容
     */
    public static ContentUnifiedCreateRequest comic(String title, String description, Long authorId) {
        ContentUnifiedCreateRequest request = new ContentUnifiedCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setContentType(ContentTypeEnum.COMIC);
        request.setAuthorId(authorId);
        return request;
    }

    /**
     * 创建文章内容
     */
    public static ContentUnifiedCreateRequest article(String title, String description, Long authorId) {
        ContentUnifiedCreateRequest request = new ContentUnifiedCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setContentType(ContentTypeEnum.ARTICLE);
        request.setAuthorId(authorId);
        return request;
    }

    /**
     * 创建视频内容
     */
    public static ContentUnifiedCreateRequest video(String title, String description, 
                                                   ContentTypeEnum videoType, Long authorId) {
        if (!videoType.isVideoType()) {
            throw new IllegalArgumentException("内容类型必须是视频类型");
        }
        
        ContentUnifiedCreateRequest request = new ContentUnifiedCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setContentType(videoType);
        request.setAuthorId(authorId);
        return request;
    }

    /**
     * 创建音频内容
     */
    public static ContentUnifiedCreateRequest audio(String title, String description, Long authorId) {
        ContentUnifiedCreateRequest request = new ContentUnifiedCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setContentType(ContentTypeEnum.AUDIO);
        request.setAuthorId(authorId);
        return request;
    }

    /**
     * 设置标签
     */
    public ContentUnifiedCreateRequest withTags(String... tags) {
        this.tags = List.of(tags);
        return this;
    }

    /**
     * 设置分类
     */
    public ContentUnifiedCreateRequest withCategory(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    /**
     * 设置封面
     */
    public ContentUnifiedCreateRequest withCover(String coverUrl) {
        this.coverUrl = coverUrl;
        return this;
    }

    /**
     * 立即发布
     */
    public ContentUnifiedCreateRequest publish() {
        this.publishImmediately = true;
        return this;
    }
} 