package com.gig.collide.goods.facade;

import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.api.goods.service.GoodsFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;
import com.gig.collide.goods.domain.service.GoodsDomainService;
import com.gig.collide.goods.domain.entity.Goods;
import com.gig.collide.goods.domain.convertor.GoodsConvertor;
import com.gig.collide.goods.infrastructure.service.IdempotentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品服务 Facade 实现
 * 
 * 提供商品相关的 RPC 服务接口实现
 * 使用标准化的collide-rpc组件进行Dubbo RPC通信
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService(version = "1.0.0", timeout = 5000)
@RequiredArgsConstructor
public class GoodsFacadeServiceImpl implements GoodsFacadeService {

    private final GoodsDomainService goodsDomainService;
    private final IdempotentService idempotentService;

    @Override
    public SingleResponse<Long> createGoods(GoodsCreateRequest createRequest) {
        try {
            log.info("创建商品，请求：{}", createRequest);
            
            if (createRequest == null) {
                return SingleResponse.fail("PARAM_ERROR", "创建商品请求不能为空");
            }

            // 幂等性处理
            if (createRequest.getIdempotentKey() != null && !createRequest.getIdempotentKey().trim().isEmpty()) {
                String idempotentKey = idempotentService.generateIdempotentKey("create_goods", createRequest.getIdempotentKey());
                
                try {
                    Long goodsId = idempotentService.executeWithIdempotent(idempotentKey, () -> {
                        return goodsDomainService.createGoods(createRequest);
                    });
                    
                    log.info("商品创建成功（幂等），商品ID：{}", goodsId);
                    return SingleResponse.of(goodsId);
                    
                } catch (IllegalStateException e) {
                    log.warn("重复创建商品请求：{}", createRequest.getIdempotentKey());
                    return SingleResponse.fail("DUPLICATE_REQUEST", "重复请求，请勿重复提交");
                }
            } else {
                // 无幂等键，直接处理
                Long goodsId = goodsDomainService.createGoods(createRequest);
                
                log.info("商品创建成功，商品ID：{}", goodsId);
                return SingleResponse.of(goodsId);
            }
            
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return SingleResponse.fail("CREATE_GOODS_ERROR", "创建商品失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Void> updateGoods(GoodsUpdateRequest updateRequest) {
        try {
            log.info("更新商品，请求：{}", updateRequest);
            
            if (updateRequest == null || updateRequest.getGoodsId() == null) {
                return SingleResponse.fail("PARAM_ERROR", "更新商品请求或商品ID不能为空");
            }

            // 幂等性处理
            if (updateRequest.getIdempotentKey() != null && !updateRequest.getIdempotentKey().trim().isEmpty()) {
                String idempotentKey = idempotentService.generateIdempotentKey("update_goods", 
                    updateRequest.getGoodsId() + ":" + updateRequest.getIdempotentKey());
                
                try {
                    idempotentService.executeWithIdempotent(idempotentKey, () -> {
                        goodsDomainService.updateGoods(updateRequest);
                        return null;
                    });
                    
                    log.info("商品更新成功（幂等），商品ID：{}", updateRequest.getGoodsId());
                    return SingleResponse.of(null);
                    
                } catch (IllegalStateException e) {
                    log.warn("重复更新商品请求，商品ID：{}，幂等键：{}", updateRequest.getGoodsId(), updateRequest.getIdempotentKey());
                    return SingleResponse.fail("DUPLICATE_REQUEST", "重复请求，请勿重复提交");
                }
            } else {
                // 无幂等键，直接处理
                goodsDomainService.updateGoods(updateRequest);
                
                log.info("商品更新成功，商品ID：{}", updateRequest.getGoodsId());
                return SingleResponse.of(null);
            }
            
        } catch (Exception e) {
            log.error("更新商品失败，商品ID：{}", updateRequest != null ? updateRequest.getGoodsId() : null, e);
            return SingleResponse.fail("UPDATE_GOODS_ERROR", "更新商品失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Void> deleteGoods(Long goodsId) {
        try {
            log.info("删除商品，商品ID：{}", goodsId);
            
            if (goodsId == null) {
                return SingleResponse.fail("PARAM_ERROR", "商品ID不能为空");
            }

            goodsDomainService.deleteGoods(goodsId);
            
            log.info("商品删除成功，商品ID：{}", goodsId);
            return SingleResponse.of(null);
            
        } catch (Exception e) {
            log.error("删除商品失败，商品ID：{}", goodsId, e);
            return SingleResponse.fail("DELETE_GOODS_ERROR", "删除商品失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<GoodsInfo> getGoodsDetail(Long goodsId) {
        try {
            log.info("获取商品详情，商品ID：{}", goodsId);
            
            if (goodsId == null) {
                return SingleResponse.fail("PARAM_ERROR", "商品ID不能为空");
            }

            GoodsInfo goodsInfo = goodsDomainService.getGoodsDetail(goodsId);
            if (goodsInfo == null) {
                return SingleResponse.fail("GOODS_NOT_FOUND", "商品不存在");
            }
            
            log.info("获取商品详情成功，商品ID：{}", goodsId);
            return SingleResponse.of(goodsInfo);
            
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID：{}", goodsId, e);
            return SingleResponse.fail("GET_GOODS_DETAIL_ERROR", "获取商品详情失败：" + e.getMessage());
        }
    }

    @Override
    public PageResponse<GoodsInfo> pageQueryGoods(GoodsPageQueryRequest queryRequest) {
        try {
            log.info("分页查询商品，请求：{}", queryRequest);
            
            if (queryRequest == null) {
                return PageResponse.empty();
            }

            PageResponse<GoodsInfo> pageResponse = goodsDomainService.pageQueryGoodsForUser(queryRequest);
            
            log.info("分页查询商品成功，总数：{}", pageResponse.getTotal());
            return pageResponse;
            
        } catch (Exception e) {
            log.error("分页查询商品失败", e);
            return PageResponse.empty();
        }
    }

    @Override
    public SingleResponse<Void> putOnSale(Long goodsId) {
        try {
            log.info("商品上架，商品ID：{}", goodsId);
            
            if (goodsId == null) {
                return SingleResponse.fail("PARAM_ERROR", "商品ID不能为空");
            }

            goodsDomainService.putOnSale(goodsId);
            
            log.info("商品上架成功，商品ID：{}", goodsId);
            return SingleResponse.of(null);
            
        } catch (Exception e) {
            log.error("商品上架失败，商品ID：{}", goodsId, e);
            return SingleResponse.fail("PUT_ON_SALE_ERROR", "商品上架失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Void> putOffSale(Long goodsId) {
        try {
            log.info("商品下架，商品ID：{}", goodsId);
            
            if (goodsId == null) {
                return SingleResponse.fail("PARAM_ERROR", "商品ID不能为空");
            }

            goodsDomainService.putOffSale(goodsId);
            
            log.info("商品下架成功，商品ID：{}", goodsId);
            return SingleResponse.of(null);
            
        } catch (Exception e) {
            log.error("商品下架失败，商品ID：{}", goodsId, e);
            return SingleResponse.fail("PUT_OFF_SALE_ERROR", "商品下架失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Object> batchPutOnSale(List<Long> goodsIds) {
        try {
            log.info("批量商品上架，商品ID列表：{}", goodsIds);
            
            if (goodsIds == null || goodsIds.isEmpty()) {
                return SingleResponse.fail("PARAM_ERROR", "商品ID列表不能为空");
            }

            goodsDomainService.batchPutOnSale(goodsIds);
            
            log.info("批量商品上架成功，数量：{}", goodsIds.size());
            return SingleResponse.of("批量上架成功，共处理 " + goodsIds.size() + " 个商品");
            
        } catch (Exception e) {
            log.error("批量商品上架失败", e);
            return SingleResponse.fail("BATCH_PUT_ON_SALE_ERROR", "批量商品上架失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Object> batchPutOffSale(List<Long> goodsIds) {
        try {
            log.info("批量商品下架，商品ID列表：{}", goodsIds);
            
            if (goodsIds == null || goodsIds.isEmpty()) {
                return SingleResponse.fail("PARAM_ERROR", "商品ID列表不能为空");
            }

            goodsDomainService.batchPutOffSale(goodsIds);
            
            log.info("批量商品下架成功，数量：{}", goodsIds.size());
            return SingleResponse.of("批量下架成功，共处理 " + goodsIds.size() + " 个商品");
            
        } catch (Exception e) {
            log.error("批量商品下架失败", e);
            return SingleResponse.fail("BATCH_PUT_OFF_SALE_ERROR", "批量商品下架失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Void> updateStock(GoodsStockRequest stockRequest) {
        try {
            log.info("更新商品库存，请求：{}", stockRequest);
            
            if (stockRequest == null || stockRequest.getGoodsId() == null) {
                return SingleResponse.fail("PARAM_ERROR", "库存更新请求或商品ID不能为空");
            }

            // 幂等性处理（库存操作需要特别注意幂等性）
            if (stockRequest.getIdempotentKey() != null && !stockRequest.getIdempotentKey().trim().isEmpty()) {
                String idempotentKey = idempotentService.generateIdempotentKey("update_stock", 
                    stockRequest.getGoodsId() + ":" + stockRequest.getOperationType() + ":" + stockRequest.getIdempotentKey());
                
                try {
                    idempotentService.executeWithIdempotent(idempotentKey, () -> {
                        goodsDomainService.updateStock(stockRequest);
                        return null;
                    });
                    
                    log.info("商品库存更新成功（幂等），商品ID：{}", stockRequest.getGoodsId());
                    return SingleResponse.of(null);
                    
                } catch (IllegalStateException e) {
                    log.warn("重复库存更新请求，商品ID：{}，操作：{}，幂等键：{}", 
                        stockRequest.getGoodsId(), stockRequest.getOperationType(), stockRequest.getIdempotentKey());
                    return SingleResponse.fail("DUPLICATE_REQUEST", "重复请求，请勿重复提交");
                }
            } else {
                // 无幂等键，直接处理（风险提示）
                log.warn("库存更新缺少幂等键，存在重复操作风险，商品ID：{}", stockRequest.getGoodsId());
                goodsDomainService.updateStock(stockRequest);
                
                log.info("商品库存更新成功，商品ID：{}", stockRequest.getGoodsId());
                return SingleResponse.of(null);
            }
            
        } catch (Exception e) {
            log.error("更新商品库存失败，商品ID：{}", stockRequest != null ? stockRequest.getGoodsId() : null, e);
            return SingleResponse.fail("UPDATE_STOCK_ERROR", "更新商品库存失败：" + e.getMessage());
        }
    }
}