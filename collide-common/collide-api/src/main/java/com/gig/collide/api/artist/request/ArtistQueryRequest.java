package com.gig.collide.api.artist.request;

import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.constant.ArtistLevel;
import com.gig.collide.api.artist.constant.ArtistStatus;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 博主查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArtistQueryRequest extends BaseRequest {

    /**
     * 博主ID列表
     */
    private List<Long> artistIds;

    /**
     * 用户ID列表
     */
    private List<Long> userIds;

    /**
     * 博主名称（支持模糊查询）
     */
    private String artistName;

    /**
     * 博主状态
     */
    private ArtistStatus status;

    /**
     * 申请类型
     */
    private ArtistApplicationType applicationType;

    /**
     * 博主分类
     */
    private ArtistCategory category;

    /**
     * 博主等级
     */
    private ArtistLevel level;

    /**
     * 是否认证
     */
    private Boolean verified;

    /**
     * 最小粉丝数
     */
    private Integer minFollowers;

    /**
     * 最大粉丝数
     */
    private Integer maxFollowers;

    /**
     * 擅长领域标签
     */
    private List<String> expertiseTags;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 排序字段
     */
    private String sortBy = "followersCount";

    /**
     * 排序方向
     */
    private String sortDirection = "DESC";

    /**
     * 是否只查询活跃博主
     */
    private Boolean onlyActive = true;

    /**
     * 限制数量
     */
    private Integer limit;
} 