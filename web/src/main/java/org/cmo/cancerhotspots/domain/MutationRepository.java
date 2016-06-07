package org.cmo.cancerhotspots.domain;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationRepository
{
    Iterable<Mutation> findAll();
    void saveAll(Iterable<Mutation> mutations);
}
