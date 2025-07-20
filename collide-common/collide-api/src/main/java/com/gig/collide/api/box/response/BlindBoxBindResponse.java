package com.gig.collide.api.box.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class BlindBoxBindResponse extends BaseResponse {
    /**
     * 盲盒绑定总数
     */
    private int bindTotal;

}
