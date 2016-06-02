package org.cmo.cancerhotspots.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class Cluster
{
    private String clusterId;
    private Map<String, Double> pdbChains;
    private List<String> residues;
    private String pValue;

    public Cluster()
    {
        this.residues = new ArrayList<>();
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

    @ApiModelProperty(value = "Residues within this cluster", required = false)
    public List<String> getResidues()
    {
        return residues;
    }

    public void setResidues(List<String> residues)
    {
        this.residues = residues;
    }

    public void addResidue(String residue)
    {
        this.residues.add(residue);
    }
}
