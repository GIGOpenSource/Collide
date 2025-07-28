package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 基于动态位置的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostLocationQueryCondition implements SocialQueryCondition {
    
    /**
     * 位置描述（城市、地标等）
     */
    private String location;
    
    /**
     * 中心点纬度
     */
    private BigDecimal centerLatitude;
    
    /**
     * 中心点经度
     */
    private BigDecimal centerLongitude;
    
    /**
     * 搜索半径（公里）
     */
    private Double radiusKm;
    
    /**
     * 最小纬度（矩形范围查询）
     */
    private BigDecimal minLatitude;
    
    /**
     * 最大纬度（矩形范围查询）
     */
    private BigDecimal maxLatitude;
    
    /**
     * 最小经度（矩形范围查询）
     */
    private BigDecimal minLongitude;
    
    /**
     * 最大经度（矩形范围查询）
     */
    private BigDecimal maxLongitude;
    
    /**
     * 构造位置描述查询条件
     */
    public PostLocationQueryCondition(String location) {
        this.location = location;
    }
    
    /**
     * 构造圆形范围查询条件
     */
    public static PostLocationQueryCondition ofCircle(BigDecimal centerLatitude, BigDecimal centerLongitude, Double radiusKm) {
        return new PostLocationQueryCondition(null, centerLatitude, centerLongitude, radiusKm, 
                null, null, null, null);
    }
    
    /**
     * 构造矩形范围查询条件
     */
    public static PostLocationQueryCondition ofRectangle(BigDecimal minLatitude, BigDecimal maxLatitude, 
                                                        BigDecimal minLongitude, BigDecimal maxLongitude) {
        return new PostLocationQueryCondition(null, null, null, null, 
                minLatitude, maxLatitude, minLongitude, maxLongitude);
    }
} 