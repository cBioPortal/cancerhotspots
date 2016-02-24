package org.cmo.cancerhotspots.domain;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class HotspotMutation
{
    @Trim
    @Parsed(field = "Hugo Symbol")
    private String hugoSymbol;

    @Trim
    @Parsed(field = "Codon")
    private String codon;

    @Trim
    @Parsed(field = "Alt Common Codon Usage *")
    private String altCommonCodonUsage;

    @Trim
    @Convert(conversionClass = MapConversion.class)
    @Parsed(field = "Variant Amino Acid")
    private Map<String, Integer> variantAminoAcid;

    @Trim
    @Parsed(field = "Q-value")
    private String qValue;

    @Trim
    @Parsed(field = "Tumor Count")
    private Integer tumorCount;

    @Trim
    @Parsed(field = "Tumor Type Count")
    private Integer tumorTypeCount;

    @Trim
    @Parsed(field = "Validation Level [a]")
    private String validationLevel;

    @Trim
    @Convert(conversionClass = MapConversion.class)
    @Parsed(field = "Tumor Type Composition")
    private Map<String, Integer> tumorTypeComposition;

    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
    }

    public String getCodon()
    {
        return codon;
    }

    public void setCodon(String codon)
    {
        this.codon = codon;
    }

    public String getAltCommonCodonUsage()
    {
        return altCommonCodonUsage;
    }

    public void setAltCommonCodonUsage(String altCommonCodonUsage)
    {
        this.altCommonCodonUsage = altCommonCodonUsage;
    }

    public Map<String, Integer> getVariantAminoAcid()
    {
        return variantAminoAcid;
    }

    public void setVariantAminoAcid(Map<String, Integer> variantAminoAcid)
    {
        this.variantAminoAcid = variantAminoAcid;
    }

    public String getqValue()
    {
        return qValue;
    }

    public void setqValue(String qValue)
    {
        this.qValue = qValue;
    }

    public Integer getTumorCount()
    {
        return tumorCount;
    }

    public void setTumorCount(Integer tumorCount)
    {
        this.tumorCount = tumorCount;
    }

    public Integer getTumorTypeCount()
    {
        return tumorTypeCount;
    }

    public void setTumorTypeCount(Integer tumorTypeCount)
    {
        this.tumorTypeCount = tumorTypeCount;
    }

    public String getValidationLevel()
    {
        return validationLevel;
    }

    public void setValidationLevel(String validationLevel)
    {
        this.validationLevel = validationLevel;
    }

    public Map<String, Integer> getTumorTypeComposition()
    {
        return tumorTypeComposition;
    }

    public void setTumorTypeComposition(Map<String, Integer> tumorTypeComposition)
    {
        this.tumorTypeComposition = tumorTypeComposition;
    }
}
