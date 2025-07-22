package com.gig.collide.collection.job;

import com.gig.collide.api.chain.constant.ChainOperateBizTypeEnum;
import com.gig.collide.api.chain.request.ChainProcessRequest;
import com.gig.collide.api.chain.service.ChainFacadeService;
import com.gig.collide.api.user.request.UserQueryRequest;
import com.gig.collide.api.user.response.UserQueryResponse;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.base.utils.RemoteCallWrapper;
import com.gig.collide.collection.domain.entity.HeldCollection;
import com.gig.collide.collection.domain.service.impl.HeldCollectionService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 藏品上链铸造重试任务
 *
 * @author Hollis
 */
@Component
public class CollectionChainMintRetryJob {

    @Autowired
    private HeldCollectionService heldCollectionService;

    @Autowired
    private ChainFacadeService chainFacadeService;

    @Autowired
    private UserFacadeService userFacadeService;

    private static final int PAGE_SIZE = 100;

    private static final Logger LOG = LoggerFactory.getLogger(CollectionChainMintRetryJob.class);

    @XxlJob("collectionChainMintRetryJob")
    public ReturnT<String> execute() {
        Long minId = heldCollectionService.queryMinIdForMint();

        List<HeldCollection> heldCollections = heldCollectionService.pageQueryForChainMint(PAGE_SIZE, minId);

        while (CollectionUtils.isNotEmpty(heldCollections)) {
            heldCollections.forEach(this::executeSingle);
            Long maxId = heldCollections.stream().mapToLong(HeldCollection::getId).max().orElse(Long.MAX_VALUE);
            heldCollections = heldCollectionService.pageQueryForChainMint(PAGE_SIZE, maxId + 1);
        }

        return ReturnT.SUCCESS;
    }

    private void executeSingle(HeldCollection heldCollection) {
        LOG.info("start to execute chainMint retry , heldCollectionId is {}", heldCollection.getId());

        UserQueryRequest userQueryRequest = new UserQueryRequest(Long.valueOf(heldCollection.getUserId()));
        UserQueryResponse<UserInfo> userQueryResponse = userFacadeService.query(userQueryRequest);

        ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
        chainProcessRequest.setRecipient(userQueryResponse.getData().getBlockChainUrl());
        chainProcessRequest.setClassId(heldCollection.getCollectionId().toString());
        chainProcessRequest.setClassName(heldCollection.getName());
        chainProcessRequest.setSerialNo(heldCollection.getSerialNo());
        chainProcessRequest.setBizId(heldCollection.getId().toString());
        chainProcessRequest.setBizType(ChainOperateBizTypeEnum.HELD_COLLECTION.name());
        chainProcessRequest.setIdentifier(heldCollection.getId().toString());

        //如果失败了，则依靠定时任务补偿
        RemoteCallWrapper.call(req -> chainFacadeService.mint(req), chainProcessRequest, "mint");
        LOG.info("transaction is commit ,end to mint , heldCollectionId : " + heldCollection.getId());
    }
}
