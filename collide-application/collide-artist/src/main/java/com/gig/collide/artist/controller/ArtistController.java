package com.gig.collide.artist.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.request.*;
import com.gig.collide.api.artist.response.data.ArtistInfo;
import com.gig.collide.api.artist.response.data.ArtistStatistics;
import com.gig.collide.artist.domain.entity.Artist;
import com.gig.collide.artist.domain.service.ArtistDomainService;
import com.gig.collide.artist.infrastructure.convertor.ArtistConvertor;
import com.gig.collide.artist.infrastructure.exception.ArtistException;
import com.gig.collide.artist.infrastructure.exception.ArtistErrorCode;
import com.gig.collide.artist.param.ArtistApplicationParam;
import com.gig.collide.artist.param.ArtistQueryParam;
import com.gig.collide.artist.param.ArtistUpdateParam;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.MultiResult;
import com.gig.collide.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 博主管理Controller
 * @author GIG
 */
@Slf4j
@RestController
@RequestMapping("/artist")
public class ArtistController {

    @Autowired
    private ArtistDomainService artistDomainService;

    /**
     * 获取当前用户的博主信息
     */
    @GetMapping("/my-info")
    public Result<ArtistInfo> getMyArtistInfo() {
        String userId = (String) StpUtil.getLoginId();
        Artist artist = artistDomainService.getArtistByUserId(Long.valueOf(userId));
        
        if (artist == null) {
            throw new ArtistException(ArtistErrorCode.ARTIST_NOT_EXIST);
        }
        
        ArtistInfo artistInfo = ArtistConvertor.INSTANCE.toArtistInfo(artist);
        return Result.success(artistInfo);
    }

    /**
     * 根据ID查询博主信息
     */
    @GetMapping("/{artistId}")
    public Result<ArtistInfo> getArtistById(@PathVariable Long artistId) {
        Artist artist = artistDomainService.getArtistById(artistId);
        ArtistInfo artistInfo = ArtistConvertor.INSTANCE.toArtistInfo(artist);
        return Result.success(artistInfo);
    }

    /**
     * 根据用户ID查询博主信息
     */
    @GetMapping("/user/{userId}")
    public Result<ArtistInfo> getArtistByUserId(@PathVariable Long userId) {
        Artist artist = artistDomainService.getArtistByUserId(userId);
        
        if (artist == null) {
            throw new ArtistException(ArtistErrorCode.ARTIST_NOT_EXIST);
        }
        
        ArtistInfo artistInfo = ArtistConvertor.INSTANCE.toArtistInfo(artist);
        return Result.success(artistInfo);
    }

    /**
     * 更新博主信息
     */
    @PostMapping("/update")
    public Result<Boolean> updateArtistInfo(@Valid @RequestBody ArtistUpdateParam updateParam) {
        String userId = (String) StpUtil.getLoginId();
        Artist artist = artistDomainService.getArtistByUserId(Long.valueOf(userId));
        
        if (artist == null) {
            throw new ArtistException(ArtistErrorCode.ARTIST_NOT_EXIST);
        }
        
        // 复制更新字段
        BeanUtils.copyProperties(updateParam, artist, "id", "userId", "status", "level", "verified");
        
        artistDomainService.updateArtistInfo(artist);
        return Result.success(true);
    }

    /**
     * 搜索博主
     */
    @GetMapping("/search")
    public Result<List<ArtistInfo>> searchArtists(@RequestParam String keyword, 
                                                  @RequestParam(defaultValue = "10") Integer limit) {
        List<Artist> artists = artistDomainService.searchArtists(keyword, limit);
        List<ArtistInfo> artistInfos = ArtistConvertor.INSTANCE.toArtistInfoList(artists);
        return Result.success(artistInfos);
    }

    /**
     * 查询热门博主
     */
    @GetMapping("/hot")
    public Result<List<ArtistInfo>> getHotArtists(@RequestParam(defaultValue = "10") Integer limit) {
        List<Artist> artists = artistDomainService.getHotArtists(limit);
        List<ArtistInfo> artistInfos = ArtistConvertor.INSTANCE.toArtistInfoList(artists);
        return Result.success(artistInfos);
    }

    /**
     * 分页查询博主
     */
    @GetMapping("/page")
    public MultiResult<ArtistInfo> pageQueryArtists(ArtistQueryParam queryParam) {
        // TODO: 实现分页查询逻辑
        // 这里需要调用领域服务进行分页查询
        
        // 临时返回空结果
        return MultiResult.successMulti(List.of(), 0L, queryParam.getCurrentPage(), queryParam.getPageSize());
    }

    /**
     * 查询博主排行榜
     */
    @GetMapping("/ranking")
    public Result<List<ArtistInfo>> getArtistRanking(@RequestParam String rankType, 
                                                     @RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现排行榜查询逻辑
        return Result.success(List.of());
    }

    /**
     * 查询推荐博主
     */
    @GetMapping("/recommend")
    public Result<List<ArtistInfo>> getRecommendedArtists(@RequestParam(defaultValue = "10") Integer limit) {
        // TODO: 实现推荐博主查询逻辑
        return Result.success(List.of());
    }

    /**
     * 检查博主名称是否可用
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkArtistName(@RequestParam String artistName) {
        boolean available = artistDomainService.isArtistNameAvailable(artistName);
        return Result.success(available);
    }

    /**
     * 检查用户是否为博主
     */
    @GetMapping("/check-user/{userId}")
    public Result<Boolean> checkIsArtist(@PathVariable Long userId) {
        boolean isArtist = artistDomainService.isArtist(userId);
        return Result.success(isArtist);
    }

    /**
     * 查询博主统计信息
     */
    @GetMapping("/{artistId}/statistics")
    public Result<ArtistStatistics> getArtistStatistics(@PathVariable Long artistId) {
        // TODO: 实现统计信息查询逻辑
        return Result.success(new ArtistStatistics());
    }

    /**
     * 更新博主等级
     */
    @PostMapping("/{artistId}/update-level")
    public Result<Boolean> updateArtistLevel(@PathVariable Long artistId) {
        artistDomainService.updateArtistLevel(artistId);
        return Result.success(true);
    }

    /**
     * 申请成为博主 - 简化接口
     */
    @PostMapping("/apply")
    public Result<Boolean> applyToBeArtist(@Valid @RequestBody ArtistApplicationParam applicationParam) {
        String userId = (String) StpUtil.getLoginId();
        
        // 转换参数
        ArtistApplicationRequest request = new ArtistApplicationRequest();
        BeanUtils.copyProperties(applicationParam, request);
        request.setUserId(Long.valueOf(userId));
        
        // TODO: 调用Application Service处理申请
        log.info("用户 {} 申请成为博主: {}", userId, applicationParam.getArtistName());
        
        return Result.success(true);
    }

    /**
     * 查询申请状态
     */
    @GetMapping("/status")
    public Result<Object> getApplicationStatus() {
        String userId = (String) StpUtil.getLoginId();
        
        // TODO: 查询用户申请状态
        log.info("查询用户 {} 的申请状态", userId);
        
        // 临时返回默认状态
        return Result.success(Map.of(
            "hasApplication", false,
            "status", "NONE",
            "message", "暂无申请记录"
        ));
    }
} 