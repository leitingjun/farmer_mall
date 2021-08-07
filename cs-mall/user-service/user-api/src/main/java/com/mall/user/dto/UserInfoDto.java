package com.mall.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class UserInfoDto implements Serializable{
    private static final long serialVersionUID = -1139106675310177319L;
    String file;
    Long uid;
    String username;


}
