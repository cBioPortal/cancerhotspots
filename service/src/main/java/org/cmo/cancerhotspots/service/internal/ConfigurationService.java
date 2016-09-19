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
    private String compositionItemSeparator;
    @Value("${conversion.separator.compositionItem}")
    public void setCompositionItemSeparator(String compositionItemSeparator)
    {
        this.compositionItemSeparator = compositionItemSeparator;
        Config.COMPOSITION_ITEM_SEPARATOR = compositionItemSeparator;
    }

    public String getCompositionItemSeparator()
    {
        return compositionItemSeparator;
    }

    private String compositionMappingSeparator;
    @Value("${conversion.separator.compositionMapping}")
    public void setCompositionMappingSeparator(String compositionMappingSeparator)
    {
        this.compositionMappingSeparator = compositionMappingSeparator;
        Config.COMPOSITION_MAPPING_SEPARATOR = compositionMappingSeparator;
    }

    public String getCompositionMappingSeparator()
    {
        return compositionMappingSeparator;
    }

    private String chainItemSeparator;
    @Value("${conversion.separator.chainItem}")
    public void setChainItemSeparator(String chainItemSeparator)
    {
        this.chainItemSeparator = chainItemSeparator;
        Config.CHAIN_ITEM_SEPARATOR = chainItemSeparator;
    }

    public String getChainItemSeparator()
    {
        return chainItemSeparator;
    }

    private String chainOpenBracket;
    @Value("${conversion.separator.chainOpenBracket}")
    public void setChainOpenBracket(String chainOpenBracket)
    {
        this.chainOpenBracket = chainOpenBracket;
        Config.CHAIN_OPEN_BRACKET = chainOpenBracket;
    }

    public String getChainOpenBracket()
    {
        return chainOpenBracket;
    }

    private String chainCloseBracket;
    @Value("${conversion.separator.chainCloseBracket}")
    public void setChainCloseBracket(String chainCloseBracket)
    {
        this.chainCloseBracket = chainCloseBracket;
        Config.CHAIN_CLOSE_BRACKET = chainCloseBracket;
    }

    public String getChainCloseBracket()
    {
        return chainCloseBracket;
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
