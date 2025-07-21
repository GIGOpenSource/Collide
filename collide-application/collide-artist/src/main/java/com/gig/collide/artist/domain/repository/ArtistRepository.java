package com.gig.collide.artist.domain.repository;

import com.gig.collide.artist.domain.entity.Artist;
import com.gig.collide.api.artist.constant.ArtistStatus;
import com.gig.collide.api.artist.constant.ArtistLevel;
import com.gig.collide.api.artist.constant.ArtistCategory;

import java.util.List;

/**
 * 博主仓储接口
 * @author GIG
 */
public interface ArtistRepository {

    /**
     * 保存博主信息
     */
    boolean save(Artist artist);

    /**
     * 更新博主信息
     */
    boolean updateById(Artist artist);

    /**
     * 根据ID查询博主
     */
    Artist findById(Long artistId);

    /**
     * 根据用户ID查询博主
     */
    Artist findByUserId(Long userId);

    /**
     * 根据博主名称查询
     */
    Artist findByArtistName(String artistName);

    /**
     * 根据状态查询博主列表
     */
    List<Artist> findByStatus(ArtistStatus status);

    /**
     * 根据等级查询博主列表
     */
    List<Artist> findByLevel(ArtistLevel level);

    /**
     * 查询热门博主
     */
    List<Artist> findHotArtists(Integer limit);

    /**
     * 根据粉丝数排序查询
     */
    List<Artist> findByFollowersCountDesc(Integer limit);

    /**
     * 根据影响力指数排序查询
     */
    List<Artist> findByInfluenceIndexDesc(Integer limit);

    /**
     * 搜索博主（根据名称或简介）
     */
    List<Artist> searchArtists(String keyword, Integer limit);

    /**
     * 分页查询博主
     */
    List<Artist> pageQuery(Integer currentPage, Integer pageSize, ArtistStatus status, 
                          ArtistLevel level, String keyword);

    /**
     * 统计博主总数
     */
    Long countArtists();

    /**
     * 根据状态统计博主数量
     */
    Long countByStatus(ArtistStatus status);

    /**
     * 批量查询博主
     */
    List<Artist> findByIds(List<Long> artistIds);

    /**
     * 查询认证博主
     */
    List<Artist> findVerifiedArtists(Integer limit);

    /**
     * 根据最后活跃时间查询活跃博主
     */
    List<Artist> findActiveArtists(Integer days, Integer limit);

    /**
     * 检查博主名称是否存在
     */
    boolean existsByArtistName(String artistName);

    /**
     * 检查用户是否已是博主
     */
    boolean existsByUserId(Long userId);
} 