package org.cmo.cancerhotspots.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String pValue;

    @ApiModelProperty(value = "Hotspot Classification", required = true)
    public String getClassification()
    {
        return classification;
    }

    public void setClassification(String classification)
    {
        this.classification = classification;
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

    @ApiModelProperty(value = "Number of 3D clusters where this mutation is observed", required = true)
    public Integer getClusterCount()
    {
        return clusters.size();
    }

    //@ApiModelProperty(value = "Cluster Specific Information", required = true)
    @JsonIgnore
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
