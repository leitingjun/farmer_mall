package com.mall.user;
import com.mall.user.dto.*;

/**
 *  会员服务
 */
public interface IMemberService {

    /**
     *  根据用户id查询用户会员信息
     */
    QueryMemberResponse queryMemberById(QueryMemberRequest request);

    /**
     *  修改用户头像
     */
    HeadImageResponse updateHeadImage(HeadImageRequest request);

    /**
     *  更新信息
     */
    UpdateMemberResponse updateMember(UpdateMemberRequest request);

    /**
     *  用户注册激活
     */
    UserVerifyResponse verify(UserVerifyRequest userVerifyRequest);

    /**
     *  用户登录
     */
    UserLoginResponse login(UserLoginRequest userLoginRequest);
}
