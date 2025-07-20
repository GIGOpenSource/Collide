package com.gig.collide.api.pro.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Pro操作响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class ProOperatorResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 操作时间
     */
    private Long timestamp;

    /**
     * 操作结果数据
     */
    private Object data;

    public ProOperatorResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ProOperatorResponse(Boolean success) {
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    public ProOperatorResponse(Boolean success, Object data) {
        this.success = success;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
} 