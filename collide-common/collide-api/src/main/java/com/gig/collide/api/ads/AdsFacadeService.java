package com.gig.collide.api.ads;

import com.gig.collide.api.ads.request.*;
import com.gig.collide.api.ads.response.*;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 广告模块门面服务接口 - 极简版
 * 核心功能：广告管理 + 类型查询 + 随机展示
 *
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
public interface AdsFacadeService {

    // =================== 基础 CRUD 操作 ===================

    /**
     * 创建广告
     */
    Result<AdResponse> createAd(AdCreateRequest request);

    /**
     * 更新广告
     */
    Result<Void> updateAd(AdUpdateRequest request);

    /**
     * 删除广告
     */
    Result<Void> deleteAd(Long adId);

    /**
     * 获取广告详情
     */
    Result<AdResponse> getAd(Long adId);

    /**
     * 分页查询广告
     */
    Result<PageResponse<AdResponse>> queryAds(AdQueryRequest request);

    // =================== 核心展示功能 ===================

    /**
     * 根据类型获取广告列表 (按权重排序)
     */
    Result<List<AdResponse>> getAdsByType(AdTypeRequest request);

    /**
     * 随机获取指定类型的广告
     */
    Result<AdResponse> getRandomAdByType(String adType);

    /**
     * 随机获取指定类型的广告列表
     */
    Result<List<AdResponse>> getRandomAdsByType(AdTypeRequest request);

    // =================== 系统管理 ===================

    /**
     * 系统健康检查
     */
    Result<String> healthCheck();
}