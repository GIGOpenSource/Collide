package com.gig.collide.api.artist.service;

import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.response.data.ArtistInfo;

import java.util.List;
import java.util.Map;

/**
 * 博主推荐服务接口（内部服务，为推荐系统提供支持）
 * @author GIG
 */
public interface ArtistRecommendService {

    /**
     * 根据用户兴趣推荐博主
     * @param userId 用户ID
     * @param userTags 用户标签
     * @param limit 推荐数量
     * @return 推荐博主列表
     */
    List<ArtistInfo> recommendByUserInterests(Long userId, List<String> userTags, Integer limit);

    /**
     * 根据用户关注推荐相似博主
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐博主列表
     */
    List<ArtistInfo> recommendSimilarArtists(Long userId, Integer limit);

    /**
     * 根据博主分类推荐
     * @param categories 分类列表
     * @param excludeArtistIds 排除的博主ID
     * @param limit 推荐数量
     * @return 推荐博主列表
     */
    List<ArtistInfo> recommendByCategories(List<ArtistCategory> categories, List<Long> excludeArtistIds, Integer limit);

    /**
     * 根据地理位置推荐博主
     * @param userLocation 用户位置
     * @param radius 半径范围（公里）
     * @param limit 推荐数量
     * @return 推荐博主列表
     */
    List<ArtistInfo> recommendByLocation(String userLocation, Double radius, Integer limit);

    /**
     * 热门博主推荐
     * @param timeRange 时间范围（天）
     * @param category 分类过滤
     * @param limit 推荐数量
     * @return 热门博主列表
     */
    List<ArtistInfo> recommendHotArtists(Integer timeRange, ArtistCategory category, Integer limit);

    /**
     * 新秀博主推荐
     * @param category 分类过滤
     * @param limit 推荐数量
     * @return 新秀博主列表
     */
    List<ArtistInfo> recommendNewArtists(ArtistCategory category, Integer limit);

    /**
     * 根据内容偏好推荐博主
     * @param userId 用户ID
     * @param contentHistory 用户内容浏览历史
     * @param limit 推荐数量
     * @return 推荐博主列表
     */
    List<ArtistInfo> recommendByContentPreference(Long userId, Map<String, Object> contentHistory, Integer limit);

    /**
     * 协同过滤推荐博主
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐博主列表
     */
    List<ArtistInfo> recommendByCollaborativeFiltering(Long userId, Integer limit);

    /**
     * 计算博主相似度
     * @param artistId1 博主1ID
     * @param artistId2 博主2ID
     * @return 相似度分数 (0.0-1.0)
     */
    Double calculateArtistSimilarity(Long artistId1, Long artistId2);

    /**
     * 获取博主推荐权重
     * @param artistId 博主ID
     * @param userId 用户ID
     * @return 推荐权重
     */
    Double getArtistRecommendWeight(Long artistId, Long userId);

    /**
     * 更新博主热度分数
     * @param artistId 博主ID
     * @return 更新后的热度分数
     */
    Double updateArtistHotScore(Long artistId);

    /**
     * 获取用户可能感兴趣的博主
     * @param userId 用户ID
     * @param excludeFollowed 是否排除已关注的博主
     * @param limit 推荐数量
     * @return 推荐博主列表
     */
    List<ArtistInfo> getInterestedArtists(Long userId, Boolean excludeFollowed, Integer limit);

    /**
     * 根据时间段推荐活跃博主
     * @param timeSlot 时间段（如：morning, afternoon, evening, night）
     * @param category 分类过滤
     * @param limit 推荐数量
     * @return 活跃博主列表
     */
    List<ArtistInfo> recommendActiveArtistsByTime(String timeSlot, ArtistCategory category, Integer limit);

    /**
     * 获取博主影响力排行
     * @param category 分类过滤
     * @param timeRange 时间范围（天）
     * @param limit 排行数量
     * @return 影响力排行列表
     */
    List<ArtistInfo> getInfluenceRanking(ArtistCategory category, Integer timeRange, Integer limit);

    /**
     * 分析用户对博主的偏好
     * @param userId 用户ID
     * @return 偏好分析结果
     */
    Map<String, Object> analyzeUserArtistPreference(Long userId);

    /**
     * 预测用户对博主的兴趣度
     * @param userId 用户ID
     * @param artistId 博主ID
     * @return 兴趣度分数 (0.0-1.0)
     */
    Double predictUserInterest(Long userId, Long artistId);
} 