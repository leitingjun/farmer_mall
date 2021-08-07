package com.mall.order.constant;

/**
 * ciggar
 * create-date: 2019/7/23-16:45
 * user-service统一错误码为  005
 */
public enum OrderRetCode{
    // 系统公用
    SUCCESS("000000","成功"),
    REQUISITE_PARAMETER_NOT_EXIST("005073","必要的参数不能为空"),
    PIPELINE_RUN_EXCEPTION("005094","系统异常"),
    SHIPPING_DB_SAVED_FAILED("005095","物流信息保存数据库失败"),
    DB_SAVE_EXCEPTION("005096","数据保存异常"),
    DB_EXCEPTION("005097","数据库异常"),
    SYSTEM_TIMEOUT("005098","系统超时"),
    SYSTEM_ERROR("005099","系统错误"),

    STOCK_NUM_ERROR("0005100","亲的,购买数量太多,库存不够哦"),
    LIMIT_COUNT("0005101","商品限购5件"),
    NO_STOCK("005102","该商品已售空,请稍后再试...");


    private String code;
    private String message;
    OrderRetCode(String code,String message)
    {
        this.code = code;
        this.message = message;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public static String getMessage(String code)
    {
        for(OrderRetCode s : OrderRetCode.values())
        {
            if(null == code) break;
            if(s.code.equals(code))
            {
                return s.message;
            }
        }
        return null;
    }
}
