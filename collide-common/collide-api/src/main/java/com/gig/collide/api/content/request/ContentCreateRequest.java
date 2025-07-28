package com.gig.collide.api.content.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 内容创建请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class ContentCreateRequest {

    @NotBlank(message = "内容标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    @Size(max = 1000, message = "描述长度不能超过1000字符")
    private String description;

    @NotBlank(message = "内容类型不能为空")
    @Pattern(regexp = "^(NOVEL|COMIC|VIDEO|ARTICLE|AUDIO)$", message = "内容类型只能是NOVEL、COMIC、VIDEO、ARTICLE或AUDIO")
    private String contentType;

    @NotBlank(message = "内容数据不能为空")
    private String contentData;

    private String coverUrl;

    private String tags;

    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    private Long categoryId;
} 