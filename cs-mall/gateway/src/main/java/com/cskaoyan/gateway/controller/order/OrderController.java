package com.cskaoyan.gateway.controller.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.dto.*;
import com.mall.user.dto.UserInfoDto;
import com.mall.user.intercepter.TokenIntercepter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: cs_mall
 * @ClassName: OrderController
 * @TODO: TODO
 * @Author caifanglin
 * @Create 2021-01-24 17:56
 */
@Slf4j
@RestController
@RequestMapping("/shopping")
public class OrderController {
    @Reference(timeout = 3000,check = false)
    private OrderQueryService orderQueryService;

    @Reference(timeout = 3000,check = false)
    private OrderCoreService orderCoreService;

    @RequestMapping(value = "/order/{id}",method = {RequestMethod.GET})
    public ResponseData orderDetail(@PathVariable("id")String id, HttpServletRequest servletRequest){
        OrderDetailRequest request = new OrderDetailRequest();
        request.setOrderId(id);
        UserInfoDto userInfo = (UserInfoDto) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
//        JSONObject object = JSON.parseObject(userInfo);
//        Long uid = Long.parseLong(object.get("uid").toString());
        OrderDetailDto response=orderQueryService.queryorderDetail(request);
        response.setUserId(userInfo.getUid());
        return new ResponseUtil<OrderDetailDto>().setData(response);
    }
    @PostMapping("/cancelOrder")
    public ResponseData cancelOrder(@RequestBody CancelOrderRequest request){
        CancelOrderResponse response = orderCoreService.cancelOrder(request);
        return new ResponseUtil<CancelOrderResponse>().setData(response);
    }
}
