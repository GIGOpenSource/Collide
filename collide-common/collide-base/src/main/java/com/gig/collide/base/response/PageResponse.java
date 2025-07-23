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

    /**
     * 创建错误响应
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 错误响应对象
     */
    public static <T> PageResponse<T> error(String errorCode, String errorMessage) {
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
}
