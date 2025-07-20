package com.gig.collide.api.inventory.request;

import com.gig.collide.api.collection.request.CollectionAirDropRequest;
import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.order.model.TradeOrderVO;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class InventoryRequest extends BaseRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "goods is null")
    private String goodsId;

    /**
     * 商品ID
     */
    @NotNull(message = "goodsType is null")
    private GoodsType goodsType;

    /**
     * 唯一标识
     */
    private String identifier;

    /**
     * 库存数量
     */
    private Integer inventory;

    public InventoryRequest(OrderCreateRequest orderCreateRequest) {
        this.goodsId = orderCreateRequest.getGoodsId();
        this.goodsType = orderCreateRequest.getGoodsType();
        this.identifier = orderCreateRequest.getOrderId();
        this.inventory = orderCreateRequest.getItemCount();
    }

    public InventoryRequest(CollectionAirDropRequest request) {
        this.goodsId = request.getCollectionId().toString();
        this.goodsType = GoodsType.COLLECTION;
        this.identifier = request.getIdentifier();
        this.inventory = request.getQuantity();
    }

    public InventoryRequest(TradeOrderVO tradeOrderVO) {
        this.setGoodsId(tradeOrderVO.getGoodsId());
        this.setInventory(tradeOrderVO.getItemCount());
        this.setIdentifier(tradeOrderVO.getOrderId());
        this.setGoodsType(tradeOrderVO.getGoodsType());
    }
}
