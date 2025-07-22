package com.gig.collide.base.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页响应
 *
 * @author GIGOpenSource
 */
@Setter
@Getter
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
    private int total;

    public static <T> PageResponse<T> of(List<T> datas, int total, int pageSize,int currentPage) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setDatas(datas);
        pageResponse.setTotal(total);
        pageResponse.setPageSize(pageSize);
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setTotalPage((pageSize + total - 1) / pageSize);
        return pageResponse;
    }

    public static <T> PageResponse<T> error(String errorMessage) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(false);
        pageResponse.setResponseMessage(errorMessage);
        pageResponse.setDatas(List.of());
        pageResponse.setTotal(0);
        pageResponse.setPageSize(0);
        pageResponse.setCurrentPage(0);
        pageResponse.setTotalPage(0);
        return pageResponse;
    }
}
