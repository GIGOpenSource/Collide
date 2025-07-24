package com.gig.collide.api.tag.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 更新标签请求
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 标签图标URL
     */
    private String iconUrl;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 标签状态
     */
    private String status;
    
    /**
     * 获取排序值
     */
    public Integer getSort() {
        return sort;
    }
} 