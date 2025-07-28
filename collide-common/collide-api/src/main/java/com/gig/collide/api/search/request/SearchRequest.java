package com.gig.collide.api.search.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 搜索请求 - 简洁版
 * 基于t_search_history表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class SearchRequest {

    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 200, message = "搜索关键词长度不能超过200字符")
    private String keyword;

    @Pattern(regexp = "^(content|goods|user)$", message = "搜索类型只能是content、goods或user")
    private String searchType = "content";

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 分页参数
     */
    private Integer pageNum = 1;
    private Integer pageSize = 20;

    /**
     * 排序方式：relevance（相关度）、time（时间）、hot（热度）
     */
    @Pattern(regexp = "^(relevance|time|hot)$", message = "排序方式只能是relevance、time或hot")
    private String sortBy = "relevance";

    /**
     * 是否记录搜索历史
     */
    private Boolean recordHistory = true;
} 