package org.cmo.cancerhotspots.domain;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public class HotspotMutation
{
    private String hugoSymbol;
    private String codon;
    private String altCommonCodonUsage;
    private List<String> variantAminoAcid;
    private String qValue;
    private Integer tumorCount;
    private Integer tumorTypeCount;
    private String validationLevel;
    private List<String> tumorTypeComposition;
}
