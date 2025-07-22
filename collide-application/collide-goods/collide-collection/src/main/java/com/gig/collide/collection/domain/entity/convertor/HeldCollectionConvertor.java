package com.gig.collide.collection.domain.entity.convertor;

import com.gig.collide.api.collection.model.HeldCollectionDTO;
import com.gig.collide.api.collection.model.HeldCollectionVO;
import com.gig.collide.collection.domain.entity.HeldCollection;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Hollis
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface HeldCollectionConvertor {

    HeldCollectionConvertor INSTANCE = Mappers.getMapper(HeldCollectionConvertor.class);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    public HeldCollectionVO mapToVo(HeldCollection request);

    /**
     * 转换为 DTO
     * @param request
     * @return
     */
    public HeldCollectionDTO mapToDto(HeldCollection request);

    /**
     * 转换为vo
     *
     * @param request
     * @return
     */
    public List<HeldCollectionVO> mapToVo(List<HeldCollection> request);

    /**
     * 转换为实体
     *
     * @param request
     * @return
     */
    public HeldCollection mapToEntity(HeldCollectionVO request);

}
