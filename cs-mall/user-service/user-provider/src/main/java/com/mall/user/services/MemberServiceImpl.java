package com.mall.user.services;/**
 * Created by ciggar on 2019/7/30.
 */

import com.alibaba.fastjson.JSON;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.IMemberService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.converter.MemberConverter;
import com.mall.user.converter.UserConverterMapper;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.*;
import com.mall.user.utils.ExceptionProcessorUtils;
import com.mall.user.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Service
public class MemberServiceImpl implements IMemberService {

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    MemberConverter memberConverter;

    @Autowired
    UserVerifyMapper userVerifyMapper;

    @Autowired
    UserConverterMapper userConverterMapper;

    /**
     *  根据用户id查询用户会员信息
     */
    @Override
    public QueryMemberResponse queryMemberById(QueryMemberRequest request) {
        QueryMemberResponse queryMemberResponse=new QueryMemberResponse();
        try{
            request.requestCheck();
            Member member=memberMapper.selectByPrimaryKey(request.getUserId());
            if(member==null){
                queryMemberResponse.setCode(SysRetCodeConstants.DATA_NOT_EXIST.getCode());
                queryMemberResponse.setMsg(SysRetCodeConstants.DATA_NOT_EXIST.getMessage());
            }
            queryMemberResponse=memberConverter.member2Res(member);
            queryMemberResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
            queryMemberResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("MemberServiceImpl.queryMemberById Occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(queryMemberResponse,e);
        }
        return queryMemberResponse;
    }

    @Override
    public HeadImageResponse updateHeadImage(HeadImageRequest request) {
        HeadImageResponse response=new HeadImageResponse();
        //TODO
        return response;
    }

    @Override
    public UpdateMemberResponse updateMember(UpdateMemberRequest request) {
        return null;
    }


    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest)
    {
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        String userName = userLoginRequest.getUserName();
        String password = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());

        try
        {
            userLoginRequest.requestCheck();
        }catch(ValidateException e)
        {
            userLoginResponse.setCode(e.getErrorCode());
            userLoginResponse.setMsg(e.getMessage());
            return userLoginResponse;
        }
        //验证用户名和密码
        Example example = new Example(Member.class);
        example.createCriteria().andEqualTo("username", userName).andEqualTo("password", password);
        List<Member> members = memberMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(members))
        {
            userLoginResponse.setCode(SysRetCodeConstants.USERORPASSWORD_ERRROR.getCode());
            userLoginResponse.setMsg(SysRetCodeConstants.USERORPASSWORD_ERRROR.getMessage());
            return userLoginResponse;
        }

        //判断用户是否已激活
        Member member = members.get(0);
        if(!member.getIsVerified().equals("Y"))
        {
            userLoginResponse.setCode(SysRetCodeConstants.USER_ISVERFIED_ERROR.getCode());
            userLoginResponse.setMsg(SysRetCodeConstants.USER_ISVERFIED_ERROR.getMessage());
            return userLoginResponse;
        }

        //产生一个合法的JWT
        Map<String,Object> map = new HashMap<>();
        map.put("username", userName);
        map.put("uid", member.getId());
        map.put("file", member.getFile());
        String token = JwtTokenUtils.builder().msg(JSON.toJSON(map).toString()).build().creatJwtToken();

        userLoginResponse = userConverterMapper.converter(member);
        userLoginResponse.setToken(token);
        userLoginResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        userLoginResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return userLoginResponse;
    }

    @Override
    public UserVerifyResponse verify(UserVerifyRequest userVerifyRequest)
    {
        UserVerifyResponse userVerifyResponse = new UserVerifyResponse();
        try
        {
            userVerifyRequest.requestCheck();
        }catch(ValidateException e)
        {
            userVerifyResponse.setCode(e.getErrorCode());
            userVerifyResponse.setMsg(e.getMessage());
            return userVerifyResponse;
        }

        String userName = userVerifyRequest.getUserName();
        String uuid = userVerifyRequest.getUuid();
        Example memberExample = new Example(Member.class);
        memberExample.createCriteria().andEqualTo("username", userName);
        Member member = new Member();
        member.setIsVerified("Y");
        member.setUpdated(new Date());
        int rows = memberMapper.updateByExampleSelective(member,memberExample);
        if(rows < 1)
        {
            userVerifyResponse.setCode(SysRetCodeConstants.VERIFY_FAIL.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.VERIFY_FAIL.getMessage());
            return userVerifyResponse;
        }

        Example verifyExample = new Example(UserVerify.class);
        verifyExample.createCriteria().andEqualTo("uuid", uuid);
        UserVerify userVerify = new UserVerify();
        userVerify.setIsVerify("Y");
        int rows2 = userVerifyMapper.updateByExampleSelective(userVerify,verifyExample);
        if(rows2 < 1)
        {
            userVerifyResponse.setCode(SysRetCodeConstants.VERIFY_FAIL.getCode());
            userVerifyResponse.setMsg(SysRetCodeConstants.VERIFY_FAIL.getMessage());
            return userVerifyResponse;
        }

        userVerifyResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        userVerifyResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return userVerifyResponse;
    }

    //    @Override
    //    public UpdateMemberResponse updateMember(UpdateMemberRequest request) {
    //        UpdateMemberResponse response = new UpdateMemberResponse();
    //        try{
    //            request.requestCheck();
    //            CheckAuthRequest checkAuthRequest = new CheckAuthRequest();
    //            checkAuthRequest.setToken(request.getToken());
    //            CheckAuthResponse authResponse = userLoginService.validToken(checkAuthRequest);
    //            if (!authResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
    //                response.setCode(authResponse.getCode());
    //                response.setMsg(authResponse.getMsg());
    //                return response;
    //            }
    //            Member member = memberConverter.updateReq2Member(request);
    //            int row = memberMapper.updateByPrimaryKeySelective(member);
    //            response.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
    //            response.setCode(SysRetCodeConstants.SUCCESS.getCode());
    //            log.info("MemberServiceImpl.updateMember effect row :"+row);
    //        }catch (Exception e){
    //            log.error("MemberServiceImpl.updateMember Occur Exception :"+e);
    //            ExceptionProcessorUtils.wrapperHandlerException(response,e);
    //        }
    //        return response;
    //    }
}
