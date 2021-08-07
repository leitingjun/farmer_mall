package com.mall.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author
 * @description 订单详细信息
 * @date 2019/8/8 14:33
 **/
@Data
public class OrderDetailInfo implements Serializable {

    private static final long serialVersionUID = 1L;
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

    private Date endTime;

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
