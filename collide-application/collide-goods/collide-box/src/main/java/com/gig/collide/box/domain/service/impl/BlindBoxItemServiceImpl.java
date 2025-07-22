package com.gig.collide.box.domain.service.impl;

import com.gig.collide.api.box.request.BlindBoxItemPageQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.box.domain.entity.BlindBoxItem;
import com.gig.collide.box.domain.listener.event.BlindBoxOpenEvent;
import com.gig.collide.box.domain.service.BlindBoxItemService;
import com.gig.collide.box.exception.BlindBoxException;
import com.gig.collide.box.infrastructure.mapper.BlindBoxItemMapper;
import com.gig.collide.collection.domain.service.impl.HeldCollectionService;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.gig.collide.box.exception.BlindBoxErrorCode.BLIND_BOX_ITEM_SAVE_FAILED;
import static com.gig.collide.box.exception.BlindBoxErrorCode.BLIND_BOX_OPEN_FAILED;

/**
 * 盲盒条目服务实现
 *
 * @author Hollis
 */
@Service
public class BlindBoxItemServiceImpl extends ServiceImpl<BlindBoxItemMapper, BlindBoxItem> implements BlindBoxItemService {
    @Autowired
    private HeldCollectionService heldCollectionService;

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private BlindBoxItemMapper blindBoxItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchCreateItem(List<BlindBoxItem> blindBoxItems) {
        //this调用会使saveBatch中的事务失效，需要在本方法外增加事务
        boolean result = this.saveBatch(blindBoxItems);

        Assert.isTrue(result, () -> new BlindBoxException(BLIND_BOX_ITEM_SAVE_FAILED));
        return true;
    }

    @Override
    public BlindBoxItem open(BlindBoxItem blindBoxItem) {
        blindBoxItem.opening();
        boolean result = this.updateById(blindBoxItem);
        Assert.isTrue(result, () -> new BlindBoxException(BLIND_BOX_OPEN_FAILED));
        //通过事件驱动解耦，具体逻辑在 BlindBoxEventListener 中处理
        applicationContext.publishEvent(new BlindBoxOpenEvent(blindBoxItem.getId()));
        return blindBoxItem;
    }

    @Override
    public BlindBoxItem queryById(Long blindBoxItemId) {
        return this.getById(blindBoxItemId);
    }

    @Override
    public List<BlindBoxItem> queryListById(List<Long> itemIds) {
        List<BlindBoxItem> retList = listByIds(itemIds);
        if (CollectionUtils.isEmpty(retList)) {
            return null;
        }
        return retList;
    }

    @Override
    public Long queryRandomByBoxIdAndState(Long blindBoxId, String state) {
        return blindBoxItemMapper.queryRandomByBoxIdAndState(blindBoxId, state);
    }

    @Override
    public List<BlindBoxItem> queryListByBoxIdAndState(Long blindBoxId, String state) {
        QueryWrapper<BlindBoxItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blind_box_id", blindBoxId);
        queryWrapper.eq("state", state);
        List<BlindBoxItem> retList = list(queryWrapper);
        if (CollectionUtils.isEmpty(retList)) {
            return null;
        }
        return retList;
    }

    @Override
    public PageResponse<BlindBoxItem> pageQueryBlindBoxItem(BlindBoxItemPageQueryRequest request) {
        Page<BlindBoxItem> page = new Page<>(request.getCurrentPage(), request.getPageSize());
        QueryWrapper<BlindBoxItem> wrapper = new QueryWrapper<>();
        wrapper.eq("state", request.getState());
        if (StringUtils.isNotBlank(request.getUserId())) {
            wrapper.eq("user_id", request.getUserId());
        }
        if (request.getBoxId() != null) {
            wrapper.eq("blind_box_id", request.getBoxId());
        }
        wrapper.like("name", request.getKeyword());
        wrapper.orderBy(true, true, "gmt_create");
        Page<BlindBoxItem> blindBoxItemPage = this.page(page, wrapper);
        return PageResponse.of(blindBoxItemPage.getRecords(), (int) blindBoxItemPage.getTotal(), request.getPageSize(), request.getCurrentPage());
    }
}
