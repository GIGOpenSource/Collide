package com.gig.collide.api.collection.request;

import com.gig.collide.api.goods.constant.GoodsEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author GIG
 * @date 2024/01/17
 */

@Setter
@Getter
@ToString
@NoArgsConstructor
public class CollectionRemoveRequest extends BaseCollectionRequest {

    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.REMOVE;
    }
}
