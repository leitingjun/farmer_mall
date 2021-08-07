package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.CookieUtil;
import com.mall.user.IKaptchaService;
import com.mall.user.RegisterService;
import com.mall.user.annotation.Anonymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.KaptchaCodeRequest;
import com.mall.user.dto.KaptchaCodeResponse;
import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 *
 */
@RestController
@Anonymous
@RequestMapping("user")
public class RegisterController{

    @Reference(interfaceClass = IKaptchaService.class,check = false)
    IKaptchaService iKaptchaService;

    @Reference(interfaceClass = RegisterService.class,check = false,timeout = 100000)
    RegisterService registerService;

    @PostMapping("register")
    public ResponseData register(@RequestBody Map<String,String> map,HttpServletRequest request)
    {
        String email = map.get("email");
        String userName = map.get("userName");
        String userPwd = map.get("userPwd");
        String captcha = map.get("captcha");

        //验证验证码
        String kaptcha_uuid = CookieUtil.getCookieValue(request,"kaptcha_uuid");
        KaptchaCodeRequest kaptchaCodeRequest = new KaptchaCodeRequest();
        kaptchaCodeRequest.setUuid(kaptcha_uuid);
        kaptchaCodeRequest.setCode(captcha);
        KaptchaCodeResponse kaptchaCodeResponse = iKaptchaService.validateKaptchaCode(kaptchaCodeRequest);
        if(!kaptchaCodeResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode()))
        {
            return new ResponseUtil<>().setErrorMsg(kaptchaCodeResponse.getMsg());
        }

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setEmail(email);
        userRegisterRequest.setUserName(userName);
        userRegisterRequest.setUserPwd(userPwd);
        UserRegisterResponse userRegisterResponse = registerService.register(userRegisterRequest);
        if(userRegisterResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode()))
        {
            return new ResponseUtil<>().setData(null);
        }
        return new ResponseUtil<>().setErrorMsg(userRegisterResponse.getMsg());
    }
}
