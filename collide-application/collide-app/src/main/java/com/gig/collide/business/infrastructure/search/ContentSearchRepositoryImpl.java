package com.gig.collide.business.infrastructure.search;

import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.business.domain.search.ContentSearchRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容搜索仓储实现
 * 
 * @author GIG Team
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentSearchRepositoryImpl implements ContentSearchRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<SearchResult> searchContent(String keyword, String contentType, Boolean onlyPublished,
                                           Integer timeRange, Integer minLikeCount,
                                           Integer pageNum, Integer pageSize, Boolean highlight) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            // 构建SQL查询
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT c.id, c.title, c.description, c.content_type, c.cover_url, ");
            sql.append("       c.author_id, c.tags, c.status, c.view_count, c.like_count, ");
            sql.append("       c.comment_count, c.favorite_count, c.share_count, ");
            sql.append("       c.created_time, c.published_time, ");
            sql.append("       u.username, u.nickname, u.avatar, ");
            
            // 相关度计算：标题匹配权重最高，描述次之
            sql.append("       (CASE ");
            sql.append("         WHEN c.title LIKE ? THEN 10 ");
            sql.append("         WHEN c.description LIKE ? THEN 8 ");
            sql.append("         WHEN c.tags LIKE ? THEN 6 ");
            sql.append("         ELSE 3 END) AS relevance_score ");
            
            sql.append("FROM t_content c ");
            sql.append("LEFT JOIN t_user u ON c.author_id = u.id ");
            sql.append("WHERE c.deleted = 0 ");

            List<Object> params = new ArrayList<>();
            String likeKeyword = "%" + keyword + "%";
            
            // 相关度计算参数
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);

            // 发布状态过滤
            if (onlyPublished != null && onlyPublished) {
                sql.append("  AND c.status = 'PUBLISHED' AND c.review_status = 'APPROVED' ");
            }

            // 内容类型过滤
            if (StringUtils.hasText(contentType)) {
                sql.append("  AND c.content_type = ? ");
                params.add(contentType);
            }

            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND c.created_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            // 最小点赞数过滤
            if (minLikeCount != null && minLikeCount > 0) {
                sql.append("  AND c.like_count >= ? ");
                params.add(minLikeCount);
            }

            // 关键词搜索条件
            sql.append("  AND (c.title LIKE ? OR c.description LIKE ? OR c.tags LIKE ?) ");
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);

            sql.append("ORDER BY relevance_score DESC, c.like_count DESC, c.created_time DESC ");
            sql.append("LIMIT ?, ?");

            int offset = (pageNum - 1) * pageSize;
            params.add(offset);
            params.add(pageSize);

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
    public long countContent(String keyword, String contentType, Boolean onlyPublished,
                            Integer timeRange, Integer minLikeCount) {
        if (!StringUtils.hasText(keyword)) {
            return 0;
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM t_content c ");
            sql.append("WHERE c.deleted = 0 ");

            List<Object> params = new ArrayList<>();
            String likeKeyword = "%" + keyword + "%";

            // 发布状态过滤
            if (onlyPublished != null && onlyPublished) {
                sql.append("  AND c.status = 'PUBLISHED' AND c.review_status = 'APPROVED' ");
            }

            // 内容类型过滤
            if (StringUtils.hasText(contentType)) {
                sql.append("  AND c.content_type = ? ");
                params.add(contentType);
            }

            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND c.created_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            // 最小点赞数过滤
            if (minLikeCount != null && minLikeCount > 0) {
                sql.append("  AND c.like_count >= ? ");
                params.add(minLikeCount);
            }

            // 关键词搜索条件
            sql.append("  AND (c.title LIKE ? OR c.description LIKE ? OR c.tags LIKE ?) ");
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);

            Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
            return count != null ? count : 0;

        } catch (Exception e) {
            log.error("统计内容搜索结果失败：keyword={}", keyword, e);
            return 0;
        }
    }

    @Override
    public List<SuggestionItem> getTagSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            // 从tags JSON字段中提取标签，这里使用简化的方式
            String sql = "SELECT DISTINCT c.tags, COUNT(*) as tag_count " +
                        "FROM t_content c " +
                        "WHERE c.deleted = 0 AND c.tags IS NOT NULL " +
                        "  AND c.tags LIKE ? " +
                        "GROUP BY c.tags " +
                        "ORDER BY tag_count DESC " +
                        "LIMIT ?";

            String likeKeyword = "%" + keyword + "%";

            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, limit},
                (rs, rowNum) -> {
                    String tagsJson = rs.getString("tags");
                    long tagCount = rs.getLong("tag_count");

                    try {
                        // 解析JSON标签
                        List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
                        
                        // 查找匹配的标签
                        for (String tag : tags) {
                            if (tag.toLowerCase().contains(keyword.toLowerCase())) {
                                String highlightText = highlightMatch(tag, keyword);
                                
                                return SuggestionItem.builder()
                                        .text(tag)
                                        .type("TAG")
                                        .searchCount(tagCount)
                                        .relevanceScore(calculateTagRelevance(tag, keyword))
                                        .highlightText(highlightText)
                                        .extraInfo(null)
                                        .build();
                            }
                        }
                    } catch (Exception e) {
                        log.debug("解析标签JSON失败：{}", tagsJson);
                    }

                    return null;
                }
            ).stream()
             .filter(item -> item != null)
             .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            log.error("获取标签建议失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    /**
     * 内容搜索结果映射器
     */
    private class ContentSearchResultMapper implements RowMapper<SearchResult> {
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
            String description = rs.getString("description");
            String contentType = rs.getString("content_type");
            String coverUrl = rs.getString("cover_url");
            Long authorId = rs.getLong("author_id");
            String tagsJson = rs.getString("tags");
            String status = rs.getString("status");
            
            Long viewCount = rs.getLong("view_count");
            Long likeCount = rs.getLong("like_count");
            Long commentCount = rs.getLong("comment_count");
            Long favoriteCount = rs.getLong("favorite_count");
            Long shareCount = rs.getLong("share_count");
            
            LocalDateTime createTime = rs.getObject("created_time", LocalDateTime.class);
            LocalDateTime publishTime = rs.getObject("published_time", LocalDateTime.class);
            
            String username = rs.getString("username");
            String nickname = rs.getString("nickname");
            String avatar = rs.getString("avatar");
            Double relevanceScore = rs.getDouble("relevance_score");

            // 解析标签
            List<String> tags = new ArrayList<>();
            if (StringUtils.hasText(tagsJson)) {
                try {
                    tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    log.debug("解析标签JSON失败：{}", tagsJson);
                }
            }

            // 构建作者信息
            SearchResult.AuthorInfo authorInfo = SearchResult.AuthorInfo.builder()
                    .userId(authorId)
                    .username(username)
                    .nickname(nickname)
                    .avatar(avatar)
                    .bio(null)
                    .verified(false)
                    .build();

            // 构建统计信息
            SearchResult.StatisticsInfo statistics = SearchResult.StatisticsInfo.builder()
                    .viewCount(viewCount)
                    .likeCount(likeCount)
                    .commentCount(commentCount)
                    .favoriteCount(favoriteCount)
                    .shareCount(shareCount)
                    .build();

            // 生成内容预览
            String contentPreview = StringUtils.hasText(description) ? 
                (description.length() > 200 ? description.substring(0, 200) + "..." : description) : "";

            // 高亮处理
            String displayTitle = title;
            String displayDescription = description;
            String displayPreview = contentPreview;
            
            if (highlight) {
                displayTitle = highlightMatch(title, keyword);
                displayDescription = highlightMatch(description, keyword);
                displayPreview = highlightMatch(contentPreview, keyword);
            }

            // 构建扩展信息
            Map<String, Object> extraInfo = new HashMap<>();
            extraInfo.put("contentType", contentType);
            extraInfo.put("status", status);

            return SearchResult.builder()
                    .id(contentId)
                    .resultType("CONTENT")
                    .title(displayTitle)
                    .description(displayDescription)
                    .contentPreview(displayPreview)
                    .coverUrl(coverUrl)
                    .author(authorInfo)
                    .statistics(statistics)
                    .tags(tags)
                    .contentType(contentType)
                    .createTime(createTime)
                    .publishTime(publishTime)
                    .relevanceScore(relevanceScore)
                    .extraInfo(extraInfo)
                    .build();
        }
    }

    /**
     * 计算标签相关度
     */
    private double calculateTagRelevance(String tag, String keyword) {
        if (tag.equalsIgnoreCase(keyword)) {
            return 10.0;
        } else if (tag.toLowerCase().startsWith(keyword.toLowerCase())) {
            return 8.0;
        } else if (tag.toLowerCase().contains(keyword.toLowerCase())) {
            return 6.0;
        }
        return 3.0;
    }

    @Override
    public List<SearchResult> searchContentEnhanced(String keyword, String contentType, Boolean onlyPublished,
                                                    Integer timeRange, Integer minLikeCount,
                                                    List<Long> categoryIds, List<Long> tagIds, String tagType,
                                                    Long userId, Boolean useUserInterest, Boolean hotContent,
                                                    Integer pageNum, Integer pageSize, Boolean highlight) {
        try {
            // 构建增强SQL查询
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT c.id, c.title, c.description, c.content_type, c.cover_url, ");
            sql.append("       c.author_id, c.tags, c.categories, c.status, c.view_count, c.like_count, ");
            sql.append("       c.comment_count, c.favorite_count, c.share_count, ");
            sql.append("       c.created_time, c.published_time, ");
            sql.append("       u.username, u.nickname, u.avatar, ");
            
            // 增强相关度计算，包含热度分数
            sql.append("       (CASE ");
            if (StringUtils.hasText(keyword)) {
                sql.append("         WHEN c.title LIKE ? THEN 10 ");
                sql.append("         WHEN c.description LIKE ? THEN 8 ");
                sql.append("         WHEN c.tags LIKE ? THEN 6 ");
                sql.append("         ELSE 3 END) ");
            } else {
                sql.append("         1 END) ");
            }
            
            // 添加热度分数加权
            if (Boolean.TRUE.equals(hotContent)) {
                sql.append("       + (c.view_count * 0.1 + c.like_count * 0.3 + c.comment_count * 0.2) / 100 ");
            }
            sql.append("       AS relevance_score ");
            
            sql.append("FROM t_content c ");
            sql.append("LEFT JOIN t_user u ON c.author_id = u.id ");
            
            // 添加分类和标签关联表
            if ((categoryIds != null && !categoryIds.isEmpty()) || 
                (tagIds != null && !tagIds.isEmpty())) {
                if (categoryIds != null && !categoryIds.isEmpty()) {
                    sql.append("LEFT JOIN t_content_category cc ON c.id = cc.content_id ");
                }
                if (tagIds != null && !tagIds.isEmpty()) {
                    sql.append("LEFT JOIN t_content_tag ct ON c.id = ct.content_id ");
                    if (StringUtils.hasText(tagType)) {
                        sql.append("LEFT JOIN t_tag t ON ct.tag_id = t.id ");
                    }
                }
            }
            
            sql.append("WHERE c.deleted = 0 ");

            List<Object> params = new ArrayList<>();
            
            // 关键词搜索参数
            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword + "%";
                params.add(likeKeyword);
                params.add(likeKeyword);
                params.add(likeKeyword);
                
                sql.append("  AND (c.title LIKE ? OR c.description LIKE ? OR c.tags LIKE ?) ");
                params.add(likeKeyword);
                params.add(likeKeyword);
                params.add(likeKeyword);
            }

            // 发布状态过滤
            if (onlyPublished != null && onlyPublished) {
                sql.append("  AND c.status = 'PUBLISHED' AND c.review_status = 'APPROVED' ");
            }

            // 内容类型过滤
            if (StringUtils.hasText(contentType)) {
                sql.append("  AND c.content_type = ? ");
                params.add(contentType);
            }

            // 分类筛选
            if (categoryIds != null && !categoryIds.isEmpty()) {
                sql.append("  AND cc.category_id IN (");
                for (int i = 0; i < categoryIds.size(); i++) {
                    if (i > 0) sql.append(", ");
                    sql.append("?");
                    params.add(categoryIds.get(i));
                }
                sql.append(") ");
            }

            // 标签筛选
            if (tagIds != null && !tagIds.isEmpty()) {
                sql.append("  AND ct.tag_id IN (");
                for (int i = 0; i < tagIds.size(); i++) {
                    if (i > 0) sql.append(", ");
                    sql.append("?");
                    params.add(tagIds.get(i));
                }
                sql.append(") ");
                
                // 标签类型过滤
                if (StringUtils.hasText(tagType)) {
                    sql.append("  AND t.tag_type = ? ");
                    params.add(tagType);
                }
            }

            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND c.created_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            // 最小点赞数过滤
            if (minLikeCount != null && minLikeCount > 0) {
                sql.append("  AND c.like_count >= ? ");
                params.add(minLikeCount);
            }

            // 排序：热门内容按热度排序，否则按相关度和时间排序
            if (Boolean.TRUE.equals(hotContent)) {
                sql.append("ORDER BY relevance_score DESC, c.like_count DESC, c.view_count DESC, c.created_time DESC ");
            } else {
                sql.append("ORDER BY relevance_score DESC, c.created_time DESC ");
            }

            // 分页
            if (pageNum != null && pageSize != null) {
                int offset = (pageNum - 1) * pageSize;
                sql.append("LIMIT ? OFFSET ? ");
                params.add(pageSize);
                params.add(offset);
            }

            log.debug("增强内容搜索SQL: {}", sql.toString());
            log.debug("参数: {}", params);

            return jdbcTemplate.query(sql.toString(), params.toArray(), new ContentSearchRowMapper(highlight, keyword));

        } catch (Exception e) {
            log.error("增强内容搜索失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public long countContentEnhanced(String keyword, String contentType, Boolean onlyPublished,
                                    Integer timeRange, Integer minLikeCount,
                                    List<Long> categoryIds, List<Long> tagIds, String tagType,
                                    Long userId, Boolean useUserInterest) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(DISTINCT c.id) FROM t_content c ");
            
            // 添加分类和标签关联表
            if ((categoryIds != null && !categoryIds.isEmpty()) || 
                (tagIds != null && !tagIds.isEmpty())) {
                if (categoryIds != null && !categoryIds.isEmpty()) {
                    sql.append("LEFT JOIN t_content_category cc ON c.id = cc.content_id ");
                }
                if (tagIds != null && !tagIds.isEmpty()) {
                    sql.append("LEFT JOIN t_content_tag ct ON c.id = ct.content_id ");
                    if (StringUtils.hasText(tagType)) {
                        sql.append("LEFT JOIN t_tag t ON ct.tag_id = t.id ");
                    }
                }
            }
            
            sql.append("WHERE c.deleted = 0 ");

            List<Object> params = new ArrayList<>();
            
            // 关键词搜索
            if (StringUtils.hasText(keyword)) {
                String likeKeyword = "%" + keyword + "%";
                sql.append("  AND (c.title LIKE ? OR c.description LIKE ? OR c.tags LIKE ?) ");
                params.add(likeKeyword);
                params.add(likeKeyword);
                params.add(likeKeyword);
            }

            // 发布状态过滤
            if (onlyPublished != null && onlyPublished) {
                sql.append("  AND c.status = 'PUBLISHED' AND c.review_status = 'APPROVED' ");
            }

            // 内容类型过滤
            if (StringUtils.hasText(contentType)) {
                sql.append("  AND c.content_type = ? ");
                params.add(contentType);
            }

            // 分类筛选
            if (categoryIds != null && !categoryIds.isEmpty()) {
                sql.append("  AND cc.category_id IN (");
                for (int i = 0; i < categoryIds.size(); i++) {
                    if (i > 0) sql.append(", ");
                    sql.append("?");
                    params.add(categoryIds.get(i));
                }
                sql.append(") ");
            }

            // 标签筛选
            if (tagIds != null && !tagIds.isEmpty()) {
                sql.append("  AND ct.tag_id IN (");
                for (int i = 0; i < tagIds.size(); i++) {
                    if (i > 0) sql.append(", ");
                    sql.append("?");
                    params.add(tagIds.get(i));
                }
                sql.append(") ");
                
                // 标签类型过滤
                if (StringUtils.hasText(tagType)) {
                    sql.append("  AND t.tag_type = ? ");
                    params.add(tagType);
                }
            }

            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND c.created_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            // 最小点赞数过滤
            if (minLikeCount != null && minLikeCount > 0) {
                sql.append("  AND c.like_count >= ? ");
                params.add(minLikeCount);
            }

            return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Long.class);

        } catch (Exception e) {
            log.error("统计增强内容搜索结果失败", e);
            return 0;
        }
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
     * 内容搜索结果行映射器
     */
    private static class ContentSearchRowMapper implements RowMapper<SearchResult> {
        
        private final Boolean highlight;
        private final String keyword;
        private final ObjectMapper objectMapper;
        
        public ContentSearchRowMapper(Boolean highlight, String keyword) {
            this.highlight = highlight;
            this.keyword = keyword;
            this.objectMapper = new ObjectMapper();
        }
        
        @Override
        public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            SearchResult result = new SearchResult();
            
            try {
                // 基本信息
                result.setId(rs.getLong("id"));
                result.setType("content");
                result.setTitle(processHighlight(rs.getString("title")));
                result.setDescription(processHighlight(rs.getString("description")));
                
                // 内容信息
                Map<String, Object> content = new HashMap<>();
                content.put("contentType", rs.getString("content_type"));
                content.put("status", rs.getInt("status"));
                content.put("authorId", rs.getLong("author_id"));
                content.put("authorName", rs.getString("author_name"));
                content.put("categoryId", rs.getLong("category_id"));
                content.put("categoryName", rs.getString("category_name"));
                            content.put("createTime", rs.getObject("create_time", LocalDateTime.class));
            content.put("updateTime", rs.getObject("update_time", LocalDateTime.class));
                
                // 统计信息
                content.put("viewCount", rs.getInt("view_count"));
                content.put("likeCount", rs.getInt("like_count"));
                content.put("commentCount", rs.getInt("comment_count"));
                content.put("shareCount", rs.getInt("share_count"));
                content.put("favoriteCount", rs.getInt("favorite_count"));
                
                // 标签信息
                String tagsJson = rs.getString("tags");
                if (StringUtils.hasText(tagsJson)) {
                    try {
                        List<Map<String, Object>> tags = objectMapper.readValue(
                            tagsJson, 
                            new TypeReference<List<Map<String, Object>>>() {}
                        );
                        content.put("tags", tags);
                    } catch (Exception e) {
                        log.warn("解析标签JSON失败: {}", tagsJson, e);
                        content.put("tags", new ArrayList<>());
                    }
                } else {
                    content.put("tags", new ArrayList<>());
                }
                
                result.setContent(content);
                
                // 计算相关性评分（可以基于匹配度、时间等因素）
                result.setScore(calculateRelevanceScore(rs));
                
            } catch (Exception e) {
                log.error("映射搜索结果失败", e);
                throw new SQLException("映射搜索结果失败", e);
            }
            
            return result;
        }
        
        /**
         * 处理高亮显示
         */
        private String processHighlight(String text) {
            if (!highlight || !StringUtils.hasText(text) || !StringUtils.hasText(keyword)) {
                return text;
            }
            
            // 简单的高亮处理，将关键词用 <em> 标签包围
            String highlightedText = text.replaceAll(
                "(?i)" + keyword.replaceAll("[\\[\\]{}()*+?.\\\\^$|]", "\\\\$0"),
                "<em>$0</em>"
            );
            
            return highlightedText;
        }
        
        /**
         * 计算相关性评分
         */
        private double calculateRelevanceScore(ResultSet rs) throws SQLException {
            double score = 0.0;
            
            try {
                // 基础分数
                score += 1.0;
                
                // 时间因子（最近的内容得分更高）
                LocalDateTime createTime = rs.getObject("create_time", LocalDateTime.class);
                LocalDateTime now = LocalDateTime.now();
                long daysDiff = java.time.Duration.between(createTime, now).toDays();
                double timeFactor = Math.max(0.1, 1.0 - (daysDiff / 365.0)); // 一年内的内容有时间加权
                score += timeFactor * 0.2;
                
                // 互动因子（点赞、评论、分享等）
                int viewCount = rs.getInt("view_count");
                int likeCount = rs.getInt("like_count");
                int commentCount = rs.getInt("comment_count");
                int shareCount = rs.getInt("share_count");
                
                double interactionScore = Math.log(viewCount + 1) * 0.1 +
                                        Math.log(likeCount + 1) * 0.3 +
                                        Math.log(commentCount + 1) * 0.4 +
                                        Math.log(shareCount + 1) * 0.5;
                score += Math.min(interactionScore, 2.0); // 最大2分
                
                // 状态因子（已发布的内容得分更高）
                int status = rs.getInt("status");
                if (status == 1) { // 假设1表示已发布
                    score += 0.5;
                }
                
            } catch (Exception e) {
                log.warn("计算相关性评分失败", e);
                score = 1.0; // 默认分数
            }
            
            return Math.round(score * 100.0) / 100.0; // 保留两位小数
        }
    }
} 