package com.gig.collide.content.domain.entity.convertor;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.api.content.response.data.ContentStatistics;
import com.gig.collide.content.domain.entity.Content;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容实体转换器
 * 使用 MapStruct 进行 Content 实体和 ContentInfo DTO 之间的转换
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ContentConvertor {

    ContentConvertor INSTANCE = Mappers.getMapper(ContentConvertor.class);

    /**
     * 转换为 ContentInfo DTO（去连表化设计）
     *
     * @param content 内容实体
     * @return ContentInfo DTO
     */
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "categoryName", source = "categoryName")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "contentData", source = "contentData", qualifiedByName = "stringToMap")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "stringToList")
    @Mapping(target = "liked", ignore = true)
    @Mapping(target = "favorited", ignore = true)
    @Mapping(target = "recommended", source = "isRecommended")
    @Mapping(target = "pinned", source = "isPinned")
    @Mapping(target = "qualityScore", ignore = true)
    ContentInfo toContentInfo(Content content);

    /**
     * 批量转换为 ContentInfo DTO 列表
     *
     * @param contents 内容实体列表
     * @return ContentInfo DTO 列表
     */
    List<ContentInfo> toContentInfoList(List<Content> contents);

    /**
     * 转换为 ContentStatistics DTO
     *
     * @param content 内容实体
     * @return ContentStatistics DTO
     */
    @Mapping(target = "contentId", source = "id")
    @Mapping(target = "totalViews", source = "viewCount")
    @Mapping(target = "totalLikes", source = "likeCount")
    @Mapping(target = "totalComments", source = "commentCount")
    @Mapping(target = "totalShares", source = "shareCount")
    @Mapping(target = "totalFavorites", source = "favoriteCount")
    @Mapping(target = "todayViews", constant = "0L")
    @Mapping(target = "todayLikes", constant = "0L")
    @Mapping(target = "todayComments", constant = "0L")
    @Mapping(target = "todayShares", constant = "0L")
    @Mapping(target = "todayFavorites", constant = "0L")
    @Mapping(target = "avgViewDuration", ignore = true)
    @Mapping(target = "bounceRate", ignore = true)
    @Mapping(target = "completionRate", ignore = true)
    @Mapping(target = "dailyStats", ignore = true)
    @Mapping(target = "userBehaviorStats", ignore = true)
    ContentStatistics toContentStatistics(Content content);

    /**
     * 将JSON字符串转换为Map对象
     *
     * @param jsonString JSON字符串
     * @return Map对象
     */
    @Named("stringToMap")
    default Map<String, Object> stringToMap(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            return jsonObject != null ? jsonObject : new HashMap<>();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * 将JSON数组字符串转换为List对象
     *
     * @param jsonString JSON数组字符串
     * @return List对象
     */
    @Named("stringToList")
    default List<String> stringToList(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<String> list = JSON.parseArray(jsonString, String.class);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 将Map对象转换为JSON字符串
     *
     * @param map Map对象
     * @return JSON字符串
     */
    @Named("mapToString")
    default String mapToString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return JSON.toJSONString(map);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将List对象转换为JSON数组字符串
     *
     * @param list List对象
     * @return JSON数组字符串
     */
    @Named("listToString")
    default String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return JSON.toJSONString(list);
        } catch (Exception e) {
            return null;
        }
    }
} 