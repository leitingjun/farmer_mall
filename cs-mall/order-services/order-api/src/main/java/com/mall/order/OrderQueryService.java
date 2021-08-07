package com.mall.order;

import com.mall.order.dto.*;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:01
 */
public interface OrderQueryService {

    OrderListResponse queryOrder(OrderListRequest request);

    OrderDetailDto queryorderDetail(OrderDetailRequest request);

    OrderDetailResponse orderDetail(OrderDetailRequest orderDetailRequest);
}
