package com.gig.collide.collection.infrastructure.mapper;

import com.gig.collide.collection.domain.entity.CollectionAirdropStream;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 藏品空投流水信息 Mapper 接口
 * </p>
 *
 * @author GIG
 * @since 2024-01-19
 */
@Mapper
public interface CollectionAirdropStreamMapper extends BaseMapper<CollectionAirdropStream> {
    /**
     * 根据标识符查询
     *
     * @param identifier
     * @param streamType
     * @param collectionId
     * @param recipientUserId
     * @return
     */
    CollectionAirdropStream selectByIdentifier(String identifier, String streamType, Long collectionId,String recipientUserId);

}
