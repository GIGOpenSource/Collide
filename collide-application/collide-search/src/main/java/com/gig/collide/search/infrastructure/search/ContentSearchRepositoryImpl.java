package com.gig.collide.search.infrastructure.search;

import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.search.domain.search.ContentSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 内容搜索仓储实现（去连表设计）
 * 基于t_content单表查询，无JOIN操作
 * 
 * @author Collide Team
 * @version 1.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentSearchRepositoryImpl implements ContentSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<SearchResult> searchContent(String keyword, String contentType, Boolean onlyPublished, 
                                           Integer timeRange, Integer minLikeCount, 
                                           Integer pageNum, Integer pageSize, Boolean highlight) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT id, title, content_text, cover_url, content_type, ");
            sql.append("       author_id, author_nickname, author_avatar, ");
            sql.append("       view_count, like_count, comment_count, share_count, favorite_count, ");
            sql.append("       create_time, publish_time, status, ");
            
            // 相关度计算：标题匹配权重最高，内容次之
            sql.append("       (CASE ");
            sql.append("         WHEN title LIKE ? THEN 10 ");
            sql.append("         WHEN content_text LIKE ? THEN 8 ");
            sql.append("         ELSE 1 END) AS relevance_score ");
            
            sql.append("FROM t_content ");
            sql.append("WHERE deleted = 0 ");

            List<Object> params = new ArrayList<>();
            params.add("%" + keyword + "%"); // 标题匹配
            params.add("%" + keyword + "%"); // 内容匹配

            // 构建WHERE条件
            sql.append("  AND (title LIKE ? OR content_text LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");

            // 是否只搜索已发布内容
            if (onlyPublished != null && onlyPublished) {
                sql.append("  AND status = 'published' ");
            }

            // 内容类型过滤
            if (StringUtils.hasText(contentType)) {
                sql.append("  AND content_type = ? ");
                params.add(contentType);
            }

            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            // 最小点赞数过滤
            if (minLikeCount != null && minLikeCount > 0) {
                sql.append("  AND like_count >= ? ");
                params.add(minLikeCount);
            }

            sql.append("ORDER BY relevance_score DESC, like_count DESC, create_time DESC ");
            sql.append("LIMIT ?, ?");

            int offset = (pageNum - 1) * pageSize;
            params.add(offset);
            params.add(pageSize);

            log.info("执行内容搜索查询，关键词：{}，类型：{}，偏移：{}，限制：{}", 
                keyword, contentType, offset, pageSize);

            return jdbcTemplate.query(sql.toString(), 
                params.toArray(),
                new ContentSearchResultMapper(keyword, highlight)
            );

        } catch (Exception e) {
            log.error("搜索内容失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SearchResult> searchContentEnhanced(String keyword, String contentType, Boolean onlyPublished, 
                                                   Integer timeRange, Integer minLikeCount,
                                                   List<Long> categoryIds, List<Long> tagIds, String tagType,
                                                   Long userId, Boolean useUserInterest, Boolean hotContent,
                                                   Integer pageNum, Integer pageSize, Boolean highlight) {
        // TODO: 实现增强搜索逻辑，支持分类和标签筛选
        log.warn("增强搜索功能暂未实现，回退到基础搜索");
        return searchContent(keyword, contentType, onlyPublished, timeRange, minLikeCount, pageNum, pageSize, highlight);
    }

    @Override
    public long countContent(String keyword, String contentType, Boolean onlyPublished, 
                            Integer timeRange, Integer minLikeCount) {
        if (!StringUtils.hasText(keyword)) {
            return 0;
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM t_content ");
            sql.append("WHERE deleted = 0 ");
            sql.append("  AND (title LIKE ? OR content_text LIKE ?) ");

            List<Object> params = new ArrayList<>();
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");

            // 构建WHERE条件（与搜索逻辑保持一致）
            if (onlyPublished != null && onlyPublished) {
                sql.append("  AND status = 'published' ");
            }

            if (StringUtils.hasText(contentType)) {
                sql.append("  AND content_type = ? ");
                params.add(contentType);
            }

            if (timeRange != null && timeRange > 0) {
                sql.append("  AND create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            if (minLikeCount != null && minLikeCount > 0) {
                sql.append("  AND like_count >= ? ");
                params.add(minLikeCount);
            }

            Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
            return count != null ? count : 0;

        } catch (Exception e) {
            log.error("统计内容搜索结果失败：keyword={}", keyword, e);
            return 0;
        }
    }

    @Override
    public long countContentEnhanced(String keyword, String contentType, Boolean onlyPublished, 
                                    Integer timeRange, Integer minLikeCount,
                                    List<Long> categoryIds, List<Long> tagIds, String tagType,
                                    Long userId, Boolean useUserInterest) {
        // TODO: 实现增强统计逻辑
        log.warn("增强统计功能暂未实现，回退到基础统计");
        return countContent(keyword, contentType, onlyPublished, timeRange, minLikeCount);
    }

    @Override
    public List<SuggestionItem> getTagSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            // TODO: 实现标签建议功能，目前返回空列表
            log.debug("获取标签建议：关键词={}，限制={}", keyword, limit);
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("获取标签建议失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    /**
     * 内容搜索结果映射器
     */
    private static class ContentSearchResultMapper implements RowMapper<SearchResult> {
        private final String keyword;
        private final Boolean highlight;

        public ContentSearchResultMapper(String keyword, Boolean highlight) {
            this.keyword = keyword;
            this.highlight = highlight != null ? highlight : false;
        }

        @Override
        public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long contentId = rs.getLong("id");
            String title = rs.getString("title");
            String contentText = rs.getString("content_text");
            String coverUrl = rs.getString("cover_url");
            String contentType = rs.getString("content_type");
            Long authorId = rs.getLong("author_id");
            String authorNickname = rs.getString("author_nickname");
            String authorAvatar = rs.getString("author_avatar");
            long viewCount = rs.getLong("view_count");
            long likeCount = rs.getLong("like_count");
            long commentCount = rs.getLong("comment_count");
            long shareCount = rs.getLong("share_count");
            long favoriteCount = rs.getLong("favorite_count");
            LocalDateTime createTime = rs.getTimestamp("create_time").toLocalDateTime();
            LocalDateTime publishTime = rs.getTimestamp("publish_time") != null ? 
                rs.getTimestamp("publish_time").toLocalDateTime() : null;
            String status = rs.getString("status");
            Double relevanceScore = rs.getDouble("relevance_score");

            // 构建作者信息
            SearchResult.AuthorInfo authorInfo = SearchResult.AuthorInfo.builder()
                    .userId(authorId)
                    .username(null) // 内容表中没有用户名信息
                    .nickname(authorNickname)
                    .avatar(authorAvatar)
                    .bio(null)
                    .verified(false) // TODO: 根据作者状态判断认证状态
                    .build();

            // 构建统计信息
            SearchResult.StatisticsInfo statisticsInfo = SearchResult.StatisticsInfo.builder()
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .shareCount(shareCount)
                    .build();

            // 生成内容预览（截取前200字符）
            String contentPreview = null;
            if (StringUtils.hasText(contentText)) {
                contentPreview = contentText.length() > 200 ? 
                    contentText.substring(0, 200) + "..." : contentText;
            }

            // 高亮处理
            String displayTitle = title;
            String displayDescription = contentPreview;
            if (highlight) {
                displayTitle = highlightMatch(title, keyword);
                displayDescription = highlightMatch(contentPreview, keyword);
            }

            // 构建标签
            List<String> tags = new ArrayList<>();
            if (StringUtils.hasText(contentType)) {
                tags.add(contentType);
            }
            if (StringUtils.hasText(status)) {
                tags.add(status);
            }

            return SearchResult.builder()
                    .id(contentId)
                    .resultType("CONTENT")
                    .title(displayTitle)
                    .description(displayDescription)
                    .contentPreview(contentPreview)
                    .coverUrl(coverUrl)
                    .author(authorInfo)
                    .statistics(statisticsInfo)
                    .tags(tags)
                    .contentType(contentType)
                    .createTime(createTime)
                    .publishTime(publishTime)
                    .relevanceScore(relevanceScore)
                    .extraInfo(Map.of(
                        "status", status,
                        "contentLength", contentText != null ? contentText.length() : 0
                    ))
                    .build();
        }
    }

    /**
     * 高亮匹配的关键词
     */
    private static String highlightMatch(String text, String keyword) {
        if (!StringUtils.hasText(text) || !StringUtils.hasText(keyword)) {
            return text;
        }
        
        // 使用HTML标签高亮显示
        return text.replaceAll("(?i)" + keyword, "<mark>$0</mark>");
    }
} 