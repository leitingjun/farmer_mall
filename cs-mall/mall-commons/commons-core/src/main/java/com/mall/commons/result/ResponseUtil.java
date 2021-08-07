package com.mall.commons.result;

import java.util.Date;

public class ResponseUtil<T> {

    private ResponseData<T> responseData;

    public ResponseUtil() {
        responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setMessage("success");
        responseData.setCode(200);
        responseData.setTimestamp(new Date().getTime());
    }

    public ResponseData<T> setData(T t) {
        this.responseData.setResult(t);
        return this.responseData;
    }

    public ResponseData<T> setData(T t, String msg) {
        this.responseData.setResult(t);
        this.responseData.setMessage(msg);
        return this.responseData;
    }

    public ResponseData<T> setErrorMsg(String msg) {
        this.responseData.setSuccess(false);
        this.responseData.setMessage(msg);
        responseData.setCode(500);
        return this.responseData;
    }

    public ResponseData<T> setErrorMsg(Integer code, String msg) {
        this.responseData.setSuccess(false);
        this.responseData.setMessage(msg);
        responseData.setCode(code);
        return this.responseData;
    }
}
