package com.gig.collide.api.category.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类统计信息
 * 包含分类的各项统计数据和分析指标
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类路径
     */
    private String categoryPath;

    /**
     * 直接内容数量（仅当前分类下的内容）
     */
    private Long directContentCount;

    /**
     * 总内容数量（包含所有子分类的内容）
     */
    private Long totalContentCount;

    /**
     * 直接子分类数量
     */
    private Integer directChildrenCount;

    /**
     * 总子分类数量（包含所有层级的子分类）
     */
    private Integer totalChildrenCount;

    /**
     * 今日新增内容数量
     */
    private Long todayContentCount;

    /**
     * 本周新增内容数量
     */
    private Long weekContentCount;

    /**
     * 本月新增内容数量
     */
    private Long monthContentCount;

    /**
     * 本年新增内容数量
     */
    private Long yearContentCount;

    /**
     * 最近活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 平均每日新增内容数量（最近30天）
     */
    private Double avgDailyContent;

    /**
     * 内容增长率（相对于上月）
     */
    private Double contentGrowthRate;

    /**
     * 分类活跃度评分
     */
    private Double activityScore;

    /**
     * 分类热度排名（在同级分类中的排名）
     */
    private Integer popularityRank;

    /**
     * 是否为热门分类
     */
    private Boolean isPopular;

    /**
     * 是否为新兴分类（最近创建且增长迅速）
     */
    private Boolean isEmerging;

    /**
     * 是否为沉寂分类（长时间无新内容）
     */
    private Boolean isInactive;

    /**
     * 内容质量评分（基于内容互动数据）
     */
    private Double contentQualityScore;

    /**
     * 分类使用频率评分
     */
    private Double usageFrequencyScore;

    /**
     * 统计数据更新时间
     */
    private LocalDateTime statisticsUpdateTime;

    /**
     * 计算内容密度（内容数量相对于子分类数量）
     *
     * @return 内容密度
     */
    public double calculateContentDensity() {
        if (totalChildrenCount == null || totalChildrenCount == 0) {
            return directContentCount != null ? directContentCount.doubleValue() : 0.0;
        }
        return totalContentCount != null ? 
            totalContentCount.doubleValue() / totalChildrenCount : 0.0;
    }

    /**
     * 计算内容增长趋势
     *
     * @return 增长趋势描述
     */
    public String getContentGrowthTrend() {
        if (contentGrowthRate == null) {
            return "无数据";
        }
        
        if (contentGrowthRate > 0.1) {
            return "快速增长";
        } else if (contentGrowthRate > 0.05) {
            return "稳定增长";
        } else if (contentGrowthRate > -0.05) {
            return "保持稳定";
        } else if (contentGrowthRate > -0.1) {
            return "轻微下降";
        } else {
            return "明显下降";
        }
    }

    /**
     * 判断是否为活跃分类
     *
     * @return true如果为活跃分类
     */
    public boolean isActiveCategory() {
        if (lastActiveTime == null) {
            return false;
        }
        
        // 最近7天内有活动则认为是活跃的
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return lastActiveTime.isAfter(weekAgo);
    }

    /**
     * 获取分类健康度评估
     *
     * @return 健康度评估结果
     */
    public String getCategoryHealthAssessment() {
        double score = 0.0;
        int factors = 0;

        // 内容数量评分
        if (totalContentCount != null && totalContentCount > 0) {
            score += Math.min(totalContentCount / 100.0, 1.0) * 30;
            factors++;
        }

        // 活跃度评分
        if (activityScore != null) {
            score += activityScore * 25;
            factors++;
        }

        // 增长率评分
        if (contentGrowthRate != null && contentGrowthRate > 0) {
            score += Math.min(contentGrowthRate * 100, 1.0) * 25;
            factors++;
        }

        // 质量评分
        if (contentQualityScore != null) {
            score += contentQualityScore * 20;
            factors++;
        }

        if (factors == 0) {
            return "无法评估";
        }

        double normalizedScore = score / factors;
        
        if (normalizedScore >= 80) {
            return "优秀";
        } else if (normalizedScore >= 60) {
            return "良好";
        } else if (normalizedScore >= 40) {
            return "一般";
        } else if (normalizedScore >= 20) {
            return "较差";
        } else {
            return "很差";
        }
    }

    /**
     * 计算分类影响力（基于内容数量和子分类数量）
     *
     * @return 影响力评分
     */
    public double calculateInfluenceScore() {
        double contentInfluence = totalContentCount != null ? 
            Math.log10(totalContentCount + 1) * 10 : 0;
        double structureInfluence = totalChildrenCount != null ? 
            totalChildrenCount * 2.0 : 0;
        
        return contentInfluence + structureInfluence;
    }

    /**
     * 获取分类发展阶段
     *
     * @return 发展阶段描述
     */
    public String getDevelopmentStage() {
        if (isEmerging != null && isEmerging) {
            return "新兴期";
        }
        
        if (isPopular != null && isPopular) {
            return "成熟期";
        }
        
        if (isInactive != null && isInactive) {
            return "衰退期";
        }
        
        if (totalContentCount != null && totalContentCount > 50) {
            return "发展期";
        }
        
        return "起步期";
    }
} 