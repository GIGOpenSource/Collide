package com.gig.collide.artist.domain.service;

import com.gig.collide.api.artist.constant.ArtistLevel;
import com.gig.collide.api.artist.constant.ArtistStatus;
import com.gig.collide.artist.domain.entity.Artist;
import com.gig.collide.artist.domain.repository.ArtistRepository;
import com.gig.collide.artist.infrastructure.exception.ArtistException;
import com.gig.collide.artist.infrastructure.exception.ArtistErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 博主领域服务
 * @author GIG
 */
@Slf4j
@Service
public class ArtistDomainService {

    @Autowired
    private ArtistRepository artistRepository;

    /**
     * 创建博主
     */
    @Transactional(rollbackFor = Exception.class)
    public Artist createArtist(Long userId, String artistName, String bio, 
                              String applicationType, String categories) {
        // 检查用户是否已经是博主
        if (artistRepository.existsByUserId(userId)) {
            throw new ArtistException(ArtistErrorCode.USER_ALREADY_ARTIST);
        }

        // 检查博主名称是否已存在
        if (artistRepository.existsByArtistName(artistName)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_NAME_EXISTS);
        }

        Artist artist = new Artist();
        artist.setUserId(userId);
        artist.setArtistName(artistName);
        artist.setBio(bio);
        artist.setStatus(ArtistStatus.APPLYING);
        artist.setCategories(categories);
        artist.setLevel(ArtistLevel.NEWCOMER);
        artist.setFollowersCount(0);
        artist.setFollowingCount(0);
        artist.setWorksCount(0);
        artist.setTotalLikes(0L);
        artist.setTotalViews(0L);
        artist.setVerified(false);
        artist.setHotScore(BigDecimal.ZERO);
        artist.setInfluenceIndex(BigDecimal.ZERO);
        artist.setLastActiveTime(new Date());

        if (!artistRepository.save(artist)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_OPERATION_FAILED);
        }

        log.info("博主创建成功，用户ID: {}, 博主ID: {}", userId, artist.getId());
        return artist;
    }

    /**
     * 激活博主
     */
    @Transactional(rollbackFor = Exception.class)
    public void activateArtist(Long artistId, Long operatorId) {
        Artist artist = getArtistById(artistId);
        
        if (artist.getStatus() != ArtistStatus.REVIEWING) {
            throw new ArtistException(ArtistErrorCode.ARTIST_STATUS_NOT_ALLOWED);
        }

        artist.activate();
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_OPERATION_FAILED);
        }

        log.info("博主激活成功，博主ID: {}, 操作员: {}", artistId, operatorId);
    }

    /**
     * 暂停博主
     */
    @Transactional(rollbackFor = Exception.class)
    public void suspendArtist(Long artistId, Long operatorId, String reason) {
        Artist artist = getArtistById(artistId);
        
        if (!artist.canOperate()) {
            throw new ArtistException(ArtistErrorCode.ARTIST_STATUS_NOT_ALLOWED);
        }

        artist.suspend();
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_OPERATION_FAILED);
        }

        log.info("博主暂停成功，博主ID: {}, 操作员: {}, 原因: {}", artistId, operatorId, reason);
    }

    /**
     * 禁用博主
     */
    @Transactional(rollbackFor = Exception.class)
    public void disableArtist(Long artistId, Long operatorId, String reason) {
        Artist artist = getArtistById(artistId);

        artist.disable();
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_OPERATION_FAILED);
        }

        log.info("博主禁用成功，博主ID: {}, 操作员: {}, 原因: {}", artistId, operatorId, reason);
    }

    /**
     * 恢复博主
     */
    @Transactional(rollbackFor = Exception.class)
    public void restoreArtist(Long artistId, Long operatorId, String reason) {
        Artist artist = getArtistById(artistId);
        
        if (artist.getStatus() != ArtistStatus.SUSPENDED && artist.getStatus() != ArtistStatus.DISABLED) {
            throw new ArtistException(ArtistErrorCode.ARTIST_STATUS_NOT_ALLOWED);
        }

        artist.restore();
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_OPERATION_FAILED);
        }

        log.info("博主恢复成功，博主ID: {}, 操作员: {}, 原因: {}", artistId, operatorId, reason);
    }

    /**
     * 注销博主
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelArtist(Long artistId, Long operatorId, String reason) {
        Artist artist = getArtistById(artistId);

        artist.cancel();
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_OPERATION_FAILED);
        }

        log.info("博主注销成功，博主ID: {}, 操作员: {}, 原因: {}", artistId, operatorId, reason);
    }

    /**
     * 更新博主信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateArtistInfo(Artist artist) {
        if (!artist.canOperate()) {
            throw new ArtistException(ArtistErrorCode.ARTIST_STATUS_NOT_ALLOWED);
        }

        // 如果更新了博主名称，需要检查是否重复
        Artist existingArtist = artistRepository.findByArtistName(artist.getArtistName());
        if (existingArtist != null && !existingArtist.getId().equals(artist.getId())) {
            throw new ArtistException(ArtistErrorCode.ARTIST_NAME_EXISTS);
        }

        artist.updateActiveTime();
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.ARTIST_OPERATION_FAILED);
        }

        log.info("博主信息更新成功，博主ID: {}", artist.getId());
    }

    /**
     * 更新博主统计数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateArtistStatistics(Long artistId, Integer followersCount, Integer followingCount, 
                                     Integer worksCount, Long totalLikes, Long totalViews) {
        Artist artist = getArtistById(artistId);
        
        artist.updateStatistics(followersCount, followingCount, worksCount, totalLikes, totalViews);
        
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.STATISTICS_UPDATE_FAILED);
        }

        log.info("博主统计数据更新成功，博主ID: {}", artistId);
    }

    /**
     * 设置博主认证
     */
    @Transactional(rollbackFor = Exception.class)
    public void setArtistVerification(Long artistId, String verificationType, String verificationDesc) {
        Artist artist = getArtistById(artistId);
        
        if (!artist.canOperate()) {
            throw new ArtistException(ArtistErrorCode.ARTIST_STATUS_NOT_ALLOWED);
        }

        artist.setVerification(verificationType, verificationDesc);
        
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.VERIFICATION_OPERATION_FAILED);
        }

        log.info("博主认证设置成功，博主ID: {}, 认证类型: {}", artistId, verificationType);
    }

    /**
     * 取消博主认证
     */
    @Transactional(rollbackFor = Exception.class)
    public void revokeArtistVerification(Long artistId, Long operatorId, String reason) {
        Artist artist = getArtistById(artistId);
        
        artist.revokeVerification();
        
        if (!artistRepository.updateById(artist)) {
            throw new ArtistException(ArtistErrorCode.VERIFICATION_OPERATION_FAILED);
        }

        log.info("博主认证取消成功，博主ID: {}, 操作员: {}, 原因: {}", artistId, operatorId, reason);
    }

    /**
     * 更新博主等级
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateArtistLevel(Long artistId) {
        Artist artist = getArtistById(artistId);
        
        ArtistLevel newLevel = ArtistLevel.calculateLevel(artist.getFollowersCount());
        if (newLevel != artist.getLevel()) {
            artist.setLevel(newLevel);
            artist.updateActiveTime();
            
            if (!artistRepository.updateById(artist)) {
                throw new ArtistException(ArtistErrorCode.LEVEL_CALCULATION_FAILED);
            }

            log.info("博主等级更新成功，博主ID: {}, 新等级: {}", artistId, newLevel);
        }
    }

    /**
     * 根据ID获取博主
     */
    public Artist getArtistById(Long artistId) {
        Artist artist = artistRepository.findById(artistId);
        if (artist == null) {
            throw new ArtistException(ArtistErrorCode.ARTIST_NOT_EXIST);
        }
        return artist;
    }

    /**
     * 根据用户ID获取博主
     */
    public Artist getArtistByUserId(Long userId) {
        return artistRepository.findByUserId(userId);
    }

    /**
     * 检查用户是否为博主
     */
    public boolean isArtist(Long userId) {
        return artistRepository.existsByUserId(userId);
    }

    /**
     * 检查博主名称是否可用
     */
    public boolean isArtistNameAvailable(String artistName) {
        return !artistRepository.existsByArtistName(artistName);
    }

    /**
     * 查询热门博主
     */
    public List<Artist> getHotArtists(Integer limit) {
        return artistRepository.findHotArtists(limit);
    }

    /**
     * 搜索博主
     */
    public List<Artist> searchArtists(String keyword, Integer limit) {
        return artistRepository.searchArtists(keyword, limit);
    }
} 