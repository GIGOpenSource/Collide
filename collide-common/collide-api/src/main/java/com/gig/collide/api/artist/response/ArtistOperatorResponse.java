package com.gig.collide.api.artist.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 博主操作响应
 * @author GIG
 */
@Setter
@Getter
@ToString
public class ArtistOperatorResponse extends BaseResponse {

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

    /**
     * 操作消息
     */
    private String operationMessage;

    public ArtistOperatorResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ArtistOperatorResponse(Boolean success) {
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    public ArtistOperatorResponse(Boolean success, Object data) {
        this.success = success;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public ArtistOperatorResponse(Boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.operationMessage = message;
        this.timestamp = System.currentTimeMillis();
    }

    public ArtistOperatorResponse(Boolean success, Object data, Integer affectedRows) {
        this.success = success;
        this.data = data;
        this.affectedRows = affectedRows;
        this.timestamp = System.currentTimeMillis();
    }
} 