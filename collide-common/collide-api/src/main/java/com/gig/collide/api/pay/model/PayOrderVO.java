package com.gig.collide.api.pay.model;

import com.gig.collide.api.pay.constant.PayOrderState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PayOrderVO implements Serializable {

    private String payOrderId;

    private String payUrl;

    private PayOrderState orderState;

    private static final long serialVersionUID = 1L;
}
