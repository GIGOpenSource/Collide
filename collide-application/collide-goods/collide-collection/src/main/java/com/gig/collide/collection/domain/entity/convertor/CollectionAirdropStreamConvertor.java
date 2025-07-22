package com.gig.collide.collection.domain.entity.convertor;

import com.gig.collide.api.collection.model.AirDropStreamVO;
import com.gig.collide.collection.domain.entity.CollectionAirdropStream;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Hollis
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CollectionAirdropStreamConvertor {

    CollectionAirdropStreamConvertor INSTANCE = Mappers.getMapper(CollectionAirdropStreamConvertor.class);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    @Mapping(target = "airDropTime", source = "request.gmtCreate")
    public AirDropStreamVO mapToVo(CollectionAirdropStream request);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    public List<AirDropStreamVO> mapToVo(List<CollectionAirdropStream> request);
}
