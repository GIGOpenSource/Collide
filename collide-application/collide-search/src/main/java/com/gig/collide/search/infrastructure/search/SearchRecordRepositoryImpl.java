package com.gig.collide.business.infrastructure.search;

import com.gig.collide.api.search.response.data.SuggestionItem;
import com.gig.collide.business.domain.search.SearchRecordRepository;
import com.gig.collide.business.domain.search.entity.SearchHistory;
import com.gig.collide.business.domain.search.entity.SearchStatistics;
import com.gig.collide.business.domain.search.entity.SearchSuggestion;
import com.gig.collide.business.infrastructure.mapper.SearchHistoryMapper;
import com.gig.collide.business.infrastructure.mapper.SearchStatisticsMapper;
import com.gig.collide.business.infrastructure.mapper.SearchSuggestionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索记录仓储实现
 * 
 * @author GIG Team
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchRecordRepositoryImpl implements SearchRecordRepository {

    private final SearchHistoryMapper searchHistoryMapper;
    private final SearchStatisticsMapper searchStatisticsMapper;
    private final SearchSuggestionMapper searchSuggestionMapper;

    @Override
    public void recordSearch(String keyword, Long userId, Long resultCount) {
        try {
            // 记录搜索历史
            if (userId != null) {
                SearchHistory searchHistory = new SearchHistory();
                searchHistory.setUserId(userId);
                searchHistory.setKeyword(keyword);
                searchHistory.setSearchType("ALL"); // 默认搜索类型
                searchHistory.setResultCount(resultCount);
                searchHistory.setSearchTime(0L); // 这里可以传入实际的搜索耗时
                searchHistoryMapper.insert(searchHistory);
            }

            // 更新搜索统计
            SearchStatistics existingStats = searchStatisticsMapper.selectByKeyword(keyword);
            if (existingStats != null) {
                // 更新统计信息
                searchStatisticsMapper.incrementSearchCount(keyword, userId);
            } else {
                // 新增统计记录
                SearchStatistics newStats = new SearchStatistics();
                newStats.setKeyword(keyword);
                newStats.setSearchCount(1L);
                newStats.setUserCount(userId != null ? 1L : 0L);
                newStats.setLastSearchTime(LocalDateTime.now());
                searchStatisticsMapper.insert(newStats);
            }

            log.debug("成功记录搜索行为：keyword={}, userId={}, resultCount={}", keyword, userId, resultCount);

        } catch (Exception e) {
            log.error("记录搜索行为失败：keyword={}, userId={}", keyword, userId, e);
        }
    }

    @Override
    public List<SuggestionItem> getKeywordSuggestions(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }

        try {
            List<SuggestionItem> suggestions = new ArrayList<>();
            
            // 从搜索统计表获取建议
            String keywordPrefix = keyword + "%";
            List<String> relatedKeywords = searchStatisticsMapper.selectKeywordsByPrefix(keywordPrefix, limit);
            
            for (String relatedKeyword : relatedKeywords) {
                SearchStatistics stats = searchStatisticsMapper.selectByKeyword(relatedKeyword);
                if (stats != null) {
                    // 计算相关度得分
                    double relevanceScore = Math.log(stats.getSearchCount() + 1) * 2.0 + 
                                          Math.log(stats.getUserCount() + 1) * 1.5;

                    // 高亮匹配部分
                    String highlightText = highlightMatch(relatedKeyword, keyword);

                    suggestions.add(SuggestionItem.builder()
                            .text(relatedKeyword)
                            .type("KEYWORD")
                            .searchCount(stats.getSearchCount())
                            .relevanceScore(relevanceScore)
                            .highlightText(highlightText)
                            .build());
                }
            }
            
            // 从搜索建议表获取预配置的建议
            List<SearchSuggestion> configuredSuggestions = searchSuggestionMapper
                    .selectByKeywordPrefixAndType(keywordPrefix, "KEYWORD", limit / 2);
            
            for (SearchSuggestion suggestion : configuredSuggestions) {
                suggestions.add(SuggestionItem.builder()
                        .text(suggestion.getKeyword())
                        .type("KEYWORD")
                        .searchCount(suggestion.getSearchCount())
                        .relevanceScore(suggestion.getWeight())
                        .highlightText(highlightMatch(suggestion.getKeyword(), keyword))
                        .build());
            }
            
            // 按相关度排序并限制数量
            return suggestions.stream()
                    .sorted((a, b) -> Double.compare(
                            b.getRelevanceScore() != null ? b.getRelevanceScore() : 0.0,
                            a.getRelevanceScore() != null ? a.getRelevanceScore() : 0.0))
                    .limit(limit)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取关键词建议失败：keyword={}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getHotKeywords(Integer limit) {
        try {
            return searchStatisticsMapper.selectHotKeywords(limit);

        } catch (Exception e) {
            log.error("获取热门关键词失败", e);
            
            // 返回一些默认的热门关键词
            return List.of("Java编程", "Spring Boot", "微服务", "数据库设计", "前端开发", 
                          "人工智能", "机器学习", "区块链", "云计算", "大数据");
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
} 