package com.gig.collide.api.collection.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class CollectionRemoveResponse extends BaseResponse {
    /**
     * 藏品id
     */
    private Long collectionId;

}
