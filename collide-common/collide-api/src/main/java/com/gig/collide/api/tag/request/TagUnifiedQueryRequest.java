package com.gig.collide.api.tag.request;

import com.gig.collide.api.tag.request.condition.TagQueryCondition;
import com.gig.collide.base.request.BaseRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 标签统一查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TagUnifiedQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 查询条件
     */
    private TagQueryCondition queryCondition;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    private Integer pageSize = 20;

    /**
     * 排序字段
     */
    private String sortField = "createTime";

    /**
     * 排序方向（asc、desc）
     */
    private String sortDirection = "desc";

    /**
     * 通过标签ID查询
     */
    public TagUnifiedQueryRequest(Long tagId) {
        // 需要创建具体的查询条件实现类
    }

    /**
     * 通过标签名称查询
     */
    public TagUnifiedQueryRequest(String name) {
        // 需要创建具体的查询条件实现类
    }

    /**
     * 通过标签类型查询
     */
    public TagUnifiedQueryRequest(String tagType, String status) {
        // 需要创建具体的查询条件实现类
    }
} 