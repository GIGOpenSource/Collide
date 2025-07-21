package com.gig.collide.artist.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.request.ArtistApplicationRequest;
import com.gig.collide.api.artist.response.data.ArtistApplicationInfo;
import com.gig.collide.artist.param.ArtistApplicationParam;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博主申请管理Controller
 * @author GIG
 */
@Slf4j
@RestController
@RequestMapping("/artist/application")
public class ArtistApplicationController {

    /**
     * 申请成为博主
     */
    @PostMapping("/apply")
    public Result<Boolean> applyToBeArtist(@Valid @RequestBody ArtistApplicationParam applicationParam) {
        String userId = (String) StpUtil.getLoginId();
        
        // 转换参数
        ArtistApplicationRequest request = new ArtistApplicationRequest();
        BeanUtils.copyProperties(applicationParam, request);
        request.setUserId(Long.valueOf(userId));
        
        // TODO: 调用Application Service处理申请
        // applicationService.applyToBeArtist(request);
        
        return Result.success(true);
    }

    /**
     * 补充申请材料
     */
    @PostMapping("/{applicationId}/supplement")
    public Result<Boolean> supplementApplication(@PathVariable Long applicationId, 
                                                @RequestParam String supplementInfo) {
        String userId = (String) StpUtil.getLoginId();
        
        // TODO: 调用Application Service处理补充材料
        // applicationService.supplementApplication(applicationId, supplementInfo);
        
        return Result.success(true);
    }

    /**
     * 撤回申请
     */
    @PostMapping("/{applicationId}/withdraw")
    public Result<Boolean> withdrawApplication(@PathVariable Long applicationId, 
                                              @RequestParam String reason) {
        String userId = (String) StpUtil.getLoginId();
        
        // TODO: 调用Application Service处理撤回申请
        // applicationService.withdrawApplication(applicationId, Long.valueOf(userId), reason);
        
        return Result.success(true);
    }

    /**
     * 重新申请
     */
    @PostMapping("/reapply")
    public Result<Boolean> reapplyToBeArtist(@Valid @RequestBody ArtistApplicationParam applicationParam) {
        String userId = (String) StpUtil.getLoginId();
        
        // 转换参数
        ArtistApplicationRequest request = new ArtistApplicationRequest();
        BeanUtils.copyProperties(applicationParam, request);
        request.setUserId(Long.valueOf(userId));
        
        // TODO: 调用Application Service处理重新申请
        // applicationService.reapplyToBeArtist(request);
        
        return Result.success(true);
    }

    /**
     * 查询我的申请历史
     */
    @GetMapping("/my-history")
    public Result<List<ArtistApplicationInfo>> getMyApplicationHistory() {
        String userId = (String) StpUtil.getLoginId();
        
        // TODO: 调用Application Service查询申请历史
        // List<ArtistApplicationInfo> history = applicationService.queryUserApplicationHistory(Long.valueOf(userId));
        
        return Result.success(List.of());
    }

    /**
     * 查询申请详情
     */
    @GetMapping("/{applicationId}")
    public Result<ArtistApplicationInfo> getApplicationById(@PathVariable Long applicationId) {
        // TODO: 调用Application Service查询申请详情
        // ArtistApplicationInfo applicationInfo = applicationService.queryApplicationById(applicationId);
        
        return Result.success(new ArtistApplicationInfo());
    }

    /**
     * 查询申请状态
     */
    @GetMapping("/status")
    public Result<ArtistApplicationInfo> getMyApplicationStatus() {
        String userId = (String) StpUtil.getLoginId();
        
        // TODO: 查询用户最新的申请状态
        // ArtistApplicationInfo latestApplication = applicationService.queryLatestApplicationByUserId(Long.valueOf(userId));
        
        return Result.success(new ArtistApplicationInfo());
    }
} 