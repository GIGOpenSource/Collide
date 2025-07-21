package com.gig.collide.chain.domain.service.impl;

import com.gig.collide.api.chain.constant.ChainOperateBizTypeEnum;
import com.gig.collide.api.chain.constant.ChainOperateTypeEnum;
import com.gig.collide.api.chain.constant.ChainType;
import com.gig.collide.api.chain.request.ChainProcessRequest;
import com.gig.collide.api.chain.request.ChainQueryRequest;
import com.gig.collide.api.chain.response.ChainProcessResponse;
import com.gig.collide.api.chain.response.data.ChainCreateData;
import com.gig.collide.api.chain.response.data.ChainOperationData;
import com.gig.collide.api.chain.response.data.ChainResultData;
import com.gig.collide.chain.domain.constant.ChainCodeEnum;
import com.gig.collide.chain.domain.constant.ChainOperateStateEnum;
import com.gig.collide.chain.domain.entity.ChainRequest;
import com.gig.collide.chain.domain.response.ChainResponse;
import com.gig.collide.chain.domain.service.AbstractChainService;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * mock链服务
 *
 * @author GIG
 */
@Service("mockChainService")
public class MockChainServiceImpl extends AbstractChainService {

    @Override
    public ChainProcessResponse<ChainCreateData> createAddr(ChainProcessRequest request) {
        return new ChainProcessResponse.Builder().responseCode(ChainCodeEnum.SUCCESS.name()).data(
                new ChainCreateData(request.getIdentifier(), UUID.randomUUID().toString(), "mockBlockChainName",
                        ChainType.MOCK.name())).buildSuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChainProcessResponse<ChainOperationData> mint(ChainProcessRequest request) {
        return doPostExecute(request, ChainOperateTypeEnum.COLLECTION_MINT, chainRequest -> {
        });
    }

    @Override
    public ChainProcessResponse<ChainOperationData> chain(ChainProcessRequest chainProcessRequest) {
        if (StringUtils.equals(ChainOperateBizTypeEnum.BLIND_BOX.name(), chainProcessRequest.getBizType())) {
            return doPostExecute(chainProcessRequest, ChainOperateTypeEnum.BLIND_BOX_CHAIN, chainRequest -> {
            });
        } else {
            return doPostExecute(chainProcessRequest, ChainOperateTypeEnum.COLLECTION_CHAIN, chainRequest -> {
            });
        }
    }

    @Override
    public ChainProcessResponse<ChainOperationData> transfer(ChainProcessRequest request) {
        return doPostExecute(request, ChainOperateTypeEnum.COLLECTION_TRANSFER, chainRequest -> {
        });
    }

    @Override
    public ChainProcessResponse<ChainOperationData> destroy(ChainProcessRequest request) {
        return doPostExecute(request, ChainOperateTypeEnum.COLLECTION_DESTROY, chainRequest -> {
        });
    }

    @Override
    public ChainProcessResponse<ChainResultData> queryChainResult(ChainQueryRequest request) {
        ChainProcessResponse<ChainResultData> response = new ChainProcessResponse<>();
        response.setSuccess(true);
        response.setResponseCode("200");
        response.setResponseMessage("SUCCESS");
        ChainResultData data = new ChainResultData();
        data.setTxHash(UUID.randomUUID().toString());
        data.setNftId("nftId");
        data.setState(ChainOperateStateEnum.SUCCEED.name());
        response.setData(data);
        return response;
    }

    @Override
    protected ChainResponse doPost(ChainRequest chainRequest) {
        ChainResponse chainResponse = new ChainResponse();
        chainResponse.setSuccess(true);
        JSONObject data = new JSONObject();
        data.put("success",true);
        data.put("chainType","mock");
        chainResponse.setData(data);
        return chainResponse;
    }

    @Override
    protected ChainResponse doDelete(ChainRequest chainRequest) {
        ChainResponse chainResponse = new ChainResponse();
        chainResponse.setSuccess(true);
        JSONObject data = new JSONObject();
        data.put("success",true);
        data.put("chainType","mock");
        chainResponse.setData(data);
        return chainResponse;
    }

    @Override
    protected ChainResponse doGetQuery(ChainRequest chainRequest) {
        ChainResponse chainResponse = new ChainResponse();
        chainResponse.setSuccess(true);
        JSONObject data = new JSONObject();
        data.put("success",true);
        data.put("chainType","mock");
        chainResponse.setData(data);
        return chainResponse;
    }

    @Override
    protected String chainType() {
        return ChainType.MOCK.name();
    }
}
