//package com.cskaoyan.gateway.controller.pay;
//
//import com.cskaoyan.gateway.form.pay.PayForm;
//import com.mall.commons.result.ResponseData;
//import com.mall.commons.result.ResponseUtil;
//import com.mall.pay.dto.GetQrCodeRequest;
//import com.mall.pay.dto.GetQrCodeResponse;
//import com.mall.user.constants.SysRetCodeConstants;
//import com.mall.user.dto.UserInfoDto;
//import com.mall.user.intercepter.TokenIntercepter;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.web.bind.annotation.*;
//import com.mall.pay.PayService;
//
//
//import javax.servlet.http.HttpServletRequest;
//
//@Slf4j
//@RestController
//@RequestMapping("/cashier")
//public class PayController {
//
//    @Reference(timeout = 3000,retries = 0,check = false)
//    private PayService payService;
//
//    @PostMapping("/pay")
//    public ResponseData getQrCode(@RequestBody PayForm payForm, HttpServletRequest servletRequest){
//        GetQrCodeRequest request = new GetQrCodeRequest();
//        request.setInfo(payForm.getInfo());
//        request.setMoney(payForm.getMoney());
//        request.setNickName(payForm.getNickName());
//        request.setPayType(payForm.getPayType());
//        request.setOrderId(payForm.getOrderId());
//        UserInfoDto userInfo = (UserInfoDto) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
//        request.setUid(userInfo.getUid().intValue());
//        GetQrCodeResponse response=payService.insertPayment(request);
//        return new ResponseUtil().setData(response);
//    }
//    @GetMapping("/queryStatus")
//    public ResponseData queryStatus(Long orderId,HttpServletRequest servletRequest){
//        UserInfoDto userInfo = (UserInfoDto) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
//        Long uid = userInfo.getUid();
//        boolean isPay=payService.queryStatus(orderId,uid);
//        if(isPay){
//            return new ResponseUtil().setData("success");
//        }
//        return new ResponseUtil().setData("fail");
//    }
//}
