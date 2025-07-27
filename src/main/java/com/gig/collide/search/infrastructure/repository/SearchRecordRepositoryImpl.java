package com.gig.collide.search.infrastructure.repository;

import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.search.domain.repository.SearchRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索记录仓储实现（完全去连表化设计）
 * 基于 t_search_history 和 t_search_statistics 单表查询，无任何 JOIN 操作
 * 包含 Redis 分布式锁的幂等性保护机制
 * 
 * @author Collide Team
 * @version 1.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRecordRepositoryImpl implements SearchRecordRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String SEARCH_RECORD_LOCK_PREFIX = "search:record:lock:";
    private static final String SEARCH_DUPLICATE_PREFIX = "search:duplicate:";
    private static final Duration LOCK_EXPIRE_TIME = Duration.ofMinutes(1);
    private static final Duration DUPLICATE_EXPIRE_TIME = Duration.ofMinutes(5);

    @Override
    public void recordSearch(String keyword, Long userId, Long resultCount) {
        if (!StringUtils.hasText(keyword)) {
            log.debug("搜索关键词为空，跳过记录");
            return;
        }

        // 幂等性保护：防止短时间内重复记录相同的搜索
        String duplicateKey = generateDuplicateKey(keyword, userId);
        if (isDuplicateRecord(duplicateKey)) {
            log.debug("检测到重复搜索记录，跳过：关键词={}，用户ID={}", keyword, userId);
            return;
        }

        // 分布式锁：防止并发重复记录
        String lockKey = SEARCH_RECORD_LOCK_PREFIX + duplicateKey;
        try {
            Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", LOCK_EXPIRE_TIME);
            
            if (!lockAcquired) {
                log.debug("获取搜索记录锁失败，跳过：关键词={}，用户ID={}", keyword, userId);
                return;
            }

            try {
                // 记录搜索历史（包含用户冗余信息，避免连表）
                recordSearchHistory(keyword, userId, resultCount);

                // 更新搜索统计（包含热度计算，避免连表）
                updateSearchStatistics(keyword, userId);

                // 标记为已处理，防止重复
                markAsProcessed(duplicateKey);

                log.debug("记录搜索行为成功：关键词={}，用户ID={}，结果数={}", keyword, userId, resultCount);

            } finally {
                // 释放锁
                redisTemplate.delete(lockKey);
            }

        } catch (DataAccessException e) {
            log.error("记录搜索行为失败：关键词={}，用户ID={}", keyword, userId, e);
            // 不抛出异常，避免影响主流程
        }
    }

    @Override
    public List<SuggestionItem> getKeywordSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            log.debug("获取关键词建议，前缀：{}，限制：{}", keyword, limit);

            // 基于 t_search_statistics 表的完全去连表化查询
            // 结合热度评分和搜索频次提供关键词建议
            String sql = """
                SELECT 
                    keyword,
                    search_count,
                    unique_user_count,
                    today_count,
                    week_count,
                    hot_score,
                    rank_score,
                    trend_score,
                    last_search_time
                FROM t_search_statistics 
                WHERE deleted = 0 
                  AND keyword LIKE ?
                  AND LENGTH(TRIM(keyword)) > 1
                ORDER BY 
                    hot_score DESC,
                    search_count DESC,
                    week_count DESC,
                    unique_user_count DESC,
                    last_search_time DESC
                LIMIT ?
                """;

            String likeKeyword = keyword + "%"; // 前缀匹配

            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, limit},
                (rs, rowNum) -> {
                    String suggestedKeyword = rs.getString("keyword");
                    long searchCount = rs.getLong("search_count");
                    long uniqueUserCount = rs.getLong("unique_user_count");
                    double hotScore = rs.getDouble("hot_score");
                    double trendScore = rs.getDouble("trend_score");

                    // 高亮匹配部分
                    String highlightText = highlightMatch(suggestedKeyword, keyword);

                    return SuggestionItem.builder()
                            .text(suggestedKeyword)
                            .type("KEYWORD")
                            .searchCount(searchCount)
                            .relevanceScore(calculateKeywordRelevance(suggestedKeyword, keyword, hotScore))
                            .highlightText(highlightText)
                            .extraInfo(buildKeywordExtraInfo(searchCount, uniqueUserCount, trendScore))
                            .build();
                }
            );

        } catch (Exception e) {
            log.error("获取关键词建议失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getHotKeywords(Integer limit) {
        try {
            log.debug("获取热门关键词，限制：{}", limit);

            // 基于 t_search_statistics 表的完全去连表化查询
            // 使用热度评分和趋势分数获取热门关键词
            String sql = """
                SELECT keyword
                FROM t_search_statistics 
                WHERE deleted = 0 
                  AND LENGTH(TRIM(keyword)) > 1
                  AND last_search_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                ORDER BY 
                    hot_score DESC,
                    trend_score DESC,
                    week_count DESC,
                    search_count DESC,
                    unique_user_count DESC
                LIMIT ?
                """;

            List<String> hotKeywords = jdbcTemplate.queryForList(sql, String.class, limit != null ? limit : 10);
            
            // 如果热门关键词不足，补充一些默认关键词
            if (hotKeywords.size() < 5) {
                List<String> defaultKeywords = List.of(
                    "Java", "Spring Boot", "微服务", "数据库", "Redis", 
                    "MySQL", "React", "Vue", "JavaScript", "Python"
                );
                
                for (String defaultKeyword : defaultKeywords) {
                    if (!hotKeywords.contains(defaultKeyword) && hotKeywords.size() < limit) {
                        hotKeywords.add(defaultKeyword);
                    }
                }
            }

            return hotKeywords;

        } catch (Exception e) {
            log.error("获取热门关键词失败", e);
            // 返回默认热门关键词
            return List.of("Java", "Spring Boot", "微服务", "数据库", "Redis");
        }
    }

    /**
     * 记录搜索历史（包含用户冗余信息，避免连表）
     */
    private void recordSearchHistory(String keyword, Long userId, Long resultCount) {
        try {
            // 获取用户冗余信息（如果用户ID不为空）
            String userNickname = null;
            String userAvatar = null;
            String userRole = "user";
            
            if (userId != null) {
                // 从用户表获取冗余信息存储到搜索历史
                String userInfoSql = """
                    SELECT nickname, avatar, role 
                    FROM t_user 
                    WHERE id = ? AND deleted = 0
                    """;
                
                jdbcTemplate.query(userInfoSql, new Object[]{userId}, rs -> {
                    // 这里直接在lambda中更新变量会有作用域问题，所以我们使用不同的方法
                });
                
                // 更安全的方法：单独查询用户信息
                try {
                    List<Object[]> userInfo = jdbcTemplate.query(userInfoSql, new Object[]{userId}, 
                        (rs, rowNum) -> new Object[]{
                            rs.getString("nickname"),
                            rs.getString("avatar"), 
                            rs.getString("role")
                        });
                    
                    if (!userInfo.isEmpty()) {
                        Object[] info = userInfo.get(0);
                        userNickname = (String) info[0];
                        userAvatar = (String) info[1];
                        userRole = (String) info[2];
                    }
                } catch (Exception e) {
                    log.warn("获取用户信息失败，使用默认值：userId={}", userId, e);
                }
            }

            // 插入搜索历史（包含用户冗余信息）
            String insertHistorySql = """
                INSERT INTO t_search_history (
                    user_id, user_nickname, user_avatar, user_role,
                    keyword, search_type, result_count, search_time,
                    ip_address, device_info, create_time
                ) VALUES (?, ?, ?, ?, ?, 'ALL', ?, 100, NULL, NULL, ?)
                """;

            jdbcTemplate.update(insertHistorySql, 
                userId, 
                userNickname,
                userAvatar,
                userRole,
                keyword, 
                resultCount != null ? resultCount : 0L,
                LocalDateTime.now()
            );

        } catch (Exception e) {
            log.error("记录搜索历史失败：keyword={}, userId={}", keyword, userId, e);
        }
    }

    /**
     * 更新搜索统计（包含热度计算，避免连表）
     */
    private void updateSearchStatistics(String keyword, Long userId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 使用 UPSERT 语法更新或插入搜索统计
            String upsertSql = """
                INSERT INTO t_search_statistics (
                    keyword, search_count, unique_user_count, today_count, week_count, month_count,
                    avg_result_count, avg_search_time, max_result_count, min_result_count,
                    all_search_count, user_search_count, content_search_count, comment_search_count,
                    hot_score, rank_score, trend_score,
                    first_search_time, last_search_time, peak_time,
                    create_time, update_time
                ) VALUES (
                    ?, 1, 1, 1, 1, 1,
                    0, 100, 0, 0,
                    1, 0, 0, 0,
                    1.0, 1.0, 1.0,
                    ?, ?, ?,
                    ?, ?
                ) ON DUPLICATE KEY UPDATE
                    search_count = search_count + 1,
                    unique_user_count = CASE WHEN ? IS NOT NULL THEN unique_user_count + 1 ELSE unique_user_count END,
                    today_count = CASE WHEN DATE(last_search_time) = CURDATE() THEN today_count + 1 ELSE 1 END,
                    week_count = CASE WHEN last_search_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN week_count + 1 ELSE 1 END,
                    month_count = CASE WHEN last_search_time >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN month_count + 1 ELSE 1 END,
                    all_search_count = all_search_count + 1,
                    hot_score = LEAST(100.0, (search_count + 1) * 0.6 + today_count * 0.4),
                    rank_score = LEAST(100.0, (search_count + 1) * 0.5 + unique_user_count * 0.5),
                    trend_score = CASE 
                        WHEN last_search_time >= DATE_SUB(NOW(), INTERVAL 1 DAY) THEN LEAST(100.0, trend_score + 1.0)
                        ELSE GREATEST(0.0, trend_score - 0.1)
                    END,
                    last_search_time = VALUES(last_search_time),
                    update_time = VALUES(update_time)
                """;
            
            int affectedRows = jdbcTemplate.update(upsertSql, 
                keyword, now, now, now, now, now,  // INSERT values
                userId  // ON DUPLICATE KEY UPDATE condition
            );
            
            log.debug("更新搜索统计：关键词={}，影响行数={}", keyword, affectedRows);

        } catch (Exception e) {
            log.error("更新搜索统计失败：keyword={}", keyword, e);
        }
    }

    /**
     * 检查是否为重复记录
     */
    private boolean isDuplicateRecord(String duplicateKey) {
        String key = SEARCH_DUPLICATE_PREFIX + duplicateKey;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 标记为已处理
     */
    private void markAsProcessed(String duplicateKey) {
        String key = SEARCH_DUPLICATE_PREFIX + duplicateKey;
        redisTemplate.opsForValue().set(key, "1", DUPLICATE_EXPIRE_TIME);
    }

    /**
     * 生成去重键
     */
    private String generateDuplicateKey(String keyword, Long userId) {
        // 使用关键词+用户ID+时间窗口(分钟)作为去重键
        long timeWindow = System.currentTimeMillis() / (60 * 1000); // 按分钟分组
        return keyword + ":" + (userId != null ? userId : "anonymous") + ":" + timeWindow;
    }

    /**
     * 计算关键词相关度评分
     */
    private double calculateKeywordRelevance(String suggestion, String keyword, double hotScore) {
        double baseScore = 0.0;
        String lowerKeyword = keyword.toLowerCase();
        String lowerSuggestion = suggestion.toLowerCase();

        if (lowerSuggestion.equals(lowerKeyword)) {
            baseScore = 10.0;
        } else if (lowerSuggestion.startsWith(lowerKeyword)) {
            baseScore = 8.0;
        } else if (lowerSuggestion.contains(lowerKeyword)) {
            baseScore = 6.0;
        } else {
            baseScore = 3.0;
        }

        // 结合热度评分
        return baseScore + (hotScore * 0.1);
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
     * 构建关键词额外信息
     */
    private String buildKeywordExtraInfo(long searchCount, long uniqueUserCount, double trendScore) {
        StringBuilder extraInfo = new StringBuilder();
        
        extraInfo.append("搜索: ").append(searchCount);
        extraInfo.append(" • 用户: ").append(uniqueUserCount);
        
        // 趋势指示
        if (trendScore > 5.0) {
            extraInfo.append(" • 🔥 热门");
        } else if (trendScore > 2.0) {
            extraInfo.append(" • 📈 上升");
        } else if (trendScore < -2.0) {
            extraInfo.append(" • 📉 下降");
        }
        
        return extraInfo.toString();
    }
} 