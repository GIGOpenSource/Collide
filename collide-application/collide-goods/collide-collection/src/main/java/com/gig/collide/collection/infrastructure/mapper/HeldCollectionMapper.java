package com.gig.collide.collection.infrastructure.mapper;

import com.gig.collide.collection.domain.entity.HeldCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 藏品持有信息 Mapper 接口
 * </p>
 *
 * @author GIG
 * @since 2024-01-19
 */
@Mapper
public interface HeldCollectionMapper extends BaseMapper<HeldCollection> {

    /**
     * 查询出需要重新上链铸造的最小id
     *
     * @return
     */
    public Long queryMinIdForMint();

}
