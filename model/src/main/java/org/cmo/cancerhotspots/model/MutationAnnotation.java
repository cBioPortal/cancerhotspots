package org.cmo.cancerhotspots.model;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;

/**
 * @author Selcuk Onur Sumer
 */
public class MutationAnnotation
{
    @Trim
    @Parsed(field = "Hugo_Symbol")
    private String hugoSymbol;

    @Trim
    @Parsed(field = "Amino_Acid_Position")
    private Integer aminoAcidPosition;

    @Trim
    @Parsed(field = "Reference_Amino_Acid")
    private String referenceAminoAcid;

    @Trim
    @Parsed(field = "Variant_Amino_Acid")
    private String variantAminoAcid;

    @Trim
    @Parsed(field = "TUMORTYPE")
    private String tumorType;

    @Trim
    @Parsed(field = "Amino_Acid_Change")
    private String aminoAcidChange;

    @Trim
    @Parsed(field = "Variant_Classification")
    private String variantClassification;

    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
    }

    public Integer getAminoAcidPosition()
    {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(Integer aminoAcidPosition)
    {
        this.aminoAcidPosition = aminoAcidPosition;
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

    public String getTumorType()
    {
        return tumorType;
    }

    public void setTumorType(String tumorType)
    {
        this.tumorType = tumorType;
    }

    public String getAminoAcidChange()
    {
        return aminoAcidChange;
    }

    public void setAminoAcidChange(String aminoAcidChange)
    {
        this.aminoAcidChange = aminoAcidChange;
    }

    public String getVariantClassification()
    {
        return variantClassification;
    }

    public void setVariantClassification(String variantClassification)
    {
        this.variantClassification = variantClassification;
    }
}
