package com.gig.collide.api.collection.request;

import com.gig.collide.api.goods.constant.GoodsEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author GIG
 * @date 2025/07/17
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDestroyRequest extends BaseCollectionRequest {
    /**
     * '持有藏品id'
     */
    private Long heldCollectionId;

    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.DESTROY;
    }
}
