package com.gig.collide.api.user.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户统一查询响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserUnifiedQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 查询结果数据
     */
    private T data;

    /**
     * 分页信息
     */
    private PageInfo pageInfo;

    /**
     * 分页信息
     */
    @Getter
    @Setter
    public static class PageInfo {
        private Integer pageNum;
        private Integer pageSize;
        private Long total;
        private Integer pages;
        private Boolean hasNextPage;
        private Boolean hasPreviousPage;
    }
} 