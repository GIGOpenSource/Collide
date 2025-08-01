package com.gig.collide.base.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * 分页响应
 *
 * @author GIGOpenSource
 */
public class PageResponse<T> extends MultiResponse<T> {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 每页结果数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总数
     */
    private long total;

    // Getter and Setter methods
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * 获取记录列表（兼容方法）
     * @return 记录列表
     */
    @JsonIgnore
    public List<T> getRecords() {
        return getDatas();
    }

    /**
     * 设置记录列表（兼容方法）
     * @param records 记录列表
     */
    @JsonIgnore
    public void setRecords(List<T> records) {
        setDatas(records);
    }

    public static <T> PageResponse<T> of(List<T> datas, long total, int pageSize, int currentPage) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setDatas(datas);
        pageResponse.setTotal(total);
        pageResponse.setPageSize(pageSize);
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setTotalPage((int) ((total + pageSize - 1) / pageSize));
        return pageResponse;
    }

    public static <T> PageResponse<T> of(List<T> datas, int total, int pageSize, int currentPage) {
        return of(datas, (long) total, pageSize, currentPage);
    }

    /**
     * 创建空的分页响应
     *
     * @param <T> 数据类型
     * @return 空的分页响应对象
     */
    public static <T> PageResponse<T> empty() {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setDatas(java.util.Collections.emptyList());
        pageResponse.setTotal(0);
        pageResponse.setPageSize(0);
        pageResponse.setCurrentPage(0);
        pageResponse.setTotalPage(0);
        return pageResponse;
    }

    /**
     * 创建失败响应（与其他响应类保持一致）
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 失败响应对象
     */
    public static <T> PageResponse<T> fail(String errorCode, String errorMessage) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(false);
        pageResponse.setResponseCode(errorCode);
        pageResponse.setResponseMessage(errorMessage);
        pageResponse.setDatas(java.util.Collections.emptyList());
        pageResponse.setTotal(0);
        pageResponse.setPageSize(0);
        pageResponse.setCurrentPage(0);
        pageResponse.setTotalPage(0);
        return pageResponse;
    }

    /**
     * 创建错误响应（保持向后兼容）
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 错误响应对象
     */
    public static <T> PageResponse<T> error(String errorCode, String errorMessage) {
        return fail(errorCode, errorMessage);
    }
}
