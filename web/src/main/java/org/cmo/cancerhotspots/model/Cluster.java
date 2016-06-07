package org.cmo.cancerhotspots.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class Cluster
{
    private String clusterId;
    private Map<String, Double> pdbChains;
    private Map<String, Integer> residues;
    private String pValue;

    public Cluster()
    {
        this.residues = new LinkedHashMap<>();
    }

    @ApiModelProperty(value = "P-value", required = false)
    public String getpValue()
    {
        return pValue;
    }

    public void setpValue(String pValue)
    {
        this.pValue = pValue;
    }

    @ApiModelProperty(value = "Cluster ID", required = false)
    public String getClusterId()
    {
        return clusterId;
    }

    public void setClusterId(String clusterId)
    {
        this.clusterId = clusterId;
    }

    @ApiModelProperty(value = "PDB chains (with p-value)", required = false)
    public Map<String, Double> getPdbChains()
    {
        return pdbChains;
    }

    public void setPdbChains(Map<String, Double> pdbChains)
    {
        this.pdbChains = pdbChains;
    }

    @ApiModelProperty(value = "Residues within this cluster (with overall tumor count)", required = false)
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
