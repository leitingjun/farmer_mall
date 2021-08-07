package com.mall.order.mq;

import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dal.persistence.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

/**
 * @Author wangjun
 * @Date 2021/1/28 20:03
 **/
@Slf4j
@Component
public class OrderDelayMessageListener implements MessageListenerConcurrently {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    StockMapper stockMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    OrderShippingMapper orderShippingMapper;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        log.info("开始执行订单[{}]的支付超时订单关闭......", list.toString());
        MessageExt messageExt = list.get(0);
        byte[] body = messageExt.getBody();
        String orderId = new String(body);
        try {
            Order order = orderMapper.selectByPrimaryKey(orderId);
            Integer status = order.getStatus();
            //查询itemID
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            List<OrderItem> orderItems = orderItemMapper.select(orderItem);

            //如果规定时间内未支付，则释放锁定的库存,
            if (status == 0) {
                //修改order表订单状态status为5(交易关闭）
                Order updateOrder = new Order();
                updateOrder.setStatus(5);
                updateOrder.setCloseTime(new Date());
                updateOrder.setOrderId(orderId);
                updateOrder.setUpdateTime(new Date());
                orderMapper.updateByPrimaryKeySelective(updateOrder);

                update(orderId, orderItems);
            }

            log.info("超时订单{}处理完毕", orderId);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }catch (Exception e){
            log.error("超时订单处理失败:{}", orderId);
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    private synchronized void update(String orderId, List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Long itemId = item.getItemId();
            // 并修改order_item表的status为2
            orderItemMapper.updateStockStatus(2,orderId);

//                    //查询每个item的库存信息
//                    Stock stock = stockMapper.selectStock(itemId);

            Stock updateStock = new Stock();
            updateStock.setItemId(itemId);
            updateStock.setStockCount(item.getNum().longValue());
            updateStock.setLockCount(-item.getNum());
            //更新库存
            stockMapper.updateStock(updateStock);

        }
    }
}