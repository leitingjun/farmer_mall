package com.mall.order.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.Charset;

/**
 * @Author wangjun
 * @Date 2021/1/28 19:19
 **/
@Slf4j
@Component
public class OrderProducer {

    @Value("${rocketMQ.nameSrvAddr")
    String url;

    private DefaultMQProducer producer;

    @PostConstruct
    public void init(){
        log.info("mqProducer初始化");
        producer=new DefaultMQProducer("order_producer_group");
        producer.setNamesrvAddr("192.168.19.130:9876");
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("初始化失败");
        }
    }
    public boolean sendMessage(String orderId){
        Message message=new Message();
        message.setTopic("delay_order");
        message.setBody(orderId.getBytes(Charset.forName("utf-8")));
        message.setDelayTimeLevel(18);
        try {
            SendResult result = producer.send(message);
            if(result!=null && result.getSendStatus().equals(SendStatus.SEND_OK)){
                log.info("延迟取消订单消息发送成功…orderId:{}",orderId);
                return true;
            }
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
//    @PreDestroy
//    public void shutdown(){
//        producer.shutdown();
//    }
}
