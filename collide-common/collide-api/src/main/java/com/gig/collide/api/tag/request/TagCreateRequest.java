package com.gig.collide.api.tag.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 标签创建请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class TagCreateRequest {

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 100, message = "标签名称长度不能超过100字符")
    private String name;

    @Size(max = 500, message = "标签描述长度不能超过500字符")
    private String description;

    @Pattern(regexp = "^(content|interest|system)$", message = "标签类型只能是content、interest或system")
    private String tagType = "content";

    /**
     * 所属分类ID（可选）
     */
    private Long categoryId;
} 