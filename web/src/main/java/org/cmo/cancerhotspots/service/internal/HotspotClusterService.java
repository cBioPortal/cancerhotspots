package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.model.Cluster;
import org.cmo.cancerhotspots.persistence.ClusterRepository;
import org.cmo.cancerhotspots.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotClusterService implements ClusterService
{
    private final ClusterRepository clusterRepository;

    @Autowired
    public HotspotClusterService(ClusterRepository clusterRepository)
    {
        this.clusterRepository = clusterRepository;
    }

    @Override
    public Cluster getCluster(String clusterId)
    {
        return clusterRepository.findOne(clusterId);
    }

    @Override
    public List<Cluster> getCluster(List<String> clusterIds)
    {
        List<Cluster> clusters = new ArrayList<>();

        for (String id : clusterIds)
        {
            clusters.add(getCluster(id));
        }

        return clusters;
    }

    @Override
    public List<Cluster> getCluster(String hugoSymbol, String residue)
    {
        List<Cluster> clusters = new ArrayList<>();

        for (Cluster cluster: clusterRepository.findByGeneAndResidue(hugoSymbol, residue))
        {
            clusters.add(cluster);
        }

        return clusters;
    }
}
