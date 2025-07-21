package com.gig.collide.order.validator;

import com.gig.collide.api.goods.constant.GoodsState;
import com.gig.collide.api.goods.model.BaseGoodsVO;
import com.gig.collide.api.goods.service.GoodsFacadeService;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.order.OrderException;

import static com.gig.collide.api.order.constant.OrderErrorCode.GOODS_NOT_AVAILABLE;
import static com.gig.collide.api.order.constant.OrderErrorCode.GOODS_PRICE_CHANGED;

/**
 * 商品校验器
 *
 * @author GIG
 */
public class GoodsValidator extends BaseOrderCreateValidator {

    private GoodsFacadeService goodsFacadeService;

    @Override
    protected void doValidate(OrderCreateRequest request) throws OrderException {
        BaseGoodsVO baseGoodsVO = goodsFacadeService.getGoods(request.getGoodsId(), request.getGoodsType());

        // 如果商品不是可售状态，则返回失败
        // PS：可售状态为什么要包含SOLD_OUT呢？因为商品查询的接口中去查询了 Redis 的最新库存，而 Redis 的库存在下单时可能已经扣减过刚好为0了，所以这里要包含 SOLD_OUT
        if (baseGoodsVO.getState() != GoodsState.SELLING && baseGoodsVO.getState() != GoodsState.SOLD_OUT) {
            throw new OrderException(GOODS_NOT_AVAILABLE);
        }

        if (baseGoodsVO.getPrice().compareTo(request.getItemPrice()) != 0) {
            throw new OrderException(GOODS_PRICE_CHANGED);
        }
    }

    public GoodsValidator(GoodsFacadeService goodsFacadeService) {
        this.goodsFacadeService = goodsFacadeService;
    }

    public GoodsValidator() {
    }
}
