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
public class GoodsBookResponse extends BaseResponse {
    private Long bookId;

    public static class GoodsBookResponseBuilder {
        private Long bookId;

        public GoodsBookResponseBuilder bookId(Long bookId) {
            this.bookId = bookId;
            return this;
        }

        public GoodsBookResponse buildSuccess() {
            GoodsBookResponse response = new GoodsBookResponse();
            response.setBookId(bookId);
            response.setSuccess(true);
            return response;
        }

        public GoodsBookResponse buildDuplicated() {
            GoodsBookResponse response = new GoodsBookResponse();
            response.setBookId(bookId);
            response.setSuccess(true);
            response.setResponseCode(DUPLICATED.getCode());
            response.setResponseMessage(DUPLICATED.getMessage());
            return response;
        }

        public GoodsBookResponse buildFail(String code, String msg) {
            GoodsBookResponse response = new GoodsBookResponse();
            response.setBookId(bookId);
            response.setSuccess(false);
            response.setResponseCode(code);
            response.setResponseMessage(msg);
            return response;
        }
    }
}
