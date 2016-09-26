package org.cmo.cancerhotspots.model;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import org.cmo.cancerhotspots.data.IntegerRange;
import org.cmo.cancerhotspots.util.ChainMapConversion;
import org.cmo.cancerhotspots.util.CompositionMapConversion;
import org.cmo.cancerhotspots.util.RangeConversion;

import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class Mutation
{
    @Trim
    @Parsed(field = "Hugo_Symbol")
    private String hugoSymbol;

    @Trim
    @Parsed(field = "Residue")
    private String residue;

    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
    @Parsed(field = "Reference_Amino_Acid")
    private Map<String, Integer> referenceAminoAcid;

    @Trim
    @Parsed(field = "Amino_Acid_Position")
    @Convert(conversionClass = RangeConversion.class)
    private IntegerRange aminoAcidPosition;

    @Trim
    @Parsed(field = "Cluster")
    private String cluster;

    @Trim
    @Convert(conversionClass = ChainMapConversion.class)
    @Parsed(field = "PDB_chains")
    private Map<String, Double> pdbChains;

    @Trim
    @Parsed(field = "Class")
    private String classification;

    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
    @Parsed(field = "Variant_Amino_Acid")
    private Map<String, Integer> variantAminoAcid;

    @Trim
    @Parsed(field = "Q-value")
    private String qValue;

    @Trim
    @Parsed(field = "P-value")
    private String pValue;

    @Trim
    @Parsed(field = "Tumor_Count")
    private Integer tumorCount;

    @Trim
    @Parsed(field = "Tumor_Type_Count")
    private Integer tumorTypeCount;

    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
    @Parsed(field = "Tumor_Type_Composition")
    private Map<String, Integer> tumorTypeComposition;

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

    public String getpValue()
    {
        return pValue;
    }

    public void setpValue(String pValue)
    {
        this.pValue = pValue;
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

    public Map<String, Integer> getTumorTypeComposition()
    {
        return tumorTypeComposition;
    }

    public void setTumorTypeComposition(Map<String, Integer> tumorTypeComposition)
    {
        this.tumorTypeComposition = tumorTypeComposition;
    }

    public String getCluster()
    {
        return cluster;
    }

    public void setCluster(String cluster)
    {
        this.cluster = cluster;
    }

    public Map<String, Double> getPdbChains()
    {
        return pdbChains;
    }

    public void setPdbChains(Map<String, Double> pdbChains)
    {
        this.pdbChains = pdbChains;
    }

    public String getClassification()
    {
        return classification;
    }

    public void setClassification(String classification)
    {
        this.classification = classification;
    }

    public Map<String, Integer> getReferenceAminoAcid()
    {
        return referenceAminoAcid;
    }

    public void setReferenceAminoAcid(Map<String, Integer> referenceAminoAcid)
    {
        this.referenceAminoAcid = referenceAminoAcid;
    }

    public IntegerRange getAminoAcidPosition()
    {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(IntegerRange aminoAcidPosition)
    {
        this.aminoAcidPosition = aminoAcidPosition;
    }

    public String mostFrequentReference()
    {
        Integer max = Integer.MIN_VALUE;
        String reference = null;

        for (String key : referenceAminoAcid.keySet())
        {
            Integer value = referenceAminoAcid.get(key);

            // update max and reference
            if (value != null &&
                value > max)
            {
                max = value;
                reference = key;
            }
        }

        // this is the case where all values are null, but there are keys
        if (reference == null &&
            referenceAminoAcid.keySet().size() > 0)
        {
            // return the first one in the list
            reference = referenceAminoAcid.keySet().iterator().next();
        }

        return reference;
    }

    //    @Trim
//    @Parsed(field = "Alt Common Codon Usage *")
//    private String altCommonCodonUsage;
//
//    @Trim
//    @Parsed(field = "Validation Level [a]")
//    private String validationLevel;
//
//    @ApiModelProperty(value = "Alternative Common Codon Usage", required = true)
//    public String getAltCommonCodonUsage()
//    {
//        return altCommonCodonUsage;
//    }
//
//    public void setAltCommonCodonUsage(String altCommonCodonUsage)
//    {
//        this.altCommonCodonUsage = altCommonCodonUsage;
//    }
//
//    @ApiModelProperty(value = "Validation Level", required = true)
//    public String getValidationLevel()
//    {
//        return validationLevel;
//    }
//
//    public void setValidationLevel(String validationLevel)
//    {
//        this.validationLevel = validationLevel;
//    }
}
