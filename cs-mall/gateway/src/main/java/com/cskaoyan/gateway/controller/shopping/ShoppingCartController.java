package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.dto.UserInfoDto;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("shopping")
public class    ShoppingCartController {
    @Reference(interfaceClass = ICartService.class,check = false,retries = 0)
    ICartService cartService;

    @PostMapping("carts")
    public ResponseData addToCart(@RequestBody Map<Object, Object> map) {
        Integer itemId = (Integer)map.get("productId");
        Integer num = (Integer)map.get("productNum");
        String uid = (String)map.get("userId");
        AddCartRequest request = new AddCartRequest();
        request.setItemId(itemId.longValue());
        request.setNum(num.longValue());
        request.setUserId(Long.parseLong(uid));
        AddCartResponse response = cartService.addToCart(request);
//        ResponseUtil<String> responseUtil = new ResponseUtil<>();

        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    @GetMapping("carts")
    public ResponseData GetCartList(HttpServletRequest servletRequest) {
        CartListByIdRequest request = new CartListByIdRequest();
        UserInfoDto userInfo = (UserInfoDto) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        Long uid = userInfo.getUid();
        request.setUserId(uid);
        CartListByIdResponse response = cartService.getCartListById(request);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getCartProductDtos());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    @PutMapping("carts")
    public ResponseData updateCartNum(@RequestBody Map<Object, Object> map) {
        Integer itemId = (Integer)map.get("productId");
        Integer num = (Integer)map.get("productNum");
        String userId = (String)map.get("userId");

        String checked = (String)map.get("checked");
        UpdateCartNumRequest request = new UpdateCartNumRequest();
        request.setItemId(itemId.longValue());
        request.setNum(num.longValue());
        request.setUserId(Long.parseLong(userId));
        request.setChecked(checked);
        UpdateCartNumResponse response = cartService.updateCartNum(request);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }
    @DeleteMapping("carts/{uid}/{pid}")
    public ResponseData deleteCartItem(@PathVariable(name = "uid") Long uid, @PathVariable("pid") Long pid) {
        DeleteCartItemRequest request = new DeleteCartItemRequest();
        request.setUserId(uid);
        request.setItemId(pid);
        DeleteCartItemResponse response = cartService.deleteCartItem(request);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }
    @PutMapping("items")
    public ResponseData selectAllItems(@RequestBody CheckAllItemRequest request) {
        CheckAllItemResponse response = cartService.checkAllCartItem(request);
        if(response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(null);
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    @DeleteMapping("items/{id}")
    public ResponseData deleteCheckedItems(@PathVariable(name = "id") Integer id){
        DeleteCheckedItemRequest request = new DeleteCheckedItemRequest();
        request.setUserId(id.longValue());
        DeleteCheckedItemResposne resposne = cartService.deleteCheckedItem(request);
        if (resposne.getCode().equals(ShoppingRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setData(null);
        }
        return new ResponseUtil<>().setErrorMsg(resposne.getMsg());
    }


}
