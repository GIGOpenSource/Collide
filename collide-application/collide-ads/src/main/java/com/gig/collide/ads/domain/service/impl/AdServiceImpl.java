package com.gig.collide.ads.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.ads.domain.entity.Ad;
import com.gig.collide.ads.domain.service.AdService;
import com.gig.collide.ads.infrastructure.mapper.AdMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告业务服务实现类 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdMapper adMapper;

    @Override
    public Ad createAd(Ad ad) {
        ad.setCreateTime(LocalDateTime.now());
        ad.setUpdateTime(LocalDateTime.now());
        if (ad.getIsActive() == null) {
            ad.setIsActive(1);
        }
        if (ad.getSortOrder() == null) {
            ad.setSortOrder(0);
        }
        adMapper.insert(ad);
        return ad;
    }

    @Override
    public Ad getAdById(Long id) {
        return adMapper.selectById(id);
    }

    @Override
    public boolean updateAd(Ad ad) {
        ad.setUpdateTime(LocalDateTime.now());
        return adMapper.updateById(ad) > 0;
    }

    @Override
    public boolean deleteAd(Long id) {
        return adMapper.deleteById(id) > 0;
    }

    @Override
    public Page<Ad> queryAds(String adName, String adType, Integer isActive, Integer currentPage, Integer pageSize) {
        LambdaQueryWrapper<Ad> wrapper = Wrappers.lambdaQuery();
        
        if (StringUtils.hasText(adName)) {
            wrapper.like(Ad::getAdName, adName);
        }
        
        if (StringUtils.hasText(adType)) {
            wrapper.eq(Ad::getAdType, adType);
        }
        
        if (isActive != null) {
            wrapper.eq(Ad::getIsActive, isActive);
        }
        
        wrapper.orderByDesc(Ad::getSortOrder, Ad::getId);
        
        Page<Ad> page = new Page<>(currentPage, pageSize);
        return adMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Ad> getAdsByType(String adType) {
        return adMapper.findByAdType(adType);
    }

    @Override
    public Ad getRandomAdByType(String adType) {
        List<Ad> ads = adMapper.findRandomByAdType(adType, 1);
        return ads.isEmpty() ? null : ads.get(0);
    }

    @Override
    public List<Ad> getRandomAdsByType(String adType, Integer limit) {
        return adMapper.findRandomByAdType(adType, limit);
    }

    @Override
    public List<String> getAllAdTypes() {
        LambdaQueryWrapper<Ad> wrapper = Wrappers.lambdaQuery();
        wrapper.select(Ad::getAdType)
               .eq(Ad::getIsActive, 1)
               .groupBy(Ad::getAdType);
        
        List<Ad> ads = adMapper.selectList(wrapper);
        return ads.stream()
                .map(Ad::getAdType)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ad> getTopRecommendedAds(Integer limit) {
        LambdaQueryWrapper<Ad> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Ad::getIsActive, 1)
               .orderByDesc(Ad::getSortOrder, Ad::getId)
               .last("LIMIT " + limit);
        
        return adMapper.selectList(wrapper);
    }

    @Override
    public List<Ad> getAllEnabledAds() {
        LambdaQueryWrapper<Ad> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Ad::getIsActive, 1)
               .orderByDesc(Ad::getSortOrder, Ad::getId);
        
        return adMapper.selectList(wrapper);
    }

    @Override
    public boolean enableAd(Long id) {
        LambdaUpdateWrapper<Ad> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Ad::getId, id)
               .set(Ad::getIsActive, 1)
               .set(Ad::getUpdateTime, LocalDateTime.now());
        
        return adMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean disableAd(Long id) {
        LambdaUpdateWrapper<Ad> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Ad::getId, id)
               .set(Ad::getIsActive, 0)
               .set(Ad::getUpdateTime, LocalDateTime.now());
        
        return adMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean batchUpdateAdStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        LambdaUpdateWrapper<Ad> wrapper = Wrappers.lambdaUpdate();
        wrapper.in(Ad::getId, ids)
               .set(Ad::getIsActive, status)
               .set(Ad::getUpdateTime, LocalDateTime.now());
        
        return adMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateAdSortOrder(Long id, Integer sortOrder) {
        LambdaUpdateWrapper<Ad> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Ad::getId, id)
               .set(Ad::getSortOrder, sortOrder)
               .set(Ad::getUpdateTime, LocalDateTime.now());
        
        return adMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean batchUpdateAdSortOrder(List<Long> ids, List<Integer> sortOrders) {
        if (ids == null || ids.isEmpty() || sortOrders == null || 
            ids.size() != sortOrders.size()) {
            return false;
        }
        
        try {
            for (int i = 0; i < ids.size(); i++) {
                updateAdSortOrder(ids.get(i), sortOrders.get(i));
            }
            return true;
        } catch (Exception e) {
            log.error("批量更新广告权重失败", e);
            return false;
        }
    }
}