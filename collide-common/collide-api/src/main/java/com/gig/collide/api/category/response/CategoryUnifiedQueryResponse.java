package com.gig.collide.api.category.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 分类统一查询响应
 * 支持泛型，可以返回不同类型的分类信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryUnifiedQueryResponse<T> extends BaseResponse {

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
    public static <T> CategoryUnifiedQueryResponse<T> success(T data) {
        CategoryUnifiedQueryResponse<T> response = new CategoryUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的多条记录响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> success(List<T> dataList) {
        CategoryUnifiedQueryResponse<T> response = new CategoryUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setDataList(dataList);
        response.setTotal((long) dataList.size());
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的分页响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> success(List<T> dataList, Long total, 
                                                            Integer currentPage, Integer pageSize) {
        CategoryUnifiedQueryResponse<T> response = new CategoryUnifiedQueryResponse<>();
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
    public static <T> CategoryUnifiedQueryResponse<T> fail(String message) {
        CategoryUnifiedQueryResponse<T> response = new CategoryUnifiedQueryResponse<>();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建失败响应（带错误码）
     */
    public static <T> CategoryUnifiedQueryResponse<T> fail(String responseCode, String message) {
        CategoryUnifiedQueryResponse<T> response = new CategoryUnifiedQueryResponse<>();
        response.setSuccess(false);
        response.setResponseCode(responseCode);
        response.setResponseMessage(message);
        return response;
    }

    /**
     * 创建空结果响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> empty() {
        CategoryUnifiedQueryResponse<T> response = new CategoryUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setResponseMessage("查询成功，无数据");
        response.setTotal(0L);
        return response;
    }

    /**
     * 创建空结果响应（分页）
     */
    public static <T> CategoryUnifiedQueryResponse<T> empty(Integer currentPage, Integer pageSize) {
        CategoryUnifiedQueryResponse<T> response = new CategoryUnifiedQueryResponse<>();
        response.setSuccess(true);
        response.setResponseMessage("查询成功，无数据");
        response.setTotal(0L);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalPages(0);
        response.setHasNext(false);
        response.setHasPrevious(false);
        return response;
    }

    // ===================== 便捷方法 =====================

    /**
     * 创建分类树查询成功响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> treeSuccess(List<T> treeData) {
        CategoryUnifiedQueryResponse<T> response = success(treeData);
        response.setResponseMessage("分类树查询成功");
        return response;
    }

    /**
     * 创建热门分类查询成功响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> popularSuccess(List<T> popularData) {
        CategoryUnifiedQueryResponse<T> response = success(popularData);
        response.setResponseMessage("热门分类查询成功");
        return response;
    }

    /**
     * 创建根分类查询成功响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> rootSuccess(List<T> rootData) {
        CategoryUnifiedQueryResponse<T> response = success(rootData);
        response.setResponseMessage("根分类查询成功");
        return response;
    }

    /**
     * 创建搜索结果成功响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> searchSuccess(List<T> searchData, String keyword) {
        CategoryUnifiedQueryResponse<T> response = success(searchData);
        response.setResponseMessage(String.format("搜索关键词'%s'成功", keyword));
        return response;
    }

    /**
     * 创建统计信息查询成功响应
     */
    public static <T> CategoryUnifiedQueryResponse<T> statisticsSuccess(T statisticsData) {
        CategoryUnifiedQueryResponse<T> response = success(statisticsData);
        response.setResponseMessage("分类统计信息查询成功");
        return response;
    }

    // ===================== 验证和辅助方法 =====================

    /**
     * 判断是否有数据
     */
    public boolean hasData() {
        return (data != null) || (dataList != null && !dataList.isEmpty());
    }

    /**
     * 判断是否为分页查询
     */
    public boolean isPaginated() {
        return currentPage != null && pageSize != null;
    }

    /**
     * 判断是否为单条数据响应
     */
    public boolean isSingleData() {
        return data != null && dataList == null;
    }

    /**
     * 判断是否为列表数据响应
     */
    public boolean isListData() {
        return data == null && dataList != null;
    }

    /**
     * 获取数据数量
     */
    public int getDataCount() {
        if (isSingleData()) {
            return 1;
        } else if (isListData()) {
            return dataList.size();
        }
        return 0;
    }

    /**
     * 获取开始记录号（分页时）
     */
    public Integer getStartRecord() {
        if (!isPaginated()) {
            return null;
        }
        return (currentPage - 1) * pageSize + 1;
    }

    /**
     * 获取结束记录号（分页时）
     */
    public Integer getEndRecord() {
        if (!isPaginated()) {
            return null;
        }
        int end = currentPage * pageSize;
        return total != null ? (int) Math.min(end, total) : end;
    }
} 