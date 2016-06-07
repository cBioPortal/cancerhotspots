package org.cmo.cancerhotspots.persistence.internal;

import org.cmo.cancerhotspots.model.Cluster;
import org.cmo.cancerhotspots.persistence.ClusterRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * @author Selcuk Onur Sumer
 */
@Repository
public class ClusterRepositoryImpl implements ClusterRepository
{
    private String clusterFileUri;
    @Value("${hotspot.cluster.uri}")
    public void setClusterFileUri(String clusterFileUri)
    {
        this.clusterFileUri = clusterFileUri;
    }

    @Override
    public Iterable<Cluster> findAll()
    {
        return null;
    }

    @Override
    public void saveAll(Iterable<Cluster> clusters)
    {

    }
}
