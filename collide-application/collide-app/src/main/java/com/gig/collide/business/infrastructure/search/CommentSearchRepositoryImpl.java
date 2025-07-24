package com.gig.collide.business.infrastructure.search;

import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.business.domain.search.CommentSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论搜索仓储实现
 * 
 * @author GIG Team
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentSearchRepositoryImpl implements CommentSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<SearchResult> searchComments(String keyword, Integer timeRange, 
                                            Integer pageNum, Integer pageSize, Boolean highlight) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT cm.id, cm.content, cm.target_id, cm.comment_type, ");
            sql.append("       cm.user_id, cm.like_count, cm.reply_count, ");
            sql.append("       cm.create_time, cm.is_pinned, cm.is_hot, ");
            sql.append("       u.username, u.nickname, u.avatar, ");
            sql.append("       (CASE WHEN cm.content LIKE ? THEN 10 ELSE 5 END) AS relevance_score ");
            sql.append("FROM t_comment cm ");
            sql.append("LEFT JOIN t_user u ON cm.user_id = u.id ");
            sql.append("WHERE cm.is_deleted = 0 AND cm.status = 'NORMAL' ");
            sql.append("  AND cm.content LIKE ? ");

            List<Object> params = new ArrayList<>();
            String likeKeyword = "%" + keyword + "%";
            
            // 相关度计算参数
            params.add(likeKeyword);
            
            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND cm.create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            // 关键词搜索条件
            params.add(likeKeyword);

            sql.append("ORDER BY relevance_score DESC, cm.like_count DESC, cm.create_time DESC ");
            sql.append("LIMIT ?, ?");

            int offset = (pageNum - 1) * pageSize;
            params.add(offset);
            params.add(pageSize);

            return jdbcTemplate.query(sql.toString(), 
                params.toArray(),
                (rs, rowNum) -> mapCommentSearchResult(rs, keyword, highlight)
            );

        } catch (Exception e) {
            log.error("搜索评论失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public long countComments(String keyword, Integer timeRange) {
        if (!StringUtils.hasText(keyword)) {
            return 0;
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM t_comment cm ");
            sql.append("WHERE cm.is_deleted = 0 AND cm.status = 'NORMAL' ");
            sql.append("  AND cm.content LIKE ? ");

            List<Object> params = new ArrayList<>();
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);

            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND cm.create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
            return count != null ? count : 0;

        } catch (Exception e) {
            log.error("统计评论搜索结果失败：keyword={}", keyword, e);
            return 0;
        }
    }

    /**
     * 映射评论搜索结果
     */
    private SearchResult mapCommentSearchResult(ResultSet rs, String keyword, Boolean highlight) throws SQLException {
        Long commentId = rs.getLong("id");
        String content = rs.getString("content");
        Long targetId = rs.getLong("target_id");
        String commentType = rs.getString("comment_type");
        Long userId = rs.getLong("user_id");
        Long likeCount = rs.getLong("like_count");
        Long replyCount = rs.getLong("reply_count");
        LocalDateTime createTime = rs.getTimestamp("create_time").toLocalDateTime();
        boolean isPinned = rs.getBoolean("is_pinned");
        boolean isHot = rs.getBoolean("is_hot");
        
        String username = rs.getString("username");
        String nickname = rs.getString("nickname");
        String avatar = rs.getString("avatar");
        Double relevanceScore = rs.getDouble("relevance_score");

        // 构建作者信息
        SearchResult.AuthorInfo authorInfo = SearchResult.AuthorInfo.builder()
                .userId(userId)
                .username(username)
                .nickname(nickname)
                .avatar(avatar)
                .bio(null)
                .verified(false)
                .build();

        // 构建统计信息
        SearchResult.StatisticsInfo statistics = SearchResult.StatisticsInfo.builder()
                .viewCount(null)
                .likeCount(likeCount)
                .commentCount(replyCount)
                .favoriteCount(null)
                .shareCount(null)
                .build();

        // 生成内容预览
        String contentPreview = content.length() > 150 ? content.substring(0, 150) + "..." : content;

        // 高亮处理
        String displayTitle = "评论";
        String displayDescription = content;
        String displayPreview = contentPreview;
        
        if (highlight != null && highlight) {
            displayDescription = highlightMatch(content, keyword);
            displayPreview = highlightMatch(contentPreview, keyword);
        }

        // 构建扩展信息
        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("commentType", commentType);
        extraInfo.put("targetId", targetId);
        extraInfo.put("isPinned", isPinned);
        extraInfo.put("isHot", isHot);

        return SearchResult.builder()
                .id(commentId)
                .resultType("COMMENT")
                .title(displayTitle)
                .description(displayDescription)
                .contentPreview(displayPreview)
                .coverUrl(null)
                .author(authorInfo)
                .statistics(statistics)
                .tags(new ArrayList<>())
                .contentType("COMMENT")
                .createTime(createTime)
                .publishTime(null)
                .relevanceScore(relevanceScore)
                .extraInfo(extraInfo)
                .build();
    }

    /**
     * 高亮匹配的关键词
     */
    private String highlightMatch(String text, String keyword) {
        if (!StringUtils.hasText(text) || !StringUtils.hasText(keyword)) {
            return text;
        }
        
        // 使用HTML标签高亮显示
        return text.replaceAll("(?i)" + keyword, "<mark>$0</mark>");
    }
} 