package com.gig.collide.api.collection.response;

import com.gig.collide.api.collection.model.HeldCollectionVO;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 空投响应
 *
 * @author GIG
 */
@Getter
@Setter
public class CollectionAirdropResponse extends BaseResponse {
    /**
     * 持有藏品信息
     */
    private List<HeldCollectionVO> heldCollections;

    /**
     * 空投流水id
     */
    private Long airDropStreamId;
}
