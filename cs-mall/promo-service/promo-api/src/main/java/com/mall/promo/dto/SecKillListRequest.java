package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.promo.constants.PromoRetCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author wangjun
 * @Date 2021/1/29 16:05
 **/

@Data
public class SecKillListRequest extends AbstractRequest {

    private Integer sessionId;
    private String yyyymmdd;

    @Override
    public void requestCheck() {
        if(sessionId==null || StringUtils.isBlank(yyyymmdd)){
            throw new ValidateException(PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),
                    PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
