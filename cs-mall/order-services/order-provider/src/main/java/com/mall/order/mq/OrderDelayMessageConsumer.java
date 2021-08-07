package com.mall.order.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author wangjun
 * @Date 2021/1/28 19:56
 **/

@Component
public class OrderDelayMessageConsumer {

    @Value("${rocketMQ.nameSrvAddr")
    String url;

    DefaultMQPushConsumer consumer;

    @Autowired
    OrderDelayMessageListener listener;

    @PostConstruct
    public void init(){
        consumer=new DefaultMQPushConsumer("order_consumer_group");
        consumer.setNamesrvAddr("192.168.19.130:9876");
        try {
            consumer.subscribe("delay_order","*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer.registerMessageListener(listener);
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
}
