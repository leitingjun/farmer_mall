package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.CartProductDto;
import com.mall.user.IMemberService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.QueryMemberRequest;
import com.mall.user.dto.QueryMemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 *  ciggar
 * create-date: 2019/8/1-下午4:47
 *
 */
@Slf4j
@Component
public class ValidateHandler extends AbstractTransHandler {

    @Reference(check = false)
    private IMemberService memberService;

    @Autowired
    StockMapper stockMapper;
    /**
     * 验证用户合法性
     * @return
     */

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {

        CreateOrderContext createOrderContext = (CreateOrderContext) context;
        QueryMemberRequest request = new QueryMemberRequest();
        request.setUserId(createOrderContext.getUserId());
        //验证用户id
        request.requestCheck();

        //根据id 查询用户信息
        memberService.queryMemberById(request);

        List<CartProductDto> list = createOrderContext.getCartProductDtoList();
        for (CartProductDto cartProductDto : list) {

            Example stockExample = new Example(Stock.class);
            stockExample.createCriteria().andEqualTo("itemId",cartProductDto.getProductId());
            Stock stock = stockMapper.selectOneByExample(stockExample);

//            if (stock.getStockCount().intValue()<1 ||stock.getLockCount()<0 || stock.getRestrictCount()< 0){
//                throw new BizException(
//                        OrderRetCode.DB_EXCEPTION.getCode(),
//                        OrderRetCode.DB_EXCEPTION.getMessage());
//            }
        }
        return true;
    }
}
