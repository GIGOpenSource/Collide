package com.gig.collide.box.facade;

import com.gig.collide.api.box.request.BlindBoxCreateRequest;
import com.gig.collide.api.box.response.BlindBoxCreateResponse;
import com.gig.collide.api.box.service.BlindBoxManageFacadeService;
import com.gig.collide.api.chain.constant.ChainOperateBizTypeEnum;
import com.gig.collide.api.chain.request.ChainProcessRequest;
import com.gig.collide.api.chain.service.ChainFacadeService;
import com.gig.collide.api.collection.constant.GoodsSaleBizType;
import com.gig.collide.box.domain.entity.BlindBox;
import com.gig.collide.box.domain.service.BlindBoxService;
import com.gig.collide.rpc.facade.Facade;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import static com.gig.collide.api.common.constant.CommonConstant.SEPARATOR;

/**
 * 盲盒管理服务
 *
 * @author hollis
 */
@DubboService(version = "1.0.0")
public class BlindBoxManageFacadeServiceImpl implements BlindBoxManageFacadeService {

    @Autowired
    private BlindBoxService blindBoxService;

    @Autowired
    private ChainFacadeService chainFacadeService;

    @Override
    @Facade
    public BlindBoxCreateResponse create(BlindBoxCreateRequest request) {
        BlindBoxCreateResponse response = new BlindBoxCreateResponse();
        //先创建盲盒
        BlindBox blindBox = blindBoxService.create(request);

        //上链藏品
        ChainProcessRequest chainProcessRequest = new ChainProcessRequest();
        chainProcessRequest.setIdentifier(request.getIdentifier() + SEPARATOR + GoodsSaleBizType.BLIND_BOX_TRADE);
        chainProcessRequest.setClassId(GoodsSaleBizType.BLIND_BOX_TRADE + SEPARATOR + blindBox.getId());
        chainProcessRequest.setClassName(request.getName());
        chainProcessRequest.setBizType(ChainOperateBizTypeEnum.BLIND_BOX.name());
        chainProcessRequest.setBizId(blindBox.getId().toString());
        var chainRes = chainFacadeService.chain(chainProcessRequest);

        response.setSuccess(chainRes.getSuccess());
        response.setBlindBoxId(blindBox.getId());
        response.setResponseCode(chainRes.getResponseCode());
        response.setResponseMessage(chainRes.getResponseMessage());
        return response;
    }
}
