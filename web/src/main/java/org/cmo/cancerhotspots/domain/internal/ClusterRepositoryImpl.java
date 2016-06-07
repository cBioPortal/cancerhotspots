package org.cmo.cancerhotspots.domain.internal;

import org.cmo.cancerhotspots.domain.Cluster;
import org.cmo.cancerhotspots.domain.ClusterRepository;
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
