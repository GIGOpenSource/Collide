package com.gig.collide.search.infrastructure.repository;

import com.gig.collide.api.search.response.data.SearchResultItem;
import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.search.domain.repository.ContentSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容搜索仓储实现（完全去连表化设计）
 * 基于 t_search_content_index 单表查询，无任何 JOIN 操作
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
    public List<SearchResultItem> searchContent(String keyword, Integer offset, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            log.debug("搜索内容：关键词={}，偏移={}，限制={}", keyword, offset, limit);

            // 基于 t_search_content_index 表的完全去连表化查询
            // 支持标题、描述、内容文本、标签的全文搜索
            String sql = """
                SELECT 
                    content_id,
                    title,
                    description,
                    content_type,
                    cover_url,
                    author_id,
                    author_nickname,
                    author_avatar,
                    author_role,
                    author_verified,
                    category_id,
                    category_name,
                    category_path,
                    tags,
                    tag_names,
                    status,
                    review_status,
                    is_top,
                    is_hot,
                    is_recommended,
                    view_count,
                    like_count,
                    comment_count,
                    share_count,
                    favorite_count,
                    quality_score,
                    popularity_score,
                    search_weight,
                    published_time,
                    last_modified_time
                FROM t_search_content_index 
                WHERE deleted = 0 
                  AND status = 'PUBLISHED'
                  AND review_status = 'APPROVED'
                  AND (
                      title LIKE ? 
                      OR description LIKE ?
                      OR tag_names LIKE ?
                      OR MATCH(title, description, content_text, keywords) AGAINST(? IN NATURAL LANGUAGE MODE)
                  )
                ORDER BY 
                    is_top DESC,
                    is_hot DESC,
                    is_recommended DESC,
                    search_weight DESC,
                    popularity_score DESC,
                    quality_score DESC,
                    published_time DESC
                LIMIT ? OFFSET ?
                """;

            String likeKeyword = "%" + keyword + "%";
            
            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, likeKeyword, likeKeyword, keyword, limit, offset},
                (rs, rowNum) -> {
                    SearchResultItem item = SearchResultItem.builder()
                            .id(rs.getLong("content_id"))
                            .title(rs.getString("title"))
                            .description(rs.getString("description"))
                            .type("CONTENT")
                            .url("/content/" + rs.getLong("content_id"))
                            .imageUrl(rs.getString("cover_url"))
                            .build();

                    // 设置作者信息（完全冗余，无需连表）
                    item.setAuthorId(rs.getLong("author_id"));
                    item.setAuthorName(rs.getString("author_nickname"));
                    item.setAuthorAvatar(rs.getString("author_avatar"));

                    // 设置统计信息
                    item.setViewCount(rs.getLong("view_count"));
                    item.setLikeCount(rs.getLong("like_count"));
                    item.setCommentCount(rs.getLong("comment_count"));

                    // 设置相关度评分
                    double relevanceScore = calculateContentRelevance(
                        rs.getString("title"),
                        rs.getString("description"), 
                        rs.getString("tag_names"),
                        keyword
                    );
                    item.setRelevanceScore(relevanceScore);

                    // 设置内容特定的额外信息
                    item.setExtraInfo(buildContentExtraInfo(rs));

                    // 设置时间信息
                    if (rs.getTimestamp("published_time") != null) {
                        item.setPublishTime(rs.getTimestamp("published_time").toLocalDateTime());
                    } else if (rs.getTimestamp("last_modified_time") != null) {
                        item.setPublishTime(rs.getTimestamp("last_modified_time").toLocalDateTime());
                    }

                    return item;
                }
            );

        } catch (DataAccessException e) {
            log.error("搜索内容失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public long countContent(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return 0L;
        }

        try {
            log.debug("统计内容搜索结果数量：关键词={}", keyword);

            // 基于 t_search_content_index 表的完全去连表化计数查询
            String sql = """
                SELECT COUNT(*) 
                FROM t_search_content_index 
                WHERE deleted = 0 
                  AND status = 'PUBLISHED'
                  AND review_status = 'APPROVED'
                  AND (
                      title LIKE ? 
                      OR description LIKE ?
                      OR tag_names LIKE ?
                      OR MATCH(title, description, content_text, keywords) AGAINST(? IN NATURAL LANGUAGE MODE)
                  )
                """;

            String likeKeyword = "%" + keyword + "%";
            
            Long count = jdbcTemplate.queryForObject(sql, 
                new Object[]{likeKeyword, likeKeyword, likeKeyword, keyword}, 
                Long.class);
            
            return count != null ? count : 0L;

        } catch (DataAccessException e) {
            log.error("统计内容搜索结果失败：keyword={}", keyword, e);
            return 0L;
        }
    }

    @Override
    public List<SuggestionItem> getTagSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            log.debug("获取标签建议：关键词={}，限制={}", keyword, limit);

            // 基于 t_search_content_index 表的标签建议查询（完全去连表化）
            // 从内容的标签字段中提取热门标签
            String sql = """
                SELECT 
                    tag_names,
                    COUNT(*) as tag_usage_count,
                    SUM(view_count) as total_views,
                    SUM(like_count) as total_likes,
                    AVG(quality_score) as avg_quality
                FROM t_search_content_index 
                WHERE deleted = 0 
                  AND status = 'PUBLISHED'
                  AND review_status = 'APPROVED'
                  AND tag_names IS NOT NULL
                  AND tag_names LIKE ?
                GROUP BY tag_names
                ORDER BY 
                    tag_usage_count DESC,
                    total_views DESC,
                    avg_quality DESC
                LIMIT ?
                """;

            String likeKeyword = "%" + keyword + "%";

            List<SuggestionItem> suggestions = new ArrayList<>();
            
            jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, limit},
                rs -> {
                    String tagNames = rs.getString("tag_names");
                    if (StringUtils.hasText(tagNames)) {
                        // 解析标签名称（逗号分隔）
                        String[] tags = tagNames.split(",");
                        for (String tag : tags) {
                            tag = tag.trim();
                            if (StringUtils.hasText(tag) && tag.toLowerCase().contains(keyword.toLowerCase())) {
                                SuggestionItem suggestion = SuggestionItem.builder()
                                        .text(tag)
                                        .type("TAG")
                                        .searchCount(rs.getLong("tag_usage_count"))
                                        .relevanceScore(calculateTagRelevance(tag, keyword))
                                        .highlightText(highlightMatch(tag, keyword))
                                        .extraInfo("使用次数: " + rs.getLong("tag_usage_count"))
                                        .build();
                                
                                // 避免重复标签
                                boolean exists = suggestions.stream()
                                    .anyMatch(s -> s.getText().equals(tag));
                                if (!exists && suggestions.size() < limit) {
                                    suggestions.add(suggestion);
                                }
                            }
                        }
                    }
                }
            );

            // 按相关度排序
            suggestions.sort((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()));
            
            return suggestions.subList(0, Math.min(suggestions.size(), limit));

        } catch (DataAccessException e) {
            log.error("获取标签建议失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    /**
     * 计算内容相关度评分
     */
    private double calculateContentRelevance(String title, String description, String tagNames, String keyword) {
        double score = 0.0;
        String lowerKeyword = keyword.toLowerCase();

        // 标题完全匹配
        if (title != null && title.toLowerCase().equals(lowerKeyword)) {
            score += 10.0;
        }
        // 标题前缀匹配
        else if (title != null && title.toLowerCase().startsWith(lowerKeyword)) {
            score += 8.0;
        }
        // 标题包含匹配
        else if (title != null && title.toLowerCase().contains(lowerKeyword)) {
            score += 6.0;
        }
        
        // 描述包含匹配
        if (description != null && description.toLowerCase().contains(lowerKeyword)) {
            score += 4.0;
        }
        
        // 标签匹配
        if (tagNames != null && tagNames.toLowerCase().contains(lowerKeyword)) {
            score += 5.0;
        }

        return Math.max(score, 1.0); // 最小分数为1.0
    }

    /**
     * 计算标签相关度评分
     */
    private double calculateTagRelevance(String tag, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        String lowerTag = tag.toLowerCase();

        if (lowerTag.equals(lowerKeyword)) {
            return 10.0;
        } else if (lowerTag.startsWith(lowerKeyword)) {
            return 8.0;
        } else if (lowerTag.contains(lowerKeyword)) {
            return 6.0;
        }
        return 3.0;
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

    /**
     * 构建内容额外信息
     */
    private String buildContentExtraInfo(java.sql.ResultSet rs) throws java.sql.SQLException {
        StringBuilder extraInfo = new StringBuilder();
        
        // 内容类型
        String contentType = rs.getString("content_type");
        extraInfo.append(getContentTypeDisplayName(contentType));

        // 分类信息
        String categoryName = rs.getString("category_name");
        if (StringUtils.hasText(categoryName)) {
            extraInfo.append(" • ").append(categoryName);
        }

        // 作者信息
        String authorNickname = rs.getString("author_nickname");
        String authorRole = rs.getString("author_role");
        boolean authorVerified = rs.getInt("author_verified") == 1;
        
        if (StringUtils.hasText(authorNickname)) {
            extraInfo.append(" • 作者: ").append(authorNickname);
            
            if ("blogger".equals(authorRole) || "admin".equals(authorRole)) {
                extraInfo.append(" [").append("admin".equals(authorRole) ? "管理员" : "博主").append("]");
            }
            if (authorVerified) {
                extraInfo.append(" ✓");
            }
        }

        // 标记信息
        boolean isTop = rs.getInt("is_top") == 1;
        boolean isHot = rs.getInt("is_hot") == 1;
        boolean isRecommended = rs.getInt("is_recommended") == 1;
        
        if (isTop) {
            extraInfo.append(" • 置顶");
        }
        if (isHot) {
            extraInfo.append(" • 热门");
        }
        if (isRecommended) {
            extraInfo.append(" • 推荐");
        }

        // 统计信息
        long viewCount = rs.getLong("view_count");
        long likeCount = rs.getLong("like_count");
        
        extraInfo.append(" • 浏览: ").append(viewCount);
        extraInfo.append(" • 点赞: ").append(likeCount);

        return extraInfo.toString();
    }

    /**
     * 获取内容类型显示名称
     */
    private String getContentTypeDisplayName(String contentType) {
        if (contentType == null) {
            return "内容";
        }
        
        return switch (contentType.toUpperCase()) {
            case "NOVEL" -> "小说";
            case "COMIC" -> "漫画";
            case "SHORT_VIDEO" -> "短视频";
            case "LONG_VIDEO" -> "长视频";
            case "ARTICLE" -> "文章";
            case "AUDIO" -> "音频";
            default -> "内容";
        };
    }
} 