package com.gig.collide.api.common.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用分页请求基类
 * 所有分页查询请求都继承此类
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasePageRequest extends BaseRequest {

    /**
     * 当前页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortBy = "createTime";

    /**
     * 排序方向: asc | desc
     */
    private String sortOrder = "desc";

    /**
     * 计算偏移量
     */
    public Integer getOffset() {
        return (currentPage - 1) * pageSize;
    }

    /**
     * 获取MySQL LIMIT语句
     */
    public String getLimitClause() {
        return String.format("LIMIT %d, %d", getOffset(), pageSize);
    }

    /**
     * 获取排序语句
     */
    public String getOrderClause() {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "";
        }
        return String.format("ORDER BY %s %s", sortBy, 
            "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC");
    }
} 