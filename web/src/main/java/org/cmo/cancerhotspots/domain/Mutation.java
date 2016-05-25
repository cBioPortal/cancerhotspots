package org.cmo.cancerhotspots.domain;

import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import org.cmo.cancerhotspots.util.ChainMapConversion;
import org.cmo.cancerhotspots.util.CompositionMapConversion;

import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class Mutation
{
    @Trim
    @Parsed(field = "Hugo Symbol")
    private String hugoSymbol;

    @Trim
    @Parsed(field = "Residue")
    private String residue;

    @Trim
    @Parsed(field = "Cluster")
    private String cluster;

    @Trim
    @Convert(conversionClass = ChainMapConversion.class)
    @Parsed(field = "PDB chains")
    private Map<String, Double> pdbChains;

    @Trim
    @Parsed(field = "Class")
    private String classification;

    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
    @Parsed(field = "Variant Amino Acid")
    private Map<String, Integer> variantAminoAcid;

    @Trim
    @Parsed(field = "Q-value")
    private String qValue;

    @Trim
    @Parsed(field = "P-value")
    private String pValue;

    @Trim
    @Parsed(field = "Tumor Count")
    private Integer tumorCount;

    @Trim
    @Parsed(field = "Tumor Type Count")
    private Integer tumorTypeCount;

    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
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
