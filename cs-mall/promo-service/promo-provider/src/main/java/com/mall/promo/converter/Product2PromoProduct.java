package com.mall.promo.converter;

import com.mall.promo.dto.PromoProductDetailDTO;
import com.mall.shopping.dto.ProductDetailDto;

import java.math.BigDecimal;

/**
 * @Author wangjun
 * @Date 2021/1/29 20:16
 **/

public class Product2PromoProduct {

    public static PromoProductDetailDTO product2Promo(ProductDetailDto productDetailDto, BigDecimal promoPrice){
        PromoProductDetailDTO dto = new PromoProductDetailDTO();
        dto.setDetail(productDetailDto.getDetail());
        dto.setLimitNum(productDetailDto.getLimitNum());
        dto.setProductId(productDetailDto.getProductId());
        dto.setProductImageBig(productDetailDto.getProductImageBig());
        dto.setProductImageSmall(productDetailDto.getProductImageSmall());
        dto.setProductName(productDetailDto.getProductName());
        dto.setPromoPrice(promoPrice);
        dto.setSalePrice(productDetailDto.getSalePrice());
        dto.setSubTitle(productDetailDto.getSubTitle());
        return dto;
    }
}
