package com.mall.shopping.services;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.shopping.IProductService;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.converter.ProductConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.entitys.ItemDesc;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.ItemDescMapper;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zyhstart
 * @description
 * @create 2021-01-25 19:49
 */
@Slf4j
@Service
@Component
public class IproductServiceImpl implements IProductService {
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    PanelContentMapper panelContentMapper;
    @Autowired
    PanelMapper panelMapper;
    @Autowired
    ItemDescMapper itemDescMapper;
    @Autowired
    ProductConverter productConverter;
    @Autowired
    ContentConverter contentConverter;


    @Override
    public ProductDetailResponse getProductDetail(Long id) {
        ProductDetailResponse response = new ProductDetailResponse();
        ProductDetailDto productDetailDto = new ProductDetailDto();
        Item item = itemMapper.selectByPrimaryKey(id);
        productDetailDto = productConverter.item2ProductDetailDto(item);
        ItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(id);
        productDetailDto.setDetail(itemDesc.getItemDesc());
        String image = item.getImage();
        String[] array = image.split(",");
        productDetailDto.setProductImageBig(array[0]);
        List<String> imageSmallList = new ArrayList<>();
        if (array.length == 1){
            imageSmallList.add(array[0]);
        }

        for (int i = 1; i < array.length; i++) {
            imageSmallList.add(array[i]);
        }
        productDetailDto.setProductImageSmall(imageSmallList);
        response.setProductDetailDto(productDetailDto);
        return response;
    }

    @Override
    public AllProductResponse getAllProduct(AllProductRequest request) {
        request.requestCheck();
        AllProductResponse response = new AllProductResponse();
        try {
            Integer page = request.getPage();
            Integer limit = request.getSize();
            String sort = request.getSort();
            Integer priceGt = request.getPriceGt();
            Integer priceLte = request.getPriceLte();
            PageHelper.startPage(page, limit);

            Example example = new Example(Item.class);
            if (!sort.equals("")) {
                if (sort.equals("1")) {
                    example.setOrderByClause("price ASC");
                }
                if (sort.equals("-1")) {
                    example.setOrderByClause("price DESC");
                }
            }
            Example.Criteria criteria = example.createCriteria();
//            if (priceGt != null || priceGt >= 0 || priceLte != null || priceLte >= 0 || priceLte >= 0 || priceLte >= priceGt)
            if (priceGt != null || priceLte != null) {
                criteria.andBetween("price", priceGt, priceLte);
            }
            List<Item> items = itemMapper.selectByExample(example);
            PageInfo<Item> itemPageInfo = new PageInfo<>(items);
            Long total = itemPageInfo.getTotal();
            response.setData(productConverter.items2Dto(items));
            response.setTotal(total);
        } catch (Exception e) {
            log.error("IProductServiceImpl.getAllProduct Occur Exception :" + e);
//            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }


    @Override
    public RecommendResponse getRecommendGoods() {
        RecommendResponse response = new RecommendResponse();
        try {
            Set<PanelDto> panelDtos = new HashSet<>();
            List<Panel> panels = panelMapper.selectPanelContentById(6);
            List<PanelContentItem> panelContentItems = panelContentMapper.selectPanelContentAndProductWithPanelId(6);
            List<PanelContentItemDto> panelContentItemDtos = contentConverter.panelContentItem2Dto(panelContentItems);
            for (Panel panel : panels) {
                PanelDto panelDto = new PanelDto();
                panelDto.setId(panel.getId());
                panelDto.setName(panel.getName());
                panelDto.setType(panel.getType());
                panelDto.setSortOrder(panel.getSortOrder());
                panelDto.setPosition(panel.getPosition());
                panelDto.setLimitNum(panel.getLimitNum());
                panelDto.setStatus(panel.getStatus());
                panelDto.setRemark(panel.getRemark());
                panelDto.setPanelContentItems(panelContentItemDtos);
                panelDtos.add(panelDto);
            }
            response.setPanelContentItemDtos(panelDtos);
        } catch (Exception e) {
            log.error("IProductServiceImpl.getRecommendGoods Occur Exception :" + e);
//            ExceptionProcessorUtils.wrapperHandlerException(response, e);
        }
        return response;
    }
}
