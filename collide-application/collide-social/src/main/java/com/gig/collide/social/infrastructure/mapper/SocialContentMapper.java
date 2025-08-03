package com.gig.collide.social.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.SocialContent;
import com.gig.collide.social.domain.service.InteractionStatsService.ContentStats;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 社交内容Mapper
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Mapper
public interface SocialContentMapper extends BaseMapper<SocialContent> {

    // ========== 统计相关 ==========
    
    @Update("UPDATE t_social_content SET like_count = like_count + 1 WHERE id = #{contentId} AND like_count >= 0")
    int incrementLikeCount(@Param("contentId") Long contentId);

    @Update("UPDATE t_social_content SET like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END WHERE id = #{contentId}")
    int decrementLikeCount(@Param("contentId") Long contentId);

    @Update("UPDATE t_social_content SET comment_count = comment_count + 1 WHERE id = #{contentId} AND comment_count >= 0")
    int incrementCommentCount(@Param("contentId") Long contentId);

    @Update("UPDATE t_social_content SET comment_count = CASE WHEN comment_count > 0 THEN comment_count - 1 ELSE 0 END WHERE id = #{contentId}")
    int decrementCommentCount(@Param("contentId") Long contentId);

    @Update("UPDATE t_social_content SET favorite_count = favorite_count + 1 WHERE id = #{contentId} AND favorite_count >= 0")
    int incrementFavoriteCount(@Param("contentId") Long contentId);

    @Update("UPDATE t_social_content SET favorite_count = CASE WHEN favorite_count > 0 THEN favorite_count - 1 ELSE 0 END WHERE id = #{contentId}")
    int decrementFavoriteCount(@Param("contentId") Long contentId);

    @Update("UPDATE t_social_content SET share_count = share_count + 1 WHERE id = #{contentId} AND share_count >= 0")
    int incrementShareCount(@Param("contentId") Long contentId);

    @Update("UPDATE t_social_content SET view_count = view_count + 1 WHERE id = #{contentId} AND view_count >= 0")
    int incrementViewCount(@Param("contentId") Long contentId);

    // ========== 统计查询 ==========
    
    @Select("SELECT like_count, comment_count, favorite_count, share_count, view_count FROM t_social_content WHERE id = #{contentId}")
    ContentStats getContentStats(@Param("contentId") Long contentId);

    @Select({
        "<script>",
        "SELECT like_count, comment_count, favorite_count, share_count, view_count",
        "FROM t_social_content WHERE id IN",
        "<foreach collection='contentIds' item='id' open='(' close=')' separator=','>#{id}</foreach>",
        "</script>"
    })
    List<ContentStats> getContentStatsBatch(@Param("contentIds") List<Long> contentIds);

    // ========== 统计重算 ==========
    
    @Select({
        "SELECT ",
        "  COALESCE((SELECT COUNT(*) FROM t_social_like WHERE content_id = #{contentId}), 0) as likeCount,",
        "  COALESCE((SELECT COUNT(*) FROM t_social_comment WHERE content_id = #{contentId} AND status = 1), 0) as commentCount,",
        "  COALESCE((SELECT COUNT(*) FROM t_social_favorite WHERE content_id = #{contentId}), 0) as favoriteCount,",
        "  COALESCE((SELECT COUNT(*) FROM t_social_share WHERE content_id = #{contentId}), 0) as shareCount,",
        "  view_count as viewCount",
        "FROM t_social_content WHERE id = #{contentId}"
    })
    ContentStats calculateRealContentStats(@Param("contentId") Long contentId);

    @Update({
        "UPDATE t_social_content SET ",
        "like_count = #{stats.likeCount}, ",
        "comment_count = #{stats.commentCount}, ",
        "favorite_count = #{stats.favoriteCount}, ",
        "share_count = #{stats.shareCount}, ",
        "view_count = #{stats.viewCount} ",
        "WHERE id = #{contentId}"
    })
    int updateContentStats(@Param("contentId") Long contentId, @Param("stats") ContentStats stats);

    // ========== 内容查询 ==========
    
    @Select("SELECT * FROM t_social_content WHERE user_id = #{userId} AND status = 1 ORDER BY create_time DESC")
    IPage<SocialContent> selectByUserId(Page<SocialContent> page, @Param("userId") Long userId);

    @Select("SELECT * FROM t_social_content WHERE category_id = #{categoryId} AND status = 1 ORDER BY create_time DESC")
    IPage<SocialContent> selectByCategoryId(Page<SocialContent> page, @Param("categoryId") Long categoryId);

    @Select("SELECT * FROM t_social_content WHERE status = 1 ORDER BY view_count DESC, like_count DESC, create_time DESC")
    IPage<SocialContent> selectHotContent(Page<SocialContent> page);

    @Select("SELECT * FROM t_social_content WHERE status = 1 ORDER BY create_time DESC")
    IPage<SocialContent> selectLatestContent(Page<SocialContent> page);

    @Select({
        "SELECT * FROM t_social_content WHERE status = 1 AND (",
        "title LIKE CONCAT('%', #{keyword}, '%') OR ",
        "description LIKE CONCAT('%', #{keyword}, '%')",
        ") ORDER BY create_time DESC"
    })
    IPage<SocialContent> searchContent(Page<SocialContent> page, @Param("keyword") String keyword);

    // ========== 统计计数 ==========
    
    @Select("SELECT COUNT(*) FROM t_social_content WHERE user_id = #{userId} AND status = 1")
    int countByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM t_social_content WHERE category_id = #{categoryId} AND status = 1")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}