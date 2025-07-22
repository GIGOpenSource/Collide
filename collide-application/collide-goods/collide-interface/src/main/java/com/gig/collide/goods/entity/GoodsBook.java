package com.gig.collide.goods.entity;

import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.goods.request.GoodsBookRequest;
import com.gig.collide.api.user.constant.UserType;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import com.gig.collide.goods.entity.convertor.GoodsBookConvertor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Hollis
 */
@Setter
@Getter
public class GoodsBook extends BaseEntity {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 商品Id
     */
    private String goodsId;

    /**
     * 商品类型
     */
    private GoodsType goodsType;

    /**
     * 买家id
     */
    private String buyerId;

    /**
     * 买家id类型
     */
    private UserType buyerType;

    /**
     * 幂等号
     */
    private String identifier;

    /**
     * 预约成功时间
     */
    private Date bookSucceedTime;

    public static GoodsBook createBook(GoodsBookRequest request) {
        GoodsBook goodsBook = GoodsBookConvertor.INSTANCE.mapToEntity(request);
        goodsBook.setBookSucceedTime(new Date());
        return goodsBook;
    }
}
