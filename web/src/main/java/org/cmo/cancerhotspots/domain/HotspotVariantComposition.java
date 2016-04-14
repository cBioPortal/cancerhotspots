package org.cmo.cancerhotspots.domain;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

/**
 * @author Selcuk Onur Sumer
 */
public class HotspotVariantComposition extends VariantComposition
{
    @Trim
    @Parsed(field = "Hugo_Symbol")
    private String hugoSymbol;

    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
    }
}
