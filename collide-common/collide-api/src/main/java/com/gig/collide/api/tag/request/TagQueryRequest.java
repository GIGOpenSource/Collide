package com.gig.collide.api.tag.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 标签查询请求
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 标签状态
     */
    private String status;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 排序字段
     */
    private String sortBy;

    /**
     * 排序方向
     */
    private String sortOrder;

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 获取标签名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取标签类型
     */
    public String getTagType() {
        return tagType;
    }
    
    /**
     * 获取分类ID
     */
    public Long getCategoryId() {
        return categoryId;
    }
    
    /**
     * 获取状态
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * 获取关键词
     */
    public String getKeyword() {
        return keyword;
    }
    
    /**
     * 获取排序字段
     */
    public String getSortBy() {
        return sortBy;
    }
    
    /**
     * 获取排序方向
     */
    public String getSortOrder() {
        return sortOrder;
    }
    
    /**
     * 获取页码
     */
    public Integer getPageNo() {
        return pageNo;
    }
    
    /**
     * 获取每页大小
     */
    public Integer getPageSize() {
        return pageSize;
    }
} 