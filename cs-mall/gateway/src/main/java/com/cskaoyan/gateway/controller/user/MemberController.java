package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.CookieUtil;
import com.mall.user.IKaptchaService;
import com.mall.user.IMemberService;
import com.mall.user.annotation.Anonymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.*;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ClassName MemberController
 * @Description 会员中心控制层
 * @Author ciggar
 * @Date 2019-08-07 14:26
 * @Version 1.0
 **/
@RestController
@RequestMapping("/user")
public class MemberController {

    @Reference(timeout = 3000000,check = false)
    IMemberService memberService;

    @Reference(interfaceClass = IKaptchaService.class,check = false)
    IKaptchaService iKaptchaService;

    /**
     * 根据ID查询单条会员信息
     * @param id 编号
     * @return
     */
    @GetMapping("/member/{id}")
    public ResponseData searchMemberById(@PathVariable(name = "id")long id) {
        QueryMemberRequest request=new QueryMemberRequest();
        request.setUserId(id);
        QueryMemberResponse queryMemberResponse = memberService.queryMemberById(request);
        if (!queryMemberResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil<>().setErrorMsg(queryMemberResponse.getMsg());
        }
        return new ResponseUtil<>().setData(queryMemberResponse);
    }

    /**
     *  会员信息更新
     */
    @PutMapping("member")
    public ResponseData updateUser(@RequestBody UpdateMemberRequest request) {
        UpdateMemberResponse response = memberService.updateMember(request);
        if(response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            return new ResponseUtil().setData(null);
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     *  用户登录
     */
    @PostMapping("login")
    @Anonymous
    public ResponseData login(@RequestBody Map<String,String> map,HttpServletRequest request,HttpServletResponse response)
    {
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

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setUserName(userName);
        userLoginRequest.setPassword(userPwd);
        UserLoginResponse userLoginResponse = memberService.login(userLoginRequest);

        if(userLoginResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode()))
        {
            Cookie cookie = CookieUtil.genCookie("access_token",userLoginResponse.getToken(),"/",24 * 60 * 60);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            return new ResponseUtil<>().setData(userLoginResponse);
        }
        return new ResponseUtil<>().setErrorMsg(userLoginResponse.getMsg());
    }

    /**
     *  验证用户登录
     */
    @GetMapping("login")
    public ResponseData login(HttpServletRequest request)
    {
        UserInfoDto userInfo = (UserInfoDto)request.getAttribute("userInfo");
        return new ResponseUtil().setData(userInfo);
    }

    /**
     *  用户退出
     */
    @GetMapping("loginOut")
    public ResponseData loginOut(HttpServletRequest request,HttpServletResponse response)
    {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies)
        {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return new ResponseUtil().setData(null);
    }

    /**
     *  用户注册激活
     */
    @GetMapping("verify")
    @Anonymous
    public ResponseData verify(String uuid,String username)
    {
        UserVerifyRequest userVerifyRequest = new UserVerifyRequest();
        userVerifyRequest.setUuid(uuid);
        userVerifyRequest.setUserName(username);
        UserVerifyResponse userRegisterResponse = memberService.verify(userVerifyRequest);

        if(userRegisterResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode()))
        {
            return new ResponseUtil().setData(null);
        }

        return new ResponseUtil().setErrorMsg(userRegisterResponse.getMsg());
    }
}

