package com.gig.collide.collection.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.chain.constant.ChainOperateBizTypeEnum;
import com.gig.collide.api.chain.constant.ChainOperateTypeEnum;
import com.gig.collide.api.chain.request.ChainProcessRequest;
import com.gig.collide.api.chain.service.ChainFacadeService;
import com.gig.collide.api.collection.model.CollectionVO;
import com.gig.collide.api.collection.model.HeldCollectionVO;
import com.gig.collide.api.collection.request.CollectionPageQueryRequest;
import com.gig.collide.api.collection.request.HeldCollectionPageQueryRequest;
import com.gig.collide.api.collection.service.CollectionReadFacadeService;
import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.goods.service.GoodsFacadeService;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;
import com.gig.collide.collection.domain.entity.HeldCollection;
import com.gig.collide.collection.domain.request.HeldCollectionDestroyRequest;
import com.gig.collide.collection.domain.request.HeldCollectionTransferRequest;
import com.gig.collide.collection.domain.service.impl.HeldCollectionService;
import com.gig.collide.collection.exception.CollectionException;
import com.gig.collide.collection.param.DestroyParam;
import com.gig.collide.collection.param.TransferParam;
import com.gig.collide.web.util.MultiResultConvertor;
import com.gig.collide.web.vo.MultiResult;
import com.gig.collide.web.vo.Result;
import cn.hutool.core.lang.Assert;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gig.collide.api.common.constant.CommonConstant.SEPARATOR;
import static com.gig.collide.api.order.constant.OrderErrorCode.*;
import static com.gig.collide.collection.exception.CollectionErrorCode.HELD_COLLECTION_OWNER_CHECK_ERROR;
import static com.gig.collide.collection.exception.CollectionErrorCode.HELD_COLLECTION_SAVE_FAILED;

/**
 * @author GIG
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("collection")
public class CollectionController {

    @Autowired
    private GoodsFacadeService goodsFacadeService;
    @Autowired
    private CollectionReadFacadeService collectionReadFacadeService;
    @Autowired
    private ChainFacadeService chainFacadeService;
    @Autowired
    private UserFacadeService userFacadeService;
    @Autowired
    private HeldCollectionService heldCollectionService;

    /**
     * 藏品列表
     *
     * @param
     * @return 结果
     */
    @GetMapping("/collectionList")
    public MultiResult<CollectionVO> collectionList(@RequestParam(required = false) String state,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer pageSize,
                                                   @RequestParam(required = false) Integer currentPage) {
        CollectionPageQueryRequest collectionPageQueryRequest = new CollectionPageQueryRequest();
        collectionPageQueryRequest.setState(state != null ? state : "SUCCEED");
        collectionPageQueryRequest.setKeyword(keyword);
        // 手动处理类型转换，避免验证问题
        collectionPageQueryRequest.setCurrentPage(currentPage != null ? currentPage.intValue() : 1);
        collectionPageQueryRequest.setPageSize(pageSize != null ? pageSize.intValue() : 10);
        PageResponse<CollectionVO> pageResponse = collectionReadFacadeService.pageQuery(collectionPageQueryRequest);
        return MultiResultConvertor.convert(pageResponse);
    }

    /**
     * 测试端点 - 不带任何验证
     */
    @GetMapping("/test")
    public MultiResult<String> test(String state, String keyword, Integer pageSize, Integer currentPage) {
        return MultiResult.successMulti(
            List.of("state=" + state, "keyword=" + keyword, "pageSize=" + pageSize, "currentPage=" + currentPage), 
            4, currentPage != null ? currentPage : 1, pageSize != null ? pageSize : 10);
    }

    /**
     * 藏品详情
     *
     * @param
     * @return 结果
     */
    @GetMapping("/collectionInfo")
    public Result<CollectionVO> collectionInfo(@NotBlank String collectionId) {
        CollectionVO collectionVO = (CollectionVO) goodsFacadeService.getGoods(collectionId, GoodsType.COLLECTION);
        if (collectionVO.canBook()) {
            try {
                String userId = (String) StpUtil.getLoginId();
                Boolean hasBooked = goodsFacadeService.isGoodsBooked(collectionId, GoodsType.COLLECTION, userId);
                collectionVO.setHasBooked(hasBooked);
            } catch (Exception e) {
                //如果用户未登录或其他异常
                collectionVO.setHasBooked(false);
            }
        }
        return Result.success(collectionVO);
    }

    /**
     * 用户持有藏品列表
     *
     * @param
     * @return 结果
     */
    @GetMapping("/heldCollectionList")
    public MultiResult<HeldCollectionVO> heldCollectionList(@RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) String state,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           @RequestParam(defaultValue = "1") Integer currentPage) {
        String userId = (String) StpUtil.getLoginId();
        HeldCollectionPageQueryRequest heldCollectionPageQueryRequest = new HeldCollectionPageQueryRequest();
        heldCollectionPageQueryRequest.setState(state);
        heldCollectionPageQueryRequest.setUserId(userId);
        heldCollectionPageQueryRequest.setCurrentPage(currentPage);
        heldCollectionPageQueryRequest.setPageSize(pageSize);
        heldCollectionPageQueryRequest.setKeyword(keyword);
        PageResponse<HeldCollectionVO> pageResponse = collectionReadFacadeService.pageQueryHeldCollection(heldCollectionPageQueryRequest);
        return MultiResultConvertor.convert(pageResponse);
    }

    /**
     * 用户持有藏品列表
     *
     * @param
     * @return 结果
     */
    @GetMapping("/heldCollectionCount")
    public Result<Long> heldCollectionCount() {
        String userId = (String) StpUtil.getLoginId();

        SingleResponse<Long> response = collectionReadFacadeService.queryHeldCollectionCount(userId);
        return Result.success(response.getData());
    }

    /**
     * 用户持有藏品详情
     *
     * @param
     * @return 结果
     */
    @GetMapping("/heldCollectionInfo")
    public Result<HeldCollectionVO> heldCollectionInfo(@NotBlank String heldCollectionId) {
        SingleResponse<HeldCollectionVO> singleResponse = collectionReadFacadeService.queryHeldCollectionById(Long.valueOf(heldCollectionId));
        return Result.success(singleResponse.getData());
    }

    @PostMapping("/destroy")
    public Result<Boolean> destroy(@Valid @RequestBody DestroyParam param) {
        String userId = (String) StpUtil.getLoginId();

        HeldCollectionDestroyRequest request = new HeldCollectionDestroyRequest();
        request.setOperatorId(userId);
        request.setHeldCollectionId(param.getHeldCollectionId());
        request.setIdentifier(param.getHeldCollectionId());
        HeldCollection heldCollection = heldCollectionService.destroy(request);

        if (null != heldCollection) {
            ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
            chainProcessRequest.setBizId(String.valueOf(param.getHeldCollectionId()));
            chainProcessRequest.setBizType(ChainOperateBizTypeEnum.HELD_COLLECTION.name());
            chainProcessRequest.setIdentifier(param.getHeldCollectionId() + SEPARATOR + ChainOperateTypeEnum.COLLECTION_DESTROY.name());
            UserInfo owner = (UserInfo) StpUtil.getSession().get(userId);
            chainProcessRequest.setOwner(owner.getBlockChainUrl());
            chainProcessRequest.setClassId(String.valueOf(heldCollection.getCollectionId()));
            chainProcessRequest.setNtfId(heldCollection.getNftId());
            var res = chainFacadeService.destroy(chainProcessRequest);
            return Result.success(res.getSuccess());
        }
        return Result.success(false);
    }


    @PostMapping("/transfer")
    public Result<Boolean> transfer(@Valid @RequestBody TransferParam param) {
        String userId = (String) StpUtil.getLoginId();
        if (userId.equals(param.getRecipientUserId())) {
            throw new CollectionException(TRANSFER_SELF_ERROR);
        }
        SingleResponse<HeldCollectionVO> response = collectionReadFacadeService.queryHeldCollectionById(Long.parseLong(param.getHeldCollectionId()));
        HeldCollectionVO heldCollection = response.getData();
        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(param.getRecipientUserId()));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        UserInfo recipient = userQueryResponse.getData();

        if (!userQueryResponse.getSuccess() || userQueryResponse.getData() == null) {
            throw new CollectionException(USER_NOT_EXIST);
        }

        UserInfo userInfo = userQueryResponse.getData();
        if (!userInfo.userCanBuy()) {
            throw new CollectionException(BUYER_STATUS_ABNORMAL);
        }

        if (null != heldCollection && null != recipient) {
            Assert.isTrue(StringUtils.equals(heldCollection.getUserId(), userId), () -> new CollectionException(HELD_COLLECTION_OWNER_CHECK_ERROR));

            //本地数据先变更
            HeldCollectionTransferRequest transferRequest = new HeldCollectionTransferRequest();
            transferRequest.setRecipientUserId(param.getRecipientUserId());
            transferRequest.setHeldCollectionId(param.getHeldCollectionId());
            transferRequest.setOperatorId(userId);
            transferRequest.setIdentifier(param.getHeldCollectionId() + "_TRANSFER");
            HeldCollection transferHeldCollection = heldCollectionService.transfer(transferRequest);
            Assert.notNull(transferHeldCollection, () -> new CollectionException(HELD_COLLECTION_SAVE_FAILED));

            ChainProcessRequest request = new ChainProcessRequest();
            request.setBizId(String.valueOf(transferHeldCollection.getId()));
            request.setBizType(ChainOperateBizTypeEnum.HELD_COLLECTION.name());
            request.setIdentifier(param.getHeldCollectionId() + SEPARATOR + param.getRecipientUserId() + SEPARATOR + ChainOperateTypeEnum.COLLECTION_TRANSFER.name());
            UserInfo owner = (UserInfo) StpUtil.getSession().get(userId);
            request.setOwner(owner.getBlockChainUrl());
            request.setClassId(String.valueOf(heldCollection.getCollectionId()));
            request.setNtfId(transferHeldCollection.getNftId());
            request.setRecipient(recipient.getBlockChainUrl());
            var res = chainFacadeService.transfer(request);
            return Result.success(res.getSuccess());
        }
        return Result.success(false);
    }
}
