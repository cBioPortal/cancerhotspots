package org.cmo.cancerhotspots.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class VariantComposition
{
    private String hugoSymbol;

    private String referenceAminoAcid;

    private String residue;

    private Integer aminoAcidPosition;

    private Map<String, Integer> variantComposition;

    public VariantComposition()
    {
        this.variantComposition = new LinkedHashMap<>();
    }

    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
    }

    public String getReferenceAminoAcid()
    {
        return referenceAminoAcid;
    }

    public void setReferenceAminoAcid(String referenceAminoAcid)
    {
        this.referenceAminoAcid = referenceAminoAcid;
    }

    public String getResidue()
    {
        return residue;
    }

    public void setResidue(String residue)
    {
        this.residue = residue;
    }

    public Integer getAminoAcidPosition()
    {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(Integer aminoAcidPosition)
    {
        this.aminoAcidPosition = aminoAcidPosition;
    }

    public Map<String, Integer> getVariantComposition()
    {
        return variantComposition;
    }

    public void updateVariantComposition(String variant)
    {
        String key = variant.toUpperCase();
        Integer count = variantComposition.get(key);

        if (count == null)
        {
            count = 0;
        }

        variantComposition.put(key, count + 1);
    }

    public Integer compositionCount()
    {
        Integer total = 0;

        for (Integer value: getVariantComposition().values())
        {
            total += value;
        }

        return total;
    }
}
