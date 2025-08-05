package com.gig.collide.api.tag.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 标签更新请求
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Data
public class TagUpdateRequest {

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    private Long id;

    /**
     * 标签名称
     */
    @Size(max = 100, message = "标签名称长度不能超过100字符")
    private String name;

    /**
     * 标签描述
     */
    @Size(max = 500, message = "标签描述长度不能超过500字符")
    private String description;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 操作人ID
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;
}