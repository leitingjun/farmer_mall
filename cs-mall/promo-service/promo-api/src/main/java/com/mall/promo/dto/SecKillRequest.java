package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.promo.constants.PromoRetCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author wangjun
 * @Date 2021/1/29 20:41
 **/

@Data
public class SecKillRequest extends AbstractRequest {

    private Long userId;

    private Long psId;

    private Long productId;

    private Long addressId;

    private String tel;

    private String streetName;

    private String userName;

    private String uniqueKey;

    @Override
    public void requestCheck() {
        if(userId==null || psId==null || productId==null || addressId==null || StringUtils.isBlank(tel) ||
            StringUtils.isBlank(streetName) || StringUtils.isBlank(userName)){
            throw new ValidateException(PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),
                    PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
