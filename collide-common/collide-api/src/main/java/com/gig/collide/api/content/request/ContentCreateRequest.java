package com.gig.collide.api.content.request;

import com.gig.collide.api.content.constant.ContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 内容创建请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /**
     * 内容描述
     */
    @Size(max = 1000, message = "描述长度不能超过1000字符")
    private String description;

    /**
     * 内容类型
     */
    @NotNull(message = "内容类型不能为空")
    private ContentType contentType;

    /**
     * 内容数据（JSON格式，结构根据contentType而定）
     */
    private Map<String, Object> contentData;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 作者用户ID
     */
    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签列表
     */
    @Size(max = 10, message = "标签数量不能超过10个")
    private List<String> tags;

    /**
     * 是否允许评论
     */
    private Boolean allowComment = true;

    /**
     * 是否允许分享
     */
    private Boolean allowShare = true;
} 