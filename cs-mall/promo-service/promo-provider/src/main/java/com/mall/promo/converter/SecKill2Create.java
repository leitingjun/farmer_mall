package com.mall.promo.converter;

import com.mall.order.dto.CartProductDto;
import com.mall.order.dto.CreateOrderRequest;
import com.mall.promo.dal.entity.PromoItem;
import com.mall.promo.dto.SecKillRequest;
import com.mall.shopping.dto.ProductDetailDto;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @Author wangjun
 * @Date 2021/1/29 22:31
 **/

public class SecKill2Create {

    public static CreateOrderRequest secKill2Create(SecKillRequest secKillRequest,
                                                    ProductDetailDto productDto, PromoItem item){
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(secKillRequest.getUserId());
        request.setAddressId(secKillRequest.getAddressId());
        request.setStreetName(secKillRequest.getStreetName());
        request.setUserName(secKillRequest.getUserName());
        request.setTel(secKillRequest.getTel());
        request.setUniqueKey(secKillRequest.getUniqueKey());


        ArrayList<CartProductDto> list = new ArrayList<>();
        CartProductDto cartProductDto = new CartProductDto();
        cartProductDto.setProductId(productDto.getProductId());
        cartProductDto.setSalePrice(productDto.getSalePrice());
        cartProductDto.setProductNum((long)1);
        cartProductDto.setLimitNum(productDto.getLimitNum());
        cartProductDto.setChecked(null);
        cartProductDto.setProductName(productDto.getProductName());
        cartProductDto.setProductImg(productDto.getProductImageBig());
        list.add(cartProductDto);
        request.setCartProductDtoList(list);

        BigDecimal seckillPrice = item.getSeckillPrice();
        request.setOrderTotal(seckillPrice);
        return request;
    }
}
