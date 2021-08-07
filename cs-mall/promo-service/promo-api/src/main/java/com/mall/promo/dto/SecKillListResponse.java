package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.util.List;

/**
 * @Author wangjun
 * @Date 2021/1/29 17:15
 **/

@Data
public class SecKillListResponse extends AbstractResponse {
    private List<SecKillListProductDto> productList;
    Integer psId;
    Integer sessionId;
}
