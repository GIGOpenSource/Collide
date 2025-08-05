package com.gig.collide.api.tag.request;

import com.gig.collide.base.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签查询请求
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagQueryRequest extends PageRequest {

    /**
     * 标签名称（模糊搜索）
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
     * 状态：active、inactive
     */
    private String status;

    /**
     * 最小使用次数
     */
    private Long minUsageCount;

    /**
     * 最大使用次数
     */
    private Long maxUsageCount;

    /**
     * 排序字段
     */
    private String sortBy;

    /**
     * 排序方向：asc、desc
     */
    private String sortDirection;
}