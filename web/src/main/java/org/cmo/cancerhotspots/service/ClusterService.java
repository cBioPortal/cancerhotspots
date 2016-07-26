package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.model.Cluster;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface ClusterService
{
    Cluster getCluster(String clusterId);
    List<Cluster> getClusters(List<String> clusterIds);
    List<Cluster> getClusters(String hugoSymbol);
    List<Cluster> getClusters(String hugoSymbol, String residue);

}
