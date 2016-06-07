package org.cmo.cancerhotspots.domain;

/**
 * @author Selcuk Onur Sumer
 */
public interface ClusterRepository
{
    Iterable<Cluster> findAll();
    void saveAll(Iterable<Cluster> clusters);
}
