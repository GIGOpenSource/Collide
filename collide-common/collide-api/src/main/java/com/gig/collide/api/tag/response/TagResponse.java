package com.gig.collide.api.tag.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签响应 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class TagResponse {

    private Long id;

    private String name;

    private String description;

    private String tagType;

    private Long categoryId;

    private Long usageCount;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 