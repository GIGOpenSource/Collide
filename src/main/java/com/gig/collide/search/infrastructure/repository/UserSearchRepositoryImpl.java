package com.gig.collide.search.infrastructure.repository;

import com.gig.collide.api.search.response.data.SearchResultItem;
import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.search.domain.repository.UserSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户搜索仓储实现（完全去连表化设计）
 * 基于 t_search_user_index 单表查询，无任何 JOIN 操作
 * 
 * @author Collide Team
 * @version 1.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserSearchRepositoryImpl implements UserSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<SearchResultItem> searchUsers(String keyword, Integer offset, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            log.debug("搜索用户：关键词={}，偏移={}，限制={}", keyword, offset, limit);

            // 基于 t_search_user_index 表的完全去连表化查询
            // 支持用户名、昵称、个人简介的全文搜索
            String sql = """
                SELECT 
                    user_id,
                    username,
                    nickname, 
                    avatar,
                    bio,
                    role,
                    status,
                    is_verified,
                    blogger_status,
                    vip_level,
                    follower_count,
                    following_count,
                    content_count,
                    like_count,
                    activity_score,
                    influence_score,
                    search_weight,
                    location,
                    last_active_time,
                    register_time
                FROM t_search_user_index 
                WHERE deleted = 0 
                  AND status = 'active'
                  AND (
                      username LIKE ? 
                      OR nickname LIKE ? 
                      OR bio LIKE ?
                      OR MATCH(username, nickname, bio, keywords) AGAINST(? IN NATURAL LANGUAGE MODE)
                  )
                ORDER BY 
                    search_weight DESC,
                    influence_score DESC,
                    activity_score DESC,
                    follower_count DESC,
                    last_active_time DESC
                LIMIT ? OFFSET ?
                """;

            String likeKeyword = "%" + keyword + "%";
            
            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, likeKeyword, likeKeyword, keyword, limit, offset},
                (rs, rowNum) -> {
                    SearchResultItem item = SearchResultItem.builder()
                            .id(rs.getLong("user_id"))
                            .title(rs.getString("nickname"))
                            .description(rs.getString("bio"))
                            .type("USER")
                            .url("/user/" + rs.getLong("user_id"))
                            .imageUrl(rs.getString("avatar"))
                            .build();

                    // 设置作者信息（用户自己）
                    item.setAuthorId(rs.getLong("user_id"));
                    item.setAuthorName(rs.getString("nickname"));
                    item.setAuthorAvatar(rs.getString("avatar"));

                    // 设置统计信息
                    item.setViewCount(rs.getLong("follower_count")); // 粉丝数作为查看数
                    item.setLikeCount(rs.getLong("like_count"));
                    item.setCommentCount(rs.getLong("content_count")); // 内容数作为评论数

                    // 设置相关度评分
                    double relevanceScore = calculateUserRelevance(
                        rs.getString("username"),
                        rs.getString("nickname"), 
                        rs.getString("bio"),
                        keyword
                    );
                    item.setRelevanceScore(relevanceScore);

                    // 设置用户特定的额外信息
                    item.setExtraInfo(buildUserExtraInfo(rs));

                    // 设置时间信息
                    if (rs.getTimestamp("last_active_time") != null) {
                        item.setPublishTime(rs.getTimestamp("last_active_time").toLocalDateTime());
                    } else if (rs.getTimestamp("register_time") != null) {
                        item.setPublishTime(rs.getTimestamp("register_time").toLocalDateTime());
                    }

                    return item;
                }
            );

        } catch (DataAccessException e) {
            log.error("搜索用户失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public long countUsers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return 0L;
        }

        try {
            log.debug("统计用户搜索结果数量：关键词={}", keyword);

            // 基于 t_search_user_index 表的完全去连表化计数查询
            String sql = """
                SELECT COUNT(*) 
                FROM t_search_user_index 
                WHERE deleted = 0 
                  AND status = 'active'
                  AND (
                      username LIKE ? 
                      OR nickname LIKE ? 
                      OR bio LIKE ?
                      OR MATCH(username, nickname, bio, keywords) AGAINST(? IN NATURAL LANGUAGE MODE)
                  )
                """;

            String likeKeyword = "%" + keyword + "%";
            
            Long count = jdbcTemplate.queryForObject(sql, 
                new Object[]{likeKeyword, likeKeyword, likeKeyword, keyword}, 
                Long.class);
            
            return count != null ? count : 0L;

        } catch (DataAccessException e) {
            log.error("统计用户搜索结果失败：keyword={}", keyword, e);
            return 0L;
        }
    }

    @Override
    public List<SuggestionItem> getUserSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            log.debug("获取用户建议：关键词={}，限制={}", keyword, limit);

            // 基于 t_search_user_index 表的用户建议查询（完全去连表化）
            String sql = """
                SELECT 
                    user_id,
                    username,
                    nickname,
                    avatar,
                    role,
                    is_verified,
                    blogger_status,
                    follower_count,
                    influence_score,
                    search_weight
                FROM t_search_user_index 
                WHERE deleted = 0 
                  AND status = 'active'
                  AND (username LIKE ? OR nickname LIKE ?)
                ORDER BY 
                    search_weight DESC,
                    influence_score DESC,
                    follower_count DESC
                LIMIT ?
                """;

            String likeKeyword = keyword + "%"; // 前缀匹配

            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, likeKeyword, limit},
                (rs, rowNum) -> {
                    String displayText = rs.getString("nickname");
                    String username = rs.getString("username");
                    if (!displayText.equals(username)) {
                        displayText += " (@" + username + ")";
                    }

                    // 用户角色标识
                    String roleTag = "";
                    String role = rs.getString("role");
                    boolean isVerified = rs.getInt("is_verified") == 1;
                    String bloggerStatus = rs.getString("blogger_status");

                    if ("admin".equals(role)) {
                        roleTag = " [管理员]";
                    } else if ("blogger".equals(role) || "approved".equals(bloggerStatus)) {
                        roleTag = " [博主]";
                    } else if ("vip".equals(role)) {
                        roleTag = " [VIP]";
                    }
                    if (isVerified) {
                        roleTag += " ✓";
                    }

                    return SuggestionItem.builder()
                            .text(displayText + roleTag)
                            .type("USER")
                            .targetId(rs.getLong("user_id"))
                            .avatarUrl(rs.getString("avatar"))
                            .searchCount(rs.getLong("follower_count")) // 使用粉丝数作为搜索计数
                            .relevanceScore(calculateUserRelevance(username, rs.getString("nickname"), null, keyword))
                            .highlightText(highlightMatch(displayText, keyword))
                            .extraInfo("粉丝: " + rs.getLong("follower_count"))
                            .build();
                }
            );

        } catch (DataAccessException e) {
            log.error("获取用户建议失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    /**
     * 计算用户相关度评分
     */
    private double calculateUserRelevance(String username, String nickname, String bio, String keyword) {
        double score = 0.0;
        String lowerKeyword = keyword.toLowerCase();

        // 用户名完全匹配
        if (username != null && username.toLowerCase().equals(lowerKeyword)) {
            score += 10.0;
        }
        // 昵称完全匹配
        else if (nickname != null && nickname.toLowerCase().equals(lowerKeyword)) {
            score += 9.0;
        }
        // 用户名前缀匹配
        else if (username != null && username.toLowerCase().startsWith(lowerKeyword)) {
            score += 8.0;
        }
        // 昵称前缀匹配
        else if (nickname != null && nickname.toLowerCase().startsWith(lowerKeyword)) {
            score += 7.0;
        }
        // 用户名包含匹配
        else if (username != null && username.toLowerCase().contains(lowerKeyword)) {
            score += 5.0;
        }
        // 昵称包含匹配
        else if (nickname != null && nickname.toLowerCase().contains(lowerKeyword)) {
            score += 4.0;
        }
        // 个人简介包含匹配
        else if (bio != null && bio.toLowerCase().contains(lowerKeyword)) {
            score += 3.0;
        }
        else {
            score = 1.0; // 默认分数
        }

        return score;
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
     * 构建用户额外信息
     */
    private String buildUserExtraInfo(java.sql.ResultSet rs) throws java.sql.SQLException {
        StringBuilder extraInfo = new StringBuilder();
        
        // 用户角色和认证状态
        String role = rs.getString("role");
        boolean isVerified = rs.getInt("is_verified") == 1;
        String bloggerStatus = rs.getString("blogger_status");

        if ("admin".equals(role)) {
            extraInfo.append("管理员");
        } else if ("blogger".equals(role) || "approved".equals(bloggerStatus)) {
            extraInfo.append("认证博主");
        } else if ("vip".equals(role)) {
            extraInfo.append("VIP用户");
        } else {
            extraInfo.append("普通用户");
        }

        if (isVerified) {
            extraInfo.append(" • 已认证");
        }

        // 统计信息
        long followerCount = rs.getLong("follower_count");
        long contentCount = rs.getLong("content_count");
        
        extraInfo.append(" • 粉丝: ").append(followerCount);
        extraInfo.append(" • 内容: ").append(contentCount);

        // 位置信息
        String location = rs.getString("location");
        if (StringUtils.hasText(location)) {
            extraInfo.append(" • ").append(location);
        }

        return extraInfo.toString();
    }
} 