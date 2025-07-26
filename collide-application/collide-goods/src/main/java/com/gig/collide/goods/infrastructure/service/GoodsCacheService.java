package com.gig.collide.goods.infrastructure.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 商品缓存服务
 * 
 * 提供商品相关的缓存操作
 * 使用标准化的collide-cache组件进行Redis缓存管理
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GoodsCacheService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    // 缓存键前缀
    private static final String GOODS_DETAIL_PREFIX = "goods:detail:";
    private static final String GOODS_LIST_PREFIX = "goods:list:";
    private static final String HOT_GOODS_PREFIX = "goods:hot:";
    private static final String RECOMMENDED_GOODS_PREFIX = "goods:recommended:";
    private static final String GOODS_SEARCH_PREFIX = "goods:search:";
    
    // 缓存过期时间
    private static final Duration GOODS_DETAIL_TTL = Duration.ofMinutes(30);  // 商品详情缓存30分钟
    private static final Duration GOODS_LIST_TTL = Duration.ofMinutes(10);    // 商品列表缓存10分钟
    private static final Duration HOT_GOODS_TTL = Duration.ofMinutes(15);     // 热门商品缓存15分钟
    private static final Duration RECOMMENDED_GOODS_TTL = Duration.ofMinutes(20); // 推荐商品缓存20分钟
    private static final Duration SEARCH_RESULT_TTL = Duration.ofMinutes(5);  // 搜索结果缓存5分钟
    
    // ==================== 商品详情缓存 ====================
    
    /**
     * 获取商品详情缓存
     * 
     * @param goodsId 商品ID
     * @return 商品信息
     */
    public GoodsInfo getGoodsDetail(Long goodsId) {
        if (goodsId == null) {
            return null;
        }
        
        String key = GOODS_DETAIL_PREFIX + goodsId;
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            log.debug("命中商品详情缓存，商品ID：{}", goodsId);
            return JSON.parseObject(json, GoodsInfo.class);
        }
        
        return null;
    }
    
    /**
     * 设置商品详情缓存
     * 
     * @param goodsInfo 商品信息
     */
    public void setGoodsDetail(GoodsInfo goodsInfo) {
        if (goodsInfo == null || goodsInfo.getGoodsId() == null) {
            return;
        }
        
        String key = GOODS_DETAIL_PREFIX + goodsInfo.getGoodsId();
        String json = JSON.toJSONString(goodsInfo);
        
        redisTemplate.opsForValue().set(key, json, GOODS_DETAIL_TTL);
        log.debug("设置商品详情缓存，商品ID：{}", goodsInfo.getGoodsId());
    }
    
    /**
     * 删除商品详情缓存
     * 
     * @param goodsId 商品ID
     */
    public void evictGoodsDetail(Long goodsId) {
        if (goodsId == null) {
            return;
        }
        
        String key = GOODS_DETAIL_PREFIX + goodsId;
        redisTemplate.delete(key);
        log.debug("删除商品详情缓存，商品ID：{}", goodsId);
    }
    
    // ==================== 商品列表缓存 ====================
    
    /**
     * 获取商品列表缓存
     * 
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param type 商品类型
     * @param keyword 关键词
     * @return 分页响应
     */
    public PageResponse<GoodsInfo> getGoodsList(Integer pageNo, Integer pageSize, String type, String keyword) {
        String key = buildGoodsListKey(pageNo, pageSize, type, keyword);
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            log.debug("命中商品列表缓存，key：{}", key);
            return JSON.parseObject(json, new TypeReference<PageResponse<GoodsInfo>>() {});
        }
        
        return null;
    }
    
    /**
     * 设置商品列表缓存
     * 
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param type 商品类型
     * @param keyword 关键词
     * @param pageResponse 分页响应
     */
    public void setGoodsList(Integer pageNo, Integer pageSize, String type, String keyword, 
                            PageResponse<GoodsInfo> pageResponse) {
        if (pageResponse == null) {
            return;
        }
        
        String key = buildGoodsListKey(pageNo, pageSize, type, keyword);
        String json = JSON.toJSONString(pageResponse);
        
        redisTemplate.opsForValue().set(key, json, GOODS_LIST_TTL);
        log.debug("设置商品列表缓存，key：{}", key);
    }
    
    /**
     * 构建商品列表缓存键
     */
    private String buildGoodsListKey(Integer pageNo, Integer pageSize, String type, String keyword) {
        StringBuilder keyBuilder = new StringBuilder(GOODS_LIST_PREFIX);
        keyBuilder.append("page:").append(pageNo != null ? pageNo : 1);
        keyBuilder.append(":size:").append(pageSize != null ? pageSize : 20);
        
        if (type != null && !type.trim().isEmpty()) {
            keyBuilder.append(":type:").append(type);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            keyBuilder.append(":keyword:").append(keyword.hashCode()); // 使用hashCode避免特殊字符
        }
        
        return keyBuilder.toString();
    }
    
    // ==================== 热门商品缓存 ====================
    
    /**
     * 获取热门商品缓存
     * 
     * @param limit 限制数量
     * @return 热门商品列表
     */
    public List<GoodsInfo> getHotGoods(Integer limit) {
        String key = HOT_GOODS_PREFIX + "limit:" + (limit != null ? limit : 10);
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            log.debug("命中热门商品缓存，key：{}", key);
            return JSON.parseArray(json, GoodsInfo.class);
        }
        
        return null;
    }
    
    /**
     * 设置热门商品缓存
     * 
     * @param limit 限制数量
     * @param hotGoods 热门商品列表
     */
    public void setHotGoods(Integer limit, List<GoodsInfo> hotGoods) {
        if (hotGoods == null) {
            return;
        }
        
        String key = HOT_GOODS_PREFIX + "limit:" + (limit != null ? limit : 10);
        String json = JSON.toJSONString(hotGoods);
        
        redisTemplate.opsForValue().set(key, json, HOT_GOODS_TTL);
        log.debug("设置热门商品缓存，key：{}", key);
    }
    
    // ==================== 推荐商品缓存 ====================
    
    /**
     * 获取推荐商品缓存
     * 
     * @param userId 用户ID（可选）
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    public List<GoodsInfo> getRecommendedGoods(Long userId, Integer limit) {
        String key = RECOMMENDED_GOODS_PREFIX;
        if (userId != null) {
            key += "user:" + userId + ":";
        }
        key += "limit:" + (limit != null ? limit : 10);
        
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            log.debug("命中推荐商品缓存，key：{}", key);
            return JSON.parseArray(json, GoodsInfo.class);
        }
        
        return null;
    }
    
    /**
     * 设置推荐商品缓存
     * 
     * @param userId 用户ID（可选）
     * @param limit 限制数量
     * @param recommendedGoods 推荐商品列表
     */
    public void setRecommendedGoods(Long userId, Integer limit, List<GoodsInfo> recommendedGoods) {
        if (recommendedGoods == null) {
            return;
        }
        
        String key = RECOMMENDED_GOODS_PREFIX;
        if (userId != null) {
            key += "user:" + userId + ":";
        }
        key += "limit:" + (limit != null ? limit : 10);
        
        String json = JSON.toJSONString(recommendedGoods);
        
        redisTemplate.opsForValue().set(key, json, RECOMMENDED_GOODS_TTL);
        log.debug("设置推荐商品缓存，key：{}", key);
    }
    
    // ==================== 搜索结果缓存 ====================
    
    /**
     * 获取搜索结果缓存
     * 
     * @param keyword 搜索关键词
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 搜索结果
     */
    public PageResponse<GoodsInfo> getSearchResult(String keyword, Integer pageNo, Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        
        String key = GOODS_SEARCH_PREFIX + "keyword:" + keyword.hashCode() + 
                    ":page:" + pageNo + ":size:" + pageSize;
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            log.debug("命中搜索结果缓存，关键词：{}，key：{}", keyword, key);
            return JSON.parseObject(json, new TypeReference<PageResponse<GoodsInfo>>() {});
        }
        
        return null;
    }
    
    /**
     * 设置搜索结果缓存
     * 
     * @param keyword 搜索关键词
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param searchResult 搜索结果
     */
    public void setSearchResult(String keyword, Integer pageNo, Integer pageSize, 
                               PageResponse<GoodsInfo> searchResult) {
        if (keyword == null || keyword.trim().isEmpty() || searchResult == null) {
            return;
        }
        
        String key = GOODS_SEARCH_PREFIX + "keyword:" + keyword.hashCode() + 
                    ":page:" + pageNo + ":size:" + pageSize;
        String json = JSON.toJSONString(searchResult);
        
        redisTemplate.opsForValue().set(key, json, SEARCH_RESULT_TTL);
        log.debug("设置搜索结果缓存，关键词：{}，key：{}", keyword, key);
    }
    
    // ==================== 缓存清理方法 ====================
    
    /**
     * 清除商品相关的所有缓存
     * 
     * @param goodsId 商品ID
     */
    public void evictGoodsAllCache(Long goodsId) {
        if (goodsId == null) {
            return;
        }
        
        log.info("清除商品相关缓存，商品ID：{}", goodsId);
        
        // 清除商品详情缓存
        evictGoodsDetail(goodsId);
        
        // 清除列表缓存（批量删除）
        evictGoodsListCache();
        
        // 清除热门商品缓存
        evictHotGoodsCache();
        
        // 清除推荐商品缓存
        evictRecommendedGoodsCache();
    }
    
    /**
     * 清除商品列表缓存
     */
    public void evictGoodsListCache() {
        Set<String> keys = redisTemplate.keys(GOODS_LIST_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("清除商品列表缓存，数量：{}", keys.size());
        }
    }
    
    /**
     * 清除热门商品缓存
     */
    public void evictHotGoodsCache() {
        Set<String> keys = redisTemplate.keys(HOT_GOODS_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("清除热门商品缓存，数量：{}", keys.size());
        }
    }
    
    /**
     * 清除推荐商品缓存
     */
    public void evictRecommendedGoodsCache() {
        Set<String> keys = redisTemplate.keys(RECOMMENDED_GOODS_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("清除推荐商品缓存，数量：{}", keys.size());
        }
    }
    
    /**
     * 清除搜索结果缓存
     */
    public void evictSearchCache() {
        Set<String> keys = redisTemplate.keys(GOODS_SEARCH_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("清除搜索结果缓存，数量：{}", keys.size());
        }
    }
    
    /**
     * 批量预热缓存
     * 
     * @param goodsInfoList 商品信息列表
     */
    public void warmUpCache(List<GoodsInfo> goodsInfoList) {
        if (goodsInfoList == null || goodsInfoList.isEmpty()) {
            return;
        }
        
        log.info("开始预热商品缓存，数量：{}", goodsInfoList.size());
        
        goodsInfoList.forEach(goodsInfo -> {
            if (goodsInfo != null && goodsInfo.getGoodsId() != null) {
                setGoodsDetail(goodsInfo);
            }
        });
        
        log.info("商品缓存预热完成");
    }
} 