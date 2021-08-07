package com.mall.promo.mq;

import com.alibaba.fastjson.JSON;
import com.mall.order.OrderCoreService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.promo.converter.SecKill2Create;
import com.mall.promo.dal.entity.PromoItem;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dto.SecKillRequest;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailDto;
import com.mall.shopping.dto.ProductDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author wangjun
 * @Date 2021/2/1 20:53
 **/
@Slf4j
@Component
public class PromoConsumer {

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @Reference(interfaceClass = OrderCoreService.class,timeout = 3000,check = false)
    private OrderCoreService orderCoreService;

    @Reference(interfaceClass = IProductService.class,timeout = 3000,check = false)
    IProductService iProductService;

    @Autowired
    PromoItemMapper promoItemMapper;

    @PostConstruct
    public void init() throws MQClientException {
         defaultMQPushConsumer = new DefaultMQPushConsumer("promo_consumer_group");
         defaultMQPushConsumer.setNamesrvAddr("192.168.19.130:9876");
         defaultMQPushConsumer.subscribe("promo_order","*");
         defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
             @Override
             public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                 MessageExt messageExt = list.get(0);
                 byte[] body = messageExt.getBody();
                 String bodyStr = new String(body);
                 SecKillRequest request = JSON.parseObject(bodyStr, SecKillRequest.class);

                 Long productId = request.getProductId();
                 ProductDetailResponse productDetail = iProductService.getProductDetail(productId);
                 ProductDetailDto productDetailDto = productDetail.getProductDetailDto();

                 PromoItem promoItem = new PromoItem();
                 promoItem.setPsId(request.getPsId().intValue());
                 promoItem.setItemId(productId.intValue());
                 PromoItem item = promoItemMapper.selectOne(promoItem);

                 CreateOrderRequest createOrderRequest = SecKill2Create.secKill2Create(request, productDetailDto, item);
                 String orderId = orderCoreService.createPromoOrder(createOrderRequest);
                 boolean result = orderCoreService.createPostInfo(createOrderRequest, orderId);

                 if (result) {
                     log.info("生成秒杀订单成功");
                     return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                 }
                 return ConsumeConcurrentlyStatus.RECONSUME_LATER;
             }
         });
         defaultMQPushConsumer.start();
    }
}
