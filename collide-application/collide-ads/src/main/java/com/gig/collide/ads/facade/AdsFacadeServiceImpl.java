package com.gig.collide.ads.facade;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.ads.domain.entity.Ad;
import com.gig.collide.ads.domain.service.AdService;
import com.gig.collide.api.ads.AdsFacadeService;
import com.gig.collide.api.ads.request.*;
import com.gig.collide.api.ads.response.*;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 广告模块门面服务实现 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Slf4j
@Component
@Service(version = "1.0.0", timeout = 30000)
@RequiredArgsConstructor
public class AdsFacadeServiceImpl implements AdsFacadeService {

    private final AdService adService;

    @Override
    public Result<AdResponse> createAd(AdCreateRequest request) {
        try {
            Ad ad = new Ad();
            BeanUtils.copyProperties(request, ad);
            
            Ad createdAd = adService.createAd(ad);
            AdResponse response = convertToResponse(createdAd);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建广告失败", e);
            return Result.failure("创建广告失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateAd(AdUpdateRequest request) {
        try {
            // 检查广告是否存在
            Ad existingAd = adService.getAdById(request.getId());
            if (existingAd == null) {
                return Result.failure("广告不存在");
            }
            
            // 更新字段
            Ad ad = new Ad();
            BeanUtils.copyProperties(request, ad);
            
            boolean success = adService.updateAd(ad);
            if (success) {
                return Result.success();
            } else {
                return Result.failure("更新广告失败");
            }
        } catch (Exception e) {
            log.error("更新广告失败", e);
            return Result.failure("更新广告失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteAd(Long adId) {
        try {
            // 检查广告是否存在
            Ad existingAd = adService.getAdById(adId);
            if (existingAd == null) {
                return Result.failure("广告不存在");
            }
            
            boolean success = adService.deleteAd(adId);
            if (success) {
                return Result.success();
            } else {
                return Result.failure("删除广告失败");
            }
        } catch (Exception e) {
            log.error("删除广告失败", e);
            return Result.failure("删除广告失败: " + e.getMessage());
        }
    }

    @Override
    public Result<AdResponse> getAd(Long adId) {
        try {
            Ad ad = adService.getAdById(adId);
            if (ad == null) {
                return Result.failure("广告不存在");
            }
            
            AdResponse response = convertToResponse(ad);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取广告详情失败", e);
            return Result.failure("获取广告详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<AdResponse>> queryAds(AdQueryRequest request) {
        try {
            Page<Ad> page = adService.queryAds(
                request.getAdName(), 
                request.getAdType(), 
                request.getIsActive(),
                request.getCurrentPage(), 
                request.getPageSize()
            );
            
            List<AdResponse> responseList = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            PageResponse<AdResponse> pageResponse = new PageResponse<>();
            pageResponse.setRecords(responseList);
            pageResponse.setTotal(page.getTotal());
            pageResponse.setCurrentPage((int) page.getCurrent());
            pageResponse.setPageSize((int) page.getSize());
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询广告失败", e);
            return Result.failure("查询广告失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<AdResponse>> getAdsByType(AdTypeRequest request) {
        try {
            List<Ad> ads;
            
            if (Boolean.TRUE.equals(request.getRandom())) {
                ads = adService.getRandomAdsByType(request.getAdType(), request.getLimit());
            } else {
                ads = adService.getAdsByType(request.getAdType());
                // 如果指定了limit，截取前N个
                if (request.getLimit() != null && ads.size() > request.getLimit()) {
                    ads = ads.subList(0, request.getLimit());
                }
            }
            
            if (CollectionUtils.isEmpty(ads)) {
                return Result.success(List.of());
            }
            
            List<AdResponse> responseList = ads.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return Result.success(responseList);
        } catch (Exception e) {
            log.error("根据类型获取广告失败", e);
            return Result.failure("根据类型获取广告失败: " + e.getMessage());
        }
    }

    @Override
    public Result<AdResponse> getRandomAdByType(String adType) {
        try {
            Ad ad = adService.getRandomAdByType(adType);
            if (ad == null) {
                return Result.failure("没有找到指定类型的广告");
            }
            
            AdResponse response = convertToResponse(ad);
            return Result.success(response);
        } catch (Exception e) {
            log.error("随机获取广告失败", e);
            return Result.failure("随机获取广告失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<AdResponse>> getRandomAdsByType(AdTypeRequest request) {
        try {
            List<Ad> ads = adService.getRandomAdsByType(request.getAdType(), request.getLimit());
            
            if (CollectionUtils.isEmpty(ads)) {
                return Result.success(List.of());
            }
            
            List<AdResponse> responseList = ads.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            return Result.success(responseList);
        } catch (Exception e) {
            log.error("随机获取广告列表失败", e);
            return Result.failure("随机获取广告列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> healthCheck() {
        try {
            // 简单的健康检查：查询广告数量
            List<Ad> ads = adService.getAdsByType("banner");
            return Result.success("广告系统运行正常，banner广告数量: " + ads.size());
        } catch (Exception e) {
            log.error("健康检查失败", e);
            return Result.failure("健康检查失败");
        }
    }

    /**
     * 转换实体到响应对象
     */
    private AdResponse convertToResponse(Ad ad) {
        AdResponse response = new AdResponse();
        BeanUtils.copyProperties(ad, response);
        return response;
    }
}