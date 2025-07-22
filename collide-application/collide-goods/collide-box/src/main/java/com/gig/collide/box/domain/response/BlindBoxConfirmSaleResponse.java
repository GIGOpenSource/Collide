package com.gig.collide.box.domain.response;

import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.box.domain.entity.BlindBox;
import com.gig.collide.box.domain.entity.BlindBoxItem;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hollis
 */
@Setter
@Getter
public class BlindBoxConfirmSaleResponse extends BaseResponse {

    private BlindBox blindBox;

    private BlindBoxItem blindBoxItem;
}
