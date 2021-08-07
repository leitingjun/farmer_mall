package com.mall.shopping.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.shopping.constants.ShoppingRetCode;
import lombok.Data;

/**
 * Created by ciggar on 2019/7/23.
 */
@Data
public class AddCartRequest extends AbstractRequest{

    private Long userId;
    private Long itemId;
    private Long num;

    @Override
    public void requestCheck() {
        if(userId == null || itemId == null || num == null || num.intValue() == 0) {
            throw new ValidateException(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(), ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
