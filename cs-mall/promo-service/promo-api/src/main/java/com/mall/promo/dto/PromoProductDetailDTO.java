package com.mall.promo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author wangjun
 * @Date 2021/1/29 19:54
 **/

@Data
public class PromoProductDetailDTO implements Serializable {
    private static final long serialVersionUID = -7393218612667238661L;

    private Long productId;

    private BigDecimal salePrice;

    private String productName;

    private String subTitle;

    private Long limitNum;

    private String productImageBig;

    private String detail;

    private List<String> productImageSmall;

    private BigDecimal promoPrice;
}
