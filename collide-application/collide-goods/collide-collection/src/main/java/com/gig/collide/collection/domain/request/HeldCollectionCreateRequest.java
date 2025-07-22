package com.gig.collide.collection.domain.request;

import com.gig.collide.api.collection.constant.CollectionRarity;
import com.gig.collide.api.collection.request.CollectionAirDropRequest;
import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.goods.request.GoodsConfirmSaleRequest;
import com.gig.collide.collection.domain.constant.HeldCollectionEventType;
import com.gig.collide.collection.domain.entity.Collection;
import com.gig.collide.collection.domain.entity.CollectionAirdropStream;
import lombok.*;

import java.math.BigDecimal;

/**
 * @author GIG
 * @date 2024/01/17
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HeldCollectionCreateRequest extends BaseHeldCollectionRequest {
    /**
     * 藏品名称
     */
    private String name;

    /**
     * 藏品封面
     */
    private String cover;

    /**
     * 购入价格
     */
    private BigDecimal purchasePrice;

    /**
     * 参考价格
     */
    private BigDecimal referencePrice;

    /**
     * 稀有度
     */
    private CollectionRarity rarity;

    /**
     * 商品 id
     */
    private Long goodsId;

    /**
     * @see com.gig.collide.api.goods.constant.GoodsType
     */
    private String goodsType;

    /**
     * '持有人id'
     */
    private String userId;

    /**
     * '藏品编号'
     *
     * @deprecated 外部不要在传入这个值了，不再使用，改为内部自己计算
     */
    @Deprecated
    private String serialNo;

    /**
     * 序列号生成的 baseId，在商品为藏品时，该 id 为藏品 id，在商品为盲盒时，该 id 为盲盒 id
     */
    private String serialNoBaseId;

    /**
     * '业务Id'
     */
    private String bizNo;

    /**
     * '业务类型'
     */
    private String bizType;

    @Deprecated
    public HeldCollectionCreateRequest(GoodsConfirmSaleRequest goodsConfirmSaleRequest, String serialNo) {
        this.goodsId = goodsConfirmSaleRequest.goodsId();
        this.userId = goodsConfirmSaleRequest.userId();
        this.bizNo = goodsConfirmSaleRequest.bizNo();
        this.bizType = goodsConfirmSaleRequest.bizType();
        this.name = goodsConfirmSaleRequest.name();
        this.cover = goodsConfirmSaleRequest.cover();
        this.purchasePrice = goodsConfirmSaleRequest.purchasePrice();
        this.serialNo = serialNo;
    }

    public HeldCollectionCreateRequest(CollectionAirDropRequest airDropRequest, Collection collection, CollectionAirdropStream airdropStream) {
        this.goodsId = airDropRequest.getCollectionId();
        this.userId = airDropRequest.getRecipientUserId();
        this.name = collection.getName();
        this.cover = collection.getCover();
        this.purchasePrice = collection.getPrice();
        this.bizType = airDropRequest.getBizType().name();
        this.bizNo = airdropStream.getId().toString();
        this.serialNoBaseId = String.valueOf(collection.getId());
        this.goodsType = GoodsType.COLLECTION.name();
    }

    @Override
    public HeldCollectionEventType getEventType() {
        return HeldCollectionEventType.CREATE;
    }
}
