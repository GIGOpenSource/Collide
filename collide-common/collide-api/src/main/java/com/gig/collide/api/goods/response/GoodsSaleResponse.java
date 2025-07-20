package com.gig.collide.api.goods.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import static com.gig.collide.base.exception.BizErrorCode.DUPLICATED;

/**
 * @author GIG
 */
@Getter
@Setter
public class GoodsSaleResponse extends BaseResponse {
    /**
     * 持有藏品id
     */
    private Long heldCollectionId;

    public static class GoodsResponseBuilder {
        private Long heldCollectionId;

        public GoodsSaleResponse.GoodsResponseBuilder heldCollectionId(Long heldCollectionId) {
            this.heldCollectionId = heldCollectionId;
            return this;
        }

        public GoodsSaleResponse buildSuccess() {
            GoodsSaleResponse goodsSaleResponse = new GoodsSaleResponse();
            goodsSaleResponse.setHeldCollectionId(heldCollectionId);
            goodsSaleResponse.setSuccess(true);
            return goodsSaleResponse;
        }

        public GoodsSaleResponse buildDuplicated() {
            GoodsSaleResponse goodsSaleResponse = new GoodsSaleResponse();
            goodsSaleResponse.setHeldCollectionId(heldCollectionId);
            goodsSaleResponse.setSuccess(true);
            goodsSaleResponse.setResponseCode(DUPLICATED.getCode());
            goodsSaleResponse.setResponseMessage(DUPLICATED.getMessage());
            return goodsSaleResponse;
        }

        public GoodsSaleResponse buildFail(String code, String msg) {
            GoodsSaleResponse goodsSaleResponse = new GoodsSaleResponse();
            goodsSaleResponse.setHeldCollectionId(heldCollectionId);
            goodsSaleResponse.setSuccess(false);
            goodsSaleResponse.setResponseCode(code);
            goodsSaleResponse.setResponseMessage(msg);
            return goodsSaleResponse;
        }
    }
}
