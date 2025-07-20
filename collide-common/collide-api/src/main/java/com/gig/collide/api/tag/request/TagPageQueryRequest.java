package com.gig.collide.api.tag.request;

import com.gig.collide.api.tag.constant.TagLevel;
import com.gig.collide.api.tag.constant.TagStatus;
import com.gig.collide.api.tag.constant.TagType;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 标签分页查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TagPageQueryRequest extends BaseRequest {

    /**
     * 当前页
     */
    private int currentPage = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 标签名称（支持模糊查询）
     */
    private String tagName;

    /**
     * 标签类型
     */
    private TagType tagType;

    /**
     * 标签状态
     */
    private TagStatus status;

    /**
     * 标签层级
     */
    private TagLevel level;

    /**
     * 父标签ID
     */
    private Long parentTagId;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 是否只查询可用标签
     */
    private Boolean onlyAvailable = true;

    /**
     * 排序字段
     */
    private String sortBy = "sort_order";

    /**
     * 排序方向
     */
    private String sortDirection = "ASC";

    /**
     * 关键词搜索
     */
    private String keyword;
} 