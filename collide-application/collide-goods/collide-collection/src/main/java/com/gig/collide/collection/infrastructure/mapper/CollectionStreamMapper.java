package com.gig.collide.collection.infrastructure.mapper;

import com.gig.collide.collection.domain.entity.CollectionStream;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 藏品流水信息 Mapper 接口
 * </p>
 *
 * @author GIG
 * @since 2024-01-19
 */
@Mapper
public interface CollectionStreamMapper extends BaseMapper<CollectionStream> {
    /**
     * 根据标识符查询
     *
     * @param identifier
     * @param streamType
     * @param collectionId
     * @return
     */
    CollectionStream selectByIdentifier(String identifier, String streamType, Long collectionId);

}
