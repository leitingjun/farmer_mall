package com.cskaoyan.gateway.controller.promo;

import com.cskaoyan.gateway.config.CacheManager;
import com.google.common.util.concurrent.RateLimiter;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.commons.tool.utils.UtilDate;
import com.mall.promo.PromoService;
import com.mall.promo.constants.PromoRetCode;
import com.mall.promo.dto.*;
import com.mall.user.annotation.Anonymous;
import com.mall.user.dto.UserInfoDto;
import com.mall.user.intercepter.TokenIntercepter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Author wangjun
 * @Date 2021/1/29 16:31
 **/

@Slf4j
@RestController
@RequestMapping("/shopping")
public class PromoController {

    @Reference(timeout = 3000000,check = false)
    PromoService promoService;

    private RateLimiter rateLimiter;

    private ExecutorService executorService;

    @Autowired
    CacheManager cacheManager;

    @PostConstruct
    public void init(){
        rateLimiter=RateLimiter.create(100);
        executorService= Executors.newFixedThreadPool(5);
    }

    @Anonymous
    @GetMapping("/seckilllist")
    public ResponseData secKillList(Integer sessionId){
        SecKillListRequest request = new SecKillListRequest();
        request.setSessionId(sessionId);
        String date = UtilDate.getDate();
        request.setYyyymmdd(date);
        SecKillListResponse response=promoService.secKillList(request);
        if(response.getCode().equals(PromoRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setData(response);
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }
    @Anonymous
    @PostMapping("/promoProductDetail")
    public ResponseData promoProductDetail(@RequestBody PromoProductDetailRequest request){
        PromoProductDetailResponse response=promoService.queryPromoProductDetail(request);
        if(response.getCode().equals(PromoRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setData(response);
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }
    @PostMapping("/seckill")
    public ResponseData secKill(@RequestBody SecKillRequest request, HttpServletRequest servletRequest){
        //限流
        rateLimiter.tryAcquire();

        UserInfoDto userInfo = (UserInfoDto) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        Long uid = userInfo.getUid();
        request.setUserId(uid);
        //设置uniqueKey
        request.setUniqueKey(UUID.randomUUID().toString());

        String key="promo_stock_empty"+request.getProductId()+"_"+request.getPsId();
        String cache = cacheManager.checkCache(key);
        if (! StringUtils.isBlank(cache)) {
            return new ResponseUtil<>().setErrorMsg("库存已经售罄");
        }

        Future<SecKillResponse> future = executorService.submit(new Callable<SecKillResponse>() {
            @Override
            public SecKillResponse call() throws Exception {
                return promoService.secKill(request);
            }
        });
        SecKillResponse response = null;
        try {
            response = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(response.getCode().equals(PromoRetCode.SUCCESS.getCode())){
            return new ResponseUtil().setData(response);
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

}
