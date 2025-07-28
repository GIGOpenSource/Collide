package com.gig.collide.api.comment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 评论统一查询响应
 * 支持泛型，可以返回不同类型的评论信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentUnifiedQueryResponse<T> extends BaseResponse {

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
     * 树形结构深度（树形查询时）
     */
    private Integer treeDepth;

    /**
     * 是否包含回复（树形查询时）
     */
    private Boolean includeReplies;

    /**
     * 创建成功的单条记录响应
     */
    public static <T> CommentUnifiedQueryResponse<T> success(T data) {
        CommentUnifiedQueryResponse<T> response = new CommentUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的多条记录响应
     */
    public static <T> CommentUnifiedQueryResponse<T> success(List<T> dataList) {
        CommentUnifiedQueryResponse<T> response = new CommentUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setDataList(dataList);
        response.setTotal((long) dataList.size());
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的分页响应
     */
    public static <T> CommentUnifiedQueryResponse<T> success(List<T> dataList, Long total, 
                                                            Integer currentPage, Integer pageSize) {
        CommentUnifiedQueryResponse<T> response = new CommentUnifiedQueryResponse<>();
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
     * 创建成功的树形响应
     */
    public static <T> CommentUnifiedQueryResponse<T> successTree(List<T> dataList, Integer treeDepth, 
                                                                Boolean includeReplies) {
        CommentUnifiedQueryResponse<T> response = success(dataList);
        response.setTreeDepth(treeDepth);
        response.setIncludeReplies(includeReplies);
        response.setResponseMessage("评论树查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static <T> CommentUnifiedQueryResponse<T> fail(String message) {
        CommentUnifiedQueryResponse<T> response = new CommentUnifiedQueryResponse<>();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建失败响应（带错误码）
     */
    public static <T> CommentUnifiedQueryResponse<T> fail(String code, String message) {
        CommentUnifiedQueryResponse<T> response = fail(message);
        return response;
    }

    /**
     * 创建空结果响应
     */
    public static <T> CommentUnifiedQueryResponse<T> empty() {
        CommentUnifiedQueryResponse<T> response = new CommentUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setResponseMessage("查询成功，无数据");
        response.setTotal(0L);
        return response;
    }

    /**
     * 创建空结果响应（带消息）
     */
    public static <T> CommentUnifiedQueryResponse<T> empty(String message) {
        CommentUnifiedQueryResponse<T> response = empty();
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 判断是否有数据
     *
     * @return true如果有数据
     */
    public boolean hasData() {
        return (data != null) || (dataList != null && !dataList.isEmpty());
    }

    /**
     * 获取数据总数
     *
     * @return 数据总数
     */
    public long getDataCount() {
        if (data != null) {
            return 1L;
        }
        if (dataList != null) {
            return dataList.size();
        }
        return 0L;
    }

    /**
     * 判断是否为分页查询
     *
     * @return true如果为分页查询
     */
    public boolean isPaginated() {
        return currentPage != null && pageSize != null;
    }

    /**
     * 判断是否为树形查询
     *
     * @return true如果为树形查询
     */
    public boolean isTreeStructure() {
        return treeDepth != null || Boolean.TRUE.equals(includeReplies);
    }
} 