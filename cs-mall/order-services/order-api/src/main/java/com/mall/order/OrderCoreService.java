package com.mall.order;

import com.mall.order.dto.*;

/**
 *  订单相关业务
 */
public interface OrderCoreService {

    /**
     *  创建订单
     */
    CreateOrderResponse createOrder(CreateOrderRequest request);

    /**
     *  取消订单
     */
    CancelOrderResponse cancelOrder(CancelOrderRequest request);

    /**
     *  删除订单
     */
    DeleteOrderResponse deleteOrder(DeleteOrderRequest request);

    /**
     * 创建秒杀订单
     */
    String createPromoOrder(CreateOrderRequest request);

    /**
     * 生成秒杀订单邮寄信息
     */
    boolean createPostInfo(CreateOrderRequest request,String orderId);

    /**
     * 修改订单支付状态
     */
    boolean updatePayStatus(String orderId);

    void updateOrder(int orderStatusPayed, String orderId);
}
