package org.cmo.cancerhotspots.domain;

import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Selcuk Onur Sumer
 */
public class ClusteredHotspotMutation extends HotspotMutation
{
    private Set<Cluster> clusters;
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
    public Set<Cluster> getClusters()
    {
        return clusters;
    }

    public void setClusters(Set<Cluster> clusters)
    {
        this.clusters = clusters;
    }

    public void addCluster(Cluster cluster)
    {
        if (clusters == null)
        {
            clusters = new LinkedHashSet<>();
        }

        clusters.add(cluster);
    }
}
