package com.gig.collide.ads.controller;

import com.gig.collide.api.ads.AdsFacadeService;
import com.gig.collide.api.ads.request.*;
import com.gig.collide.api.ads.response.*;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 广告管理控制器 - 极简版
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ads")
@RequiredArgsConstructor
@Validated
@Tag(name = "广告管理", description = "广告管理相关接口")
public class AdsController {

    private final AdsFacadeService adsFacadeService;

    @PostMapping
    @Operation(summary = "创建广告", description = "创建新的广告")
    public Result<AdResponse> createAd(@Valid @RequestBody AdCreateRequest request) {
        return adsFacadeService.createAd(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新广告", description = "更新指定广告信息")
    public Result<Void> updateAd(
            @Parameter(description = "广告ID") @PathVariable Long id,
            @Valid @RequestBody AdUpdateRequest request) {
        request.setId(id);
        return adsFacadeService.updateAd(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除广告", description = "删除指定广告")
    public Result<Void> deleteAd(@Parameter(description = "广告ID") @PathVariable Long id) {
        return adsFacadeService.deleteAd(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取广告详情", description = "根据ID获取广告详细信息")
    public Result<AdResponse> getAd(@Parameter(description = "广告ID") @PathVariable Long id) {
        return adsFacadeService.getAd(id);
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询广告", description = "根据条件分页查询广告列表")
    public Result<PageResponse<AdResponse>> queryAds(@Valid @RequestBody AdQueryRequest request) {
        return adsFacadeService.queryAds(request);
    }

    @PostMapping("/type")
    @Operation(summary = "根据类型获取广告", description = "根据广告类型获取广告列表")
    public Result<List<AdResponse>> getAdsByType(@Valid @RequestBody AdTypeRequest request) {
        return adsFacadeService.getAdsByType(request);
    }

    @GetMapping("/random/{adType}")
    @Operation(summary = "随机获取广告", description = "随机获取指定类型的单个广告")
    public Result<AdResponse> getRandomAdByType(
            @Parameter(description = "广告类型", example = "banner") @PathVariable String adType) {
        return adsFacadeService.getRandomAdByType(adType);
    }

    @PostMapping("/random")
    @Operation(summary = "随机获取广告列表", description = "随机获取指定类型的广告列表")
    public Result<List<AdResponse>> getRandomAdsByType(@Valid @RequestBody AdTypeRequest request) {
        // 强制设置为随机模式
        request.setRandom(true);
        return adsFacadeService.getRandomAdsByType(request);
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查广告系统运行状态")
    public Result<String> healthCheck() {
        return adsFacadeService.healthCheck();
    }
}