package com.gig.collide.api.content.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 内容统一查询响应
 * 支持泛型，可以返回不同类型的内容信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentUnifiedQueryResponse<T> extends BaseResponse {

    /**
     * 查询结果数据
     */
    private T data;

    /**
     * 查询结果列表（批量查询）
     */
    private List<T> dataList;

    /**
     * 总记录数（分页查询时）
     */
    private Long total;

    /**
     * 当前页码（分页查询时）
     */
    private Integer currentPage;

    /**
     * 每页大小（分页查询时）
     */
    private Integer pageSize;

    /**
     * 总页数（分页查询时）
     */
    private Integer totalPages;

    /**
     * 是否有下一页（分页查询时）
     */
    private Boolean hasNext;

    /**
     * 是否有上一页（分页查询时）
     */
    private Boolean hasPrevious;

    /**
     * 创建成功的单条记录响应
     */
    public static <T> ContentUnifiedQueryResponse<T> success(T data) {
        ContentUnifiedQueryResponse<T> response = new ContentUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的多条记录响应
     */
    public static <T> ContentUnifiedQueryResponse<T> success(List<T> dataList) {
        ContentUnifiedQueryResponse<T> response = new ContentUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setDataList(dataList);
        response.setTotal((long) dataList.size());
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的分页响应
     */
    public static <T> ContentUnifiedQueryResponse<T> success(List<T> dataList, Long total, 
                                                            Integer currentPage, Integer pageSize) {
        ContentUnifiedQueryResponse<T> response = new ContentUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setDataList(dataList);
        response.setTotal(total);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        
        // 计算总页数
        if (pageSize != null && pageSize > 0) {
            response.setTotalPages((int) Math.ceil((double) total / pageSize));
            response.setHasNext(currentPage < response.getTotalPages());
            response.setHasPrevious(currentPage > 1);
        }
        
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static <T> ContentUnifiedQueryResponse<T> fail(String message) {
        ContentUnifiedQueryResponse<T> response = new ContentUnifiedQueryResponse<>();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建空结果响应
     */
    public static <T> ContentUnifiedQueryResponse<T> empty() {
        ContentUnifiedQueryResponse<T> response = new ContentUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setResponseMessage("查询成功，无数据");
        response.setTotal(0L);
        return response;
    }
} 