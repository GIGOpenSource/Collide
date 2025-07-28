package com.gig.collide.search.domain.entity.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gig.collide.api.search.request.SearchAdvancedRequest;
import com.gig.collide.api.search.request.SearchHistoryRecordRequest;
import com.gig.collide.api.search.request.SearchUnifiedRequest;
import com.gig.collide.api.search.response.data.SearchHistoryInfo;
import com.gig.collide.api.search.response.data.SearchResultInfo;
import com.gig.collide.api.search.response.data.SearchStatisticsInfo;
import com.gig.collide.api.search.response.data.SearchSuggestionInfo;
import com.gig.collide.search.domain.entity.SearchCommentIndex;
import com.gig.collide.search.domain.entity.SearchContentIndex;
import com.gig.collide.search.domain.entity.SearchHistory;
import com.gig.collide.search.domain.entity.SearchStatistics;
import com.gig.collide.search.domain.entity.SearchSuggestion;
import com.gig.collide.search.domain.entity.SearchUserIndex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索实体转换器
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Mapper(componentModel = "spring")
@Component
public interface SearchConvertor {

    SearchConvertor INSTANCE = Mappers.getMapper(SearchConvertor.class);

    // ========== 搜索历史转换 ==========
    
    @Mapping(target = "deleted", source = "deleted", qualifiedByName = "integerToBoolean")
    SearchHistoryInfo historyToInfo(SearchHistory history);
    
    SearchHistory recordRequestToHistory(SearchHistoryRecordRequest request);

    // ========== 搜索统计转换 ==========
    
    SearchStatisticsInfo statisticsToInfo(SearchStatistics statistics);

    // ========== 搜索建议转换 ==========
    
    @Mapping(target = "isManual", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "extraData", ignore = true)
    SearchSuggestionInfo suggestionToInfo(SearchSuggestion suggestion);

    // ========== 搜索结果转换 ==========
    
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "resultType", constant = "USER")
    @Mapping(target = "title", source = "nickname")
    @Mapping(target = "description", source = "bio")
    @Mapping(target = "coverUrl", source = "avatar")
    @Mapping(target = "relevanceScore", source = "searchWeight")
    @Mapping(target = "author.id", source = "userId")
    @Mapping(target = "author.nickname", source = "nickname")
    @Mapping(target = "author.avatar", source = "avatar")
    @Mapping(target = "author.verified", source = "isVerified", qualifiedByName = "integerToBoolean")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "updateTime", source = "updateTime")
    SearchResultInfo userIndexToResultInfo(SearchUserIndex userIndex);
    
    @Mapping(target = "id", source = "contentId")
    @Mapping(target = "resultType", constant = "CONTENT")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "contentText")
    @Mapping(target = "coverUrl", source = "coverUrl")
    @Mapping(target = "relevanceScore", source = "searchWeight")
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "author.nickname", source = "authorNickname")
    @Mapping(target = "author.avatar", source = "authorAvatar")
    @Mapping(target = "createTime", source = "publishedTime")
    @Mapping(target = "updateTime", source = "updateTime")
    SearchResultInfo contentIndexToResultInfo(SearchContentIndex contentIndex);
    
    @Mapping(target = "id", source = "commentId")
    @Mapping(target = "resultType", constant = "COMMENT")
    @Mapping(target = "title", expression = "java(\"评论：\" + truncateText(commentIndex.getContent(), 30))")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "coverUrl", ignore = true)
    @Mapping(target = "relevanceScore", source = "searchWeight")
    @Mapping(target = "author.id", source = "userId")
    @Mapping(target = "author.nickname", source = "userNickname")
    @Mapping(target = "author.avatar", source = "userAvatar")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "updateTime", source = "updateTime")
    SearchResultInfo commentIndexToResultInfo(SearchCommentIndex commentIndex);

    // ========== 请求转换 ==========
    
    @Mapping(target = "searchType", ignore = true)
    @Mapping(target = "contentType", ignore = true) 
    @Mapping(target = "pageNum", ignore = true)  // SearchAdvancedRequest没有page字段
    @Mapping(target = "pageSize", ignore = true) // SearchAdvancedRequest没有size字段
    @Mapping(target = "filters", ignore = true)
    @Mapping(target = "searchFields", ignore = true)
    SearchUnifiedRequest advancedToUnified(SearchAdvancedRequest request);

    // ========== 辅助方法 ==========
    
    /**
     * 截断文本
     */
    default String truncateText(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
    
    /**
     * 格式化时间
     */
    default String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    /**
     * Integer转Boolean
     */
    @Named("integerToBoolean")
    default Boolean integerToBoolean(Integer value) {
        if (value == null) {
            return null;
        }
        return value == 1;
    }
    
    /**
     * 解析JSON字符串为Map
     */
    default Map<String, Object> parseJsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    /**
     * 解析JSON字符串为List
     */
    default List<String> parseJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * List转换为JSON字符串
     */
    default String listToJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    /**
     * Map转换为JSON字符串
     */
    default String mapToJsonString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
} 