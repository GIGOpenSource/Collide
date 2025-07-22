package com.gig.collide.goods.service;

import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.GoodsSaleResponse;
import com.gig.collide.api.goods.service.GoodsTransactionFacadeService;
import com.gig.collide.box.domain.service.BlindBoxService;
import com.gig.collide.collection.domain.service.CollectionService;
import com.gig.collide.lock.DistributeLock;
import com.gig.collide.rpc.facade.Facade;
import com.gig.collide.tcc.entity.TransCancelSuccessType;
import com.gig.collide.tcc.entity.TransConfirmSuccessType;
import com.gig.collide.tcc.entity.TransTrySuccessType;
import com.gig.collide.tcc.request.TccRequest;
import com.gig.collide.tcc.response.TransactionCancelResponse;
import com.gig.collide.tcc.response.TransactionConfirmResponse;
import com.gig.collide.tcc.response.TransactionTryResponse;
import com.gig.collide.tcc.service.TransactionLogService;
import cn.hutool.core.lang.Assert;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Hollis
 */
@DubboService(version = "1.0.0")
public class GoodsTransactionFacadeServiceImpl implements GoodsTransactionFacadeService {

    private static final String ERROR_CODE_UNSUPPORTED_GOODS_TYPE = "UNSUPPORTED_GOODS_TYPE";

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private BlindBoxService blindBoxService;

    @Autowired
    private TransactionLogService transactionLogService;


    @Override
    @Facade
    @Transactional(rollbackFor = Exception.class)
    @DistributeLock(keyExpression = "#request.bizNo",scene = "NORMAL_BUY_GOODS")
    public GoodsSaleResponse tryDecreaseInventory(GoodsSaleRequest request) {

        GoodsFreezeInventoryRequest goodsTrySaleRequest = new GoodsFreezeInventoryRequest(request.getBizNo(), request.getGoodsId(), request.getQuantity());

        GoodsType goodsType = GoodsType.valueOf(request.getGoodsType());

        TransactionTryResponse transactionTryResponse = transactionLogService.tryTransaction(new TccRequest(request.getBizNo(), "normalBuy", goodsType.name()));
        Assert.isTrue(transactionTryResponse.getSuccess(), "transaction try failed");

        if (transactionTryResponse.getTransTrySuccessType() == TransTrySuccessType.TRY_SUCCESS) {
            Boolean freezeResult = switch (goodsType) {
                case BLIND_BOX -> blindBoxService.freezeInventory(goodsTrySaleRequest);
                case COLLECTION -> collectionService.freezeInventory(goodsTrySaleRequest);
                default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
            };
            Assert.isTrue(freezeResult, "freeze inventory failed");
            GoodsSaleResponse response = new GoodsSaleResponse();
            response.setSuccess(true);
            return response;
        }

        return new GoodsSaleResponse.GoodsResponseBuilder().buildSuccess();
    }

    @Override
    @Facade
    @Transactional(rollbackFor = Exception.class)
    @DistributeLock(keyExpression = "#request.bizNo",scene = "NORMAL_BUY_GOODS")
    public GoodsSaleResponse confirmDecreaseInventory(GoodsSaleRequest request) {
        GoodsUnfreezeAndSaleRequest unfreezeAndSaleRequest = new GoodsUnfreezeAndSaleRequest(request.getBizNo(), request.getGoodsId(), request.getQuantity());
        GoodsType goodsType = GoodsType.valueOf(request.getGoodsType());
        TransactionConfirmResponse transactionConfirmResponse = transactionLogService.confirmTransaction(new TccRequest(request.getBizNo(), "normalBuy", goodsType.name()));
        Assert.isTrue(transactionConfirmResponse.getSuccess(), "transaction confirm failed");

        if (transactionConfirmResponse.getTransConfirmSuccessType() == TransConfirmSuccessType.CONFIRM_SUCCESS) {
            Boolean unfreezeResult = switch (goodsType) {
                case BLIND_BOX -> blindBoxService.unfreezeAndSale(unfreezeAndSaleRequest);
                case COLLECTION -> collectionService.unfreezeAndSale(unfreezeAndSaleRequest);
                default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
            };
            Assert.isTrue(unfreezeResult, "unfreeze inventory failed");

            GoodsSaleResponse response = new GoodsSaleResponse();
            response.setSuccess(true);
            return response;
        }

        return new GoodsSaleResponse.GoodsResponseBuilder().buildSuccess();
    }

    @Override
    @Facade
    @Transactional(rollbackFor = Exception.class)
    @DistributeLock(keyExpression = "#request.bizNo",scene = "NORMAL_BUY_GOODS")
    public GoodsSaleResponse cancelDecreaseInventory(GoodsSaleRequest request) {
        GoodsType goodsType = GoodsType.valueOf(request.getGoodsType());
        TransactionCancelResponse transactionCancelResponse = transactionLogService.cancelTransaction(new TccRequest(request.getBizNo(), "normalBuy", goodsType.name()));
        Assert.isTrue(transactionCancelResponse.getSuccess(), "transaction cancel failed");

        //如果发生空回滚，或者回滚幂等，则不进行解冻库存操作
        //Try成功后的Cancel，直接解冻库存
        if (transactionCancelResponse.getTransCancelSuccessType() == TransCancelSuccessType.CANCEL_AFTER_TRY_SUCCESS) {
            GoodsUnfreezeInventoryRequest unfreezeInventoryRequest = new GoodsUnfreezeInventoryRequest(request.getBizNo(), request.getGoodsId(), request.getQuantity());
            Boolean unfreezeResult = switch (goodsType) {
                case BLIND_BOX -> blindBoxService.unfreezeInventory(unfreezeInventoryRequest);
                case COLLECTION -> collectionService.unfreezeInventory(unfreezeInventoryRequest);
                default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
            };
            Assert.isTrue(unfreezeResult, "unfreeze inventory failed");
        }

        //如果发生空回滚，或者回滚幂等，则不进行解冻库存操作
        //Confirm成功后的Cancel，直接回滚库存
        if (transactionCancelResponse.getTransCancelSuccessType() == TransCancelSuccessType.CANCEL_AFTER_CONFIRM_SUCCESS) {
            GoodsCancelSaleRequest goodsCancelSaleRequest = new GoodsCancelSaleRequest(request.getBizNo(), request.getGoodsId(), request.getQuantity());
            Boolean cancelResult = switch (goodsType) {
                case BLIND_BOX -> blindBoxService.cancel(goodsCancelSaleRequest);
                case COLLECTION -> collectionService.cancel(goodsCancelSaleRequest);
                default -> throw new UnsupportedOperationException(ERROR_CODE_UNSUPPORTED_GOODS_TYPE);
            };
            Assert.isTrue(cancelResult, "cancel inventory failed");
        }

        GoodsSaleResponse response = new GoodsSaleResponse();
        response.setSuccess(true);
        return response;
    }
}
