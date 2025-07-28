package com.gig.collide.search.domain.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索建议表实体
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_search_suggestion")
public class SearchSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 建议ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 建议关键词
     */
    @TableField("keyword")
    private String keyword;

    /**
     * 建议类型：KEYWORD-关键词建议, USER-用户建议, TAG-标签建议, CONTENT-内容建议
     */
    @TableField("suggestion_type")
    private String suggestionType;

    /**
     * 原始文本（用于高亮显示）
     */
    @TableField("original_text")
    private String originalText;

    /**
     * 高亮文本（HTML格式）
     */
    @TableField("highlight_text")
    private String highlightText;

    /**
     * 关联目标ID（用户ID、内容ID等）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 关联目标类型：USER/CONTENT/TAG
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 关联目标标题（冗余字段）
     */
    @TableField("target_title")
    private String targetTitle;

    /**
     * 关联目标头像URL（冗余字段）
     */
    @TableField("target_avatar")
    private String targetAvatar;

    /**
     * 关联目标描述（冗余字段）
     */
    @TableField("target_description")
    private String targetDescription;

    /**
     * 搜索次数
     */
    @TableField("search_count")
    private Long searchCount;

    /**
     * 点击次数
     */
    @TableField("click_count")
    private Long clickCount;

    /**
     * 权重（用于排序）
     */
    @TableField("weight")
    private Double weight;

    /**
     * 相关度评分
     */
    @TableField("relevance_score")
    private Double relevanceScore;

    /**
     * 质量评分
     */
    @TableField("quality_score")
    private Double qualityScore;

    /**
     * 是否手动配置：0-自动生成，1-手动配置
     */
    @TableField("is_manual")
    private Integer isManual;

    /**
     * 优先级（数值越大优先级越高）
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用，EXPIRED-过期
     */
    @TableField("status")
    private String status;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 标签列表
     */
    @TableField("tags")
    private String tags;

    /**
     * 扩展数据
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 生效开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 获取标签列表对象
     */
    public String[] getTagsArray() {
        if (tags == null) {
            return new String[0];
        }
        return JSON.parseObject(tags, String[].class);
    }

    /**
     * 设置标签列表对象
     */
    public void setTagsArray(String[] tagsArray) {
        this.tags = tagsArray == null ? null : JSON.toJSONString(tagsArray);
    }

    /**
     * 获取扩展数据对象
     */
    public <T> T getExtraDataObject(Class<T> clazz) {
        if (extraData == null) {
            return null;
        }
        return JSON.parseObject(extraData, clazz);
    }

    /**
     * 设置扩展数据对象
     */
    public void setExtraDataObject(Object extraDataObject) {
        this.extraData = extraDataObject == null ? null : JSON.toJSONString(extraDataObject);
    }
} 