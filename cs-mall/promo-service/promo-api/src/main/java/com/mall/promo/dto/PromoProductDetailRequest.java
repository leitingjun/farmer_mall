package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.promo.constants.PromoRetCode;
import lombok.Data;

/**
 * @Author wangjun
 * @Date 2021/1/29 19:47
 **/

@Data
public class PromoProductDetailRequest extends AbstractRequest {

    Long psId;

    Long productId;

    @Override
    public void requestCheck() {
        if(psId==null || productId==null){
            throw new ValidateException(PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),
                    PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
