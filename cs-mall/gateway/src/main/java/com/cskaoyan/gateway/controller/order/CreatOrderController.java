package com.cskaoyan.gateway.controller.order;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.order.dto.CreateOrderResponse;
import com.mall.order.dto.OrderListRequest;
import com.mall.order.dto.OrderListResponse;
import com.mall.user.dto.UserInfoDto;
import com.mall.user.intercepter.TokenIntercepter;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/shopping")
@Api(tags = "ShoppingOrderController", description = "商品订单控制")
public class CreatOrderController {

    @Reference(timeout = 30000000,check = false,retries = 0)
    private OrderCoreService orderCoreService;

    @Reference(timeout = 30000000,check = false,retries = 0)
    private OrderQueryService orderQueryService;

    /**
     * 创建订单
     */
    @PostMapping("/order")
    public ResponseData creatOrder(@RequestBody CreateOrderRequest request, HttpServletRequest servletRequest){

        UserInfoDto userInfo = (UserInfoDto) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        Long uid = userInfo.getUid();
        request.setUserId(uid);
        //设置uniqueKey
        request.setUniqueKey(UUID.randomUUID().toString());
        CreateOrderResponse response = orderCoreService.createOrder(request);
        if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setData(response.getOrderId());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }


    @GetMapping("/order")
    public ResponseData queryOrder(Integer size, Integer page, HttpServletRequest servletRequest){

        OrderListRequest request = new OrderListRequest();
        UserInfoDto userInfo = (UserInfoDto) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);

        Long uid = userInfo.getUid();

        request.setUserId(uid);
        request.setPage(page);
        request.setSize(size);

        OrderListResponse response =  orderQueryService.queryOrder(request);
        if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            Map<Object, Object> map = new HashMap<>();
            map.put("data",response.getDetailInfoList());
            return new ResponseUtil<>().setData(map);
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

}
