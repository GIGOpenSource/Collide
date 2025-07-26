package com.gig.collide.comment.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.api.comment.request.CommentQueryRequest;
import com.gig.collide.api.comment.response.CommentQueryResponse;
import com.gig.collide.api.comment.response.CommentResponse;
import com.gig.collide.api.comment.response.data.CommentInfo;
import com.gig.collide.api.comment.service.CommentFacadeService;
import com.gig.collide.comment.domain.entity.Comment;
import com.gig.collide.comment.domain.entity.convertor.CommentConvertor;
import com.gig.collide.comment.domain.service.CommentDomainService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.rpc.facade.Facade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务 Facade 实现
 * 完全去连表化设计，所有操作都基于单表
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class CommentFacadeServiceImpl implements CommentFacadeService {

    private final CommentDomainService commentDomainService;
    private final CommentConvertor commentConvertor;

    /**
     * 创建评论
     */
    @Override
    @Facade
    public CommentResponse createComment(CommentCreateRequest createRequest) {
        try {
            Comment comment = commentDomainService.createComment(createRequest);
            CommentInfo commentInfo = commentConvertor.convertToCommentInfo(comment);
            
            return CommentResponse.success(comment.getId(), "评论创建成功");
            
        } catch (Exception e) {
            log.error("创建评论失败，请求：{}，错误：{}", createRequest, e.getMessage(), e);
            return CommentResponse.error("CREATE_COMMENT_ERROR", "评论创建失败");
        }
    }

    /**
     * 删除评论
     */
    @Override
    @Facade
    public CommentResponse deleteComment(Long commentId, Long userId) {
        try {
            commentDomainService.deleteComment(commentId, userId);
            return CommentResponse.success(commentId, "评论删除成功");
            
        } catch (Exception e) {
            log.error("删除评论失败，评论ID：{}，用户ID：{}，错误：{}", commentId, userId, e.getMessage(), e);
            return CommentResponse.error("DELETE_COMMENT_ERROR", "评论删除失败");
        }
    }

    /**
     * 点赞/取消点赞评论
     */
    @Override
    @Facade
    public CommentResponse likeComment(Long commentId, Long userId, Boolean isLike) {
        try {
            // 使用带幂等性检查的点赞方法
            Integer increment = isLike ? 1 : -1;
            boolean success = commentDomainService.updateCommentLikeCount(commentId, userId, increment);
            
            if (success) {
                return CommentResponse.success(commentId, isLike ? "点赞成功" : "取消点赞成功");
            } else {
                return CommentResponse.error("LIKE_ALREADY_EXISTS", isLike ? "您已经点过赞了" : "您还未点赞该评论");
            }
            
        } catch (Exception e) {
            log.error("评论点赞失败，评论ID：{}，用户ID：{}，点赞：{}，错误：{}", commentId, userId, isLike, e.getMessage(), e);
            return CommentResponse.error("LIKE_COMMENT_ERROR", "评论点赞失败");
        }
    }

    /**
     * 查询评论详情
     */
    @Override
    @Facade
    public CommentQueryResponse<CommentInfo> queryComment(CommentQueryRequest queryRequest) {
        try {
            // 基于单表查询评论
            List<Comment> comments = commentDomainService.queryComments(
                queryRequest.getCommentType(), 
                queryRequest.getTargetId(), 
                CommentStatus.NORMAL, 
                null, 
                1, 
                10
            );
            
            // 转换为响应对象
            List<CommentInfo> commentInfos = comments.stream()
                .map(commentConvertor::convertToCommentInfo)
                .collect(Collectors.toList());
            
            return CommentQueryResponse.success(commentInfos.isEmpty() ? null : commentInfos.get(0));
            
        } catch (Exception e) {
            log.error("查询评论详情失败，请求：{}，错误：{}", queryRequest, e.getMessage(), e);
            return CommentQueryResponse.error("QUERY_COMMENT_ERROR", "查询评论详情失败");
        }
    }

    /**
     * 分页查询评论列表
     */
    @Override
    @Facade
    public PageResponse<CommentInfo> pageQueryComments(CommentQueryRequest queryRequest) {
        try {
            // 基于单表分页查询
            IPage<Comment> commentPage = commentDomainService.pageQueryComments(
                queryRequest.getCommentType(), 
                CommentStatus.NORMAL, 
                queryRequest.getCurrentPage(), 
                queryRequest.getPageSize()
            );
            
            // 转换为响应对象
            List<CommentInfo> commentInfos = commentPage.getRecords().stream()
                .map(commentConvertor::convertToCommentInfo)
                .collect(Collectors.toList());
            
            return PageResponse.of(
                commentInfos,
                (int) commentPage.getTotal(),
                queryRequest.getPageSize(),
                queryRequest.getCurrentPage()
            );
            
        } catch (Exception e) {
            log.error("分页查询评论失败，请求：{}，错误：{}", queryRequest, e.getMessage(), e);
            return PageResponse.error("PAGE_QUERY_COMMENTS_ERROR", "分页查询评论失败");
        }
    }

    /**
     * 查询评论树（包含子评论的树形结构）
     */
    @Override
    @Facade
    public PageResponse<CommentInfo> queryCommentTree(CommentQueryRequest queryRequest) {
        try {
            // 基于单表递归查询评论树
            List<Comment> treeComments = commentDomainService.queryCommentTree(
                queryRequest.getCommentType(), 
                queryRequest.getTargetId()
            );
            
            // 转换为响应对象
            List<CommentInfo> commentInfos = treeComments.stream()
                .map(commentConvertor::convertToCommentInfo)
                .collect(Collectors.toList());
            
            return PageResponse.of(
                commentInfos,
                commentInfos.size(),
                commentInfos.size(),
                1
            );
            
        } catch (Exception e) {
            log.error("查询评论树失败，请求：{}，错误：{}", queryRequest, e.getMessage(), e);
            return PageResponse.error("QUERY_COMMENT_TREE_ERROR", "查询评论树失败");
        }
    }

    /**
     * 查询评论统计信息
     */
    @Override
    @Facade
    public CommentQueryResponse<Long> queryCommentCount(Long targetId, String commentType) {
        try {
            // 基于单表统计评论数量
            Long count = commentDomainService.countComments(
                CommentType.valueOf(commentType), 
                targetId
            );
            
            return CommentQueryResponse.success(count);
            
        } catch (Exception e) {
            log.error("查询评论统计失败，目标ID：{}，评论类型：{}，错误：{}", targetId, commentType, e.getMessage(), e);
            return CommentQueryResponse.error("QUERY_COMMENT_COUNT_ERROR", "查询评论统计失败");
        }
    }

    /**
     * 查询用户评论历史
     */
    @Override
    @Facade
    public PageResponse<CommentInfo> queryUserComments(Long userId, String commentType, Integer pageNum, Integer pageSize) {
        try {
            log.info("查询用户评论历史，用户ID：{}，评论类型：{}，页码：{}", userId, commentType, pageNum);
            
            CommentType type = null;
            if (commentType != null) {
                type = CommentType.valueOf(commentType.toUpperCase());
            }
            
            IPage<Comment> commentPage = commentDomainService.queryUserComments(
                userId, type, CommentStatus.NORMAL, pageNum, pageSize
            );
            
            List<CommentInfo> commentInfos = commentPage.getRecords().stream()
                .map(commentConvertor::convertToCommentInfo)
                .collect(Collectors.toList());
            
            return PageResponse.of(
                commentInfos,
                (int) commentPage.getTotal(),
                pageSize,
                pageNum
            );
            
        } catch (Exception e) {
            log.error("查询用户评论历史失败，用户ID：{}，错误：{}", userId, e.getMessage(), e);
            return PageResponse.error("QUERY_USER_COMMENTS_ERROR", "查询用户评论历史失败");
        }
    }

    /**
     * 查询热门评论
     */
    @Override
    @Facade
    public CommentQueryResponse<List<CommentInfo>> queryHotComments(Long targetId, String commentType, Integer limit) {
        try {
            log.info("查询热门评论，目标ID：{}，评论类型：{}，数量：{}", targetId, commentType, limit);
            
            CommentType type = CommentType.valueOf(commentType.toUpperCase());
            List<Comment> hotComments = commentDomainService.queryHotComments(targetId, type, limit);
            
            List<CommentInfo> commentInfos = hotComments.stream()
                .map(commentConvertor::convertToCommentInfo)
                .collect(Collectors.toList());
            
            return CommentQueryResponse.success(commentInfos);
            
        } catch (Exception e) {
            log.error("查询热门评论失败，目标ID：{}，错误：{}", targetId, e.getMessage(), e);
            return CommentQueryResponse.error("QUERY_HOT_COMMENTS_ERROR", "查询热门评论失败");
        }
    }

    /**
     * 获取评论详细统计信息
     */
    @Override
    @Facade
    public CommentQueryResponse<Map<String, Object>> getCommentStatistics(Long targetId, String commentType) {
        try {
            log.info("获取评论统计信息，目标ID：{}，评论类型：{}", targetId, commentType);
            
            CommentType type = CommentType.valueOf(commentType.toUpperCase());
            Map<String, Object> statistics = commentDomainService.getCommentStatistics(targetId, type);
            
            return CommentQueryResponse.success(statistics);
            
        } catch (Exception e) {
            log.error("获取评论统计信息失败，目标ID：{}，错误：{}", targetId, e.getMessage(), e);
            return CommentQueryResponse.error("GET_COMMENT_STATISTICS_ERROR", "获取评论统计信息失败");
        }
    }

    /**
     * 检查用户是否已点赞评论
     */
    @Override
    @Facade
    public CommentQueryResponse<Boolean> checkUserLiked(Long commentId, Long userId) {
        try {
            log.info("检查用户点赞状态，评论ID：{}，用户ID：{}", commentId, userId);
            
            boolean isLiked = commentDomainService.isCommentLikedByUser(commentId, userId);
            return CommentQueryResponse.success(isLiked);
            
        } catch (Exception e) {
            log.error("检查用户点赞状态失败，评论ID：{}，用户ID：{}，错误：{}", commentId, userId, e.getMessage(), e);
            return CommentQueryResponse.error("CHECK_LIKE_STATUS_ERROR", "检查点赞状态失败");
        }
    }

    /**
     * 查询子评论列表
     */
    @Override
    @Facade
    public CommentQueryResponse<List<CommentInfo>> queryChildComments(Long parentCommentId, Integer limit) {
        try {
            log.info("查询子评论列表，父评论ID：{}，数量：{}", parentCommentId, limit);
            
            List<Comment> childComments = commentDomainService.queryChildComments(parentCommentId, limit);
            
            List<CommentInfo> commentInfos = childComments.stream()
                .map(commentConvertor::convertToCommentInfo)
                .collect(Collectors.toList());
            
            return CommentQueryResponse.success(commentInfos);
            
        } catch (Exception e) {
            log.error("查询子评论列表失败，父评论ID：{}，错误：{}", parentCommentId, e.getMessage(), e);
            return CommentQueryResponse.error("QUERY_CHILD_COMMENTS_ERROR", "查询子评论列表失败");
        }
    }

    /**
     * 更新评论状态
     */
    @Override
    @Facade
    public CommentResponse updateCommentStatus(Long commentId, Long userId, String status) {
        try {
            log.info("更新评论状态，评论ID：{}，用户ID：{}，状态：{}", commentId, userId, status);
            
            CommentStatus commentStatus = CommentStatus.valueOf(status.toUpperCase());
            commentDomainService.updateCommentStatus(commentId, userId, commentStatus);
            
            return CommentResponse.success(commentId, "评论状态更新成功");
            
        } catch (Exception e) {
            log.error("更新评论状态失败，评论ID：{}，错误：{}", commentId, e.getMessage(), e);
            return CommentResponse.error("UPDATE_COMMENT_STATUS_ERROR", "更新评论状态失败");
        }
    }
} 