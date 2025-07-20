package com.gig.collide.api.box.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class BlindBoxSaleResponse extends BaseResponse {
    /**
     * 盲盒条目id
     */
    private Long blindBoxItemId;

}
