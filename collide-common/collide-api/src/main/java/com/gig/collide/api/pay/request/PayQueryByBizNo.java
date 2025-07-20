package com.gig.collide.api.pay.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class PayQueryByBizNo implements PayQueryCondition {

    private String bizNo;

    private String bizType;
}
