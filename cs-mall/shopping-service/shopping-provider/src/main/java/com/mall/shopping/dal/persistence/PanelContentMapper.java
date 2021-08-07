package com.mall.shopping.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.entitys.PanelContentItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PanelContentMapper extends TkMapper<PanelContent> {

    List<PanelContentItem> selectPanelContentAndProductWithPanelId(@Param("panelId") Integer panelId);

    List<PanelContent> selectByPanelId(@Param("panelId") Integer panelId);
}