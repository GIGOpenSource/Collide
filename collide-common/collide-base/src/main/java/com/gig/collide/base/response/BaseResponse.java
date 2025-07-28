package com.gig.collide.base.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 通用出参
 *
 * @author GIGOpenSource
 */
@Setter
@Getter
@ToString
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean success;

    private String responseCode;

    private String responseMessage;

    private String Message;

    /**
     * 是否成功
     *
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
}
