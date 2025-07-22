package com.gig.collide.box.domain.listener;

import com.gig.collide.api.chain.constant.ChainOperateBizTypeEnum;
import com.gig.collide.api.chain.request.ChainProcessRequest;
import com.gig.collide.api.chain.response.ChainProcessResponse;
import com.gig.collide.api.chain.response.data.ChainOperationData;
import com.gig.collide.api.chain.service.ChainFacadeService;
import com.gig.collide.api.collection.constant.GoodsSaleBizType;
import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.utils.RemoteCallWrapper;
import com.gig.collide.box.domain.entity.BlindBoxItem;
import com.gig.collide.box.domain.listener.event.BlindBoxOpenEvent;
import com.gig.collide.box.domain.service.BlindBoxItemService;
import com.gig.collide.box.exception.BlindBoxException;
import com.gig.collide.collection.domain.entity.HeldCollection;
import com.gig.collide.collection.domain.request.HeldCollectionCreateRequest;
import com.gig.collide.collection.domain.service.impl.HeldCollectionService;
import cn.hutool.core.lang.Assert;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.gig.collide.api.common.constant.CommonConstant.SEPARATOR;
import static com.gig.collide.box.exception.BlindBoxErrorCode.BLIND_BOX_ITEM_SAVE_FAILED;
import static com.gig.collide.box.exception.BlindBoxErrorCode.BLIND_BOX_OPEN_FAILED;

/**
 * @author Hollis
 */
@Component
public class BlindBoxEventListener {

    @Autowired
    private UserFacadeService userFacadeService;

    @Autowired
    private ChainFacadeService chainFacadeService;

    @Autowired
    private BlindBoxItemService blindBoxItemService;

    @Autowired
    private HeldCollectionService heldCollectionService;

    @EventListener(value = BlindBoxOpenEvent.class)
    @Async("blindBoxListenExecutor")
    public void onApplicationEvent(BlindBoxOpenEvent event) {
        Long blindBoxItemId = (Long) event.getSource();

        //查询出更新后的最新值，避免后续 cas 操作失败
        BlindBoxItem blindBoxItem = blindBoxItemService.getById(blindBoxItemId);

        //创建heldCollection
        HeldCollectionCreateRequest heldCollectionCreateRequest = getHeldCollectionCreateRequest(blindBoxItem);
        var heldCollection = heldCollectionService.create(heldCollectionCreateRequest);
        Assert.notNull(heldCollection, () -> new BlindBoxException(BLIND_BOX_OPEN_FAILED));

        //上链
        ChainProcessRequest chainProcessRequest = getChainProcessRequest(blindBoxItem, heldCollection);
        //如果失败了，则依靠定时任务补偿
        ChainProcessResponse<ChainOperationData> response = RemoteCallWrapper.call(req -> chainFacadeService.mint(req), chainProcessRequest, "mint");

        //修改盲盒状态
        if (response.getSuccess()) {
            blindBoxItem.openSuccess();
            var saveResult = blindBoxItemService.updateById(blindBoxItem);
            Assert.isTrue(saveResult, () -> new BlindBoxException(BLIND_BOX_ITEM_SAVE_FAILED));
        }
    }

    private @NotNull ChainProcessRequest getChainProcessRequest(BlindBoxItem blindBoxItem, HeldCollection heldCollection) {
        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(blindBoxItem.getUserId()));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);
        ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
        chainProcessRequest.setRecipient(userQueryResponse.getData().getBlockChainUrl());
        chainProcessRequest.setClassId(GoodsSaleBizType.BLIND_BOX_TRADE + SEPARATOR + blindBoxItem.getBlindBoxId());
        chainProcessRequest.setClassName(blindBoxItem.getName());
        chainProcessRequest.setSerialNo(heldCollection.getSerialNo());
        chainProcessRequest.setBizId(heldCollection.getId().toString());
        chainProcessRequest.setBizType(ChainOperateBizTypeEnum.HELD_COLLECTION.name());
        chainProcessRequest.setIdentifier(blindBoxItem.getId().toString());
        return chainProcessRequest;
    }

    private static @NotNull HeldCollectionCreateRequest getHeldCollectionCreateRequest(BlindBoxItem blindBoxItem) {
        HeldCollectionCreateRequest heldCollectionCreateRequest = new HeldCollectionCreateRequest();
        heldCollectionCreateRequest.setName(blindBoxItem.getCollectionName());
        heldCollectionCreateRequest.setCover(blindBoxItem.getCollectionCover());
        heldCollectionCreateRequest.setBizNo(blindBoxItem.getOrderId());
        heldCollectionCreateRequest.setBizType(GoodsSaleBizType.BLIND_BOX_TRADE.name());
        heldCollectionCreateRequest.setPurchasePrice(blindBoxItem.getPurchasePrice());
        heldCollectionCreateRequest.setReferencePrice(blindBoxItem.getReferencePrice());
        heldCollectionCreateRequest.setRarity(blindBoxItem.getRarity());
        heldCollectionCreateRequest.setUserId(blindBoxItem.getUserId());
        heldCollectionCreateRequest.setGoodsId(blindBoxItem.getId());
        heldCollectionCreateRequest.setGoodsType(GoodsType.BLIND_BOX.name());
        heldCollectionCreateRequest.setSerialNoBaseId(blindBoxItem.getBlindBoxId().toString());
        return heldCollectionCreateRequest;
    }
}
