package com.gig.collide.api.pay.response;

import com.gig.collide.api.pay.model.PayOrderVO;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author GIG
 */
@Getter
@Setter
public class PayQueryResponse extends BaseResponse {

    private List<PayOrderVO> payOrders;
}
