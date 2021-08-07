package com.cskaoyan.gateway.controller.shopping;

import com.mall.comment.dto.CommentListResponse;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.AllProductRequest;
import com.mall.shopping.dto.AllProductResponse;
import com.mall.shopping.dto.RecommendResponse;
import com.mall.user.annotation.Anonymous;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: cs_mall
 * @ClassName: IProductController
 * @TODO: TODO
 * @Author caifanglin
 * @Create 2021-01-23 15:36
 */
@Slf4j
@Anonymous
@RestController
@RequestMapping("/shopping")
public class ProductController {
    @Reference(timeout = 3000,check = false)
    private IProductService iProductService;
    @RequestMapping(value = "/goods",method ={RequestMethod.GET})
    public ResponseData goods(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(value = "sort",required = false) String sort,
            @RequestParam(value = "priceGt",required = false) Integer priceGt,
            @RequestParam(value = "priceLte",required = false) Integer priceLte,
            @RequestParam(value = "cid", required = false) Long cid){
        AllProductRequest request = new AllProductRequest();
        request.setPage(page);
        request.setSize(size);
        request.setSort(sort);
        request.setPriceGt(priceGt);
        request.setPriceLte(priceLte);
        AllProductResponse response = iProductService.getAllProduct(request);
        return new ResponseUtil<AllProductResponse>().setData(response);
    }
    @RequestMapping(value = "/recommend",method ={RequestMethod.GET})
    public ResponseData recommend(){
        RecommendResponse response = iProductService.getRecommendGoods();
        return new ResponseUtil<>().setData(response.getPanelContentItemDtos());
    }
}
