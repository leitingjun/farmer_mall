package com.mall.promo.service;

import com.mall.order.OrderCoreService;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.promo.PromoService;
import com.mall.promo.cache.CacheManager;
import com.mall.promo.constants.PromoRetCode;
import com.mall.promo.converter.Product2PromoProduct;
import com.mall.promo.converter.SecKill2Create;
import com.mall.promo.dal.entity.PromoItem;
import com.mall.promo.dal.entity.PromoSession;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dal.persistence.PromoSessionMapper;
import com.mall.promo.dto.*;
import com.mall.promo.mq.PromoProducer;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailDto;
import com.mall.shopping.dto.ProductDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author wangjun
 * @Date 2021/1/29 16:23
 **/

@Slf4j
@Service
@Component
public class PromoServiceImpl implements PromoService {

    @Autowired
    PromoSessionMapper promoSessionMapper;

    @Autowired
    PromoItemMapper promoItemMapper;

    @Reference(timeout = 3000,check = false)
    IProductService iProductService;

    @Reference(timeout = 3000,check = false)
    OrderCoreService orderCoreService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    PromoProducer promoProducer;

    @Autowired
    CacheManager cacheManager;

    @Override
    public SecKillListResponse secKillList(SecKillListRequest request) {
        request.requestCheck();
        SecKillListResponse response = new SecKillListResponse();
        try {
            PromoSession promoSession = new PromoSession();
            Integer sessionId = request.getSessionId();
            promoSession.setSessionId(sessionId);
            promoSession.setYyyymmdd(request.getYyyymmdd());
            PromoSession promoSession1 = promoSessionMapper.selectOne(promoSession);

            //获取秒杀场次id,并拿到对应的item
            Integer psId = promoSession1.getId();
            PromoItem promoItem = new PromoItem();
            promoItem.setPsId(psId);
            List<PromoItem> promoItems = promoItemMapper.select(promoItem);

            List<SecKillListProductDto> list = new ArrayList<>();
            for (PromoItem item : promoItems) {
                SecKillListProductDto secKillListProductDto = new SecKillListProductDto();
                //拿到商品信息
                ProductDetailResponse productDetail = iProductService.getProductDetail(item.getItemId().longValue());
                ProductDetailDto productDetailDto = productDetail.getProductDetailDto();

                //封装要返回的秒杀商品信息
                secKillListProductDto.setId(item.getItemId());
                secKillListProductDto.setInventory(item.getItemStock());
                secKillListProductDto.setPicUrl(productDetailDto.getProductImageBig());
                secKillListProductDto.setPrice(productDetailDto.getSalePrice());
                secKillListProductDto.setProductName(productDetailDto.getProductName());
                secKillListProductDto.setSeckillPrice(item.getSeckillPrice());
                list.add(secKillListProductDto);
            }
            response.setProductList(list);
            response.setPsId(psId);
            response.setSessionId(sessionId);
            response.setCode(PromoRetCode.SUCCESS.getCode());
            response.setMsg(PromoRetCode.SUCCESS.getMessage());
        }catch (Exception e){
            response.setCode(PromoRetCode.SYSTEM_ERROR.getCode());
            response.setMsg(PromoRetCode.SYSTEM_ERROR.getMessage());
        }
        return response;
    }

    @Override
    public PromoProductDetailResponse queryPromoProductDetail(PromoProductDetailRequest request) {
        request.requestCheck();
        PromoProductDetailResponse response = new PromoProductDetailResponse();
        try {
            //查询秒杀价
            PromoItem promoItem = new PromoItem();
            promoItem.setPsId(request.getPsId().intValue());
            promoItem.setItemId(request.getProductId().intValue());
            PromoItem selectedPromoItem = promoItemMapper.selectOne(promoItem);
            BigDecimal seckillPrice = selectedPromoItem.getSeckillPrice();

            //商品详情
            ProductDetailResponse productDetail = iProductService.getProductDetail(request.getProductId());
            ProductDetailDto productDetailDto = productDetail.getProductDetailDto();
            PromoProductDetailDTO promoProductDetailDTO = Product2PromoProduct.product2Promo(productDetailDto, seckillPrice);
            response.setPromoProductDetailDTO(promoProductDetailDTO);
            response.setCode(PromoRetCode.SUCCESS.getCode());
            response.setMsg(PromoRetCode.SUCCESS.getMessage());
        }catch (Exception e){
            response.setCode(PromoRetCode.SYSTEM_ERROR.getCode());
            response.setMsg(PromoRetCode.SYSTEM_ERROR.getMessage());
        }
        return response;
    }

    @Override
    public SecKillResponse secKill(SecKillRequest request) {
        request.requestCheck();
        SecKillResponse response = new SecKillResponse();
        try {
            Long productId = request.getProductId();
            ProductDetailResponse productDetail = iProductService.getProductDetail(productId);
            ProductDetailDto productDetailDto = productDetail.getProductDetailDto();

            PromoItem promoItem = new PromoItem();
            promoItem.setPsId(request.getPsId().intValue());
            promoItem.setItemId(productId.intValue());
            PromoItem item = promoItemMapper.selectOne(promoItem);

            if(item.getItemStock()<1){
                String key="promo_stock_empty"+productId+"_"+request.getPsId();
                cacheManager.setCache(key,"stock_empty",1);
            }

            //调用initOrderHandler方法生成订单
            CreateOrderRequest createOrderRequest = SecKill2Create.secKill2Create(request, productDetailDto, item);
//            String orderId = orderCoreService.createPromoOrder(createOrderRequest);

            //扣减库存


//            //生成商品邮寄信息
//            orderCoreService.createPostInfo(createOrderRequest,orderId);

            boolean result = promoProducer.sendMessage(request);
            if(result){
                response.setInventory(item.getItemStock()-1);
                response.setProductId(productId);
                response.setCode(PromoRetCode.SUCCESS.getCode());
                response.setMsg(PromoRetCode.SUCCESS.getMessage());
            }else {
                response.setCode(PromoRetCode.SYSTEM_ERROR.getCode());
                response.setMsg(PromoRetCode.SYSTEM_ERROR.getMessage());
            }
        }catch (Exception e){
            log.error(e.getCause().getMessage());
            response.setCode(PromoRetCode.SYSTEM_TIMEOUT.getCode());
            response.setMsg(PromoRetCode.SYSTEM_TIMEOUT.getMessage());
        }
        return response;
    }

}
