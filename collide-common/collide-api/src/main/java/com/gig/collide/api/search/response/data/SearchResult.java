package com.gig.collide.api.search.response.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 搜索结果数据模型
 *
 * @author GIG Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索结果")
public class SearchResult {

    @Schema(description = "结果ID")
    private Long id;

    @Schema(description = "结果类型：USER-用户, CONTENT-内容, COMMENT-评论")
    private String resultType;
    
    @Schema(description = "类型")
    private String type;

    @Schema(description = "标题（高亮后）")
    private String title;

    @Schema(description = "描述/摘要（高亮后）")
    private String description;

    @Schema(description = "内容预览（高亮后）")
    private String contentPreview;

    @Schema(description = "封面图片URL")
    private String coverUrl;

    @Schema(description = "作者/用户信息")
    private AuthorInfo author;

    @Schema(description = "统计信息")
    private StatisticsInfo statistics;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "内容类型")
    private String contentType;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "相关度得分")
    private Double relevanceScore;
    
    @Schema(description = "搜索得分")
    private Double score;

    @Schema(description = "扩展信息")
    private Map<String, Object> extraInfo;
    
    @Schema(description = "内容详情")
    private Map<String, Object> content;

    /**
     * 作者信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "作者信息")
    public static class AuthorInfo {
        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "头像URL")
        private String avatar;

        @Schema(description = "个人简介")
        private String bio;

        @Schema(description = "是否为认证用户")
        private Boolean verified;
    }

    /**
     * 统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "统计信息")
    public static class StatisticsInfo {
        @Schema(description = "查看数")
        private Long viewCount;

        @Schema(description = "点赞数")
        private Long likeCount;

        @Schema(description = "评论数")
        private Long commentCount;

        @Schema(description = "收藏数")
        private Long favoriteCount;

        @Schema(description = "分享数")
        private Long shareCount;
    }
    
    /**
     * 手动设置器方法（Lombok 可能没有正确生成）
     */
    public void setType(String type) {
        this.type = type;
    }
    
    public void setContent(Map<String, Object> content) {
        this.content = content;
    }
    
    public void setScore(Double score) {
        this.score = score;
    }
} 