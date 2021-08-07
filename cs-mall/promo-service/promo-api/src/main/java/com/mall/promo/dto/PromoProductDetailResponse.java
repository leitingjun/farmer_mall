package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

/**
 * @Author wangjun
 * @Date 2021/1/29 19:58
 **/

@Data
public class PromoProductDetailResponse extends AbstractResponse {
    private PromoProductDetailDTO promoProductDetailDTO;
}
