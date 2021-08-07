package com.mall.promo.mq;

import com.mall.order.dto.CreateOrderRequest;
import com.mall.promo.cache.CacheManager;
import com.mall.promo.dal.entity.PromoItem;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dto.SecKillRequest;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailDto;
import com.mall.shopping.dto.ProductDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author WangJun
 * @Date 2021/2/1 20:28
 **/
@Slf4j
@Component
public class PromoTransactionListener implements TransactionListener {

    @Autowired
    private PromoItemMapper promoItemMapper;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    RedissonClient redissonClient;

    @Reference(interfaceClass = IProductService.class,timeout = 3000,check = false)
    IProductService iProductService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        String transactionId = message.getTransactionId();
        cacheManager.setCache(transactionId,"init",1);

        SecKillRequest request= (SecKillRequest) o;

        Long productId = request.getProductId();
        ProductDetailResponse productDetail = iProductService.getProductDetail(productId);
        ProductDetailDto productDetailDto = productDetail.getProductDetailDto();

        PromoItem promoItem = new PromoItem();
        promoItem.setPsId(request.getPsId().intValue());
        promoItem.setItemId(productId.intValue());
        PromoItem item = promoItemMapper.selectOne(promoItem);

        try {
            RLock lock = redissonClient.getLock("promo");
            PromoItem updateItem=new PromoItem();
            updateItem.setId(item.getId());
            updateItem.setItemStock(item.getItemStock()-1);
            //redis锁解决并发修改问题
            boolean isLock=true;
            int i =0;
            try {
                isLock = lock.tryLock();
                i = promoItemMapper.updateByPrimaryKeySelective(updateItem);
            }catch (Exception e){
                log.error("修改库存时加锁失败");
            }finally {
                if(isLock){
                    lock.unlock();
                }
            }

            if(i<1){
                cacheManager.setCache(transactionId,"fail",1);
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
            cacheManager.setCache(transactionId,"success",1);
            log.info("修改秒杀库存成功");
        }catch (Exception e){
            e.printStackTrace();
            cacheManager.setCache(transactionId,"unKnow",1);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        String transactionId = messageExt.getTransactionId();
        String value = cacheManager.checkCache(transactionId);
        if (value.equals("init")) {
            return LocalTransactionState.UNKNOW;
        }
        if (value.equals("success")) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        if (value.equals("fail")) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }
}
