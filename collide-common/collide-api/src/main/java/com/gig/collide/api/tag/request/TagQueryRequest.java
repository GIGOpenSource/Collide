package com.gig.collide.api.tag.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签查询请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagQueryRequest extends PageRequest {

    /**
     * 标签名称（模糊搜索）
     */
    private String name;

    /**
     * 标签类型：content、interest、system
     */
    private String tagType;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 状态：active、inactive
     */
    private String status;
} 