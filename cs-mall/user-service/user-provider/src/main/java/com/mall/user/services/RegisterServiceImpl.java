package com.mall.user.services;

import com.mall.commons.tool.exception.ValidateException;
import com.mall.user.RegisterService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dal.entitys.Member;
import com.mall.user.dal.entitys.UserVerify;
import com.mall.user.dal.persistence.MemberMapper;
import com.mall.user.dal.persistence.UserVerifyMapper;
import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
@Service(interfaceClass = RegisterService.class)
@Component
public class RegisterServiceImpl implements RegisterService{

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private UserVerifyMapper userVerifyMapper;

    @Autowired
    JavaMailSender mailSender;

    private ExecutorService executorService;

    @PostConstruct
    public void init()
    {
        executorService = Executors.newFixedThreadPool(30);
    }

    @Override
    public UserRegisterResponse register(UserRegisterRequest userRegisterRequest)
    {
        UserRegisterResponse userRegisterResponse = new UserRegisterResponse();
        try
        {
            userRegisterRequest.requestCheck();
            isUserRepeated(userRegisterRequest);
        }catch(ValidateException e)
        {
            userRegisterResponse.setCode(e.getErrorCode());
            userRegisterResponse.setMsg(e.getMessage());
            return userRegisterResponse;
        }

        //向用户表中插入一条记录
        Member member = new Member();
        String userName = userRegisterRequest.getUserName();
        member.setUsername(userName);
        String password = userRegisterRequest.getUserPwd();
        member.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        member.setEmail(userRegisterRequest.getEmail());
        Date date = new Date();
        member.setCreated(date);
        member.setUpdated(date);
        member.setState(1);
        member.setIsVerified("N");
        int insert = memberMapper.insert(member);
        if(insert < 1)
        {
            userRegisterResponse.setCode(SysRetCodeConstants.USER_REGISTER_FAILED.getCode());
            userRegisterResponse.setMsg(SysRetCodeConstants.USER_REGISTER_FAILED.getMessage());
            return userRegisterResponse;
        }

        //向用户验证表中插入一条记录
        UserVerify userVerify = new UserVerify();
        userVerify.setUsername(userName);
        String key = userName + password + UUID.randomUUID().toString();
        String uuid = DigestUtils.md5DigestAsHex(key.getBytes());
        userVerify.setUuid(uuid);
        userVerify.setRegisterDate(date);
        userVerify.setIsVerify("N");
        userVerify.setIsExpire("N");
        int insert1 = userVerifyMapper.insert(userVerify);
        if(insert1 < 1)
        {
            userRegisterResponse.setCode(SysRetCodeConstants.USER_REGISTER_VERIFY_FAILED.getCode());
            userRegisterResponse.setMsg(SysRetCodeConstants.USER_REGISTER_VERIFY_FAILED.getMessage());
            return userRegisterResponse;
        }

        executorService.submit(() -> sendEmail(uuid, userRegisterRequest));

        userRegisterResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        userRegisterResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return userRegisterResponse;
    }

    private void isUserRepeated(UserRegisterRequest userRegisterRequest)
    {
        Example usernameExample = new Example(Member.class);
        String userName = userRegisterRequest.getUserName();
        usernameExample.createCriteria().andEqualTo("username",userName);
        List<Member> members = memberMapper.selectByExample(usernameExample);
        if(!CollectionUtils.isEmpty(members) && members.get(0).getUsername().equals(userName))
        {
            throw new ValidateException(SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getCode(), SysRetCodeConstants.USERNAME_ALREADY_EXISTS.getMessage());
        }

        Example emailExample = new Example(Member.class);
        String email = userRegisterRequest.getEmail();
        emailExample.createCriteria().andEqualTo("email",email);
        List<Member> members2 = memberMapper.selectByExample(emailExample);
        if(!CollectionUtils.isEmpty(members2) && members.get(0).getEmail().equals(email))
        {
            throw new ValidateException(SysRetCodeConstants.EMAIL_ALREADY_EXISTS.getCode(), SysRetCodeConstants.EMAIL_ALREADY_EXISTS.getMessage());
        }
    }

    private void sendEmail(String uuid,UserRegisterRequest userRegisterRequest)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("cs_mall用户激活");
        message.setFrom("yh_it1106@163.com");
        message.setTo((userRegisterRequest.getEmail()));
        StringBuilder sb = new StringBuilder();
        sb.append("http://localhost:8080/user/verify?uuid=").append(uuid).append("&username=").append(userRegisterRequest.getUserName());

        message.setText(sb.toString());
        mailSender.send(message);
    }
}
