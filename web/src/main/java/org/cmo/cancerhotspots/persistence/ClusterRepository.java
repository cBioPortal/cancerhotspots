package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.Cluster;

/**
 * @author Selcuk Onur Sumer
 */
public interface ClusterRepository
{
    Iterable<Cluster> findAll();
    void saveAll(Iterable<Cluster> clusters);
    Cluster findOne(String clusterId);
    Iterable<Cluster> findByGeneAndResidue(String hugoSymbol, String residue);
}
