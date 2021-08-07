package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.converter.OrderConverter;
import com.mall.order.dto.CartProductDto;
import com.mall.order.utils.ExceptionProcessorUtils;
import com.mall.shopping.ICartService;
import com.mall.shopping.dto.ClearCartItemRequest;
import com.mall.shopping.dto.ClearCartItemResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 *  ciggar
 * create-date: 2019/8/1-下午5:05
 * 将购物车中的缓存失效
 */
@Slf4j
@Component
public class ClearCartItemHandler extends AbstractTransHandler {

    @Reference(interfaceClass = ICartService.class,check = false,retries = 0)
    ICartService iCartService;


    //是否采用异步方式执行
    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {

        CreateOrderContext orderContext = (CreateOrderContext) context;
        ClearCartItemResponse response = new ClearCartItemResponse();
        ClearCartItemRequest request = new ClearCartItemRequest();

        List<CartProductDto> cartProductDtoList = orderContext.getCartProductDtoList();
        List<Long> list = new ArrayList<>();

        for (CartProductDto cartProductDto : cartProductDtoList) {
            Long productId = cartProductDto.getProductId();
            list.add(productId);
        }

            request.setUserId(orderContext.getUserId());
            request.setProductIds(list);
            iCartService.clearCartItemByUserID(request);

            response.setCode(OrderRetCode.SUCCESS.getCode());
            response.setMsg(OrderRetCode.SUCCESS.getMessage());
        return true;
    }
}
