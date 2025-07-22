package com.gig.collide.goods.entity.convertor;

import com.gig.collide.api.goods.model.GoodsBookVO;
import com.gig.collide.api.goods.request.GoodsBookRequest;
import com.gig.collide.goods.entity.GoodsBook;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Hollis
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface GoodsBookConvertor {

    GoodsBookConvertor INSTANCE = Mappers.getMapper(GoodsBookConvertor.class);

    /**
     * 转换实体
     *
     * @param request
     * @return
     */
    public GoodsBook mapToEntity(GoodsBookRequest request);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    public GoodsBookVO mapToVo(GoodsBook request);
}
