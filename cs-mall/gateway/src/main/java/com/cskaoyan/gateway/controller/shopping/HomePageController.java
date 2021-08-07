package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IHomeService;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.shopping.dto.NavListResponse;
import com.mall.shopping.dto.ProductDetailResponse;
import com.mall.user.annotation.Anonymous;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zyhstart
 * @description
 * @create 2021-01-23 14:49
 */
@RestController
@RequestMapping("/shopping")
@Anonymous
public class HomePageController {

    @Reference(timeout = 3000000,check = false)
    IHomeService iHomeService;

    @Reference(timeout = 3000000,check = false)
    IProductService iProductService;

    @GetMapping("/homepage")
    public ResponseData homepage(){
        HomePageResponse response = iHomeService.homepage();
        return new ResponseUtil().setData(response.getPanelContentItemDtos());
    }

    @GetMapping("/navigation")
    public ResponseData navigation(){
        NavListResponse response = iHomeService.navList();
       // ResponseData responseData = new ResponseData();
        return new ResponseUtil().setData(response.getPannelContentDtos());
    }


    @GetMapping("/categories")
    public ResponseData categories(String sort){
        AllProductCateResponse response = iHomeService.allCategories(sort);
        return new ResponseUtil().setData(response.getProductCateDtoList());
    }
    @GetMapping("/product/{id}")
    public ResponseData product(@PathVariable Long id){
        ProductDetailResponse productDetail = iProductService.getProductDetail(id);
        return new ResponseUtil().setData(productDetail.getProductDetailDto());
    }
}


