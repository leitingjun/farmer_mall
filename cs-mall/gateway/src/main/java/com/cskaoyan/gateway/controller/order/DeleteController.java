package com.cskaoyan.gateway.controller.order;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/shopping")
public class DeleteController {


    @Reference(timeout = 30000000,check = false,retries = 0)
    private OrderCoreService orderCoreService;

    @DeleteMapping("/order/{id}")
    public ResponseData deleteOrder(@PathVariable("id") Long id){
        DeleteOrderRequest request = new DeleteOrderRequest();
        request.setOrderId(id.toString());
        DeleteOrderResponse response = orderCoreService.deleteOrder(request);
        if(response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setData(response.getMsg());
    }



}
