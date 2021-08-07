package com.mall.user;

import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;

/**
 *
 */
public interface RegisterService{
    UserRegisterResponse register(UserRegisterRequest request);
}
