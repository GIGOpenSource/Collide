package com.gig.collide.search.infrastructure.search;

import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.search.domain.search.SearchRecordRepository;
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
 * 搜索记录仓储实现（去连表设计）
 * 基于t_search_history和t_search_statistics单表查询，无JOIN操作
 * 添加幂等性保护机制
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
                // 记录搜索历史
                recordSearchHistory(keyword, userId, resultCount);

                // 更新搜索统计
                updateSearchStatistics(keyword);

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
     * 记录搜索历史
     */
    private void recordSearchHistory(String keyword, Long userId, Long resultCount) {
        String insertHistorySql = "INSERT INTO t_search_history " +
            "(user_id, keyword, search_type, result_count, search_time, ip_address, device_info, create_time) " +
            "VALUES (?, ?, 'ALL', ?, 100, NULL, NULL, ?)";

        jdbcTemplate.update(insertHistorySql, 
            userId, 
            keyword, 
            resultCount != null ? resultCount : 0L,
            LocalDateTime.now()
        );
    }

    @Override
    public List<SuggestionItem> getKeywordSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            // 基于搜索历史提供关键词建议
            String sql = "SELECT keyword, COUNT(*) as search_count " +
                        "FROM t_search_history " +
                        "WHERE keyword LIKE ? " +
                        "GROUP BY keyword " +
                        "ORDER BY search_count DESC, keyword ASC " +
                        "LIMIT ?";

            String likeKeyword = keyword + "%"; // 前缀匹配

            log.debug("获取关键词建议，前缀：{}，限制：{}", keyword, limit);

            return jdbcTemplate.query(sql, 
                new Object[]{likeKeyword, limit},
                (rs, rowNum) -> {
                    String suggestedKeyword = rs.getString("keyword");
                    long searchCount = rs.getLong("search_count");

                    // 高亮匹配部分
                    String highlightText = highlightMatch(suggestedKeyword, keyword);

                    return SuggestionItem.builder()
                            .text(suggestedKeyword)
                            .type("KEYWORD")
                            .searchCount(searchCount)
                            .relevanceScore(calculateKeywordRelevance(suggestedKeyword, keyword))
                            .highlightText(highlightText)
                            .extraInfo(null)
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
            // 基于最近7天的搜索统计获取热门关键词
            String sql = "SELECT s.keyword " +
                        "FROM t_search_statistics s " +
                        "WHERE s.update_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                        "  AND LENGTH(TRIM(s.keyword)) > 1 " + // 过滤空白和单字符
                        "ORDER BY s.search_count DESC, s.unique_user_count DESC " +
                        "LIMIT ?";

            log.debug("获取热门关键词，限制：{}", limit);

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
     * 更新搜索统计（添加幂等性保护）
     */
    private void updateSearchStatistics(String keyword) {
        try {
            // 使用UPSERT语法或先查询再决定插入/更新
            String upsertSql = "INSERT INTO t_search_statistics " +
                "(keyword, search_count, unique_user_count, create_time, update_time) " +
                "VALUES (?, 1, 1, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "search_count = search_count + 1, " +
                "update_time = VALUES(update_time)";
            
            LocalDateTime now = LocalDateTime.now();
            int affectedRows = jdbcTemplate.update(upsertSql, keyword, now, now);
            
            log.debug("更新搜索统计：关键词={}，影响行数={}", keyword, affectedRows);

        } catch (Exception e) {
            log.error("更新搜索统计失败：keyword={}", keyword, e);
        }
    }

    /**
     * 计算关键词相关度
     */
    private double calculateKeywordRelevance(String suggestion, String keyword) {
        if (suggestion.equalsIgnoreCase(keyword)) {
            return 10.0;
        } else if (suggestion.toLowerCase().startsWith(keyword.toLowerCase())) {
            return 8.0;
        } else if (suggestion.toLowerCase().contains(keyword.toLowerCase())) {
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
     * 清理过期的搜索历史记录（带幂等性保护）
     * 可以通过定时任务调用此方法
     */
    public void cleanExpiredSearchHistory(int daysToKeep) {
        String cleanLockKey = "search:clean:lock";
        
        try {
            // 使用分布式锁防止重复清理
            Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(cleanLockKey, "1", Duration.ofHours(1));
            
            if (!lockAcquired) {
                log.info("清理任务已在执行中，跳过");
                return;
            }

            try {
                String deleteSql = "DELETE FROM t_search_history " +
                                  "WHERE create_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
                
                int deletedRows = jdbcTemplate.update(deleteSql, daysToKeep);
                
                log.info("清理过期搜索历史记录完成，删除{}条记录，保留{}天内的记录", deletedRows, daysToKeep);

            } finally {
                redisTemplate.delete(cleanLockKey);
            }

        } catch (Exception e) {
            log.error("清理过期搜索历史记录失败", e);
        }
    }

    /**
     * 获取用户的搜索历史
     */
    public List<String> getUserSearchHistory(Long userId, Integer limit) {
        if (userId == null) {
            return new ArrayList<>();
        }

        try {
            String sql = "SELECT DISTINCT keyword " +
                        "FROM t_search_history " +
                        "WHERE user_id = ? " +
                        "ORDER BY create_time DESC " +
                        "LIMIT ?";

            return jdbcTemplate.queryForList(sql, String.class, userId, limit != null ? limit : 20);

        } catch (Exception e) {
            log.error("获取用户搜索历史失败：userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取搜索趋势统计
     */
    public List<SuggestionItem> getSearchTrends(Integer days, Integer limit) {
        try {
            String sql = "SELECT keyword, COUNT(*) as search_count, " +
                        "       COUNT(DISTINCT user_id) as unique_users " +
                        "FROM t_search_history " +
                        "WHERE create_time >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                        "GROUP BY keyword " +
                        "HAVING search_count >= 2 " + // 至少被搜索2次
                        "ORDER BY search_count DESC, unique_users DESC " +
                        "LIMIT ?";

            return jdbcTemplate.query(sql, 
                new Object[]{days != null ? days : 7, limit != null ? limit : 10},
                (rs, rowNum) -> {
                    String keyword = rs.getString("keyword");
                    long searchCount = rs.getLong("search_count");
                    long uniqueUsers = rs.getLong("unique_users");

                    return SuggestionItem.builder()
                            .text(keyword)
                            .type("TREND")
                            .searchCount(searchCount)
                            .relevanceScore((double) searchCount)
                            .highlightText(keyword)
                            .extraInfo("独立用户数：" + uniqueUsers)
                            .build();
                }
            );

        } catch (Exception e) {
            log.error("获取搜索趋势统计失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 批量记录搜索行为（批量处理，提高性能）
     */
    public void batchRecordSearch(List<String> keywords, Long userId) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }

        try {
            String sql = "INSERT INTO t_search_history " +
                "(user_id, keyword, search_type, result_count, search_time, create_time) " +
                "VALUES (?, ?, 'BATCH', 0, 0, ?)";

            LocalDateTime now = LocalDateTime.now();
            
            // 使用批量插入提高性能
            jdbcTemplate.batchUpdate(sql, keywords, keywords.size(), 
                (ps, keyword) -> {
                    ps.setObject(1, userId);
                    ps.setString(2, keyword);
                    ps.setObject(3, now);
                });

            log.info("批量记录搜索行为完成，用户ID：{}，关键词数量：{}", userId, keywords.size());

        } catch (Exception e) {
            log.error("批量记录搜索行为失败：userId={}, keywords={}", userId, keywords, e);
        }
    }
} 