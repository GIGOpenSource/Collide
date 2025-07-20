package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.user.constant.UserType;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class GoodsBookRequest extends BaseRequest {
    /**
     * 操作幂等号
     */
    @NotNull(message = "identifier 不能为空")
    private String identifier;

    /**
     * 买家id
     */
    @NotNull(message = "买家id不能为空")
    private String buyerId;

    /**
     * 买家id类型
     */
    private UserType buyerType = UserType.CUSTOMER;


    /**
     * 商品Id
     */
    @NotNull(message = "商品Id不能为空")
    private String goodsId;

    /**
     * 商品类型
     */
    private GoodsType goodsType;

}
