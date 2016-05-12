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
    @Value("${conversion.separator.item}")
    public void itemSeparator(String itemSeparator)
    {
        Config.ITEM_SEPARATOR = itemSeparator;
    }

    @Value("${conversion.separator.mapping}")
    public void mappingSeparator(String mappingSeparator)
    {
        Config.MAPPING_SEPARATOR = mappingSeparator;
    }
}
