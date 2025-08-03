package com.gig.collide.api.tag.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * 标签更新请求 - 简洁版
 * 基于t_tag表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class TagUpdateRequest implements Serializable {

    @NotNull(message = "标签ID不能为空")
    private Long id;

    @Size(max = 100, message = "标签名称长度不能超过100字符")
    private String name;

    @Size(max = 500, message = "标签描述长度不能超过500字符")
    private String description;

    @Pattern(regexp = "^(content|interest|system)$", message = "标签类型只能是content、interest或system")
    private String tagType;

    /**
     * 所属分类ID（可选）
     */
    private Long categoryId;

    @Pattern(regexp = "^(active|inactive)$", message = "状态只能是active或inactive")
    private String status;
} 