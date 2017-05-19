package org.cmo.cancerhotspots.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import org.cmo.cancerhotspots.data.IntegerRange;
import org.cmo.cancerhotspots.util.CompositionMapConversion;
import org.cmo.cancerhotspots.util.DataUtils;
import org.cmo.cancerhotspots.util.RangeConversion;

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
    @Parsed(field = "Residue")
    private String residue;

    @Trim
    @Parsed(field = "Reference_Amino_Acid")
    private String referenceAminoAcid;

    @Trim
    @Parsed(field = "Variant_Amino_Acid")
    private String variantAminoAcid;

    @Trim
    @Parsed(field = "Amino_Acid_Position")
    @Convert(conversionClass = RangeConversion.class)
    private IntegerRange aminoAcidPosition;

    // tumor type composition as <tumor type, count> pairs
    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
    @Parsed(field = "Tumor_Type_Composition")
    private Map<String, Integer> tumorTypeCompositionMap;

    public TumorTypeComposition()
    {
        this.tumorTypeCompositionMap = new LinkedHashMap<>();
    }

    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
    }

    public String getResidue()
    {
        return residue;
    }

    public void setResidue(String residue)
    {
        this.residue = residue;
    }

    public void setTumorTypeCompositionMap(Map<String, Integer> tumorTypeCompositionMap)
    {
        this.tumorTypeCompositionMap = tumorTypeCompositionMap;
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

    public IntegerRange getAminoAcidPosition()
    {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(IntegerRange aminoAcidPosition)
    {
        this.aminoAcidPosition = aminoAcidPosition;
    }

    public Object getTumorTypeComposition()
    {
        return getTumorTypeCompositionMap();
    }

    @JsonIgnore
    public Map<String, Integer> getTumorTypeCompositionMap()
    {
        return tumorTypeCompositionMap;
    }

    public void updateTumorTypeComposition(String tumorType)
    {
        String key = tumorType.toLowerCase();
        Integer count = tumorTypeCompositionMap.get(key);

        if (count == null)
        {
            count = 0;
        }

        tumorTypeCompositionMap.put(key, count + 1);
    }

    public void merge(TumorTypeComposition composition)
    {
        DataUtils.mergeCompositions(this.getTumorTypeCompositionMap(),
                                    composition.getTumorTypeCompositionMap());
    }

    public Integer tumorCount()
    {
        Integer total = 0;

        for (Integer value: getTumorTypeCompositionMap().values())
        {
            total += value;
        }

        return total;
    }

    public Integer tumorTypeCount()
    {
        return getTumorTypeCompositionMap().keySet().size();
    }
}
