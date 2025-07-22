package com.gig.collide.collection.domain.request;

import com.gig.collide.collection.domain.constant.HeldCollectionEventType;
import lombok.*;

/**
 * @author GIG
 * @date 2024/01/17
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HeldCollectionDestroyRequest extends BaseHeldCollectionRequest {

    /**
     * 操作人Id
     */
    private String operatorId;

    @Override
    public HeldCollectionEventType getEventType() {
        return HeldCollectionEventType.DESTROY;
    }
}
