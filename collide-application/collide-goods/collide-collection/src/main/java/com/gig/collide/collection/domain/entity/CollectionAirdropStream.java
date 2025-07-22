package com.gig.collide.collection.domain.entity;

import com.gig.collide.api.goods.constant.GoodsEvent;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 藏品空投流水信息
 * </p>
 *
 * @author GIG
 * @since 2024-01-19
 */
@Getter
@Setter
@NoArgsConstructor
public class CollectionAirdropStream extends BaseEntity {

    /**
     * 藏品id
     */
    private Long collectionId;

    /**
     * '接收用户ID'
     */
    private String recipientUserId;

    /**
     * '空投数量'
     */
    private Integer quantity;

    /**
     * 流水类型
     */
    private GoodsEvent streamType;

    /**
     * '幂等号'
     */
    private String identifier;

    public CollectionAirdropStream(Collection collection, String identifier, GoodsEvent streamType, Integer quantity, String recipientUserId) {
        this.collectionId = collection.getId();
        this.quantity = quantity;
        this.streamType = streamType;
        this.identifier = identifier;
        this.recipientUserId = recipientUserId;
        super.setLockVersion(collection.getLockVersion());
        super.setDeleted(collection.getDeleted());
    }

}
