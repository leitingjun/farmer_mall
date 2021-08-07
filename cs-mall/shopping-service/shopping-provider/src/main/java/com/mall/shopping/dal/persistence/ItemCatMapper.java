package com.mall.shopping.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.shopping.dal.entitys.ItemCat;
import com.mall.shopping.dto.ProductCateDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemCatMapper extends TkMapper<ItemCat> {
    List<ProductCateDto> selectBySort(@Param("sort") String sort);
}