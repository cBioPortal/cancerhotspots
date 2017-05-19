package org.cmo.cancerhotspots.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.univocity.parsers.annotations.Convert;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import io.swagger.annotations.ApiModelProperty;
import org.cmo.cancerhotspots.util.ChainMapConversion;
import org.cmo.cancerhotspots.util.CompositionMapConversion;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class Cluster
{
    @Trim
    @Parsed(field = "Cluster_ID")
    private String clusterId;

    @Trim
    @Parsed(field = "Hugo_Symbol")
    private String hugoSymbol;

    @Trim
    @Convert(conversionClass = ChainMapConversion.class)
    @Parsed(field = "PDB_Chains")
    private Map<String, Double> pdbChainMap;

    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
    @Parsed(field = "Residues")
    private Map<String, Integer> residueMap;

    @Trim
    @Parsed(field = "P-Value")
    private String pValue;

    public Cluster()
    {
        this.residueMap = new LinkedHashMap<>();
    }

    @ApiModelProperty(value = "Hugo gene symbol", required = true)
    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
    }

    @ApiModelProperty(value = "P-value", required = true)
    public String getpValue()
    {
        return pValue;
    }

    public void setpValue(String pValue)
    {
        this.pValue = pValue;
    }

    @ApiModelProperty(value = "Cluster ID", required = true)
    public String getClusterId()
    {
        return clusterId;
    }

    public void setClusterId(String clusterId)
    {
        this.clusterId = clusterId;
    }

    @ApiModelProperty(value = "PDB chains (with p-value)", required = true)
    public Object getPdbChains()
    {
        return getPdbChainMap();
    }

    @JsonIgnore
    public Map<String, Double> getPdbChainMap()
    {
        return pdbChainMap;
    }

    public void setPdbChainMap(Map<String, Double> pdbChainMap)
    {
        this.pdbChainMap = pdbChainMap;
    }

    @ApiModelProperty(value = "Residues within this cluster (with overall tumor count)", required = true)
    public Object getResidues()
    {
        return getResidueMap();
    }

    @JsonIgnore
    public Map<String, Integer> getResidueMap()
    {
        return residueMap;
    }

    public void setResidueMap(Map<String, Integer> residueMap)
    {
        this.residueMap = residueMap;
    }

    public void addResidue(String residue, Integer tumorCount)
    {
        this.residueMap.put(residue, tumorCount);
    }
}
