package com.gig.collide.chain.infrastructure.mapper;

import com.gig.collide.chain.domain.entity.ChainOperateInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 链操作 Mapper 接口
 * </p>
 *
 * @author GIG
 * @since 2025-07-19
 */
@Mapper
public interface ChainOperateInfoMapper extends BaseMapper<ChainOperateInfo> {
    /**
     * 扫描所有
     *
     * @return
     */
    List<ChainOperateInfo> scanAll();

    /**
     * 根据 ID 查询出最小的 ID
     * @param state
     * @return
     */
    public Long queryMinIdByState(String state);

}
