package com.gig.collide.api.collection.request;

import com.gig.collide.api.goods.constant.GoodsEvent;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseCollectionRequest extends BaseRequest {

    /**
     * 幂等号
     */
    @NotNull(message = "identifier is not null")
    private String identifier;

    /**
     * '藏品id'
     */
    private Long collectionId;

    /**
     * 获取事件类型
     * @return
     */
    public abstract GoodsEvent getEventType();
}
