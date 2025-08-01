package com.gig.collide.ads.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.ads.domain.entity.Ad;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 广告数据访问层 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Mapper
public interface AdMapper extends BaseMapper<Ad> {

    /**
     * 根据广告类型查询启用的广告列表（按权重排序）
     */
    @Select("SELECT * FROM t_ad WHERE ad_type = #{adType} AND is_active = 1 ORDER BY sort_order DESC, id ASC")
    List<Ad> findByAdType(@Param("adType") String adType);

    /**
     * 随机获取指定类型的广告列表
     */
    @Select("SELECT * FROM t_ad WHERE ad_type = #{adType} AND is_active = 1 ORDER BY RAND() LIMIT #{limit}")
    List<Ad> findRandomByAdType(@Param("adType") String adType, @Param("limit") Integer limit);

    /**
     * 获取所有启用的广告类型
     */
    @Select("SELECT DISTINCT ad_type FROM t_ad WHERE is_active = 1")
    List<String> findAllAdTypes();

    /**
     * 按权重获取推荐广告
     */
    @Select("SELECT * FROM t_ad WHERE is_active = 1 ORDER BY sort_order DESC LIMIT #{limit}")
    List<Ad> findTopRecommended(@Param("limit") Integer limit);

    /**
     * 获取所有启用的广告（按权重排序）
     */
    @Select("SELECT * FROM t_ad WHERE is_active = 1 ORDER BY sort_order DESC, id ASC")
    List<Ad> findAllEnabled();
}