package org.cmo.cancerhotspots.model;

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
    @Parsed(field = "Cluster ID")
    private String clusterId;

    @Trim
    @Parsed(field = "Hugo Symbol")
    private String hugoSymbol;

    @Trim
    @Convert(conversionClass = ChainMapConversion.class)
    @Parsed(field = "PDB Chains")
    private Map<String, Double> pdbChains;

    @Trim
    @Convert(conversionClass = CompositionMapConversion.class)
    @Parsed(field = "Residues")
    private Map<String, Integer> residues;

    @Trim
    @Parsed(field = "P-Value")
    private String pValue;

    public Cluster()
    {
        this.residues = new LinkedHashMap<>();
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
    public Map<String, Double> getPdbChains()
    {
        return pdbChains;
    }

    public void setPdbChains(Map<String, Double> pdbChains)
    {
        this.pdbChains = pdbChains;
    }

    @ApiModelProperty(value = "Residues within this cluster (with overall tumor count)", required = true)
    public Map<String, Integer> getResidues()
    {
        return residues;
    }

    public void setResidues(Map<String, Integer> residues)
    {
        this.residues = residues;
    }

    public void addResidue(String residue, Integer tumorCount)
    {
        this.residues.put(residue, tumorCount);
    }
}
