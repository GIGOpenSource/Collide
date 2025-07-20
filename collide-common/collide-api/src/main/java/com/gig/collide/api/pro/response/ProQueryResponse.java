package com.gig.collide.api.pro.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Pro查询响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class ProQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 查询数据
     */
    private T data;
} 