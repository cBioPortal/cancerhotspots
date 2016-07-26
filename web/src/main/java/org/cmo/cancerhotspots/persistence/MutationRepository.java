package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.Mutation;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationRepository
{
    Iterable<Mutation> findAll();
    Iterable<Mutation> findByGene(String hugoSymbol);
    void saveAll(Iterable<Mutation> mutations);
}
