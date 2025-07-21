package com.gig.collide.order.validator;

import com.gig.collide.api.goods.model.BaseGoodsVO;
import com.gig.collide.api.goods.service.GoodsFacadeService;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.order.OrderException;

import static com.gig.collide.api.order.constant.OrderErrorCode.GOODS_NOT_BOOKED;

/**
 * 商品预约校验器
 *
 * @author GIG
 */
public class GoodsBookValidator extends BaseOrderCreateValidator {

    private GoodsFacadeService goodsFacadeService;

    @Override
    protected void doValidate(OrderCreateRequest request) throws OrderException {
        BaseGoodsVO baseGoodsVO = goodsFacadeService.getGoods(request.getGoodsId(), request.getGoodsType());
        if(baseGoodsVO.canBook()){
            Boolean hasBooked = goodsFacadeService.isGoodsBooked(request.getGoodsId(), request.getGoodsType(), request.getBuyerId());

            if (!hasBooked) {
                throw new OrderException(GOODS_NOT_BOOKED);
            }
        }
    }

    public GoodsBookValidator(GoodsFacadeService goodsFacadeService) {
        this.goodsFacadeService = goodsFacadeService;
    }

    public GoodsBookValidator() {
    }
}
