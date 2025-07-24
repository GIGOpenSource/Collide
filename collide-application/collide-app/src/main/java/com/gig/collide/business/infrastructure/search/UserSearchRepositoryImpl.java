package com.gig.collide.business.infrastructure.search;

import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.business.domain.search.UserSearchRepository;
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
 * 用户搜索仓储实现
 * 
 * @author GIG Team
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserSearchRepositoryImpl implements UserSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<SearchResult> searchUsers(String keyword, Integer pageNum, Integer pageSize, Boolean highlight) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            // 构建SQL查询
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT u.id, u.username, u.nickname, u.avatar, u.bio, u.created_time, ");
            sql.append("       p.gender, p.location, ");
            
            // 相关度计算：用户名匹配权重最高，昵称次之，简介最低
            sql.append("       (CASE ");
            sql.append("         WHEN u.username LIKE ? THEN 10 ");
            sql.append("         WHEN u.nickname LIKE ? THEN 8 ");
            sql.append("         WHEN u.bio LIKE ? THEN 5 ");
            sql.append("         ELSE 1 END) AS relevance_score ");
            
            sql.append("FROM t_user u ");
            sql.append("LEFT JOIN t_user_profile p ON u.id = p.user_id ");
            sql.append("WHERE u.deleted = 0 AND u.status = 1 ");
            sql.append("  AND (u.username LIKE ? OR u.nickname LIKE ? OR u.bio LIKE ?) ");
            sql.append("ORDER BY relevance_score DESC, u.created_time DESC ");
            sql.append("LIMIT ?, ?");

            String likeKeyword = "%" + keyword + "%";
            int offset = (pageNum - 1) * pageSize;

            return jdbcTemplate.query(sql.toString(), 
                new Object[]{
                    likeKeyword, likeKeyword, likeKeyword, // 相关度计算
                    likeKeyword, likeKeyword, likeKeyword, // WHERE条件
                    offset, pageSize
                },
                new UserSearchResultMapper(keyword, highlight)
            );

        } catch (Exception e) {
            log.error("搜索用户失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public long countUsers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return 0;
        }

        try {
            String sql = "SELECT COUNT(*) FROM t_user u " +
                        "WHERE u.deleted = 0 AND u.status = 1 " +
                        "  AND (u.username LIKE ? OR u.nickname LIKE ? OR u.bio LIKE ?)";
            
            String likeKeyword = "%" + keyword + "%";
            Long count = jdbcTemplate.queryForObject(sql, Long.class, likeKeyword, likeKeyword, likeKeyword);
            return count != null ? count : 0;

        } catch (Exception e) {
            log.error("统计用户搜索结果失败：keyword={}", keyword, e);
            return 0;
        }
    }

    @Override
    public List<SuggestionItem> getUserSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            String sql = "SELECT u.username, u.nickname, u.avatar, " +
                        "       (CASE WHEN u.username LIKE ? THEN 2 ELSE 1 END) AS relevance " +
                        "FROM t_user u " +
                        "WHERE u.deleted = 0 AND u.status = 1 " +
                        "  AND (u.username LIKE ? OR u.nickname LIKE ?) " +
                        "ORDER BY relevance DESC, u.username ASC " +
                        "LIMIT ?";

            String likeKeyword = keyword + "%"; // 前缀匹配

            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, likeKeyword, likeKeyword, limit},
                (rs, rowNum) -> {
                    String username = rs.getString("username");
                    String nickname = rs.getString("nickname");
                    String avatar = rs.getString("avatar");
                    double relevance = rs.getDouble("relevance");

                    // 构建显示文本
                    String displayText = StringUtils.hasText(nickname) ? 
                        nickname + " (@" + username + ")" : username;

                    // 高亮匹配部分
                    String highlightText = highlightMatch(displayText, keyword);

                    return SuggestionItem.builder()
                            .text(displayText)
                            .type("USER")
                            .searchCount(0L) // 用户建议不需要搜索次数
                            .relevanceScore(relevance)
                            .highlightText(highlightText)
                            .extraInfo(avatar) // 头像URL
                            .build();
                }
            );

        } catch (Exception e) {
            log.error("获取用户建议失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    /**
     * 用户搜索结果映射器
     */
    private static class UserSearchResultMapper implements RowMapper<SearchResult> {
        private final String keyword;
        private final Boolean highlight;

        public UserSearchResultMapper(String keyword, Boolean highlight) {
            this.keyword = keyword;
            this.highlight = highlight != null ? highlight : false;
        }

        @Override
        public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long userId = rs.getLong("id");
            String username = rs.getString("username");
            String nickname = rs.getString("nickname");
            String avatar = rs.getString("avatar");
            String bio = rs.getString("bio");
            LocalDateTime createTime = rs.getTimestamp("created_time").toLocalDateTime();
            String gender = rs.getString("gender");
            String location = rs.getString("location");
            Double relevanceScore = rs.getDouble("relevance_score");

            // 构建作者信息
            SearchResult.AuthorInfo authorInfo = SearchResult.AuthorInfo.builder()
                    .userId(userId)
                    .username(username)
                    .nickname(nickname)
                    .avatar(avatar)
                    .bio(bio)
                    .verified(false) // TODO: 实现用户认证逻辑
                    .build();

            // 构建标题和描述
            String title = StringUtils.hasText(nickname) ? nickname : username;
            String description = StringUtils.hasText(bio) ? bio : "用户：" + username;

            // 高亮处理
            if (highlight) {
                title = highlightMatch(title, keyword);
                description = highlightMatch(description, keyword);
            }

            return SearchResult.builder()
                    .id(userId)
                    .resultType("USER")
                    .title(title)
                    .description(description)
                    .contentPreview(null)
                    .coverUrl(avatar)
                    .author(authorInfo)
                    .statistics(null) // 用户搜索结果不需要统计信息
                    .tags(location != null ? List.of(location) : new ArrayList<>())
                    .contentType("USER")
                    .createTime(createTime)
                    .publishTime(null)
                    .relevanceScore(relevanceScore)
                    .extraInfo(gender != null ? Map.of("gender", gender, "location", location) : null)
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