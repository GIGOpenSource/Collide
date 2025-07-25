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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public CommentResponse likeComment(Long commentId, Long userId, Boolean isLike) {
        try {
            // 更新评论状态（这里暂时使用NORMAL状态，实际应该是点赞逻辑）
            commentDomainService.updateCommentStatus(commentId, userId, CommentStatus.NORMAL);
            return CommentResponse.success(commentId, isLike ? "点赞成功" : "取消点赞成功");
            
        } catch (Exception e) {
            log.error("评论点赞失败，评论ID：{}，用户ID：{}，点赞：{}，错误：{}", commentId, userId, isLike, e.getMessage(), e);
            return CommentResponse.error("LIKE_COMMENT_ERROR", "评论点赞失败");
        }
    }

    /**
     * 查询评论详情
     */
    @Override
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
} 