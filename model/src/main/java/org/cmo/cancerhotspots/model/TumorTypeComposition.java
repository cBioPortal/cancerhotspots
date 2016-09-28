package org.cmo.cancerhotspots.model;

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

    public String getResidue()
    {
        return residue;
    }

    public void setResidue(String residue)
    {
        this.residue = residue;
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

    public IntegerRange getAminoAcidPosition()
    {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(IntegerRange aminoAcidPosition)
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
        DataUtils.mergeCompositions(this.getTumorTypeComposition(),
                                    composition.getTumorTypeComposition());
    }

    public Integer tumorCount()
    {
        Integer total = 0;

        for (Integer value: getTumorTypeComposition().values())
        {
            total += value;
        }

        return total;
    }

    public Integer tumorTypeCount()
    {
        return getTumorTypeComposition().keySet().size();
    }
}
