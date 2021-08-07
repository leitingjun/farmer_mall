package com.mall.order.biz.handler;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.BizException;
import com.mall.commons.tool.utils.UtilDate;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.CartProductDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.mapstruct.ap.shaded.freemarker.template.utility.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 扣减库存处理器
 * @Author： wz
 * @Date: 2019-09-16 00:03
 **/
@Component
@Slf4j
public class SubStockHandler extends AbstractTransHandler {

	@Autowired
	OrderMapper orderMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
	private OrderItemMapper orderItemMapper;

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	@Transactional
	public boolean handle(TransHandlerContext context) {

		CreateOrderContext orderContext = (CreateOrderContext) context;
		List<CartProductDto> cartProductDtoList = orderContext.getCartProductDtoList();

		for (CartProductDto cartProductDto : cartProductDtoList) {
			Stock stock = new Stock();
			Long productId = cartProductDto.getProductId();
			Integer num = cartProductDto.getProductNum().intValue();


			//单件商品购买不能过超5件
			if (num>5){
				throw new BizException(
						OrderRetCode.LIMIT_COUNT.getCode(),
						OrderRetCode.LIMIT_COUNT.getMessage()
				);
			}

			Example example = new Example(Stock.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("itemId",productId);
			Stock oneStock = stockMapper.selectOneByExample(example);

			stock.setRestrictCount(5);
			stock.setItemId(productId);

			long count = 0;

			try {
				//库存中没有该商品
				count = oneStock.getStockCount() - num;
			}catch (NullPointerException e){
				String productName = cartProductDto.getProductName();
				OrderRetCode.NO_STOCK.setMessage(productName+"  商品已售空...");

				throw new BizException(
						OrderRetCode.NO_STOCK.getCode(),
						OrderRetCode.NO_STOCK.getMessage()
				);
			}

			//库存不够
			if (count < 0){
				throw new BizException(
						OrderRetCode.STOCK_NUM_ERROR.getCode(),
						OrderRetCode.STOCK_NUM_ERROR.getMessage()
				);
			}
			stock.setStockCount( - num.longValue() );


			//冻结库存 TODO:限定时间内未下单或取消订单 则需去该订单的除冻结库存,
			stock.setLockCount(+ num);

			//设置售卖id 为orderId
			stock.setSellId(null);


			stockMapper.updateStock(stock);
		}
		return true;
	}
}