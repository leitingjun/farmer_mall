package com.mall.promo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author wangjun
 * @Date 2021/1/29 17:17
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecKillListProductDto implements Serializable {
    private static final long serialVersionUID = 1L;

    Integer id;
    Integer inventory;
    String picUrl;
    BigDecimal price;
    String productName;
    BigDecimal seckillPrice;

}
