package org.cmo.cancerhotspots.domain;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class TumorTypeComposition
{
    @Trim
    @Parsed(field = "Hugo_Symbol")
    private String hugoSymbol;

    @Trim
    @Parsed(field = "Reference_Amino_Acid")
    private String referenceAminoAcid;

    @Trim
    @Parsed(field = "Variant_Amino_Acid")
    private String variantAminoAcid;

    @Trim
    @Parsed(field = "Amino_Acid_Position")
    private Integer aminoAcidPosition;

    // tumor type composition as <tumor type, count> pairs
    @Trim
    @Convert(conversionClass = MapConversion.class)
    @Parsed(field = "Tumor_Type_Composition")
    private Map<String, Integer> tumorTypeComposition;

    public TumorTypeComposition()
    {
        this.tumorTypeComposition = new LinkedHashMap<>();
    }

    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
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

    public void merge(TumorTypeComposition composition)
    {
        for(String tumorType: composition.getTumorTypeComposition().keySet())
        {
            Integer count = this.getTumorTypeComposition().get(tumorType);

            // if no value yet, just copy from the source
            if (count == null)
            {
                this.getTumorTypeComposition().put(tumorType,
                    composition.getTumorTypeComposition().get(tumorType));
            }
            // if already exists add to the current value
            else
            {
                this.getTumorTypeComposition().put(tumorType,
                    count + composition.getTumorTypeComposition().get(tumorType));
            }
        }
    }

    public Integer compositionCount()
    {
        Integer total = 0;

        for (Integer value: getTumorTypeComposition().values())
        {
            total += value;
        }

        return total;
    }
}
