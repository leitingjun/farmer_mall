package com.mall.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ProjectName: cs_mall
 * @ClassName: OrderDetailDto
 * @TODO: TODO
 * @Author caifanglin
 * @Create 2021-01-26 18:54
 */
@Data
public class OrderDetailDto implements Serializable {
    private String userName;
    private Long userId;
    private BigDecimal orderTotal;
    private List<OrderItemDto> goodsList;
    private String tel;
    private String streetName;
    private Integer orderStatus;

}
