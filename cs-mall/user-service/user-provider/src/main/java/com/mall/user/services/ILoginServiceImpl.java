package com.mall.user.services;

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.ILoginService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;
import com.mall.user.dto.UserInfoDto;
import com.mall.user.utils.JwtTokenUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 */
@Service(interfaceClass = ILoginService.class)
@Component
public class ILoginServiceImpl implements ILoginService{

    @Autowired
    MemberMapper memberMapper;

    @Override
    public CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest)
    {
        CheckAuthResponse checkAuthResponse = new CheckAuthResponse();
        String token = checkAuthRequest.getToken();
        String userStr = null;
        try
        {
            userStr = JwtTokenUtils.builder().token(token).build().freeJwt();
        }catch(ValidateException e)
        {
            e.printStackTrace();
            checkAuthResponse.setCode(e.getErrorCode());
            checkAuthResponse.setMsg(e.getMessage());
            return checkAuthResponse;
        }

        Map<String,Object> map = JSON.parseObject(userStr,Map.class);
        String username = (String)map.get("username");
        String file = (String)map.get("file");
        Integer uid = (Integer)map.get("uid");

        checkAuthResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        checkAuthResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setFile(file);
        userInfoDto.setUid(Long.valueOf(uid.toString()));
        userInfoDto.setUsername(username);
        checkAuthResponse.setUserinfo(userInfoDto);
        return checkAuthResponse;
    }
}
