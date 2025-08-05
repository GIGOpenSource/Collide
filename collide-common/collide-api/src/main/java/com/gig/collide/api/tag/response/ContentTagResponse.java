package com.gig.collide.api.tag.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容标签响应
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Data
public class ContentTagResponse {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 标签使用次数
     */
    private Long tagUsageCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}