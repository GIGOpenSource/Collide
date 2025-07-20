package com.gig.collide.api.tag.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 标签操作响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class TagOperatorResponse extends BaseResponse {

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

    /**
     * 影响的记录数
     */
    private Integer affectedRows;

    public TagOperatorResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public TagOperatorResponse(Boolean success) {
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    public TagOperatorResponse(Boolean success, Object data) {
        this.success = success;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public TagOperatorResponse(Boolean success, Object data, Integer affectedRows) {
        this.success = success;
        this.data = data;
        this.affectedRows = affectedRows;
        this.timestamp = System.currentTimeMillis();
    }
} 