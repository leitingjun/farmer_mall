package com.mall.shopping.services;

import com.alibaba.fastjson.JSON;
import com.mall.shopping.IHomeService;
import com.mall.shopping.constant.GlobalConstants;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.persistence.ItemCatMapper;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.*;
import com.mall.shopping.services.cache.CacheManager;
import com.mall.shopping.utils.ExceptionProcessorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RBucket;
import org.redisson.api.RSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zyhstart
 * @description
 * @create 2021-01-23 15:04
 */
@Slf4j
@Service
@Component
public class IHomeServiceImpl implements IHomeService {
    @Autowired
    PanelMapper panelMapper;

    @Autowired
    PanelContentMapper panelContentMapper;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemCatMapper itemCatMapper;

    @Autowired
    ContentConverter contentConverter;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    CacheManager cacheManager;

    @Override
    public HomePageResponse homepage() {
        HomePageResponse homePageResponse = new HomePageResponse();
        Set<PanelDto> panelDtos = new HashSet<PanelDto>();
        homePageResponse.setCode(ShoppingRetCode.SUCCESS.getCode());
        homePageResponse.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        try {
            String cache = cacheManager.checkCache(GlobalConstants.HOMEPAGE_CACHE_KEY);
            if(StringUtils.isNotEmpty(cache)){
                List<PanelDto> panelDtoList = JSON.parseArray(cache, PanelDto.class);
                HashSet<PanelDto> set = new HashSet<>(panelDtoList);
                homePageResponse.setPanelContentItemDtos(set);
                return homePageResponse;
            }

            List<Panel> panels = panelMapper.selectAll();
            for (Panel panel : panels) {
                PanelDto panelDto = new PanelDto();
                if (panel.getId() != 6){
                    List<PanelContent> panelContents = panelContentMapper.selectByPanelId(panel.getId());
                    List<PanelContentItemDto> panelContentItemDtos = contentConverter.panelContent2Dto(panelContents);
                    panelDto.setId(panel.getId());
                    panelDto.setName(panel.getName());
                    panelDto.setType(panel.getType());
                    panelDto.setSortOrder(panel.getSortOrder());
                    panelDto.setPosition(panel.getPosition());
                    panelDto.setLimitNum(panel.getLimitNum());
                    panelDto.setStatus(panel.getStatus());
                    panelDto.setRemark(panel.getRemark());
                    for (PanelContentItemDto contentItemDto : panelContentItemDtos) {
                        if (contentItemDto.getProductId() == null){
                            contentItemDto.setSalePrice(null);
                            contentItemDto.setSubTitle(null);
                            continue;
                        }
                        Item item = itemMapper.selectByPrimaryKey(contentItemDto.getProductId());
                        contentItemDto.setSalePrice(item.getPrice());
                        contentItemDto.setSubTitle(item.getTitle());
                    }
                    panelDto.setPanelContentItems(panelContentItemDtos);
                    panelDtos.add(panelDto);
                }
            }
            homePageResponse.setPanelContentItemDtos(panelDtos);
            cacheManager.setCache(GlobalConstants.HOMEPAGE_CACHE_KEY,JSON.toJSONString(panelDtos),GlobalConstants.HOMEPAGE_EXPIRE_TIME);

        }catch (Exception e){
            log.error("HomeServiceImpl.homepage Occur Exception :"+e);
            ExceptionProcessorUtils.wrapperHandlerException(homePageResponse,e);
            homePageResponse.setMsg(ShoppingRetCode.SYSTEM_ERROR.getMessage());
            homePageResponse.setCode(ShoppingRetCode.SYSTEM_ERROR.getCode());
        }
        return homePageResponse;
    }

    @Override
    public NavListResponse navList() {
        NavListResponse response = new NavListResponse();
        List<PanelContent> panelContents = panelContentMapper.selectByPanelId(0);
        List<PanelContentDto> panelContentDtos = contentConverter.panelContents2Dto(panelContents);
        response.setPannelContentDtos(panelContentDtos);
        return response;
    }

    @Override
    public AllProductCateResponse allCategories(String sort) {
        AllProductCateResponse response = new AllProductCateResponse();
        List<ProductCateDto> productCateDtos = itemCatMapper.selectBySort(sort);
        response.setProductCateDtoList(productCateDtos);
        return response;
    }
}
