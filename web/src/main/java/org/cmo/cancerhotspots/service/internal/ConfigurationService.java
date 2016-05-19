package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class ConfigurationService
{
    private String itemSeparator;
    @Value("${conversion.separator.item}")
    public void setItemSeparator(String itemSeparator)
    {
        this.itemSeparator = itemSeparator;
        Config.ITEM_SEPARATOR = itemSeparator;
    }

    public String getItemSeparator()
    {
        return itemSeparator;
    }

    private String mappingSeparator;
    @Value("${conversion.separator.mapping}")
    public void setMappingSeparator(String mappingSeparator)
    {
        this.mappingSeparator = mappingSeparator;
        Config.MAPPING_SEPARATOR = mappingSeparator;
    }

    public String getMappingSeparator()
    {
        return mappingSeparator;
    }

    private String listSeparator;

    @Value("${conversion.separator.list}")
    public void setListSeparator(String listSeparator)
    {
        this.listSeparator = listSeparator;
        Config.LIST_SEPARATOR = listSeparator;
    }

    public String getListSeparator()
    {
        return listSeparator;
    }

    private String profile;
    @Value("${hotspot.profile}")
    public void setProfile(String profile)
    {
        this.profile = profile;
    }

    public String getProfile()
    {
        return profile;
    }
}
