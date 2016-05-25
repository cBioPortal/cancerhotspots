package org.cmo.cancerhotspots.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public class ClusteredHotspotMutation extends HotspotMutation
{
    private List<Cluster> cluster;
    private String classification;

    @ApiModelProperty(value = "Hotspot Classification", required = true)
    public String getClassification()
    {
        return classification;
    }

    public void setClassification(String classification)
    {
        this.classification = classification;
    }

    @ApiModelProperty(value = "Cluster Specific Information", required = true)
    public List<Cluster> getCluster()
    {
        return cluster;
    }

    public void setCluster(List<Cluster> cluster)
    {
        this.cluster = cluster;
    }
}
