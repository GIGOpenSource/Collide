package com.gig.collide.search.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.search.domain.entity.SearchHistory;
import com.gig.collide.search.domain.entity.HotSearch;
import com.gig.collide.search.domain.service.SearchService;
import com.gig.collide.search.infrastructure.mapper.SearchHistoryMapper;
import com.gig.collide.search.infrastructure.mapper.HotSearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索服务实现类 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    
    @Autowired
    private HotSearchMapper hotSearchMapper;

    @Override
    @Transactional
    public List<Object> search(String keyword, String searchType, Long userId, 
                              Integer pageNum, Integer pageSize, String sortBy) {
        // 这里模拟搜索逻辑，实际应该调用不同模块的搜索接口
        List<Object> results = new ArrayList<>();
        
        try {
            switch (searchType) {
                case "content":
                    // 调用内容模块搜索
                    results = searchContent(keyword, pageNum, pageSize, sortBy);
                    break;
                case "goods":
                    // 调用商品模块搜索
                    results = searchGoods(keyword, pageNum, pageSize, sortBy);
                    break;
                case "user":
                    // 调用用户模块搜索
                    results = searchUsers(keyword, pageNum, pageSize, sortBy);
                    break;
                default:
                    log.warn("未知搜索类型: {}", searchType);
            }
            
            // 记录搜索历史
            recordSearchHistory(userId, keyword, searchType, results.size());
            
            // 更新热搜统计
            updateHotSearchStats(keyword);
            
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new RuntimeException("搜索失败: " + e.getMessage());
        }
        
        return results;
    }

    @Override
    @Transactional
    public void recordSearchHistory(Long userId, String keyword, String searchType, Integer resultCount) {
        // 检查是否已存在相同的搜索记录
        int count = searchHistoryMapper.countByUserIdAndKeyword(userId, keyword);
        if (count == 0) {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setKeyword(keyword);
            history.setSearchType(searchType);
            history.setResultCount(resultCount);
            searchHistoryMapper.insert(history);
        }
    }

    @Override
    public IPage<SearchHistory> getSearchHistory(Long userId, String searchType, String keyword,
                                                int pageNum, int pageSize, String sortBy, String sortDirection) {
        Page<SearchHistory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SearchHistory> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(SearchHistory::getUserId, userId);
        
        if (StringUtils.hasText(searchType)) {
            queryWrapper.eq(SearchHistory::getSearchType, searchType);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(SearchHistory::getKeyword, keyword);
        }
        
        // 排序
        if ("result_count".equals(sortBy)) {
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(SearchHistory::getResultCount);
            } else {
                queryWrapper.orderByDesc(SearchHistory::getResultCount);
            }
        } else {
            // 默认按创建时间排序
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(SearchHistory::getCreateTime);
            } else {
                queryWrapper.orderByDesc(SearchHistory::getCreateTime);
            }
        }
        
        return searchHistoryMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public void clearSearchHistory(Long userId) {
        searchHistoryMapper.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteSearchHistory(Long historyId) {
        searchHistoryMapper.deleteById(historyId);
    }

    @Override
    public List<HotSearch> getHotSearchKeywords(Integer limit) {
        return hotSearchMapper.selectHotKeywords(limit);
    }

    @Override
    public List<String> getSearchSuggestions(String keyword, Integer limit) {
        return searchHistoryMapper.selectSuggestionsByPrefix(keyword, limit);
    }

    @Override
    public List<HotSearch> getHotKeywordsByType(String searchType, Integer limit) {
        return hotSearchMapper.selectHotKeywordsByType(searchType, limit);
    }

    @Override
    public List<SearchHistory> getUserSearchPreferences(Long userId) {
        return searchHistoryMapper.selectUserSearchPreferences(userId, 20);
    }

    @Override
    @Transactional
    public void updateHotSearchTrend(String keyword, Double trendScore) {
        hotSearchMapper.updateTrendScore(keyword, BigDecimal.valueOf(trendScore));
    }

    @Override
    @Transactional
    public void updateHotSearchStats(String keyword) {
        HotSearch hotSearch = hotSearchMapper.selectByKeyword(keyword);
        if (hotSearch != null) {
            hotSearchMapper.increaseSearchCount(keyword);
        } else {
            // 新增热搜记录
            hotSearchMapper.insertOrUpdateHotSearch(keyword, 1L);
        }
    }

    /**
     * 搜索内容（模拟实现）
     */
    private List<Object> searchContent(String keyword, Integer pageNum, Integer pageSize, String sortBy) {
        // 这里应该调用内容模块的搜索接口
        // 暂时返回空列表
        return new ArrayList<>();
    }

    /**
     * 搜索商品（模拟实现）
     */
    private List<Object> searchGoods(String keyword, Integer pageNum, Integer pageSize, String sortBy) {
        // 这里应该调用商品模块的搜索接口
        // 暂时返回空列表
        return new ArrayList<>();
    }

    /**
     * 搜索用户（模拟实现）
     */
    private List<Object> searchUsers(String keyword, Integer pageNum, Integer pageSize, String sortBy) {
        // 这里应该调用用户模块的搜索接口
        // 暂时返回空列表
        return new ArrayList<>();
    }
} 