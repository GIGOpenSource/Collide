package com.gig.collide.api.tag.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * 标签创建请求 - 简洁版
 * 基于t_tag表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class TagCreateRequest implements Serializable {

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