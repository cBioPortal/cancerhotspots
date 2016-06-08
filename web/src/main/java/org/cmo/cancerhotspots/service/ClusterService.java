package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.model.Cluster;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface ClusterService
{
    Cluster getCluster(String clusterId);
    List<Cluster> getCluster(List<String> clusterIds);
    List<Cluster> getCluster(String hugoSymbol, String residue);
}
