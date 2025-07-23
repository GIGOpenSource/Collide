package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.social.constant.SocialPostStatus;
import com.gig.collide.api.social.constant.SocialPostType;
import com.gig.collide.social.domain.entity.SocialPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态 Mapper 接口
 * 
 * <p>完全去连表化设计，所有查询都是单表操作</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper
public interface SocialPostMapper extends BaseMapper<SocialPost> {

    /**
     * 分页查询用户时间线动态（无连表）
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 动态状态
     * @return 分页结果
     */
    IPage<SocialPost> selectUserTimelinePage(Page<SocialPost> page, 
                                             @Param("userId") Long userId,
                                             @Param("status") SocialPostStatus status);

    /**
     * 分页查询关注用户的动态流（无连表）
     * 应用层需要先查询关注关系，然后传入关注用户ID列表
     *
     * @param page 分页参数
     * @param userId 当前用户ID（用于日志记录）
     * @param followingUserIds 关注的用户ID列表
     * @param status 动态状态
     * @return 分页结果
     */
    IPage<SocialPost> selectFollowingFeedPage(Page<SocialPost> page,
                                              @Param("userId") Long userId,
                                              @Param("followingUserIds") List<Long> followingUserIds,
                                              @Param("status") SocialPostStatus status);

    /**
     * 分页查询热门动态（无连表）
     *
     * @param page 分页参数
     * @param status 动态状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param minHotScore 最小热度分数
     * @return 分页结果
     */
    IPage<SocialPost> selectHotPostsPage(Page<SocialPost> page,
                                         @Param("status") SocialPostStatus status,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("minHotScore") Double minHotScore);

    /**
     * 分页查询附近动态（无连表）
     *
     * @param page 分页参数
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 搜索半径（公里）
     * @param status 动态状态
     * @return 分页结果
     */
    IPage<SocialPost> selectNearbyPostsPage(Page<SocialPost> page,
                                            @Param("longitude") Double longitude,
                                            @Param("latitude") Double latitude,
                                            @Param("radius") Double radius,
                                            @Param("status") SocialPostStatus status);

    /**
     * 全文搜索动态（无连表）
     *
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @param status 动态状态
     * @param postType 动态类型（可选）
     * @return 分页结果
     */
    IPage<SocialPost> searchPostsPage(Page<SocialPost> page,
                                      @Param("keyword") String keyword,
                                      @Param("status") SocialPostStatus status,
                                      @Param("postType") SocialPostType postType);

    /**
     * 按话题查询动态（无连表）
     *
     * @param page 分页参数
     * @param topic 话题标签
     * @param status 动态状态
     * @return 分页结果
     */
    IPage<SocialPost> selectPostsByTopicPage(Page<SocialPost> page,
                                             @Param("topic") String topic,
                                             @Param("status") SocialPostStatus status);

    /**
     * 根据作者ID列表查询动态（无连表）
     * 用于时间线生成，应用层传入关注用户ID列表
     *
     * @param authorIds 作者ID列表
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量（可选）
     * @return 动态列表
     */
    List<SocialPost> selectPostsByAuthorIds(@Param("authorIds") List<Long> authorIds,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("limit") Integer limit);

    /**
     * 批量查询动态（无连表）
     *
     * @param postIds 动态ID列表
     * @return 动态列表
     */
    List<SocialPost> selectPostsByIds(@Param("postIds") List<Long> postIds);

    /**
     * 统计用户动态数量（无连表）
     *
     * @param userId 用户ID
     * @param status 动态状态
     * @return 动态数量
     */
    Long countUserPosts(@Param("userId") Long userId, @Param("status") SocialPostStatus status);

    /**
     * 批量更新动态热度分数（无连表）
     *
     * @param postIds 动态ID列表
     * @return 更新行数
     */
    int batchUpdateHotScore(@Param("postIds") List<Long> postIds);

    /**
     * 增加动态浏览数（无连表）
     *
     * @param postId 动态ID
     * @return 更新行数
     */
    int incrementViewCount(@Param("postId") Long postId);

    /**
     * 增加动态点赞数（无连表）
     *
     * @param postId 动态ID
     * @param increment 增加数量（可为负数）
     * @return 更新行数
     */
    int incrementLikeCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * 增加动态评论数（无连表）
     *
     * @param postId 动态ID
     * @param increment 增加数量（可为负数）
     * @return 更新行数
     */
    int incrementCommentCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * 增加动态转发数（无连表）
     *
     * @param postId 动态ID
     * @param increment 增加数量（可为负数）
     * @return 更新行数
     */
    int incrementShareCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * 增加动态收藏数（无连表）
     *
     * @param postId 动态ID
     * @param increment 增加数量（可为负数）
     * @return 更新行数
     */
    int incrementFavoriteCount(@Param("postId") Long postId, @Param("increment") Integer increment);

    /**
     * 查询需要更新热度分数的动态（无连表）
     *
     * @param limit 限制数量
     * @param lastUpdateTime 最后更新时间
     * @return 动态列表
     */
    List<SocialPost> selectPostsForHotScoreUpdate(@Param("limit") Integer limit,
                                                  @Param("lastUpdateTime") LocalDateTime lastUpdateTime);

    /**
     * 批量更新作者信息（无连表）
     * 当用户信息变更时，同步更新所有动态中的冗余作者信息
     *
     * @param authorId 作者ID
     * @param authorUsername 作者用户名
     * @param authorNickname 作者昵称
     * @param authorAvatar 作者头像
     * @param authorVerified 作者认证状态
     * @return 更新行数
     */
    int batchUpdateAuthorInfo(@Param("authorId") Long authorId,
                             @Param("authorUsername") String authorUsername,
                             @Param("authorNickname") String authorNickname,
                             @Param("authorAvatar") String authorAvatar,
                             @Param("authorVerified") Boolean authorVerified);
} 