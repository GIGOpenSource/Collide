package com.gig.collide.api.content.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 内容更新请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 操作用户ID
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorId;

    /**
     * 内容标题
     */
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /**
     * 内容描述
     */
    @Size(max = 1000, message = "描述长度不能超过1000字符")
    private String description;

    /**
     * 内容数据（JSON格式）
     */
    private Map<String, Object> contentData;

    /**
     * 封面图片URL
     */
    private String coverUrl;

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
    private Boolean allowComment;

    /**
     * 是否允许分享
     */
    private Boolean allowShare;
} 