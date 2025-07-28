package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.SocialPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态Mapper接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface SocialPostMapper extends BaseMapper<SocialPost> {

    /**
     * 分页查询用户动态
     */
    IPage<SocialPost> selectUserPosts(Page<SocialPost> page, @Param("authorId") Long authorId, 
                                      @Param("status") String status, @Param("viewerUserId") Long viewerUserId);

    /**
     * 分页查询热门动态
     */
    IPage<SocialPost> selectHotPosts(Page<SocialPost> page, @Param("minHotScore") BigDecimal minHotScore);

    /**
     * 分页查询最新动态
     */
    IPage<SocialPost> selectLatestPosts(Page<SocialPost> page, @Param("status") String status, 
                                        @Param("visibility") Integer visibility);

    /**
     * 根据话题搜索动态
     */
    IPage<SocialPost> selectPostsByTopic(Page<SocialPost> page, @Param("topic") String topic, 
                                         @Param("status") String status);

    /**
     * 根据关键词搜索动态内容
     */
    IPage<SocialPost> selectPostsByKeyword(Page<SocialPost> page, @Param("keyword") String keyword, 
                                           @Param("status") String status);

    /**
     * 根据地理位置搜索动态
     */
    IPage<SocialPost> selectPostsByLocation(Page<SocialPost> page, @Param("longitude") BigDecimal longitude, 
                                            @Param("latitude") BigDecimal latitude, @Param("radiusKm") Double radiusKm,
                                            @Param("status") String status);

    /**
     * 获取用户关注时间线动态
     */
    IPage<SocialPost> selectFollowingTimeline(Page<SocialPost> page, @Param("followingUserIds") List<Long> followingUserIds, 
                                              @Param("status") String status);

    /**
     * 批量获取动态信息
     */
    List<SocialPost> selectBatchByIds(@Param("postIds") List<Long> postIds, @Param("viewerUserId") Long viewerUserId);

    /**
     * 统计用户动态数量
     */
    Long countUserPosts(@Param("authorId") Long authorId, @Param("status") String status, 
                        @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 更新动态统计数据
     */
    int updatePostStats(@Param("postId") Long postId, @Param("likeCount") Long likeCount, 
                        @Param("commentCount") Long commentCount, @Param("shareCount") Long shareCount, 
                        @Param("viewCount") Long viewCount, @Param("favoriteCount") Long favoriteCount);

    /**
     * 更新动态热度分数
     */
    int updateHotScore(@Param("postId") Long postId, @Param("hotScore") BigDecimal hotScore);

    /**
     * 批量更新用户冗余信息
     */
    int batchUpdateUserInfo(@Param("userId") Long userId, @Param("username") String username, 
                            @Param("nickname") String nickname, @Param("avatar") String avatar, 
                            @Param("verified") Boolean verified);

    /**
     * 获取推荐动态（基于热度和时间）
     */
    IPage<SocialPost> selectRecommendedPosts(Page<SocialPost> page, @Param("userId") Long userId, 
                                             @Param("minHotScore") BigDecimal minHotScore, 
                                             @Param("hoursBack") Integer hoursBack);

    /**
     * 获取草稿动态
     */
    IPage<SocialPost> selectDraftPosts(Page<SocialPost> page, @Param("authorId") Long authorId);

    /**
     * 软删除动态
     */
    int softDeletePost(@Param("postId") Long postId, @Param("version") Integer version);

    /**
     * 增加浏览数
     */
    int incrementViewCount(@Param("postId") Long postId);

    /**
     * 增加点赞数
     */
    int incrementLikeCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * 增加评论数
     */
    int incrementCommentCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * 增加转发数
     */
    int incrementShareCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * 增加收藏数
     */
    int incrementFavoriteCount(@Param("postId") Long postId, @Param("increment") Integer increment);
}