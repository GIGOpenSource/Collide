package com.gig.collide.api.goods.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品分页查询请求
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Schema(description = "商品分页查询请求")
public class GoodsPageQueryRequest {

    @Schema(description = "页码", example = "1")
    private Integer pageNo;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize;

    @Schema(description = "商品类型", allowableValues = {"COIN", "SUBSCRIPTION"})
    private String type;

    @Schema(description = "商品状态", allowableValues = {"DRAFT", "ON_SALE", "OFF_SALE", "SOLD_OUT", "DISABLED"})
    private String status;

    @Schema(description = "是否推荐")
    private Boolean recommended;

    @Schema(description = "是否热门")
    private Boolean hot;

    @Schema(description = "关键词搜索")
    private String keyword;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "创建时间开始")
    private String createTimeStart;

    @Schema(description = "创建时间结束")
    private String createTimeEnd;

    public GoodsPageQueryRequest() {}

    // Getters and Setters
    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getRecommended() { return recommended; }
    public void setRecommended(Boolean recommended) { this.recommended = recommended; }

    public Boolean getHot() { return hot; }
    public void setHot(Boolean hot) { this.hot = hot; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getCreateTimeStart() { return createTimeStart; }
    public void setCreateTimeStart(String createTimeStart) { this.createTimeStart = createTimeStart; }

    public String getCreateTimeEnd() { return createTimeEnd; }
    public void setCreateTimeEnd(String createTimeEnd) { this.createTimeEnd = createTimeEnd; }

    @Override
    public String toString() {
        return "GoodsPageQueryRequest{" +
                "type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", keyword='" + keyword + '\'' +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                '}';
    }
} 