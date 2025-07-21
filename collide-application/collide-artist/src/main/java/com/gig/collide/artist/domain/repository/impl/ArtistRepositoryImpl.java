package com.gig.collide.artist.domain.repository.impl;

import com.gig.collide.artist.domain.entity.Artist;
import com.gig.collide.artist.domain.repository.ArtistRepository;
import com.gig.collide.artist.infrastructure.mapper.ArtistMapper;
import com.gig.collide.api.artist.constant.ArtistLevel;
import com.gig.collide.api.artist.constant.ArtistStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 博主仓储实现
 * @author GIG
 */
@Repository
public class ArtistRepositoryImpl implements ArtistRepository {

    @Autowired
    private ArtistMapper artistMapper;

    @Override
    public boolean save(Artist artist) {
        return artistMapper.insert(artist) > 0;
    }

    @Override
    public boolean updateById(Artist artist) {
        return artistMapper.updateById(artist) > 0;
    }

    @Override
    public Artist findById(Long artistId) {
        return artistMapper.selectById(artistId);
    }

    @Override
    public Artist findByUserId(Long userId) {
        return artistMapper.findByUserId(userId);
    }

    @Override
    public Artist findByArtistName(String artistName) {
        return artistMapper.findByArtistName(artistName);
    }

    @Override
    public List<Artist> findByStatus(ArtistStatus status) {
        return artistMapper.findByStatus(status, null);
    }

    @Override
    public List<Artist> findByLevel(ArtistLevel level) {
        return artistMapper.findByLevel(level, null);
    }

    @Override
    public List<Artist> findHotArtists(Integer limit) {
        return artistMapper.findHotArtists(limit);
    }

    @Override
    public List<Artist> findByFollowersCountDesc(Integer limit) {
        return artistMapper.findByFollowersCountDesc(limit);
    }

    @Override
    public List<Artist> findByInfluenceIndexDesc(Integer limit) {
        return artistMapper.findByInfluenceIndexDesc(limit);
    }

    @Override
    public List<Artist> searchArtists(String keyword, Integer limit) {
        return artistMapper.searchArtists(keyword, limit);
    }

    @Override
    public List<Artist> pageQuery(Integer currentPage, Integer pageSize, ArtistStatus status, 
                                 ArtistLevel level, String keyword) {
        Integer offset = (currentPage - 1) * pageSize;
        return artistMapper.pageQuery(offset, pageSize, status, level, keyword);
    }

    @Override
    public Long countArtists() {
        return artistMapper.countArtists();
    }

    @Override
    public Long countByStatus(ArtistStatus status) {
        return artistMapper.countByStatus(status);
    }

    @Override
    public List<Artist> findByIds(List<Long> artistIds) {
        return artistMapper.findByIds(artistIds);
    }

    @Override
    public List<Artist> findVerifiedArtists(Integer limit) {
        return artistMapper.findVerifiedArtists(limit);
    }

    @Override
    public List<Artist> findActiveArtists(Integer days, Integer limit) {
        return artistMapper.findActiveArtists(days, limit);
    }

    @Override
    public boolean existsByArtistName(String artistName) {
        return artistMapper.countByArtistName(artistName) > 0;
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return artistMapper.countByUserId(userId) > 0;
    }
} 