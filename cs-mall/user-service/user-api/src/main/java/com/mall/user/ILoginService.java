package com.mall.user;

import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;

/**
 *
 */
public interface ILoginService{
    CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest);
}
