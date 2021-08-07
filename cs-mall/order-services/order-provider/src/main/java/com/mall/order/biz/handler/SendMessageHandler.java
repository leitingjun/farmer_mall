package com.mall.order.biz.handler;

import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.mq.OrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @Description: 利用mq发送延迟取消订单消息
 * @Author： ciggar
 * @Date: 2019-09-17 23:14
 **/
@Component
@Slf4j
public class SendMessageHandler extends AbstractTransHandler {

	@Autowired
	OrderProducer producer;

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	public boolean handle(TransHandlerContext context) {
		CreateOrderContext createOrderContext= (CreateOrderContext) context;
        try{
			producer.sendMessage(createOrderContext.getOrderId());
		}catch (Exception e){
        	log.error("发送订单id:{}到延迟队列失败",((CreateOrderContext) context).getOrderId());
        	return false;
		}
		return true;
	}
}