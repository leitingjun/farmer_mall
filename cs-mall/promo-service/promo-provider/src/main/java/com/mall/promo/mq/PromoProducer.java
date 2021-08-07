package com.mall.promo.mq;

import com.alibaba.fastjson.JSON;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.promo.dto.SecKillRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

/**
 * @Author wangjun
 * @Date 2021/2/1 10:54
 **/
@Slf4j
@Component
public class PromoProducer {

    @Autowired
    private PromoTransactionListener promoTransactionListener;

    private TransactionMQProducer transactionMQProducer;

    @PostConstruct
    public void init() throws MQClientException {
        transactionMQProducer=new TransactionMQProducer("promo_producer");
        transactionMQProducer.setNamesrvAddr("192.168.19.130:9876");
        transactionMQProducer.start();
        transactionMQProducer.setTransactionListener(promoTransactionListener);
        log.info("transactionMQProducer.started ...");
    }
    public boolean sendMessage(SecKillRequest secKillRequest){
        Message message = new Message();
        message.setTopic("promo_order");
        message.setBody(JSON.toJSONString(secKillRequest).getBytes(Charset.forName("utf-8")));
//        message.setDelayTimeLevel(5);
        TransactionSendResult result =null;
        try {
            result = transactionMQProducer.sendMessageInTransaction(message, secKillRequest);

        } catch (MQClientException e) {
            e.printStackTrace();
        }
        if(result==null){
            return false;
        }
        LocalTransactionState state = result.getLocalTransactionState();
        if(state.equals(LocalTransactionState.COMMIT_MESSAGE)){
            log.info("消息发送成功");
            return true;
        }
        return false;
    }
}
