package com.gig.collide.api.tag.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 标签创建请求
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Data
public class TagCreateRequest {

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 100, message = "标签名称长度不能超过100字符")
    private String name;

    /**
     * 标签描述
     */
    @Size(max = 500, message = "标签描述长度不能超过500字符")
    private String description;

    /**
     * 标签类型：content、interest、system
     */
    @NotBlank(message = "标签类型不能为空")
    private String tagType;

    /**
     * 所属分类ID
     */
    private Long categoryId;
}