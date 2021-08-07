package com.mall.shopping;

import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.shopping.dto.NavListResponse;

/**
 *  ciggar
 * create-date: 2019/7/23-17:16
 */
public interface IHomeService {

    HomePageResponse homepage();

    NavListResponse navList();

    AllProductCateResponse allCategories(String sort);
}
