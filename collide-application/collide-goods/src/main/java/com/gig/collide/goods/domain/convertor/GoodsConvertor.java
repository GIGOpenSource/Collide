package com.gig.collide.goods.domain.convertor;

import com.gig.collide.api.goods.request.GoodsCreateRequest;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.goods.domain.entity.Goods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 商品转换器
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Mapper
public interface GoodsConvertor {
    
    GoodsConvertor INSTANCE = Mappers.getMapper(GoodsConvertor.class);
    
    /**
     * 创建请求转换为实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "soldCount", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    Goods mapFromCreateRequest(GoodsCreateRequest request);
    
    /**
     * 实体转换为VO
     */
    GoodsInfo mapToVo(Goods goods);
    
    /**
     * VO转换为实体
     */
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "version", ignore = true)
    Goods mapFromVo(GoodsInfo goodsInfo);
} 