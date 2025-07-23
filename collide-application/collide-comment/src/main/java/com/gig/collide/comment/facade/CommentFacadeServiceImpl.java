package com.gig.collide.comment.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.response.CommentQueryResponse;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.api.comment.response.data.CommentInfo;
import com.gig.collide.api.comment.service.CommentFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.comment.domain.entity.Comment;
import com.gig.collide.comment.domain.entity.convertor.CommentConvertor;
import com.gig.collide.comment.domain.service.CommentDomainService;
import com.gig.collide.comment.infrastructure.exception.CommentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务 Facade 实现
 * 对外提供 RPC 服务接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Component
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class CommentFacadeServiceImpl implements CommentFacadeService {

    private final CommentDomainService commentDomainService;

    @Override
    public CommentResponse createComment(CommentCreateRequest createRequest) {
        try {
            log.info("创建评论请求，用户ID: {}, 目标ID: {}, 类型: {}", 
                createRequest.getUserId(), createRequest.getTargetId(), createRequest.getCommentType());

            // 调用领域服务创建评论
            Comment comment = commentDomainService.createComment(createRequest);

            // 构建成功响应
            CommentResponse response = CommentResponse.success(comment.getId(), "评论创建成功");
            response.setIsNew(true);

            log.info("评论创建成功，评论ID: {}", comment.getId());
            return response;

        } catch (Exception e) {
            log.error("评论创建失败", e);
            return CommentResponse.error("COMMENT_CREATE_ERROR", "评论创建失败：" + e.getMessage());
        }
    }

    @Override
    public CommentResponse deleteComment(Long commentId, Long userId) {
        try {
            log.info("删除评论请求，评论ID: {}, 用户ID: {}", commentId, userId);

            // 参数校验
            if (commentId == null) {
                return CommentResponse.error(CommentErrorCode.COMMENT_ID_REQUIRED.getCode(),
                    CommentErrorCode.COMMENT_ID_REQUIRED.getMessage());
            }
            if (userId == null) {
                return CommentResponse.error(CommentErrorCode.USER_ID_REQUIRED.getCode(),
                    CommentErrorCode.USER_ID_REQUIRED.getMessage());
            }

            // 调用领域服务删除评论
            boolean deleted = commentDomainService.deleteComment(commentId, userId);

            if (deleted) {
                log.info("评论删除成功，评论ID: {}", commentId);
                return CommentResponse.success(commentId, "评论删除成功");
            } else {
                return CommentResponse.error("COMMENT_DELETE_FAILED", "评论删除失败");
            }

        } catch (Exception e) {
            log.error("评论删除失败，评论ID: {}", commentId, e);
            return CommentResponse.error("COMMENT_DELETE_ERROR", "评论删除失败：" + e.getMessage());
        }
    }

    @Override
    public CommentResponse likeComment(Long commentId, Long userId, Boolean isLike) {
        try {
            log.info("评论点赞请求，评论ID: {}, 用户ID: {}, 点赞: {}", commentId, userId, isLike);

            // 参数校验
            if (commentId == null) {
                return CommentResponse.error(CommentErrorCode.COMMENT_ID_REQUIRED.getCode(),
                    CommentErrorCode.COMMENT_ID_REQUIRED.getMessage());
            }
            if (userId == null) {
                return CommentResponse.error(CommentErrorCode.USER_ID_REQUIRED.getCode(),
                    CommentErrorCode.USER_ID_REQUIRED.getMessage());
            }

            // TODO: 这里应该调用点赞服务处理点赞逻辑，目前简化处理
            Integer increment = Boolean.TRUE.equals(isLike) ? 1 : -1;
            boolean updated = commentDomainService.updateCommentLikeCount(commentId, increment);

            if (updated) {
                CommentResponse response = CommentResponse.success(commentId, 
                    Boolean.TRUE.equals(isLike) ? "点赞成功" : "取消点赞成功");
                response.setIsLiked(isLike);
                
                // 获取最新点赞数
                Comment comment = commentDomainService.getCommentById(commentId);
                response.setLikeCount(comment != null ? comment.getLikeCount() : 0);
                
                log.info("评论点赞操作成功，评论ID: {}", commentId);
                return response;
            } else {
                return CommentResponse.error("COMMENT_LIKE_FAILED", "点赞操作失败");
            }

        } catch (Exception e) {
            log.error("评论点赞操作失败，评论ID: {}", commentId, e);
            return CommentResponse.error("COMMENT_LIKE_ERROR", "点赞操作失败：" + e.getMessage());
        }
    }

    @Override
    public CommentQueryResponse<CommentInfo> queryComment(CommentQueryRequest queryRequest) {
        try {
            log.info("查询评论详情请求，评论ID: {}", queryRequest.getCommentId());

            if (queryRequest.getCommentId() == null) {
                return CommentQueryResponse.error(CommentErrorCode.COMMENT_ID_REQUIRED.getCode(),
                    CommentErrorCode.COMMENT_ID_REQUIRED.getMessage());
            }

            // 查询评论
            Comment comment = commentDomainService.getCommentById(queryRequest.getCommentId());
            if (comment == null) {
                return CommentQueryResponse.error(CommentErrorCode.COMMENT_NOT_FOUND.getCode(),
                    CommentErrorCode.COMMENT_NOT_FOUND.getMessage());
            }

            // 转换为VO
            CommentInfo commentInfo = CommentConvertor.INSTANCE.mapToVo(comment);
            
            // TODO: 补充用户信息和点赞状态
            enrichCommentInfo(commentInfo, queryRequest.getCurrentUserId());

            return CommentQueryResponse.success(commentInfo);

        } catch (Exception e) {
            log.error("查询评论详情失败", e);
            return CommentQueryResponse.error("COMMENT_QUERY_ERROR", "查询评论详情失败：" + e.getMessage());
        }
    }

    @Override
    public PageResponse<CommentInfo> pageQueryComments(CommentQueryRequest queryRequest) {
        try {
            log.info("分页查询评论列表请求，目标ID: {}, 类型: {}", 
                queryRequest.getTargetId(), queryRequest.getCommentType());

            // 参数校验和默认值设置
            validateAndSetDefaults(queryRequest);

            // 分页查询评论
            IPage<Comment> commentPage = commentDomainService.pageQueryComments(
                queryRequest.getCommentType(),
                queryRequest.getTargetId(),
                queryRequest.getParentCommentId(),
                queryRequest.getPageNum(),
                queryRequest.getPageSize(),
                queryRequest.getSortBy(),
                queryRequest.getSortOrder()
            );

            // 转换为VO列表
            List<CommentInfo> commentInfoList = CommentConvertor.INSTANCE.mapToVoList(commentPage.getRecords());
            
            // 补充额外信息
            enrichCommentInfoList(commentInfoList, queryRequest.getCurrentUserId());

            return PageResponse.of(commentInfoList, (int) commentPage.getTotal(), 
                queryRequest.getPageSize(), queryRequest.getPageNum());

        } catch (Exception e) {
            log.error("分页查询评论列表失败", e);
            return PageResponse.error("COMMENT_PAGE_QUERY_ERROR", "查询评论列表失败：" + e.getMessage());
        }
    }

    @Override
    public PageResponse<CommentInfo> queryCommentTree(CommentQueryRequest queryRequest) {
        try {
            log.info("查询评论树请求，目标ID: {}, 类型: {}", 
                queryRequest.getTargetId(), queryRequest.getCommentType());

            // 参数校验和默认值设置
            validateAndSetDefaults(queryRequest);

            // 查询根评论
            IPage<Comment> rootCommentPage = commentDomainService.queryCommentTree(
                queryRequest.getCommentType(),
                queryRequest.getTargetId(),
                queryRequest.getPageNum(),
                queryRequest.getPageSize(),
                queryRequest.getSortBy(),
                queryRequest.getSortOrder()
            );

            List<CommentInfo> commentTreeList = new ArrayList<>();

            if (!CollectionUtils.isEmpty(rootCommentPage.getRecords())) {
                for (Comment rootComment : rootCommentPage.getRecords()) {
                    // 转换根评论
                    CommentInfo rootCommentInfo = CommentConvertor.INSTANCE.mapToVo(rootComment);
                    
                    // 查询子评论
                    if (Boolean.TRUE.equals(queryRequest.getIncludeChildren())) {
                        List<Comment> childComments = commentDomainService.getChildComments(rootComment.getId());
                        if (!CollectionUtils.isEmpty(childComments)) {
                            List<CommentInfo> childCommentInfos = CommentConvertor.INSTANCE.mapToVoList(childComments);
                            rootCommentInfo.setChildren(buildCommentTree(childCommentInfos, rootComment.getId()));
                        }
                    }
                    
                    commentTreeList.add(rootCommentInfo);
                }
            }

            // 补充额外信息
            enrichCommentInfoList(commentTreeList, queryRequest.getCurrentUserId());

            return PageResponse.of(commentTreeList, (int) rootCommentPage.getTotal(), 
                queryRequest.getPageSize(), queryRequest.getPageNum());

        } catch (Exception e) {
            log.error("查询评论树失败", e);
            return PageResponse.error("COMMENT_TREE_QUERY_ERROR", "查询评论树失败：" + e.getMessage());
        }
    }

    @Override
    public CommentQueryResponse<Long> queryCommentCount(Long targetId, String commentType) {
        try {
            log.info("查询评论数量请求，目标ID: {}, 类型: {}", targetId, commentType);

            if (targetId == null) {
                return CommentQueryResponse.error(CommentErrorCode.TARGET_ID_REQUIRED.getCode(),
                    CommentErrorCode.TARGET_ID_REQUIRED.getMessage());
            }

            CommentType type = null;
            if (commentType != null) {
                try {
                    type = CommentType.valueOf(commentType.toUpperCase());
                } catch (Exception e) {
                    return CommentQueryResponse.error(CommentErrorCode.COMMENT_TYPE_REQUIRED.getCode(),
                        "评论类型格式错误");
                }
            }

            Long count = commentDomainService.countComments(type, targetId);
            return CommentQueryResponse.success(count);

        } catch (Exception e) {
            log.error("查询评论数量失败", e);
            return CommentQueryResponse.error("COMMENT_COUNT_ERROR", "查询评论数量失败：" + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 参数校验和默认值设置
     */
    private void validateAndSetDefaults(CommentQueryRequest queryRequest) {
        if (queryRequest.getPageNum() == null || queryRequest.getPageNum() < 1) {
            queryRequest.setPageNum(1);
        }
        if (queryRequest.getPageSize() == null || queryRequest.getPageSize() < 1) {
            queryRequest.setPageSize(10);
        }
        if (queryRequest.getPageSize() > 100) {
            queryRequest.setPageSize(100); // 限制最大页面大小
        }
        if (queryRequest.getSortBy() == null) {
            queryRequest.setSortBy("time");
        }
        if (queryRequest.getSortOrder() == null) {
            queryRequest.setSortOrder("desc");
        }
    }

    /**
     * 补充单个评论信息
     */
    private void enrichCommentInfo(CommentInfo commentInfo, Long currentUserId) {
        if (commentInfo == null) return;
        
        // TODO: 调用用户服务获取用户信息
        // TODO: 调用点赞服务获取点赞状态
        // TODO: 设置删除权限
        
        // 临时设置
        commentInfo.setIsDeletable(currentUserId != null && 
            currentUserId.equals(commentInfo.getUserId()));
        commentInfo.setIsLiked(false); // 默认未点赞
    }

    /**
     * 批量补充评论信息
     */
    private void enrichCommentInfoList(List<CommentInfo> commentInfoList, Long currentUserId) {
        if (CollectionUtils.isEmpty(commentInfoList)) return;
        
        for (CommentInfo commentInfo : commentInfoList) {
            enrichCommentInfo(commentInfo, currentUserId);
            
            // 递归处理子评论
            if (!CollectionUtils.isEmpty(commentInfo.getChildren())) {
                enrichCommentInfoList(commentInfo.getChildren(), currentUserId);
            }
        }
    }

    /**
     * 构建评论树
     */
    private List<CommentInfo> buildCommentTree(List<CommentInfo> allComments, Long rootCommentId) {
        if (CollectionUtils.isEmpty(allComments)) {
            return new ArrayList<>();
        }

        // 按父评论ID分组
        Map<Long, List<CommentInfo>> parentChildMap = allComments.stream()
            .collect(Collectors.groupingBy(comment -> 
                comment.getParentCommentId() != null ? comment.getParentCommentId() : 0L));

        // 递归构建树
        return buildTreeRecursive(parentChildMap, rootCommentId);
    }

    /**
     * 递归构建评论树
     */
    private List<CommentInfo> buildTreeRecursive(Map<Long, List<CommentInfo>> parentChildMap, Long parentId) {
        List<CommentInfo> children = parentChildMap.get(parentId);
        if (CollectionUtils.isEmpty(children)) {
            return new ArrayList<>();
        }

        for (CommentInfo child : children) {
            List<CommentInfo> grandChildren = buildTreeRecursive(parentChildMap, child.getCommentId());
            child.setChildren(grandChildren);
        }

        return children;
    }
} 