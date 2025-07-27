package com.gig.collide.search.infrastructure.repository;

import com.gig.collide.api.search.response.data.SearchResultItem;
import com.gig.collide.search.domain.repository.CommentSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论搜索仓储实现（完全去连表化设计）
 * 基于 t_search_comment_index 单表查询，无任何 JOIN 操作
 * 
 * @author Collide Team
 * @version 1.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentSearchRepositoryImpl implements CommentSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<SearchResultItem> searchComments(String keyword, Integer offset, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            log.debug("搜索评论：关键词={}，偏移={}，限制={}", keyword, offset, limit);

            // 基于 t_search_comment_index 表的完全去连表化查询
            // 支持评论内容的全文搜索
            String sql = """
                SELECT 
                    comment_id,
                    content,
                    comment_type,
                    target_id,
                    parent_comment_id,
                    root_comment_id,
                    user_id,
                    user_nickname,
                    user_avatar,
                    user_role,
                    user_verified,
                    target_title,
                    target_type,
                    target_author_id,
                    target_author_nickname,
                    reply_to_user_id,
                    reply_to_user_nickname,
                    status,
                    audit_status,
                    is_pinned,
                    is_hot,
                    is_essence,
                    like_count,
                    reply_count,
                    quality_score,
                    popularity_score,
                    search_weight,
                    location,
                    comment_time
                FROM t_search_comment_index 
                WHERE deleted = 0 
                  AND status = 'NORMAL'
                  AND audit_status = 'PASS'
                  AND (
                      content LIKE ?
                      OR MATCH(content, keywords) AGAINST(? IN NATURAL LANGUAGE MODE)
                  )
                ORDER BY 
                    is_pinned DESC,
                    is_hot DESC,
                    is_essence DESC,
                    search_weight DESC,
                    popularity_score DESC,
                    quality_score DESC,
                    like_count DESC,
                    comment_time DESC
                LIMIT ? OFFSET ?
                """;

            String likeKeyword = "%" + keyword + "%";
            
            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, keyword, limit, offset},
                (rs, rowNum) -> {
                    SearchResultItem item = SearchResultItem.builder()
                            .id(rs.getLong("comment_id"))
                            .title(buildCommentTitle(rs))
                            .description(rs.getString("content"))
                            .type("COMMENT")
                            .url(buildCommentUrl(rs))
                            .imageUrl(rs.getString("user_avatar"))
                            .build();

                    // 设置评论用户信息（完全冗余，无需连表）
                    item.setAuthorId(rs.getLong("user_id"));
                    item.setAuthorName(rs.getString("user_nickname"));
                    item.setAuthorAvatar(rs.getString("user_avatar"));

                    // 设置统计信息
                    item.setViewCount(0L); // 评论没有查看数，设为0
                    item.setLikeCount(rs.getLong("like_count"));
                    item.setCommentCount(rs.getLong("reply_count"));

                    // 设置相关度评分
                    double relevanceScore = calculateCommentRelevance(
                        rs.getString("content"),
                        keyword
                    );
                    item.setRelevanceScore(relevanceScore);

                    // 设置评论特定的额外信息
                    item.setExtraInfo(buildCommentExtraInfo(rs));

                    // 设置时间信息
                    if (rs.getTimestamp("comment_time") != null) {
                        item.setPublishTime(rs.getTimestamp("comment_time").toLocalDateTime());
                    }

                    return item;
                }
            );

        } catch (DataAccessException e) {
            log.error("搜索评论失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public long countComments(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return 0L;
        }

        try {
            log.debug("统计评论搜索结果数量：关键词={}", keyword);

            // 基于 t_search_comment_index 表的完全去连表化计数查询
            String sql = """
                SELECT COUNT(*) 
                FROM t_search_comment_index 
                WHERE deleted = 0 
                  AND status = 'NORMAL'
                  AND audit_status = 'PASS'
                  AND (
                      content LIKE ?
                      OR MATCH(content, keywords) AGAINST(? IN NATURAL LANGUAGE MODE)
                  )
                """;

            String likeKeyword = "%" + keyword + "%";
            
            Long count = jdbcTemplate.queryForObject(sql, 
                new Object[]{likeKeyword, keyword}, 
                Long.class);
            
            return count != null ? count : 0L;

        } catch (DataAccessException e) {
            log.error("统计评论搜索结果失败：keyword={}", keyword, e);
            return 0L;
        }
    }

    /**
     * 构建评论标题
     */
    private String buildCommentTitle(java.sql.ResultSet rs) throws java.sql.SQLException {
        StringBuilder title = new StringBuilder();
        
        String userNickname = rs.getString("user_nickname");
        String commentType = rs.getString("comment_type");
        String targetTitle = rs.getString("target_title");
        String replyToUserNickname = rs.getString("reply_to_user_nickname");
        
        // 评论用户
        if (StringUtils.hasText(userNickname)) {
            title.append(userNickname);
        } else {
            title.append("匿名用户");
        }
        
        // 评论类型和目标
        if ("CONTENT".equals(commentType)) {
            if (replyToUserNickname != null) {
                title.append(" 回复 ").append(replyToUserNickname);
            } else {
                title.append(" 评论了");
            }
            
            if (StringUtils.hasText(targetTitle)) {
                title.append(" 《").append(targetTitle).append("》");
            } else {
                title.append(" 内容");
            }
        } else if ("DYNAMIC".equals(commentType)) {
            if (replyToUserNickname != null) {
                title.append(" 回复 ").append(replyToUserNickname);
            } else {
                title.append(" 评论了动态");
            }
        } else {
            title.append(" 的评论");
        }
        
        return title.toString();
    }

    /**
     * 构建评论URL
     */
    private String buildCommentUrl(java.sql.ResultSet rs) throws java.sql.SQLException {
        String commentType = rs.getString("comment_type");
        long targetId = rs.getLong("target_id");
        long commentId = rs.getLong("comment_id");
        
        if ("CONTENT".equals(commentType)) {
            return "/content/" + targetId + "#comment-" + commentId;
        } else if ("DYNAMIC".equals(commentType)) {
            return "/dynamic/" + targetId + "#comment-" + commentId;
        } else {
            return "/comment/" + commentId;
        }
    }

    /**
     * 计算评论相关度评分
     */
    private double calculateCommentRelevance(String content, String keyword) {
        if (content == null || keyword == null) {
            return 1.0;
        }
        
        double score = 0.0;
        String lowerKeyword = keyword.toLowerCase();
        String lowerContent = content.toLowerCase();

        // 内容包含匹配程度
        if (lowerContent.contains(lowerKeyword)) {
            // 计算匹配位置（越靠前分数越高）
            int index = lowerContent.indexOf(lowerKeyword);
            double positionScore = Math.max(1.0, 10.0 - (index / 10.0));
            
            // 计算匹配密度（匹配次数）
            long matchCount = lowerContent.split(lowerKeyword, -1).length - 1;
            double densityScore = Math.min(5.0, matchCount);
            
            score = positionScore + densityScore;
        } else {
            score = 1.0; // 默认分数
        }

        return score;
    }

    /**
     * 构建评论额外信息
     */
    private String buildCommentExtraInfo(java.sql.ResultSet rs) throws java.sql.SQLException {
        StringBuilder extraInfo = new StringBuilder();
        
        // 用户角色和认证状态
        String userRole = rs.getString("user_role");
        boolean userVerified = rs.getInt("user_verified") == 1;
        
        if ("admin".equals(userRole)) {
            extraInfo.append("管理员");
        } else if ("blogger".equals(userRole)) {
            extraInfo.append("博主");
        } else if ("vip".equals(userRole)) {
            extraInfo.append("VIP用户");
        } else {
            extraInfo.append("用户");
        }
        
        if (userVerified) {
            extraInfo.append(" • 已认证");
        }

        // 评论特殊标记
        boolean isPinned = rs.getInt("is_pinned") == 1;
        boolean isHot = rs.getInt("is_hot") == 1;
        boolean isEssence = rs.getInt("is_essence") == 1;
        
        if (isPinned) {
            extraInfo.append(" • 置顶");
        }
        if (isHot) {
            extraInfo.append(" • 热门");
        }
        if (isEssence) {
            extraInfo.append(" • 精华");
        }

        // 统计信息
        long likeCount = rs.getLong("like_count");
        long replyCount = rs.getLong("reply_count");
        
        extraInfo.append(" • 点赞: ").append(likeCount);
        if (replyCount > 0) {
            extraInfo.append(" • 回复: ").append(replyCount);
        }

        // 目标内容信息
        String targetTitle = rs.getString("target_title");
        String targetAuthorNickname = rs.getString("target_author_nickname");
        
        if (StringUtils.hasText(targetTitle)) {
            extraInfo.append(" • 目标: ").append(targetTitle);
        }
        if (StringUtils.hasText(targetAuthorNickname)) {
            extraInfo.append(" • 原作者: ").append(targetAuthorNickname);
        }

        // 位置信息
        String location = rs.getString("location");
        if (StringUtils.hasText(location)) {
            extraInfo.append(" • ").append(location);
        }

        return extraInfo.toString();
    }
} 