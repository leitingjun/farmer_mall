package com.mall.order.services;

import com.mall.order.OrderCoreService;
import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.biz.convert.CreateOrderConvert;
import com.mall.order.biz.convert.TransConvert;
import com.mall.order.biz.factory.OrderProcessPipelineFactory;
import com.mall.order.biz.handler.InitOrderHandler;
import com.mall.order.biz.handler.LogisticalHandler;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import com.mall.user.constants.SysRetCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:05
 */
@Slf4j
@Component
@Service//(cluster = "failfast") 集群容错机制，也可以不配置
public class OrderCoreServiceImpl implements OrderCoreService {

	@Autowired
	OrderMapper orderMapper;

	@Autowired
	OrderItemMapper orderItemMapper;

	@Autowired
	OrderShippingMapper orderShippingMapper;

	@Autowired
	StockMapper stockMapper;

	@Autowired
    OrderProcessPipelineFactory orderProcessPipelineFactory;

	@Autowired
	InitOrderHandler initOrderHandler;

	@Autowired
	LogisticalHandler logisticalHandler;

	/**
	 * 创建订单的处理流程
	 *
	 * @param request
	 * @return
	 */
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest request) {
		CreateOrderResponse response = new CreateOrderResponse();
		try {
			//创建pipeline对象
			TransOutboundInvoker invoker = orderProcessPipelineFactory.build(request);

			//启动pipeline
			invoker.start(); //启动流程（pipeline来处理）

			//获取处理结果
			AbsTransHandlerContext context = invoker.getContext();

			//把处理结果转换为response
			response = (CreateOrderResponse) context.getConvert().convertCtx2Respond(context);
		} catch (Exception e) {
			log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}
		return response;
	}

	@Override
	public CancelOrderResponse cancelOrder(CancelOrderRequest request)
	{
		request.requestCheck();
		CancelOrderResponse response = new CancelOrderResponse();
		try {
			String orderId = request.getOrderId();
			Order order = new Order();
			order.setOrderId(orderId);
			order.setStatus(OrderConstants.ORDER_STATUS_TRANSACTION_CANCEL);
			order.setUpdateTime(new Date());
			orderMapper.updateByPrimaryKeySelective(order);
			orderItemMapper.updateStockStatus(2,orderId);

			Example example = new Example(OrderItem.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("orderId",orderId);
			List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
			for (OrderItem orderItem : orderItems) {
				Stock stock = new Stock();
				Long itemId = orderItem.getItemId();
				Stock selectStock = stockMapper.selectStock(itemId);

				stock.setItemId(itemId);
				stock.setLockCount(selectStock.getLockCount()-orderItem.getNum());
				stock.setStockCount(selectStock.getStockCount()+orderItem.getNum());

				Example example1 = new Example(Stock.class);
				Example.Criteria criteria1 = example1.createCriteria();
				criteria1.andEqualTo("itemId",itemId);

				stockMapper.updateByExampleSelective(stock,example1);
			}

			response.setCode(SysRetCodeConstants.SUCCESS.getCode());
			response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
		}catch (Exception e){
			log.error("OrderCoreServiceImpl.cancelOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}
		return response;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public DeleteOrderResponse deleteOrder(DeleteOrderRequest request) {
		log.error("begin-OrderCoreServiceImpl.deleteOrder request:"+request);
		DeleteOrderResponse response = new DeleteOrderResponse();
		try{
			request.requestCheck();
			//删除订单表的数据
			int row1 = orderMapper.deleteByPrimaryKey(request.getOrderId());
			//删除订单商品表的数据
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(request.getOrderId());
			int row2 = orderItemMapper.delete(orderItem);
			//删除订单收货表的数据
			int row3 = orderShippingMapper.deleteByPrimaryKey(request.getOrderId());
			if(row1>0 && row2>0 && row3>0){
				response.setCode(SysRetCodeConstants.SUCCESS.getCode());
				response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
			}else {
				response.setCode(SysRetCodeConstants.DATA_NOT_EXIST.getCode());
				response.setMsg(SysRetCodeConstants.DATA_NOT_EXIST.getMessage());
			}
			log.info("OrderCoreServiceImpl.deleteOrder effect row:"+(row1+row2+row3));
		}catch (Exception e){
			log.error("OrderCoreServiceImpl.deleteOrder occur Exception:"+e);
			ExceptionProcessorUtils.wrapperHandlerException(response,e);
		}
		return response;
	}

	@Override
	public String createPromoOrder(CreateOrderRequest request) {
		CreateOrderContext createOrderContext = new CreateOrderContext();
		CreateOrderConvert convert = new CreateOrderConvert();
		TransHandlerContext context = convert.convertRequest2Ctx(request, createOrderContext);
		return initOrderHandler.createOrder(context);
	}

	@Override
	public boolean createPostInfo(CreateOrderRequest request,String orderId) {
		CreateOrderContext createOrderContext = new CreateOrderContext();
		CreateOrderConvert convert = new CreateOrderConvert();
		CreateOrderContext context = (CreateOrderContext) convert.convertRequest2Ctx(request, createOrderContext);
		boolean result = logisticalHandler.createShipping(context, orderId);
		if(result){
			return true;
		}
		return false;
	}

	@Override
	public boolean updatePayStatus(String orderId) {
		Order order = new Order();
		order.setOrderId(orderId);
		order.setStatus(1);
		order.setPaymentTime(new Date());
		order.setUpdateTime(new Date());
		int result = orderMapper.updateByPrimaryKeySelective(order);
		if(result==1){
			return true;
		}
		return false;
	}

	@Override
	public void updateOrder(int status, String orderId) {
		Order order = new Order();
		order.setOrderId(orderId);
		order.setStatus(status);
		orderMapper.updateByPrimaryKeySelective(order);
	}
}
