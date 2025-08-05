package com.gig.collide.api.tag.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 内容标签请求
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Data
public class ContentTagRequest {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    private Long tagId;

    /**
     * 操作人ID
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;
}