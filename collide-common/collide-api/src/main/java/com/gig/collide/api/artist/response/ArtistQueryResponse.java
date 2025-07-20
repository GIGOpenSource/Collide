package com.gig.collide.api.artist.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 博主查询响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class ArtistQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 查询数据
     */
    private T data;

    /**
     * 查询结果总数
     */
    private Long total;

    public ArtistQueryResponse() {
    }

    public ArtistQueryResponse(T data) {
        this.data = data;
    }

    public ArtistQueryResponse(T data, Long total) {
        this.data = data;
        this.total = total;
    }
} 