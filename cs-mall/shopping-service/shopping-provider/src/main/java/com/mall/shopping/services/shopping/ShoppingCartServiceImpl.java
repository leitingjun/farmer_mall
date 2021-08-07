package com.mall.shopping.services.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dto.*;
import com.mall.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
@Slf4j
@Service
@Component
public class ShoppingCartServiceImpl implements ICartService {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    ItemMapper itemMapper;

    @Override
    public CartListByIdResponse getCartListById(CartListByIdRequest request) {
        CartListByIdResponse response = new CartListByIdResponse();
        try {
            request.requestCheck();
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            response.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            return response;
        }
        Long userId = request.getUserId();
        RMap<Object, Object> rMap = redissonClient.getMap(userId + "");
        Set<Map.Entry<Object, Object>> entries = rMap.readAllEntrySet();
        Iterator<Map.Entry<Object, Object>> iterator = entries.iterator();
        ArrayList<CartProductDto> productDtoList = new ArrayList<>();
        while(iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            Long goodsId = (Long)entry.getKey();
            CartProductDto productDto = (CartProductDto)entry.getValue();
            productDtoList.add(productDto);
        }
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        response.setCartProductDtos(productDtoList);
        return response;
    }

    @Override
    public AddCartResponse addToCart(AddCartRequest request) {
        AddCartResponse response = new AddCartResponse();
        try {
            request.requestCheck();
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            response.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            return response;
        }
        Long userId = request.getUserId();
        Long itemId = request.getItemId();
        Long num = request.getNum();
        Item item = itemMapper.selectByPrimaryKey(itemId);
        if(item != null) {
            String images = item.getImage();
            String[] split = images.split(",");
            String image = split[0];
            BigDecimal price = item.getPrice();
            RMap<Object, Object> rMap = redissonClient.getMap(userId + "");
            //先查询有没有该商品
            CartProductDto oldProductDto = (CartProductDto)rMap.get(itemId);
            if(oldProductDto != null) {
                num += oldProductDto.getProductNum();
            }
//            price = BigDecimal.valueOf(price.doubleValue() * num);
            CartProductDto productDto = new CartProductDto(itemId, price, num, item.getLimitNum(), "true", item.getTitle(), image);
            rMap.put(itemId, productDto);

            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
            return response;
        }
        response.setCode(ShoppingRetCode.DB_EXCEPTION.getCode());
        response.setMsg(ShoppingRetCode.DB_EXCEPTION.getMessage());
       return response;
    }

    @Override
    public UpdateCartNumResponse updateCartNum(UpdateCartNumRequest request) {
        UpdateCartNumResponse response = new UpdateCartNumResponse();
        try {
            request.requestCheck();
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            response.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            return response;
        }
        Long userId = request.getUserId();
        Long itemId = request.getItemId();
        Long num = request.getNum();
        String checked = request.getChecked();
        RMap<Object, Object> rMap = redissonClient.getMap(userId + "");

        CartProductDto productDto = (CartProductDto)rMap.get(itemId);
        productDto.setProductNum(num);
        productDto.setChecked(checked);
        rMap.put(itemId, productDto);
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return response;
    }

    @Override
    public CheckAllItemResponse checkAllCartItem(CheckAllItemRequest request) {
        CheckAllItemResponse response = new CheckAllItemResponse();
        try {
            request.requestCheck();
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            response.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            return response;
        }
        Long userId = request.getUserId();
        String checked = request.getChecked();
        RMap<Object, Object> rMap = redissonClient.getMap(userId + "");

        Set<Map.Entry<Object, Object>> entries = rMap.readAllEntrySet();
        Iterator<Map.Entry<Object, Object>> iterator = entries.iterator();
        while(iterator.hasNext()) {
            Map.Entry<Object, Object> entry = iterator.next();
            CartProductDto productDto = (CartProductDto)entry.getValue();
            productDto.setChecked(checked);
            rMap.put(productDto.getProductId(), productDto);
        }
//        rMap.putAll(entry);
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return response;
    }

    @Override
    public DeleteCartItemResponse deleteCartItem(DeleteCartItemRequest request) {
        DeleteCartItemResponse response = new DeleteCartItemResponse();
        try {
            request.requestCheck();
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            response.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            return response;
        }
        Long userId = request.getUserId();
        Long itemId = request.getItemId();
        RMap<Object, Object> rMap = redissonClient.getMap(userId + "");
        rMap.fastRemove(itemId);
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return response;
    }

    @Override
    public DeleteCheckedItemResposne deleteCheckedItem(DeleteCheckedItemRequest request) {
        DeleteCheckedItemResposne response = new DeleteCheckedItemResposne();

        try {
            request.requestCheck();
            Long userId = request.getUserId();
            RMap<Object, CartProductDto> rMap = redissonClient.getMap(userId + "");
            Collection<CartProductDto> values = rMap.readAllValues();
            for (CartProductDto value : values) {
                Boolean checked = Boolean.valueOf(value.getChecked());
                if (checked.equals(true)){
                    rMap.fastRemove(value.getProductId());
                }
            }
            response.setCode(ShoppingRetCode.SUCCESS.getCode());
            response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("ShoppingCartServiceImpl.deleteCheckedItem occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(response,e);
        }
        return response;
    }

    @Override
    public ClearCartItemResponse clearCartItemByUserID(ClearCartItemRequest request) {
        ClearCartItemResponse response = new ClearCartItemResponse();
        try {
            request.requestCheck();
        } catch (Exception e) {
            e.printStackTrace();
            response.setCode(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
            response.setMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
            return response;
        }
        Long userId = request.getUserId();
        List<Long> productIds = request.getProductIds();
        RMap<Object, Object> rMap = redissonClient.getMap(userId + "");
        rMap.fastRemove(productIds.toArray());
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return response;
    }
}
