package com.mall.shopping.converter;

import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dto.PanelContentDto;
import com.mall.shopping.dto.PanelContentItemDto;
import com.mall.shopping.dto.PanelDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentConverter {

    @Mappings({

    })
    PanelContentDto panelContent2Dto(PanelContent panelContent);

    @Mappings({})
    PanelContentDto panelContentItem2Dto(PanelContentItem panelContentItem);

    @Mappings({})
    PanelDto panel2Dto(Panel panel);

    @Mappings({})
    PanelContentItemDto panelContentItemDto(PanelContent panelContent);

    List<PanelContentDto> panelContents2Dto(List<PanelContent> panelContents);

    List<PanelContentItemDto> panelContentItem2Dto(List<PanelContentItem> panelContentItems);

    List<PanelContentItemDto> panelContent2Dto(List<PanelContent> panelContents);
}
