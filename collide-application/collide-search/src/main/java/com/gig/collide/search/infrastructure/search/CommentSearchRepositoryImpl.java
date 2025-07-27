package com.gig.collide.search.infrastructure.search;

import com.gig.collide.api.search.response.data.SearchResult;
import com.gig.collide.search.domain.CommentSearchRepository;
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
 * 评论搜索仓储实现（去连表设计）
 * 基于t_comment单表查询，无JOIN操作
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
    public List<SearchResult> searchComments(String keyword, Integer timeRange, 
                                            Integer pageNum, Integer pageSize, Boolean highlight) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT id, content_text, target_id, target_type, ");
            sql.append("       user_id, user_nickname, user_avatar, ");
            sql.append("       parent_comment_id, root_comment_id, ");
            sql.append("       like_count, reply_count, ");
            sql.append("       create_time, status, ");
            
            // 相关度计算：评论内容匹配
            sql.append("       (CASE ");
            sql.append("         WHEN content_text LIKE ? THEN 10 ");
            sql.append("         ELSE 1 END) AS relevance_score ");
            
            sql.append("FROM t_comment ");
            sql.append("WHERE deleted = 0 AND status = 'normal' ");
            sql.append("  AND content_text LIKE ? ");

            List<Object> params = new ArrayList<>();
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword); // 相关度计算
            params.add(likeKeyword); // WHERE条件

            // 时间范围过滤
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
                params.add(timeRange);
            }

            sql.append("ORDER BY relevance_score DESC, like_count DESC, create_time DESC ");
            sql.append("LIMIT ?, ?");

            int offset = (pageNum - 1) * pageSize;
            params.add(offset);
            params.add(pageSize);

            log.info("执行评论搜索查询，关键词：{}，时间范围：{}天，偏移：{}，限制：{}", 
                keyword, timeRange, offset, pageSize);

            return jdbcTemplate.query(sql.toString(), 
                params.toArray(),
                new CommentSearchResultMapper(keyword, highlight)
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
            sql.append("SELECT COUNT(*) FROM t_comment ");
            sql.append("WHERE deleted = 0 AND status = 'normal' ");
            sql.append("  AND content_text LIKE ? ");

            List<Object> params = new ArrayList<>();
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);

            // 时间范围过滤（与搜索逻辑保持一致）
            if (timeRange != null && timeRange > 0) {
                sql.append("  AND create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) ");
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
     * 评论搜索结果映射器
     */
    private static class CommentSearchResultMapper implements RowMapper<SearchResult> {
        private final String keyword;
        private final Boolean highlight;

        public CommentSearchResultMapper(String keyword, Boolean highlight) {
            this.keyword = keyword;
            this.highlight = highlight != null ? highlight : false;
        }

        @Override
        public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long commentId = rs.getLong("id");
            String contentText = rs.getString("content_text");
            Long targetId = rs.getLong("target_id");
            String targetType = rs.getString("target_type");
            Long userId = rs.getLong("user_id");
            String userNickname = rs.getString("user_nickname");
            String userAvatar = rs.getString("user_avatar");
            Long parentCommentId = rs.getLong("parent_comment_id");
            Long rootCommentId = rs.getLong("root_comment_id");
            long likeCount = rs.getLong("like_count");
            long replyCount = rs.getLong("reply_count");
            LocalDateTime createTime = rs.getTimestamp("create_time").toLocalDateTime();
            String status = rs.getString("status");
            Double relevanceScore = rs.getDouble("relevance_score");

            // 构建作者信息
            SearchResult.AuthorInfo authorInfo = SearchResult.AuthorInfo.builder()
                    .userId(userId)
                    .username(null) // 评论表中没有用户名信息
                    .nickname(userNickname)
                    .avatar(userAvatar)
                    .bio(null)
                    .verified(false) // TODO: 根据用户状态判断认证状态
                    .build();

            // 构建统计信息
            SearchResult.StatisticsInfo statisticsInfo = SearchResult.StatisticsInfo.builder()
                    .viewCount(0L) // 评论没有浏览量概念
                    .likeCount(likeCount)
                    .commentCount(replyCount) // 使用回复数作为评论数
                    .shareCount(0L) // 评论没有分享量概念
                    .build();

            // 生成内容预览（截取前100字符）
            String contentPreview = null;
            if (StringUtils.hasText(contentText)) {
                contentPreview = contentText.length() > 100 ? 
                    contentText.substring(0, 100) + "..." : contentText;
            }

            // 高亮处理
            String displayTitle = "评论：" + (contentPreview != null ? contentPreview : "");
            String displayDescription = contentText;
            if (highlight) {
                displayTitle = highlightMatch(displayTitle, keyword);
                displayDescription = highlightMatch(contentText, keyword);
            }

            // 构建标签
            List<String> tags = new ArrayList<>();
            if (StringUtils.hasText(targetType)) {
                tags.add("评论对象：" + targetType);
            }
            if (parentCommentId != null && parentCommentId > 0) {
                tags.add("回复评论");
            } else {
                tags.add("原始评论");
            }
            if (StringUtils.hasText(status)) {
                tags.add(status);
            }

            return SearchResult.builder()
                    .id(commentId)
                    .resultType("COMMENT")
                    .title(displayTitle)
                    .description(displayDescription)
                    .contentPreview(contentPreview)
                    .coverUrl(null) // 评论没有封面图
                    .author(authorInfo)
                    .statistics(statisticsInfo)
                    .tags(tags)
                    .contentType("COMMENT")
                    .createTime(createTime)
                    .publishTime(null) // 评论没有发布时间概念
                    .relevanceScore(relevanceScore)
                    .extraInfo(Map.of(
                        "targetId", targetId,
                        "targetType", targetType,
                        "parentCommentId", parentCommentId != null ? parentCommentId : 0,
                        "rootCommentId", rootCommentId != null ? rootCommentId : 0,
                        "status", status,
                        "contentLength", contentText.length()
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