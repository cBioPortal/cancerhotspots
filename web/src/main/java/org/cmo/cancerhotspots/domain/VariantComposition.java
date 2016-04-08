package org.cmo.cancerhotspots.domain;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class VariantComposition
{
    private String referenceAminoAcid;
    private String variantAminoAcid;
    private Integer aminoAcidPosition;

    // tumor type composition as <tumor type, count> pairs
    private Map<String, Integer> tumorTypeComposition;

    public VariantComposition()
    {
        this.tumorTypeComposition = new LinkedHashMap<>();
    }

    public void setTumorTypeComposition(Map<String, Integer> tumorTypeComposition)
    {
        this.tumorTypeComposition = tumorTypeComposition;
    }

    public String getReferenceAminoAcid()
    {
        return referenceAminoAcid;
    }

    public void setReferenceAminoAcid(String referenceAminoAcid)
    {
        this.referenceAminoAcid = referenceAminoAcid;
    }

    public String getVariantAminoAcid()
    {
        return variantAminoAcid;
    }

    public void setVariantAminoAcid(String variantAminoAcid)
    {
        this.variantAminoAcid = variantAminoAcid;
    }

    public Integer getAminoAcidPosition()
    {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(Integer aminoAcidPosition)
    {
        this.aminoAcidPosition = aminoAcidPosition;
    }

    public Map<String, Integer> getTumorTypeComposition()
    {
        return tumorTypeComposition;
    }

    public void updateTumorTypeComposition(String tumorType)
    {
        String key = tumorType.toLowerCase();
        Integer count = tumorTypeComposition.get(key);

        if (count == null)
        {
            count = 0;
        }

        tumorTypeComposition.put(key, count + 1);
    }
}
