package com.mall.order.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.converter.OrderConverter;
import com.mall.order.dal.entitys.*;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:04
 */
@Slf4j
@Component
@Service
public class OrderQueryServiceImpl implements OrderQueryService {


    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    OrderShippingMapper orderShippingMapper;

    @Autowired
    OrderConverter orderConverter;

    @Override
    public OrderListResponse queryOrder(OrderListRequest res) {
        OrderListResponse rsp = new OrderListResponse();

        try {
            res.requestCheck();
            PageHelper.startPage(res.getPage(), res.getSize());

            Example orderExample = new Example(Order.class);
            orderExample.createCriteria().andEqualTo("userId", res.getUserId());
            orderExample.setOrderByClause("create_time desc");
            List<Order> orders = orderMapper.selectByExample(orderExample);

            Integer size = orders.size();
            rsp.setTotal(size.longValue());
            ArrayList<OrderDetailInfo> orderDetailInfos = new ArrayList<>();

            for (Order order : orders) {
                //物流查询
                Example example = new Example(OrderShipping.class);
                example.createCriteria().andEqualTo("orderId", order.getOrderId());
                OrderShipping orderShipping = orderShippingMapper.selectOneByExample(example);

                //由订单直接转详情
                OrderDetailInfo orderDetailInfo = orderConverter.order2detail(order);
                OrderShippingDto orderShippingDto = orderConverter.shipping2dto(orderShipping);
                orderDetailInfo.setOrderShippingDto(orderShippingDto);
                Example exampleItem = new Example(OrderItem.class);

                //一个订单查询到多个商品
                exampleItem.createCriteria().andEqualTo("orderId", order.getOrderId());
                List<OrderItem> orderItems = orderItemMapper.selectByExample(exampleItem);
                List<OrderItemDto> orderItemDtos = orderConverter.item2dto(orderItems);

                orderDetailInfo.setOrderItemDto(orderItemDtos);
                orderDetailInfos.add(orderDetailInfo);
                rsp.setDetailInfoList(orderDetailInfos);

            }
            rsp.setCode(OrderRetCode.SUCCESS.getCode());
            rsp.setMsg(OrderRetCode.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("OrderQueryServiceImpl.queryOrder occur Exception :" + e);
            ExceptionProcessorUtils.wrapperHandlerException(rsp, e);
        }
        return rsp;
    }

        @Override
        public OrderDetailDto queryorderDetail(OrderDetailRequest request){
            request.requestCheck();
            OrderDetailDto response = new OrderDetailDto();
            try {
                Order order = orderMapper.selectByPrimaryKey(request.getOrderId());
                OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(request.getOrderId());
                List<OrderItem> orderItems = orderItemMapper.queryByOrderId(request.getOrderId());
                List<OrderItemDto> orderItemDtos = orderConverter.item2dto(orderItems);
                response.setUserName(order.getBuyerNick());
                response.setOrderTotal(order.getPayment());
                response.setStreetName(orderShipping.getReceiverAddress());
                response.setTel(orderShipping.getReceiverPhone());
                response.setOrderStatus(order.getStatus());
                response.setGoodsList(orderItemDtos);
            } catch (Exception e) {
                log.error("OrderQueryServiceImpl.queryorderDetail Occur Exception :" + e);
                //ExceptionProcessorUtils.wrapperHandlerException(response, e);
            }
            return response;
        }

    @Override
    public OrderDetailResponse orderDetail(OrderDetailRequest request) {
        OrderDetailResponse response=new OrderDetailResponse();
        try{
            request.requestCheck();
            Order order=orderMapper.selectByPrimaryKey(request.getOrderId());
//            OrderItemExample example=new OrderItemExample();
//            OrderItemExample.Criteria criteria=example.createCriteria();
//            criteria.andOrderIdEqualTo(order.getOrderId());
//            List<OrderItem> list=orderItemMapper.selectByExample(example);
            List<OrderItem> list =  orderItemMapper.queryByOrderId(order.getOrderId());
            OrderShipping orderShipping=orderShippingMapper.selectByPrimaryKey(order.getOrderId());
            response=orderConverter.order2res(order);
            response.setOrderItemDto(orderConverter.item2dto(list));
            response.setOrderShippingDto(orderConverter.shipping2dto(orderShipping));
            response.setCode(OrderRetCode.SUCCESS.getCode());
            response.setMsg(OrderRetCode.SUCCESS.getMessage());
            return response;
        }catch (Exception e){
            log.error("OrderQueryServiceImpl.orderDetail occur Exception :" +e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;

    }
}