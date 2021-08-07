package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

/**
 * @Author wangjun
 * @Date 2021/1/29 20:54
 **/

@Data
public class SecKillResponse extends AbstractResponse {

    private Integer inventory;

    private Long productId;
}
