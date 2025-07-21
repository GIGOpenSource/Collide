package com.gig.collide.artist.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.artist.domain.entity.Artist;
import com.gig.collide.api.artist.constant.ArtistLevel;
import com.gig.collide.api.artist.constant.ArtistStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博主Mapper
 * @author GIG
 */
@Mapper
public interface ArtistMapper extends BaseMapper<Artist> {

    /**
     * 根据用户ID查询博主
     */
    Artist findByUserId(@Param("userId") Long userId);

    /**
     * 根据博主名称查询
     */
    Artist findByArtistName(@Param("artistName") String artistName);

    /**
     * 根据状态查询博主列表
     */
    List<Artist> findByStatus(@Param("status") ArtistStatus status, @Param("limit") Integer limit);

    /**
     * 根据等级查询博主列表
     */
    List<Artist> findByLevel(@Param("level") ArtistLevel level, @Param("limit") Integer limit);

    /**
     * 查询热门博主
     */
    List<Artist> findHotArtists(@Param("limit") Integer limit);

    /**
     * 根据粉丝数排序查询
     */
    List<Artist> findByFollowersCountDesc(@Param("limit") Integer limit);

    /**
     * 根据影响力指数排序查询
     */
    List<Artist> findByInfluenceIndexDesc(@Param("limit") Integer limit);

    /**
     * 搜索博主（根据名称或简介）
     */
    List<Artist> searchArtists(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 分页查询博主
     */
    List<Artist> pageQuery(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize, 
                          @Param("status") ArtistStatus status, @Param("level") ArtistLevel level, 
                          @Param("keyword") String keyword);

    /**
     * 根据状态统计博主数量
     */
    Long countByStatus(@Param("status") ArtistStatus status);

    /**
     * 批量查询博主
     */
    List<Artist> findByIds(@Param("artistIds") List<Long> artistIds);

    /**
     * 查询认证博主
     */
    List<Artist> findVerifiedArtists(@Param("limit") Integer limit);

    /**
     * 根据最后活跃时间查询活跃博主
     */
    List<Artist> findActiveArtists(@Param("days") Integer days, @Param("limit") Integer limit);

    /**
     * 检查博主名称是否存在
     */
    int countByArtistName(@Param("artistName") String artistName);

    /**
     * 检查用户是否已是博主
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计博主总数
     */
    Long countArtists();
} 