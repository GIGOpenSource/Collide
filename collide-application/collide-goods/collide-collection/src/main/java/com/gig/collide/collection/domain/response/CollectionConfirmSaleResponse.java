package com.gig.collide.collection.domain.response;

import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.collection.domain.entity.Collection;
import com.gig.collide.collection.domain.entity.HeldCollection;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hollis
 */
@Setter
@Getter
public class CollectionConfirmSaleResponse extends BaseResponse {

    private Collection collection;

    private HeldCollection heldCollection;
}
