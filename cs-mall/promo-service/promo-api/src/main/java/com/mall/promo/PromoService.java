package com.mall.promo;

import com.mall.promo.dto.*;

/**
 * @Author wangjun
 * @Date 2021/1/29 16:00
 **/

public interface PromoService {
    /**
     * 获得秒杀商品信息
     * @param request
     * @return
     */
    SecKillListResponse secKillList(SecKillListRequest request);

    /**
     * 获取商品详情
     * @param request
     * @return
     */
    PromoProductDetailResponse queryPromoProductDetail(PromoProductDetailRequest request);

    /**
     * 秒杀下单
     * @param request
     * @return
     */
    SecKillResponse secKill(SecKillRequest request);
}
