package com.gig.collide.api.pay.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class PayCreateResponse extends BaseResponse {

    private String payOrderId;

    private String payUrl;
}
