package com.mall.order.biz.handler;

import com.mall.commons.tool.utils.UtilDate;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.CartProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 *  ciggar
 * create-date: 2019/8/1-下午5:01
 * 初始化订单 生成订单
 */

@Slf4j
@Component
public class InitOrderHandler extends AbstractTransHandler {


    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    StockMapper stockMapper;

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
        CreateOrderContext orderContext = (CreateOrderContext) context;

       initOrder(orderContext);
       insertPrdocus(orderContext);

       return true;
    }

    //不走管道的创建订单方法
    public String createOrder(TransHandlerContext context){
        CreateOrderContext orderContext = (CreateOrderContext) context;
        String orderId = initOrder(orderContext);
        insertPrdocus(orderContext);
        return orderId;
    }

    private String initOrder(CreateOrderContext orderContext) {
        //邮费
        BigDecimal postFree = BigDecimal.valueOf(0.0);

        Date updateTime  = new Date();
        Date paymentTime = null;
        Date consignTime = null;
        Date endTime     = null;
        Date closeTime   = null;

        String[] shippingName = new String[]{"ZT-中通快递", "YD-韵达快递", "YZ-邮政快递"};
        Random random = new Random();
        int anInt = random.nextInt(2);

        //快递号
        String shipCode = shippingName[anInt].substring(0,3)+ UtilDate.getThree() + UtilDate.getOrderNum();

        //买家留言
        String buyerMessage = null;

        String orderId = UtilDate.getOrderNum()+UtilDate.getThree();
        orderContext.setOrderId(orderId);

        Order order = new Order(
                orderId,
                orderContext.getOrderTotal(),
                1,
                postFree,
                OrderConstants.ORDER_STATUS_INIT,
                new Date(),
                updateTime,
                paymentTime,
                consignTime,
                endTime,
                closeTime,
                shippingName[anInt],
                shipCode,
                orderContext.getUserId(),
                buyerMessage,
                orderContext.getUserName(),
                0,
                orderContext.getUniqueKey()
        );
        orderMapper.insertSelective(order);
        return orderId;
    }

    private void insertPrdocus(CreateOrderContext orderContext) {
        List<CartProductDto> cartProductDtoList = orderContext.getCartProductDtoList();

        //订单商品状态1:锁定 2:库存释放 3:扣减库存成功
        Integer status = 1;

        for (CartProductDto cartProductDto : cartProductDtoList) {

            //有该商品的库存才插入商品表中
            Stock stock = new Stock();
            stock.setItemId(cartProductDto.getProductId());
            int count = stockMapper.selectCount(stock);

            if (count != 0) {
                OrderItem orderItem = new OrderItem(
                        UtilDate.getOrderNum() + UtilDate.getThree(),
                        cartProductDto.getProductId(),
                        orderContext.getOrderId(),
                        cartProductDto.getProductNum().intValue(),
                        cartProductDto.getProductName(),
                        cartProductDto.getSalePrice().doubleValue(),
                        cartProductDto.getSalePrice().doubleValue() * cartProductDto.getProductNum().intValue(),
                        cartProductDto.getProductImg(),
                        status
                );
                orderItemMapper.insert(orderItem);
            }
        }
    }

}
