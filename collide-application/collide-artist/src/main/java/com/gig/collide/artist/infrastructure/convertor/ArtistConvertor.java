package com.gig.collide.artist.infrastructure.convertor;

import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.response.data.ArtistInfo;
import com.gig.collide.api.artist.response.data.ArtistApplicationInfo;
import com.gig.collide.api.artist.response.data.ArtistStatistics;
import com.gig.collide.artist.domain.entity.Artist;
import com.gig.collide.artist.domain.entity.ArtistApplication;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Artist转换器
 * @author GIG
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ArtistConvertor {

    ArtistConvertor INSTANCE = Mappers.getMapper(ArtistConvertor.class);

    /**
     * Artist实体转ArtistInfo
     */
    @Mapping(target = "artistId", source = "id")
    @Mapping(target = "createdTime", source = "gmtCreate")
    @Mapping(target = "updatedTime", source = "gmtModified")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "stringToCategories")
    @Mapping(target = "expertiseTags", source = "expertiseTags", qualifiedByName = "stringToStringList")
    ArtistInfo toArtistInfo(Artist artist);

    /**
     * Artist实体列表转ArtistInfo列表
     */
    List<ArtistInfo> toArtistInfoList(List<Artist> artists);

    /**
     * ArtistApplication实体转ArtistApplicationInfo
     */
    @Mapping(target = "applicationId", source = "id")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "stringToCategories")
    ArtistApplicationInfo toArtistApplicationInfo(ArtistApplication application);

    /**
     * ArtistApplication实体列表转ArtistApplicationInfo列表
     */
    List<ArtistApplicationInfo> toArtistApplicationInfoList(List<ArtistApplication> applications);

    /**
     * ArtistStatistics实体转ArtistStatistics DTO
     */
    @Mapping(target = "artistId", source = "artistId")
    @Mapping(target = "followersGrowthTrend", source = "followersGrowthTrend", qualifiedByName = "stringToIntegerMap")
    @Mapping(target = "contentPublishTrend", source = "contentPublishTrend", qualifiedByName = "stringToIntegerMap")
    @Mapping(target = "engagementTrend", source = "engagementTrend", qualifiedByName = "stringToLongMap")
    @Mapping(target = "popularTags", source = "popularTags", qualifiedByName = "stringToIntegerMap")
    @Mapping(target = "followersAgeDistribution", source = "followersAgeDistribution", qualifiedByName = "stringToIntegerMap")
    @Mapping(target = "followersGenderDistribution", source = "followersGenderDistribution", qualifiedByName = "stringToIntegerMap")
    @Mapping(target = "followersRegionDistribution", source = "followersRegionDistribution", qualifiedByName = "stringToIntegerMap")
    com.gig.collide.api.artist.response.data.ArtistStatistics toArtistStatisticsInfo(com.gig.collide.artist.domain.entity.ArtistStatistics statistics);

    /**
     * ArtistStatistics实体列表转DTO列表
     */
    List<com.gig.collide.api.artist.response.data.ArtistStatistics> toArtistStatisticsInfoList(List<com.gig.collide.artist.domain.entity.ArtistStatistics> statisticsList);

    /**
     * String categories 转换为 List<ArtistCategory>
     */
    @Named("stringToCategories")
    default List<ArtistCategory> stringToCategories(String categories) {
        if (!StringUtils.hasText(categories)) {
            return new ArrayList<>();
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 尝试解析为字符串数组，然后转换为枚举
            List<String> categoryNames = objectMapper.readValue(categories, new TypeReference<List<String>>() {});
            List<ArtistCategory> result = new ArrayList<>();
            
            for (String categoryName : categoryNames) {
                try {
                    ArtistCategory category = ArtistCategory.valueOf(categoryName);
                    result.add(category);
                } catch (IllegalArgumentException e) {
                    // 忽略无效的分类名称
                    System.err.println("Unknown category: " + categoryName);
                }
            }
            
            return result;
        } catch (Exception e) {
            // JSON解析失败时，尝试按逗号分割
            try {
                String[] parts = categories.split(",");
                List<ArtistCategory> result = new ArrayList<>();
                
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (StringUtils.hasText(trimmed)) {
                        try {
                            ArtistCategory category = ArtistCategory.valueOf(trimmed);
                            result.add(category);
                        } catch (IllegalArgumentException ex) {
                            // 忽略无效的分类名称
                            System.err.println("Unknown category: " + trimmed);
                        }
                    }
                }
                
                return result;
            } catch (Exception ex) {
                System.err.println("Failed to parse categories: " + categories);
                return new ArrayList<>();
            }
        }
    }

    /**
     * List<ArtistCategory> 转换为 String categories
     */
    default String categoriesToString(List<ArtistCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> categoryNames = new ArrayList<>();
            
            for (ArtistCategory category : categories) {
                categoryNames.add(category.name());
            }
            
            return objectMapper.writeValueAsString(categoryNames);
        } catch (Exception e) {
            // 回退到逗号分割格式
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < categories.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(categories.get(i).name());
            }
            return sb.toString();
        }
    }

    /**
     * String expertiseTags 转换为 List<String>
     */
    @Named("stringToStringList")
    default List<String> stringToStringList(String value) {
        if (!StringUtils.hasText(value)) {
            return new ArrayList<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> result = objectMapper.readValue(value, new TypeReference<List<String>>() {});
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            // Fallback to comma-separated parsing
            try {
                String[] parts = value.split(",");
                List<String> result = new ArrayList<>();
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (StringUtils.hasText(trimmed)) {
                        result.add(trimmed);
                    }
                }
                return result;
            } catch (Exception ex) {
                System.err.println("Failed to parse string list: " + value);
                return new ArrayList<>();
            }
        }
    }

    /**
     * List<String> 转换为 String
     */
    default String stringListToString(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(stringList);
        } catch (Exception e) {
            return String.join(",", stringList);
        }
    }

    /**
     * String 转换为 Map<String, Integer>
     */
    @Named("stringToIntegerMap")
    default Map<String, Integer> stringToIntegerMap(String value) {
        if (!StringUtils.hasText(value)) {
            return new HashMap<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Integer> result = objectMapper.readValue(value, new TypeReference<Map<String, Integer>>() {});
            return result != null ? result : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Failed to parse integer map: " + value);
            return new HashMap<>();
        }
    }

    /**
     * String 转换为 Map<String, Long>
     */
    @Named("stringToLongMap")
    default Map<String, Long> stringToLongMap(String value) {
        if (!StringUtils.hasText(value)) {
            return new HashMap<>();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Long> result = objectMapper.readValue(value, new TypeReference<Map<String, Long>>() {});
            return result != null ? result : new HashMap<>();
        } catch (Exception e) {
            System.err.println("Failed to parse long map: " + value);
            return new HashMap<>();
        }
    }

    /**
     * Map<String, Integer> 转换为 String
     */
    default String integerMapToString(Map<String, Integer> integerMap) {
        if (integerMap == null || integerMap.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(integerMap);
        } catch (Exception e) {
            System.err.println("Failed to serialize integer map: " + integerMap);
            return "{}";
        }
    }

    /**
     * Map<String, Long> 转换为 String
     */
    default String longMapToString(Map<String, Long> longMap) {
        if (longMap == null || longMap.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(longMap);
        } catch (Exception e) {
            System.err.println("Failed to serialize long map: " + longMap);
            return "{}";
        }
    }
} 