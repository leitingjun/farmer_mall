package com.mall.order.biz.handler;/**
 * Created by ciggar on 2019/8/1.
 */

import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dal.entitys.OrderShipping;
import com.mall.order.dal.persistence.OrderShippingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *  ciggar
 * create-date: 2019/8/1-下午5:06
 *
 * 处理物流信息（商品寄送的地址）
 */
@Slf4j
@Component
public class LogisticalHandler extends AbstractTransHandler {

    @Autowired
    OrderShippingMapper orderShippingMapper;

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {

        CreateOrderContext orderContext = (CreateOrderContext) context;

        return initShipping(orderContext);
    }

    private boolean initShipping(CreateOrderContext orderContext) {
        String streetName = orderContext.getStreetName();
        String[] strings = streetName.split("-");

        if (streetName.endsWith("-")){
            OrderRetCode.SHIPPING_DB_SAVED_FAILED.setMessage("请填写完整的地址");
            throw new BizException(
                    OrderRetCode.SHIPPING_DB_SAVED_FAILED.getCode(),
                    OrderRetCode.SHIPPING_DB_SAVED_FAILED.getMessage()
            );
        }

        //邮政编号
        String receiveZip = null;
        OrderShipping orderShipping = new OrderShipping(
                orderContext.getOrderId(),
                orderContext.getUserName(),
                orderContext.getTel(),
                orderContext.getTel(),
                strings[0],
                strings[1],
                strings[2],
                strings[3],
                receiveZip,
                new Date(),
                new Date()
        );
        orderShippingMapper.insertSelective(orderShipping);
        return true;
    }

    //不走管道
    public boolean createShipping(CreateOrderContext orderContext,String orderId){
        orderContext.setOrderId(orderId);
        return initShipping(orderContext);
    }
}
