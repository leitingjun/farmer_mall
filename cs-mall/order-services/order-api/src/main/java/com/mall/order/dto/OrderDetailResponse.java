package com.mall.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/31-上午9:40
 */
@Data
public class OrderDetailResponse extends AbstractResponse{

    private String orderId;

    private BigDecimal payment;

    private Integer paymentType;

    private BigDecimal postFee;

    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date paymentTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date consignTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date closeTime;

    private String shippingName;

    private String shippingCode;

    private Long userId;

    private String buyerMessage;

    private String buyerNick;

    private Integer buyerComment;

    private List<OrderItemDto> orderItemDto;

    private OrderShippingDto orderShippingDto;
}
